package loan_project.loan.DTOs.ReturnDTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import loan_project.loan.Models.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsersDTO {

    private Long id;

    @JsonFormat(shape = STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime registrationTime;

    private String passport;
    private String pinfl;
    private String fullName;
    private String phone;
    private Integer age;
    private String passportImageUrl;

    public UsersDTO(Users user) {

        id = user.getId();
        registrationTime = user.getRegistrationTime();
        passport = user.getPassport();
        pinfl = user.getPinfl();
        fullName = user.getFullName();
        phone = user.getPhone();
        age = user.getAge();
        passportImageUrl = user.getPassportImageUrl();
    }
}