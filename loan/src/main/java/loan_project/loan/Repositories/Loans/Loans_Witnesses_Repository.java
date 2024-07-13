package loan_project.loan.Repositories.Loans;

import loan_project.loan.Models.Loans.Loans;
import loan_project.loan.Models.Loans.Loans_Witnesses;
import loan_project.loan.Models.Loans.Loans_Witnesses_Ids;
import loan_project.loan.Models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Loans_Witnesses_Repository extends JpaRepository<Loans_Witnesses, Loans_Witnesses_Ids> {

    Optional<Loans_Witnesses> findByLoanIdAndWitnessId(Loans loan, Users user);

    List<Loans_Witnesses> findAllByWitnessId(Users witnessId);

    List<Loans_Witnesses> findAllByLoanId(Loans loan);

    void deleteAllByLoanIdAndWitnessIdIn(Loans loanId, List<Users> witnessId);

    void deleteAllByLoanId(Loans loan);
}