package loan_project.loan.Controllers;

import loan_project.loan.Configurations.JWT.Authorization;
import loan_project.loan.DTOs.Requests.UserSearchRequest;
import loan_project.loan.Services.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/all")
    public ResponseEntity<?> all() {

        return ResponseEntity.ok(usersService.getAllUsers());
    }

    @PostMapping("/search")
    public ResponseEntity<?> searchUser(@RequestBody UserSearchRequest userSearchRequest) {

        return ResponseEntity.ok(usersService.findByPassportAndPhone(userSearchRequest));
    }
}