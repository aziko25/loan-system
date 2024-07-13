package loan_project.loan.Controllers.Loans;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import loan_project.loan.Configurations.JWT.Authorization;
import loan_project.loan.DTOs.Filters.LoanFilters;
import loan_project.loan.DTOs.Requests.Loans.LoansRequest;
import loan_project.loan.Services.Loans.LoansService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
@CrossOrigin(maxAge = 3600)
public class LoansController {

    private final LoansService loansService;

    @Authorization(requiredRoles = {"ADMIN", "USER"})
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestParam(value = "media", required = false) MultipartFile media,
                                    @RequestParam("loan") String loanJson) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        LoansRequest loansRequest = objectMapper.readValue(loanJson, LoansRequest.class);

        return new ResponseEntity<>(loansService.createLoan(media, loansRequest), HttpStatus.CREATED);
    }

    @Authorization(requiredRoles = {"ADMIN", "USER"})
    @PostMapping("/allMy")
    public ResponseEntity<?> getAllMy(@RequestParam Integer page, @RequestBody(required = false) LoanFilters filters) {

        return ResponseEntity.ok(loansService.getAllMyLoans(page, filters));
    }

    @Authorization(requiredRoles = {"ADMIN", "USER"})
    @PutMapping("/approveAsBorrower/{id}")
    public ResponseEntity<?> approveAsBorrower(@PathVariable Long id, @RequestParam Boolean isApproved) {

        return ResponseEntity.ok(loansService.approveLoanAsBorrower(id, isApproved));
    }

    @Authorization(requiredRoles = {"ADMIN", "USER"})
    @PutMapping("/approveAsWitness/{id}")
    public ResponseEntity<?> approveAsWitness(@PathVariable Long id, @RequestParam Boolean isApproved) {

        return ResponseEntity.ok(loansService.approveLoanAsWitness(id, isApproved));
    }

    @Authorization(requiredRoles = {"ADMIN", "USER"})
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestParam(value = "media", required = false) MultipartFile media,
                                    @RequestParam("loan") String loanJson) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        LoansRequest loansRequest = objectMapper.readValue(loanJson, LoansRequest.class);

        return ResponseEntity.ok(loansService.update(id, media, loansRequest));
    }

    @Authorization(requiredRoles = {"ADMIN", "USER"})
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        return ResponseEntity.ok(loansService.delete(id));
    }
}