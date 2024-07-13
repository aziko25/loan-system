package loan_project.loan.Models.Loans;

import jakarta.persistence.*;
import loan_project.loan.Models.Users;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@IdClass(Loans_Witnesses_Ids.class)
@Table(name = "loans_witnesses")
public class Loans_Witnesses {

    @Id
    @ManyToOne
    @JoinColumn(name = "loan_id")
    private Loans loanId;

    @Id
    @ManyToOne
    @JoinColumn(name = "witness_id")
    private Users witnessId;

    private Boolean isApproved = Boolean.FALSE;
}