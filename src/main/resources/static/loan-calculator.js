const principalInput = document.getElementById('principal');
const repayableAmount = document.getElementById('repayableAmount');
const zarFormatter = new Intl.NumberFormat('en-ZA', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
});

principalInput.addEventListener('input', () => {
    const principal = principalInput.valueAsNumber;

    if (!Number.isFinite(principal) || principal <= 0) {
        repayableAmount.textContent = 'Enter a loan amount';
        return;
    }

    const principalCents = Math.round(principal * 100);
    const repayableCents = Math.round(principalCents * 1.25);
    repayableAmount.textContent = `R ${zarFormatter.format(repayableCents / 100)}`;
});
