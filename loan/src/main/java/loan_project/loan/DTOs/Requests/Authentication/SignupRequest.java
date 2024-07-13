package loan_project.loan.DTOs.Requests.Authentication;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {

    private String passport;
    private String pinfl;
    private String fullName;
    private Integer age;
    private String phone;
    private String password;
    private String rePassword;
}