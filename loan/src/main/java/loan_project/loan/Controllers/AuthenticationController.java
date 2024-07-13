package loan_project.loan.Controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import loan_project.loan.DTOs.Requests.Authentication.LoginRequest;
import loan_project.loan.DTOs.Requests.Authentication.SignupRequest;
import loan_project.loan.Services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(maxAge = 3600)
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@RequestParam(value = "media", required = false) MultipartFile media,
                                    @RequestParam("user") String userJson) throws JsonProcessingException {

        SignupRequest signupRequest = new ObjectMapper().readValue(userJson, SignupRequest.class);

        return new ResponseEntity<>(authenticationService.signUp(media, signupRequest), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        return ResponseEntity.ok(authenticationService.login(loginRequest));
    }
}