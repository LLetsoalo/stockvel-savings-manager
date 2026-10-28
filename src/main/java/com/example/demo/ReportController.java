package com.example.demo;

import com.example.demo.model.Contribution;
import com.example.demo.model.Loan;
import com.example.demo.repository.ContributionRepository;
import com.example.demo.repository.LoanRepository;
import com.example.demo.repository.RepaymentRepository;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReportController {

    private final ContributionRepository contributionRepository;
    private final LoanRepository loanRepository;
    private final RepaymentRepository repaymentRepository;

    public ReportController(
            ContributionRepository contributionRepository,
            LoanRepository loanRepository,
            RepaymentRepository repaymentRepository) {
        this.contributionRepository = contributionRepository;
        this.loanRepository = loanRepository;
        this.repaymentRepository = repaymentRepository;
    }

    @GetMapping("/reports/contributions.csv")
    public ResponseEntity<byte[]> contributions() {
        StringBuilder csv = new StringBuilder("Member,Contact,Date,Amount (R)\r\n");
        for (Contribution contribution : contributionRepository.findAllByOrderByContributionDateDesc()) {
            appendRow(csv,
                    contribution.getMember().getFullName(),
                    contribution.getMember().getContactNumber(),
                    contribution.getContributionDate().toString(),
                    contribution.getAmount().toPlainString());
        }
        return csvResponse("stockvel-contributions.csv", csv);
    }

    @GetMapping("/reports/loans.csv")
    public ResponseEntity<byte[]> loans() {
        Map<Long, BigDecimal> repaidByLoan = repaymentRepository.sumAmountsByLoan().stream()
                .collect(Collectors.toMap(
                        RepaymentRepository.RepaymentTotal::getLoanId,
                        RepaymentRepository.RepaymentTotal::getTotal));
        StringBuilder csv = new StringBuilder("Member,Contact,Issued,Principal (R),Repaid (R),Outstanding (R)\r\n");
        for (Loan loan : loanRepository.findAllByOrderByLoanDateDesc()) {
            BigDecimal repaid = repaidByLoan.getOrDefault(loan.getId(), BigDecimal.ZERO);
            appendRow(csv,
                    loan.getMember().getFullName(),
                    loan.getMember().getContactNumber(),
                    loan.getLoanDate().toString(),
                    loan.getPrincipal().toPlainString(),
                    repaid.toPlainString(),
                    loan.getPrincipal().subtract(repaid).toPlainString());
        }
        return csvResponse("stockvel-loans.csv", csv);
    }

    private ResponseEntity<byte[]> csvResponse(String filename, StringBuilder csv) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(csv.toString().getBytes(StandardCharsets.UTF_8));
    }

    private void appendRow(StringBuilder csv, String... values) {
        for (int index = 0; index < values.length; index++) {
            if (index > 0) {
                csv.append(',');
            }
            String value = values[index];
            String firstContent = value.stripLeading();
            if (!firstContent.isEmpty() && "=+-@".indexOf(firstContent.charAt(0)) >= 0) {
                value = "'" + value;
            }
            csv.append('"').append(value.replace("\"", "\"\"")).append('"');
        }
        csv.append("\r\n");
    }
}
