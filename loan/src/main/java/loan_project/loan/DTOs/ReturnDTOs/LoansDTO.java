package loan_project.loan.DTOs.ReturnDTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import loan_project.loan.Models.Loans.Loans;
import loan_project.loan.Models.Loans.Loans_Witnesses;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Getter
@Setter
//@AllArgsConstructor
//@NoArgsConstructor
public class LoansDTO {

    private Long id;

    @JsonFormat(shape = STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdTime;

    private Long loanerId;
    private String loanerPassport;
    private String loanerPinfl;
    private String loanerPhone;
    private String loanerFullName;

    private Long borrowerId;
    private String borrowerPassport;
    private String borrowerPinfl;
    private String borrowerPhone;
    private String borrowerFullName;

    private Double sum;

    @JsonFormat(shape = STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime deadline;

    private String proofMediaUrl;

    private Boolean isBorrowerApproved;
    private Boolean isCameIntoForce;
    private Boolean isBorrowerReturnedLoan;

    private List<Map<String, Object>> witnessesList;

    public LoansDTO(Loans loan) {

        id = loan.getId();
        createdTime = loan.getCreatedTime();

        loanerId = loan.getLoaner().getId();
        loanerPassport = loan.getLoaner().getPassport();
        loanerPinfl = loan.getLoaner().getPinfl();
        loanerPhone = loan.getLoaner().getPhone();
        loanerFullName = loan.getLoaner().getFullName();

        borrowerId = loan.getBorrower().getId();
        borrowerPassport = loan.getBorrower().getPassport();
        borrowerPinfl = loan.getBorrower().getPinfl();
        borrowerPhone = loan.getBorrower().getPhone();
        borrowerFullName = loan.getBorrower().getFullName();

        sum = loan.getSum();
        deadline = loan.getDeadline();
        proofMediaUrl = loan.getProofMediaUrl();

        isBorrowerApproved = loan.getIsBorrowerApproved();
        isCameIntoForce = loan.getIsCameIntoForce();
        isBorrowerReturnedLoan = loan.getIsBorrowerReturnedLoan();

        Set<Loans_Witnesses> witnesses = new HashSet<>(loan.getLoansWitnessesList());
        witnessesList = witnesses.stream()
                .map(witness -> {

                    Map<String, Object> map = new LinkedHashMap<>();

                    map.put("witnessId", witness.getWitnessId().getId());
                    map.put("witnessPassport", witness.getWitnessId().getPassport());
                    map.put("witnessPinfl", witness.getWitnessId().getPinfl());
                    map.put("witnessPhone", witness.getWitnessId().getPhone());
                    map.put("witnessFullName", witness.getWitnessId().getFullName());
                    map.put("isApproved", witness.getIsApproved());

                    return map;
                }).collect(Collectors.toList());
    }
}