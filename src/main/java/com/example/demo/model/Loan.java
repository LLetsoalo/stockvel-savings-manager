package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Entity
@Table(name = "loans")
public class Loan {

    private static final BigDecimal INTEREST_RATE = new BigDecimal("0.25");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal principal;

    @Column(name = "loan_date", nullable = false)
    private LocalDate loanDate;

    protected Loan() {
    }

    public Loan(Member member, BigDecimal principal, LocalDate loanDate) {
        this.member = member;
        this.principal = principal;
        this.loanDate = loanDate;
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public BigDecimal getPrincipal() {
        return principal;
    }

    public BigDecimal getTotalRepayable() {
        return principal.multiply(BigDecimal.ONE.add(INTEREST_RATE))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }
}
