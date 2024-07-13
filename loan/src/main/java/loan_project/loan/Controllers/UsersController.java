package loan_project.loan.Controllers;

import loan_project.loan.Configurations.JWT.Authorization;
import loan_project.loan.Services.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@CrossOrigin(maxAge = 3600)
public class UsersController {

    private final UsersService usersService;

    @Authorization(requiredRoles = {"ADMIN", "USER"})
    @GetMapping("/me")
    public ResponseEntity<?> me() {

        return ResponseEntity.ok(usersService.getMe());
    }
}