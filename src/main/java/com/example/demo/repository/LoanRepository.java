package com.example.demo.repository;

import com.example.demo.model.Loan;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findAllByOrderByLoanDateDesc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from Loan l where l.id = :loanId")
    Optional<Loan> findByIdForUpdate(@Param("loanId") Long loanId);
}
