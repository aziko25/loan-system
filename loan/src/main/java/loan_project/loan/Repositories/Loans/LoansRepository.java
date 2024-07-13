package loan_project.loan.Repositories.Loans;

import loan_project.loan.Models.Loans.Loans;
import loan_project.loan.Models.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoansRepository extends JpaRepository<Loans, Long> {

    Optional<Loans> findByIdAndBorrower(Long loanId, Users user);

    Page<Loans> findAllByLoanerOrBorrowerOrIdIn(Users loaner, Users borrower, List<Long> id, Pageable pageable);

    Optional<Loans> findByIdAndLoaner(Long loanId, Users loaner);

    @Query("SELECT l FROM Loans l " +
            "WHERE (COALESCE(:startDate, l.createdTime) = l.createdTime OR l.createdTime >= :startDate) " +
            "AND (COALESCE(:endDate, l.createdTime) = l.createdTime OR l.createdTime <= :endDate) " +
            "AND (COALESCE(:onlyLoaning, false) = false OR l.loaner = :user) " +
            "AND (COALESCE(:onlyBorrowing, false) = false OR l.borrower = :user) " +
            "AND (COALESCE(:onlyWitnessing, false) = false OR l.id IN :witnessLoanIds) " +
            "AND (COALESCE(:onlyCameIntoForce, false) = false OR l.isCameIntoForce = true) " +
            "AND (COALESCE(:onlyBorrowerReturnedLoan, false) = false OR l.isBorrowerReturnedLoan = true)")
    Page<Loans> findAllByFilters(
            @Param("user") Users user,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("onlyLoaning") Boolean onlyLoaning,
            @Param("onlyBorrowing") Boolean onlyBorrowing,
            @Param("onlyWitnessing") Boolean onlyWitnessing,
            @Param("witnessLoanIds") List<Long> witnessLoanIds,
            @Param("onlyCameIntoForce") Boolean onlyCameIntoForce,
            @Param("onlyBorrowerReturnedLoan") Boolean onlyBorrowerReturnedLoan,
            Pageable pageable);
}