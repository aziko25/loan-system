package loan_project.loan.Services;

import jakarta.persistence.EntityNotFoundException;
import loan_project.loan.DTOs.ReturnDTOs.UsersDTO;
import loan_project.loan.Repositories.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static loan_project.loan.Configurations.JWT.AuthorizationMethods.USER_ID;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;

    public UsersDTO getMe() {

        return new UsersDTO(usersRepository.findById(USER_ID)
                .orElseThrow(() -> new EntityNotFoundException("User not found")));
    }
}