package loan_project.loan.Repositories;

import loan_project.loan.Models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

    boolean existsByPassport(String passport);
    boolean existsByPinfl(String pinfl);
    boolean existsByPhone(String phone);

    Optional<Users> findByPhone(String phone);

    Optional<Users> findByPassportAndPhone(String passport, String phone);
}