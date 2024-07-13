package loan_project.loan.Services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import loan_project.loan.Configurations.Images.FileUploadUtilService;
import loan_project.loan.DTOs.Requests.Authentication.LoginRequest;
import loan_project.loan.DTOs.Requests.Authentication.SignupRequest;
import loan_project.loan.Models.Users;
import loan_project.loan.Repositories.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static loan_project.loan.Configurations.JWT.AuthorizationMethods.getSecretKey;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UsersRepository usersRepository;

    private final FileUploadUtilService fileUploadUtilService;

    @Value("${jwt.token.expired}")
    private Long expired;

    public Users signUp(MultipartFile passportImage, SignupRequest request) {

        /*if (request.getPassword().length() < 8) {

            throw new IllegalArgumentException("Password must be at least 8 characters");
        }*/

        if (!request.getPassword().equals(request.getRePassword())) {

            throw new IllegalArgumentException("Passwords do not match");
        }

        if (request.getAge() < 18) {

            throw new IllegalArgumentException("Age must be at least 18");
        }

        if (request.getPassport().length() != 9) {

            throw new IllegalArgumentException("Passport length must be 9");
        }

        if (request.getPinfl().length() != 14) {

            throw new IllegalArgumentException("Pinfl length must be 14");
        }

        if (usersRepository.existsByPassport(request.getPassport())) {

            throw new EntityExistsException("This Passport already exists");
        }

        if (usersRepository.existsByPinfl(request.getPinfl())) {

            throw new EntityExistsException("This Pinfl already exists");
        }

        if (usersRepository.existsByPhone(request.getPhone())) {

            throw new EntityExistsException("This Phone already exists");
        }

        String imageName = fileUploadUtilService.handleMediaUpload(request.getPassport(), passportImage);

        Users user = Users.builder()
                .passport(request.getPassport())
                .pinfl(request.getPinfl())
                .fullName(request.getFullName())
                .age(request.getAge())
                .phone(request.getPhone())
                .passportImageUrl(imageName)
                .password(request.getPassword())
                .registrationTime(LocalDateTime.now())
                .role("USER")
                .build();

        return usersRepository.save(user);
    }

    public Map<String, Object> login(LoginRequest request) {

        Users user = usersRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new EntityNotFoundException("Phone Not Found"));

        if (Objects.equals(user.getPassword(), request.getPassword())) {

            Claims claims = Jwts.claims();

            claims.put("id", user.getId());
            claims.put("role", user.getRole());

            // Expires in a week
            Date expiration = new Date(System.currentTimeMillis() + expired);

            Map<String, Object> map = new LinkedHashMap<>();

            map.put("id", user.getId());
            map.put("fullName", user.getFullName());
            map.put("role", user.getRole());
            map.put("token", Jwts.builder().setClaims(claims).setExpiration(expiration).signWith(getSecretKey()).compact());

            return map;
        }
        else {

            throw new IllegalArgumentException("Password Didn't Match!");
        }
    }
}