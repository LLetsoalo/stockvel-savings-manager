
package com.example.demo;

import com.example.demo.model.Contribution;
import com.example.demo.model.Loan;
import com.example.demo.model.Member;
import com.example.demo.model.Repayment;
import com.example.demo.repository.ContributionRepository;
import com.example.demo.repository.LoanRepository;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.RepaymentRepository;
import com.example.demo.web.ContributionForm;
import com.example.demo.web.LoanForm;
import com.example.demo.web.MemberForm;
import com.example.demo.web.RepaymentForm;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

    private final MemberRepository memberRepository;
    private final ContributionRepository contributionRepository;
    private final LoanRepository loanRepository;
    private final RepaymentRepository repaymentRepository;

    public HomeController(
            MemberRepository memberRepository,
            ContributionRepository contributionRepository,
            LoanRepository loanRepository,
            RepaymentRepository repaymentRepository) {
        this.memberRepository = memberRepository;
        this.contributionRepository = contributionRepository;
        this.loanRepository = loanRepository;
        this.repaymentRepository = repaymentRepository;
    }

    @GetMapping("/")
    public String home(@RequestParam(required = false) String month, Model model) {
        addDashboardData(model, month);
        return "index";
    }

    @GetMapping("/members/{memberId}/contributions")
    public String memberContributions(@PathVariable Long memberId, Model model) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found"));
        var contributions = contributionRepository
                .findAllByMemberIdOrderByContributionDateAscIdAsc(memberId);
        BigDecimal total = contributions.stream()
                .map(Contribution::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("member", member);
        model.addAttribute("contributions", contributions);
        model.addAttribute("contributionTotal", total);
        return "member-contributions";
    }

    @PostMapping("/members")
    public String addMember(
            @Valid @ModelAttribute("memberForm") MemberForm memberForm,
            BindingResult bindingResult,
            @RequestParam(required = false) String month,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            addDashboardData(model, month);
            model.addAttribute("error", "Please enter the member's name and contact number.");
            return "index";
        }

        Member member = new Member(
                memberForm.getFullName().trim(),
                memberForm.getContactNumber().trim());
        memberRepository.save(member);

        redirectAttributes.addFlashAttribute("success", "Member added successfully.");
        return dashboardRedirect(month);
    }

    @PostMapping("/contributions")
    public String addContribution(
            @Valid @ModelAttribute("contributionForm") ContributionForm contributionForm,
            BindingResult bindingResult,
            @RequestParam(required = false) String month,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            addDashboardData(model, month);
            model.addAttribute("error", "Choose a member, enter a positive amount, and select the contribution date.");
            return "index";
        }

        Member member = memberRepository.findById(contributionForm.getMemberId()).orElse(null);
        if (member == null) {
            addDashboardData(model, month);
            model.addAttribute("error", "That member could not be found. Please choose a member and try again.");
            return "index";
        }

        Contribution contribution = new Contribution(
                member,
                contributionForm.getAmount(),
                contributionForm.getContributionDate());
        contributionRepository.save(contribution);

        redirectAttributes.addFlashAttribute("success", "Contribution recorded successfully.");
        return dashboardRedirect(month);
    }

    @PostMapping("/loans")
    public String addLoan(
            @Valid @ModelAttribute("loanForm") LoanForm loanForm,
            BindingResult bindingResult,
            @RequestParam(required = false) String month,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            addDashboardData(model, month);
            model.addAttribute("error", "Choose a member, enter a positive loan amount, and select the issue date.");
            return "index";
        }

        Member member = memberRepository.findById(loanForm.getMemberId()).orElse(null);
        if (member == null) {
            addDashboardData(model, month);
            model.addAttribute("error", "That member could not be found. Please choose a member and try again.");
            return "index";
        }

        loanRepository.save(new Loan(member, loanForm.getPrincipal(), loanForm.getLoanDate()));
        redirectAttributes.addFlashAttribute("success", "Loan recorded successfully.");
        return dashboardRedirect(month);
    }

    @Transactional
    @PostMapping("/repayments")
    public String addRepayment(
            @Valid @ModelAttribute("repaymentForm") RepaymentForm repaymentForm,
            BindingResult bindingResult,
            @RequestParam(required = false) String month,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            addDashboardData(model, month);
            model.addAttribute("error", "Choose an open loan, enter a positive amount, and select the repayment date.");
            return "index";
        }

        Loan loan = loanRepository.findByIdForUpdate(repaymentForm.getLoanId()).orElse(null);
        if (loan == null) {
            addDashboardData(model, month);
            model.addAttribute("error", "That loan could not be found. Please choose an open loan and try again.");
            return "index";
        }

        BigDecimal repaid = repaymentRepository.sumAmountByLoanId(loan.getId()).orElse(BigDecimal.ZERO);
        BigDecimal balance = loan.getTotalRepayable().subtract(repaid);
        if (repaymentForm.getAmount().compareTo(balance) > 0) {
            addDashboardData(model, month);
            model.addAttribute("error", "The repayment cannot be greater than the outstanding loan balance.");
            return "index";
        }

        repaymentRepository.save(new Repayment(
                loan, repaymentForm.getAmount(), repaymentForm.getRepaymentDate()));
        redirectAttributes.addFlashAttribute("success", "Loan repayment recorded successfully.");
        return dashboardRedirect(month);
    }

    private void addDashboardData(Model model, String requestedMonth) {
        YearMonth selectedMonth = parseMonth(requestedMonth);
        LocalDate startDate = selectedMonth.atDay(1);
        LocalDate endDate = selectedMonth.atEndOfMonth();

        model.addAttribute("appName", "Stockvel Savings Manager");
        model.addAttribute("selectedMonth", selectedMonth.toString());
        model.addAttribute("members", memberRepository.findAllByOrderByFullNameAsc());
        model.addAttribute(
                "contributions",
                contributionRepository.findAllByContributionDateBetweenOrderByContributionDateDesc(
                        startDate, endDate));
        model.addAttribute("memberCount", memberRepository.count());
        var loans = loanRepository.findAllByOrderByLoanDateDesc();
        Map<Long, BigDecimal> repaidByLoan = repaymentRepository.sumAmountsByLoan().stream()
                .collect(Collectors.toMap(
                        RepaymentRepository.RepaymentTotal::getLoanId,
                        RepaymentRepository.RepaymentTotal::getTotal));
        Map<Long, BigDecimal> repaidAmounts = loans.stream()
                .collect(Collectors.toMap(
                        Loan::getId,
                        loan -> repaidByLoan.getOrDefault(loan.getId(), BigDecimal.ZERO)));
        Map<Long, BigDecimal> loanBalances = loans.stream()
                .collect(Collectors.toMap(
                        Loan::getId,
                        loan -> loan.getTotalRepayable().subtract(repaidAmounts.get(loan.getId()))));
        model.addAttribute("loans", loans);
        model.addAttribute(
                "openLoans",
                loans.stream().filter(loan -> loanBalances.get(loan.getId()).signum() > 0).toList());
        model.addAttribute("repaidAmounts", repaidAmounts);
        model.addAttribute("loanBalances", loanBalances);
        model.addAttribute(
                "outstandingLoanTotal",
                loanBalances.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add));
        model.addAttribute(
                "monthlyTotal",
                contributionRepository.sumAmountBetween(startDate, endDate).orElse(BigDecimal.ZERO));
        model.addAttribute(
                "lifetimeTotal",
                contributionRepository.sumAllAmounts().orElse(BigDecimal.ZERO));
        model.addAttribute("monthlyContributionCount", contributionRepository.countByContributionDateBetween(
                startDate, endDate));

        if (!model.containsAttribute("memberForm")) {
            model.addAttribute("memberForm", new MemberForm());
        }
        if (!model.containsAttribute("contributionForm")) {
            ContributionForm form = new ContributionForm();
            LocalDate today = LocalDate.now();
            form.setContributionDate(
                    YearMonth.from(today).equals(selectedMonth) ? today : selectedMonth.atDay(1));
            model.addAttribute("contributionForm", form);
        }
        if (!model.containsAttribute("loanForm")) {
            LoanForm form = new LoanForm();
            form.setLoanDate(LocalDate.now());
            model.addAttribute("loanForm", form);
        }
        if (!model.containsAttribute("repaymentForm")) {
            RepaymentForm form = new RepaymentForm();
            form.setRepaymentDate(LocalDate.now());
            model.addAttribute("repaymentForm", form);
        }
    }

    private YearMonth parseMonth(String month) {
        if (month == null || month.isBlank()) {
            return YearMonth.now();
        }
        try {
            return YearMonth.parse(month);
        } catch (java.time.format.DateTimeParseException exception) {
            return YearMonth.now();
        }
    }

    private String dashboardRedirect(String month) {
        return "redirect:/?month=" + parseMonth(month);
    }
}
