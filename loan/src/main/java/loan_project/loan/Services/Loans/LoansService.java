package loan_project.loan.Services.Loans;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import loan_project.loan.Configurations.Images.FileUploadUtilService;
import loan_project.loan.DTOs.Filters.LoanFilters;
import loan_project.loan.DTOs.Requests.Loans.LoansRequest;
import loan_project.loan.DTOs.ReturnDTOs.LoansDTO;
import loan_project.loan.Models.Loans.Loans;
import loan_project.loan.Models.Loans.Loans_Witnesses;
import loan_project.loan.Models.Users;
import loan_project.loan.Repositories.Loans.LoansRepository;
import loan_project.loan.Repositories.Loans.Loans_Witnesses_Repository;
import loan_project.loan.Repositories.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static loan_project.loan.Configurations.JWT.AuthorizationMethods.USER_ID;

@Service
@RequiredArgsConstructor
public class LoansService {

    private final LoansRepository loansRepository;
    private final Loans_Witnesses_Repository loansWitnessesRepository;
    private final UsersRepository usersRepository;

    private final FileUploadUtilService fileUploadUtilService;

    @Value("${pageSize}")
    private Integer pageSize;

    @Transactional
    public LoansDTO createLoan(MultipartFile mediaProof, LoansRequest loansRequest) {

        Users loaner = usersRepository.findById(USER_ID)
                .orElseThrow(() -> new EntityNotFoundException("Loaner not found"));

        Users borrower = usersRepository.findById(loansRequest.getBorrowerId())
                .orElseThrow(() -> new EntityNotFoundException("Borrower not found"));

        if (loaner.equals(borrower)) {

            throw new IllegalArgumentException("Loaner Can't Be Borrower!");
        }

        Loans loan = Loans.builder()
                .createdTime(LocalDateTime.now())
                .loaner(loaner)
                .borrower(borrower)
                .sum(loansRequest.getSum())
                .deadline(loansRequest.getDeadline())
                .isBorrowerApproved(false)
                .isCameIntoForce(false)
                .isBorrowerReturnedLoan(false)
                .build();

        loansRepository.save(loan);

        if (loansRequest.getWitnessesIds() != null && !loansRequest.getWitnessesIds().isEmpty()) {

            List<Loans_Witnesses> loansWitnessesList = new ArrayList<>();

            for (Long witnessId : loansRequest.getWitnessesIds()) {

                if (witnessId.equals(loan.getLoaner().getId()) || witnessId.equals(loan.getBorrower().getId())) {

                    throw new IllegalArgumentException("Loaner Or Borrower Can't Be A Witness!");
                }

                Users witness = usersRepository.findById(witnessId)
                        .orElseThrow(() -> new EntityNotFoundException("Witness not found"));

                Loans_Witnesses loansWitness = new Loans_Witnesses(loan, witness, false);

                loansWitnessesRepository.save(loansWitness);

                loansWitnessesList.add(loansWitness);
            }

            loan.setLoansWitnessesList(loansWitnessesList);
        }

        if (mediaProof != null && !mediaProof.isEmpty()) {

            loan.setProofMediaUrl(fileUploadUtilService.handleMediaUpload(loan.getId() + "_loanProof", mediaProof));

            loansRepository.save(loan);
        }

        return new LoansDTO(loan);
    }

