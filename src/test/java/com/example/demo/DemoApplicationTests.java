package com.example.demo;

import com.example.demo.model.Loan;
import com.example.demo.model.Contribution;
import com.example.demo.model.Member;
import com.example.demo.repository.ContributionRepository;
import com.example.demo.repository.LoanRepository;
import com.example.demo.repository.MemberRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DemoApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private LoanRepository loanRepository;

	@Autowired
	private ContributionRepository contributionRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void dashboardRendersContributionTracker() throws Exception {
		mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("F&F Stockvel overview")))
				.andExpect(content().string(containsString("YOUR GROUP, IN GOOD ORDER")))
				.andExpect(content().string(containsString("Track member savings, contributions, and loan repayments in one place.")))
				.andExpect(content().string(containsString("View month")))
				.andExpect(content().string(containsString("Download reports:")))
				.andExpect(content().string(containsString("Contributions Report")))
				.andExpect(content().string(containsString("Loans Report")))
				.andExpect(content().string(containsString("Contributed in")))
				.andExpect(content().string(containsString("Payments recorded")))
				.andExpect(content().string(containsString("Registered members")))
				.andExpect(content().string(containsString("Total contributions")))
				.andExpect(content().string(containsString("Loan balance outstanding")))
				.andExpect(content().string(containsString("No contributions for this month yet")))
				.andExpect(content().string(containsString("Record a payment below")))
				.andExpect(content().string(containsString("Record contribution")))
				.andExpect(content().string(containsString("PAYMENT HISTORY")))
				.andExpect(content().string(containsString("Issue a loan")))
				.andExpect(content().string(containsString("Repayable amount")))
				.andExpect(content().string(containsString("Record repayment")))
				.andExpect(content().string(containsString("Loan balances")))
				.andExpect(content().string(containsString("Total due (+25%)")))
				.andExpect(content().string(containsString("Add a member")))
				.andExpect(content().string(containsString("Members")))
				.andExpect(content().string(containsString("YOUR STOCKVEL")));
	}

	@Test
	void loanCalculatorScriptIsServed() throws Exception {
		mockMvc.perform(get("/loan-calculator.js"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("principalCents * 1.25")));
	}

	@Test
	@Transactional
	void memberContributionHistoryShowsOnlyTheirPaymentsInChronologicalOrder() throws Exception {
		Member member = memberRepository.save(new Member("History Test Member", "0000000002"));
		Member otherMember = memberRepository.save(new Member("Other Test Member", "0000000003"));
		contributionRepository.save(new Contribution(member, new BigDecimal("30.00"), LocalDate.of(2024, 3, 1)));
		contributionRepository.save(new Contribution(member, new BigDecimal("10.00"), LocalDate.of(2024, 1, 1)));
		contributionRepository.save(new Contribution(otherMember, new BigDecimal("99.00"), LocalDate.of(2024, 2, 1)));

		String page = mockMvc.perform(get("/members/{memberId}/contributions", member.getId()))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("MEMBER CONTRIBUTIONS")))
				.andExpect(content().string(containsString("History Test Member")))
				.andExpect(content().string(containsString("0000000002")))
				.andExpect(content().string(containsString("Total contributed")))
				.andExpect(content().string(containsString("R 40.00")))
				.andExpect(content().string(containsString("Payments recorded")))
				.andExpect(content().string(containsString(">2</strong>")))
				.andExpect(content().string(containsString("Contribution period")))
				.andExpect(content().string(containsString("FIRST TO MOST RECENT")))
				.andExpect(content().string(containsString("Contribution history")))
				.andExpect(content().string(containsString("Date paid")))
				.andExpect(content().string(containsString("Amount")))
				.andExpect(content().string(containsString("R 10.00")))
				.andExpect(content().string(containsString("R 30.00")))
				.andExpect(content().string(containsString("01 Jan 2024")))
				.andExpect(content().string(containsString("01 Mar 2024")))
				.andExpect(content().string(containsString("Back to overview")))
				.andReturn()
				.getResponse()
				.getContentAsString();

		assertTrue(page.indexOf("01 Jan 2024") < page.indexOf("01 Mar 2024"));
		org.junit.jupiter.api.Assertions.assertFalse(page.contains("99.00"));
	}

	@Test
	@Transactional
	void memberContributionHistoryShowsEmptyStateWhenNoPaymentsExist() throws Exception {
		Member member = memberRepository.save(new Member("New Member", "0000000004"));

		mockMvc.perform(get("/members/{memberId}/contributions", member.getId()))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("New Member")))
				.andExpect(content().string(containsString("Total contributed")))
				.andExpect(content().string(containsString("R 0.00")))
				.andExpect(content().string(containsString("Payments recorded")))
				.andExpect(content().string(containsString(">0</strong>")))
				.andExpect(content().string(containsString("No contributions recorded yet")))
				.andExpect(content().string(containsString(
						"This member's contribution history will appear here after their first payment.")));
	}

	@Test
	@Transactional
	void contributionRecordedForSelectedMonthAppearsInMonthlyHistory() throws Exception {
		YearMonth selectedMonth = YearMonth.now().minusMonths(1);
		Member member = memberRepository.save(new Member("Monthly Test Member", "0000000000"));

		mockMvc.perform(get("/").param("month", selectedMonth.toString()))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(
						"value=\"" + selectedMonth.atDay(1) + "\"")));

		mockMvc.perform(post("/contributions")
						.param("month", selectedMonth.toString())
						.param("memberId", member.getId().toString())
						.param("amount", "123.45")
						.param("contributionDate", selectedMonth.atDay(1).toString()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/?month=" + selectedMonth));

		mockMvc.perform(get("/").param("month", selectedMonth.toString()))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Monthly Test Member")))
				.andExpect(content().string(containsString("123.45")));
		assertTrue(contributionRepository
				.findAllByContributionDateBetweenOrderByContributionDateDesc(
						selectedMonth.atDay(1), selectedMonth.atEndOfMonth())
				.stream()
				.anyMatch(contribution -> contribution.getMember().getId().equals(member.getId())
						&& contribution.getAmount().compareTo(new BigDecimal("123.45")) == 0));
	}

	@Test
	void contributionReportDownloadsAsCsv() throws Exception {
		mockMvc.perform(get("/reports/contributions.csv"))
				.andExpect(status().isOk())
				.andExpect(content().contentType("text/csv;charset=UTF-8"))
				.andExpect(content().string(containsString("Member,Contact,Date,Amount (R)")));
	}

	@Test
	@Transactional
	void loanRequiresTwentyFivePercentInterestAndRejectsOverpayment() throws Exception {
		Member member = memberRepository.save(new Member("Loan Test Member", "0000000001"));
		Loan loan = loanRepository.save(new Loan(member, new BigDecimal("1000.00"), LocalDate.now()));

		mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("1250.00")))
				.andExpect(content().string(containsString("Includes 25% loan interest")));

		mockMvc.perform(post("/repayments")
						.param("loanId", loan.getId().toString())
						.param("amount", "1000.00")
						.param("repaymentDate", LocalDate.now().toString()))
				.andExpect(status().is3xxRedirection());

		mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("250.00")));

		mockMvc.perform(post("/repayments")
						.param("loanId", loan.getId().toString())
						.param("amount", "250.01")
						.param("repaymentDate", LocalDate.now().toString()))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(
						"The repayment cannot be greater than the outstanding loan balance.")));

		mockMvc.perform(post("/repayments")
						.param("loanId", loan.getId().toString())
						.param("amount", "250.00")
						.param("repaymentDate", LocalDate.now().toString()))
				.andExpect(status().is3xxRedirection());

		mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("1250.00")))
				.andExpect(content().string(containsString("Loan Test Member")));
	}

}
