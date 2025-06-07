import io.github.logprocessor.Calculator.BalanceCalculator;
import io.github.logprocessor.Model.Enum.TransactionType;
import io.github.logprocessor.Model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class BalanceCalculatorTest {

    private BalanceCalculator calculator;

    @BeforeEach
    public void setUp() {
        calculator = new BalanceCalculator();
    }

    @Test
    public void testCalculateBalanceWithInquiry() {
        List<Transaction> transactions = new ArrayList<>();

        // Balance inquiry shows 1000
        transactions.add(new Transaction(TransactionType.BALANCE_INQUIRY,
                LocalDateTime.of(2025, 5, 10, 9, 0, 22),
                 "user001", null, new BigDecimal("1000")));

        // Transfer out 100
        transactions.add(new Transaction(
                LocalDateTime.of(2025, 5, 10, 9, 5, 44),
                "user001", TransactionType.TRANSFER_OUT, new BigDecimal("100"), "user002"));

        // Receive 990
        transactions.add(new Transaction(
                LocalDateTime.of(2025, 5, 10, 10, 3, 23),
                "user001", TransactionType.TRANSFER_IN, new BigDecimal("990"), "user002"));

        BigDecimal finalBalance = calculator.calculateFinalBalance(transactions);

        // 1000 - 100 + 990 = 1890
        assertEquals(new BigDecimal("1890"), finalBalance);
    }

    @Test
    public void testCalculateBalanceWithoutInquiry() {
        List<Transaction> transactions = new ArrayList<>();

        // Start - 0, receive 100
        transactions.add(new Transaction(
                LocalDateTime.of(2025, 5, 10, 9, 5, 44),
                "user001", TransactionType.TRANSFER_IN, new BigDecimal("100"), "user002"));

        // Withdraw 50
        transactions.add(new Transaction(
                LocalDateTime.of(2025, 5, 10, 10, 0, 0),
                "user001", TransactionType.WITHDRAWAL, new BigDecimal("50"), null));

        BigDecimal finalBalance = calculator.calculateFinalBalance(transactions);

        // 0 + 100 - 50 = 50
        assertEquals(new BigDecimal("50"), finalBalance);
    }

    @Test
    public void testCalculateBalanceEmptyTransactions() {
        List<Transaction> transactions = new ArrayList<>();

        BigDecimal finalBalance = calculator.calculateFinalBalance(transactions);

        assertEquals(BigDecimal.ZERO, finalBalance);
    }

    @Test
    public void testCalculateBalanceMultipleInquiries() {
        List<Transaction> transactions = new ArrayList<>();

        // First inquiry - 500
        transactions.add(new Transaction(
                LocalDateTime.of(2025, 5, 10, 9, 0, 0),
                "user001", TransactionType.BALANCE_INQUIRY, new BigDecimal("500"), null));

        // Transfer out 100
        transactions.add(new Transaction(
                LocalDateTime.of(2025, 5, 10, 10, 0, 0),
                "user001", TransactionType.TRANSFER_OUT, new BigDecimal("100"), "user002"));

        // Second inquiry - 400
        transactions.add(new Transaction(
                LocalDateTime.of(2025, 5, 10, 11, 0, 0),
                "user001", TransactionType.BALANCE_INQUIRY, new BigDecimal("400"), null));

        // Transfer in 200
        transactions.add(new Transaction(
                LocalDateTime.of(2025, 5, 10, 12, 0, 0),
                "user001", TransactionType.TRANSFER_IN, new BigDecimal("200"), "user003"));

        BigDecimal finalBalance = calculator.calculateFinalBalance(transactions);

        // Last inquiry was 400, then + 200 = 600
        assertEquals(new BigDecimal("600"), finalBalance);
    }
}