    public Page<LoansDTO> getAllMyLoans(int page, LoanFilters filters) {

        Pageable pageable = PageRequest.of(page - 1, pageSize);

        Users user = usersRepository.findById(USER_ID).orElseThrow(() -> new EntityNotFoundException("User Not Found"));

        if (filters != null) {

            List<Long> witnessLoanIds = new ArrayList<>();

            Boolean onlyWitnessing = filters.getOnlyWitnessing() != null ? filters.getOnlyWitnessing() : false;
            Boolean onlyLoaning = filters.getOnlyLoaning() != null ? filters.getOnlyLoaning() : false;
            Boolean onlyBorrowing = filters.getOnlyBorrowing() != null ? filters.getOnlyBorrowing() : false;

            Boolean onlyCameIntoForce = filters.getOnlyCameIntoForce() != null ? filters.getOnlyCameIntoForce() : false;
            Boolean onlyBorrowerReturnedLoan = filters.getOnlyBorrowerReturnedLoan() != null ? filters.getOnlyBorrowerReturnedLoan() : false;

            if (onlyWitnessing) {
                List<Loans_Witnesses> loansWitnessesList = loansWitnessesRepository.findAllByWitnessId(user);
                witnessLoanIds = loansWitnessesList.stream()
                        .map(loan -> loan.getLoanId().getId())
                        .collect(Collectors.toList());
            }

            Page<Loans> loansPage = loansRepository.findAllByFilters(
                    user,
                    filters.getStartDate() != null ? filters.getStartDate().atStartOfDay() : null,
                    filters.getEndDate() != null ? filters.getEndDate().atTime(23, 59) : null,
                    onlyLoaning,
                    onlyBorrowing,
                    onlyWitnessing,
                    witnessLoanIds,
                    onlyCameIntoForce,
                    onlyBorrowerReturnedLoan,
                    pageable
            );

            return loansPage.map(LoansDTO::new);
        }

        List<Loans_Witnesses> loansWitnessesList = loansWitnessesRepository.findAllByWitnessId(user);

        List<Long> loansIds = loansWitnessesList.stream()
                .map(loan -> loan.getLoanId().getId())
                .collect(Collectors.toList());

        return loansRepository.findAllByLoanerOrBorrowerOrIdIn(user, user, loansIds, pageable).map(LoansDTO::new);
    }

    public LoansDTO approveLoanAsBorrower(Long loanId, Boolean approve) {

        Users user = usersRepository.findById(USER_ID).orElseThrow(() -> new EntityNotFoundException("User Not Found"));

        Loans loan = loansRepository.findByIdAndBorrower(loanId, user)
                .orElseThrow(() -> new EntityNotFoundException("Loan Not Found"));

        if (loan.getIsCameIntoForce()) {

            throw new IllegalArgumentException("The Loan Is Already In Force");
        }

        loan.setIsBorrowerApproved(approve);

        if (approve) {

            if (loan.getLoansWitnessesList() == null || loan.getLoansWitnessesList().isEmpty()) {

                loan.setIsCameIntoForce(true);
            }
            else {

                boolean allWitnessesApproved = true;

                for (Loans_Witnesses witness : loan.getLoansWitnessesList()) {

                    if (!witness.getIsApproved()) {

                        allWitnessesApproved = false;

                        break;
                    }
                }

                loan.setIsCameIntoForce(allWitnessesApproved);
            }
        }
        else {

            loan.setIsCameIntoForce(false);
        }

        return new LoansDTO(loansRepository.save(loan));
    }

    public LoansDTO approveLoanAsWitness(Long loanId, Boolean approve) {

        Users user = usersRepository.findById(USER_ID).orElseThrow(() -> new EntityNotFoundException("User Not Found"));

        Loans loan = loansRepository.findById(loanId)
                .orElseThrow(() -> new EntityNotFoundException("Loan Not Found"));

        Loans_Witnesses loansWitness = loansWitnessesRepository.findByLoanIdAndWitnessId(loan, user)
                .orElseThrow(() -> new EntityNotFoundException("You Are Not A Witness In This Loan!"));

        if (loan.getIsCameIntoForce()) {

            throw new IllegalArgumentException("The Loan Is Already In Force");
        }

        loansWitness.setIsApproved(approve);

        loansWitnessesRepository.save(loansWitness);

        if (approve) {

            if (loan.getIsBorrowerApproved()) {

                boolean allWitnessesApproved = true;

                for (Loans_Witnesses witness : loan.getLoansWitnessesList()) {

                    if (!witness.getIsApproved()) {

                        allWitnessesApproved = false;

                        break;
                    }
                }

                loan.setIsCameIntoForce(allWitnessesApproved);
            }
            else {

                loan.setIsCameIntoForce(false);
            }
        }
        else {

            loan.setIsCameIntoForce(false);
        }

        return new LoansDTO(loansRepository.save(loan));
    }

