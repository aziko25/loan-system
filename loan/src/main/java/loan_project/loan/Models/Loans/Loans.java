package loan_project.loan.Models.Loans;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import loan_project.loan.Models.Users;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "loans")
public class Loans {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(shape = STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdTime;

    @ManyToOne
    @JoinColumn(name = "loaner_id")
    private Users loaner;

    @ManyToOne
    @JoinColumn(name = "borrower_id")
    private Users borrower;

    private String text;

    private Double sum;

    @JsonFormat(shape = STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime deadline;

    private String proofMediaUrl;

    private Boolean isBorrowerApproved = Boolean.FALSE;
    private Boolean isCameIntoForce = Boolean.FALSE;
    private Boolean isBorrowerReturnedLoan = Boolean.FALSE;

    @OneToMany(mappedBy = "loanId")
    private List<Loans_Witnesses> loansWitnessesList;
}