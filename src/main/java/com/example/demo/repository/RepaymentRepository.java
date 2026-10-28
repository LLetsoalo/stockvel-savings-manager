package com.example.demo.repository;

import com.example.demo.model.Repayment;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RepaymentRepository extends JpaRepository<Repayment, Long> {

    @Query("select sum(r.amount) from Repayment r where r.loan.id = :loanId")
    Optional<BigDecimal> sumAmountByLoanId(@Param("loanId") Long loanId);

    @Query("select r.loan.id as loanId, sum(r.amount) as total from Repayment r group by r.loan.id")
    List<RepaymentTotal> sumAmountsByLoan();

    interface RepaymentTotal {
        Long getLoanId();

        BigDecimal getTotal();
    }
}
