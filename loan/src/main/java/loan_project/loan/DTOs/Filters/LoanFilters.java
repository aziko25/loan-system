package loan_project.loan.DTOs.Filters;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LoanFilters {

    private LocalDate startDate;
    private LocalDate endDate;

    private Boolean onlyLoaning;
    private Boolean onlyBorrowing;
    private Boolean onlyWitnessing;
}