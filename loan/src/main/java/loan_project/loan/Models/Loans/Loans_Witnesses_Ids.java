package loan_project.loan.Models.Loans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Loans_Witnesses_Ids implements Serializable {

    private Long loanId;
    private Long witnessId;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Loans_Witnesses_Ids that = (Loans_Witnesses_Ids) o;

        return Objects.equals(loanId, that.loanId) &&
                Objects.equals(witnessId, that.witnessId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(loanId, witnessId);
    }
}