package loan_project.loan.DTOs.Requests.Authentication;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    private String phone;
    private String password;
}