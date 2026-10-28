package com.example.demo.repository;

import com.example.demo.model.Contribution;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContributionRepository extends JpaRepository<Contribution, Long> {

    List<Contribution> findAllByContributionDateBetweenOrderByContributionDateDesc(
            LocalDate startDate, LocalDate endDate);

    List<Contribution> findAllByOrderByContributionDateDesc();

    List<Contribution> findAllByMemberIdOrderByContributionDateAscIdAsc(Long memberId);

    long countByContributionDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("""
            select sum(c.amount)
            from Contribution c
            where c.contributionDate between :startDate and :endDate
            """)
    Optional<BigDecimal> sumAmountBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("select sum(c.amount) from Contribution c")
    Optional<BigDecimal> sumAllAmounts();
}
