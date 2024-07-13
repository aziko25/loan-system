package loan_project.loan.Services;

import jakarta.persistence.EntityNotFoundException;
import loan_project.loan.DTOs.Requests.UserSearchRequest;
import loan_project.loan.DTOs.ReturnDTOs.UsersDTO;
import loan_project.loan.Models.Users;
import loan_project.loan.Repositories.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static loan_project.loan.Configurations.JWT.AuthorizationMethods.USER_ID;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;

    public UsersDTO getMe() {

        return new UsersDTO(usersRepository.findById(USER_ID)
                .orElseThrow(() -> new EntityNotFoundException("User not found")));
    }

    public List<Users> getAllUsers() {

        return usersRepository.findAll();
    }

    public UsersDTO findByPassportAndPhone(UserSearchRequest userSearchRequest) {

        return new UsersDTO(usersRepository.findByPassportAndPhone(userSearchRequest.getPassport(), userSearchRequest.getPhone())
                .orElseThrow(() -> new EntityNotFoundException("User Not Found")));
    }
}