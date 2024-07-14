package loan_project.loan.DTOs.Requests.Loans;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class LoansRequest {

    private Long borrowerId;
    private String text;
    private Double sum;
    private LocalDateTime deadline;
    private Boolean isBorrowerReturnedLoan;
    private List<Long> witnessesIds;
}