    @Transactional
    public LoansDTO update(Long loanId, MultipartFile mediaProof, LoansRequest loansRequest) {

        Users loaner = usersRepository.findById(USER_ID).orElseThrow(() -> new EntityNotFoundException("User Not Found"));

        Loans loan = loansRepository.findByIdAndLoaner(loanId, loaner)
                .orElseThrow(() -> new EntityNotFoundException("Loan Not Found Or You Are Not A Loaner"));

        if (loan.getIsBorrowerReturnedLoan()) {

            throw new IllegalArgumentException("The Loan Is Already Returned!");
        }

        Optional.ofNullable(loansRequest.getDeadline()).ifPresent(loan::setDeadline);
        Optional.ofNullable(loansRequest.getSum()).ifPresent(loan::setSum);
        Optional.ofNullable(loansRequest.getIsBorrowerReturnedLoan()).ifPresent(loan::setIsBorrowerReturnedLoan);

        if (loan.getIsCameIntoForce()) {

            throw new IllegalArgumentException("The Loan Is Already In Force, You Can Update Only Deadline, Sum And Loan Status");
        }

        if (loansRequest.getBorrowerId() != null) {

            Users borrower = usersRepository.findById(USER_ID)
                    .orElseThrow(() -> new EntityNotFoundException("Borrower Not Found"));

            if (loaner.equals(borrower)) {

                throw new IllegalArgumentException("Loaner Can't Be A Borrower!");
            }

            loan.setBorrower(borrower);
        }

        if (loansRequest.getWitnessesIds() != null && !loansRequest.getWitnessesIds().isEmpty()) {

            List<Users> notMentionedWitnessesList = new ArrayList<>();
            List<Loans_Witnesses> loansWitnessesList = loansWitnessesRepository.findAllByLoanId(loan);

            loansWitnessesList.forEach(loansWitness -> notMentionedWitnessesList.add(loansWitness.getWitnessId()));

            for (Long witnessId : loansRequest.getWitnessesIds()) {

                Users witness = usersRepository.findById(witnessId)
                        .orElseThrow(() -> new EntityNotFoundException("Witness Not Found"));

                Loans_Witnesses loansWitness = loansWitnessesRepository.findByLoanIdAndWitnessId(loan, witness)
                        .orElse(null);

                if (loansWitness == null) {

                    loansWitness = new Loans_Witnesses(loan, witness, false);

                    loansWitnessesRepository.save(loansWitness);
                }
                else {

                    notMentionedWitnessesList.remove(loansWitness.getWitnessId());
                }
            }

            loansWitnessesRepository.deleteAllByLoanIdAndWitnessIdIn(loan, notMentionedWitnessesList);
        }
        else {

            loansWitnessesRepository.deleteAllByLoanId(loan);

            if (loan.getIsBorrowerApproved()) {

                loan.setIsCameIntoForce(true);
            }
        }

        if (mediaProof != null && !mediaProof.isEmpty()) {

            fileUploadUtilService.handleMediaDeletion(loan.getProofMediaUrl());

            loan.setProofMediaUrl(fileUploadUtilService.handleMediaUpload(loan.getId() + "_loanProof", mediaProof));
        }

        return new LoansDTO(loansRepository.save(loan));
    }

    public String delete(Long loanId) {

        Users loaner = usersRepository.findById(USER_ID)
                .orElseThrow(() -> new EntityNotFoundException("User Not Found"));

        Loans loan = loansRepository.findByIdAndLoaner(loanId, loaner)
                .orElseThrow(() -> new EntityNotFoundException("Loan Not Found Or You Are Not A Loaner"));

        List<Loans_Witnesses> loansWitnessesList = loansWitnessesRepository.findAllByLoanId(loan);

        loansWitnessesRepository.deleteAll(loansWitnessesList);
        loansRepository.delete(loan);

        return "You Successfully Deleted A Loan";
    }
}