
import io.github.logprocessor.Model.Enum.TransactionType;
import io.github.logprocessor.Model.Transaction;
import io.github.logprocessor.Parser.Impl.LogParserImpl;
import io.github.logprocessor.Service.LogProcessorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class LogParserTest {

    private LogParserImpl parser;

    @BeforeEach
    public void setUp() {
        parser = new LogParserImpl();
    }

    @Test
    public void testParseBalanceInquiry() throws Exception {

        String logLine = "[2025-05-10 09:00:22] user001 balance inquiry 1000.00";

        List<Transaction> transactions = parser.parseLine(logLine);

        assertEquals(1, transactions.size());
        Transaction transaction = transactions.get(0);
        assertEquals("user001", transaction.getSender());
        assertEquals(TransactionType.BALANCE_INQUIRY, transaction.getType());
        assertEquals(new BigDecimal("1000.00"), transaction.getAmount());
        assertEquals(LocalDateTime.of(2025, 5, 10, 9, 0, 22), transaction.getTimestamp());
        assertNull(transaction.getReceiver());
    }

    @Test
    public void testParseTransfer() throws Exception {


        String logLine = "[2025-05-10 09:05:44] user001 transferred 100.00 to user002";

        List<Transaction> transactions = parser.parseLine(logLine);

        assertEquals(2, transactions.size());

        // transaction out
        Transaction outgoing = transactions.get(0);
        assertEquals("user001", outgoing.getSender());
        assertEquals(TransactionType.TRANSFER_OUT, outgoing.getType());
        assertEquals(new BigDecimal("100.00"), outgoing.getAmount());
        assertEquals("user002", outgoing.getReceiver());

        // transaction in
        Transaction incoming = transactions.get(1);
        assertEquals("user002", incoming.getSender());
        assertEquals(TransactionType.TRANSFER_IN, incoming.getType());
        assertEquals(new BigDecimal("100.00"), incoming.getAmount());
        assertEquals("user001", incoming.getReceiver());
    }

    @Test
    public void testParseWithdrawal() throws Exception {
        String logLine = "[2025-05-10 23:55:32] user002 withdrew 50";

        List<Transaction> transactions = parser.parseLine(logLine);

        assertEquals(1, transactions.size());
        Transaction transaction = transactions.get(0);
        assertEquals("user002", transaction.getSender());
        assertEquals(TransactionType.WITHDRAWAL, transaction.getType());
        assertEquals(new BigDecimal("50"), transaction.getAmount());
        assertNull(transaction.getReceiver());
    }


    @Test
    public void testParseEmptyLine() throws Exception {
        List<Transaction> transactions = parser.parseLine("");
        assertTrue(transactions.isEmpty());
    }

    @Test
    public void testParseNullLine() throws Exception {
        List<Transaction> transactions = parser.parseLine(null);
        assertTrue(transactions.isEmpty());
    }
}