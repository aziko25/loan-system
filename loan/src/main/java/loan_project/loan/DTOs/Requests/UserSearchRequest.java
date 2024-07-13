package loan_project.loan.DTOs.Requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSearchRequest {

    private String passport;
    private String phone;
}