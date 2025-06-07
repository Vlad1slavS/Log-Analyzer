package io.github.logprocessor.Parser.Transactions;

import io.github.logprocessor.Exception.BalanceInquiryException;
import io.github.logprocessor.Exception.LogParserException;
import io.github.logprocessor.Model.Enum.TransactionType;
import io.github.logprocessor.Model.Transaction;
import io.github.logprocessor.Parser.OperationParser;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static io.github.logprocessor.Parser.Patterns.AmountPattern.AMOUNT_PATTERN;

public class BalanceInquiryParser implements OperationParser {

    private static final Pattern PATTERN = Pattern.compile(
            "balance inquiry\\s+([\\d.]+)"
    );

    @Override
    public boolean canParse(String operation) {
        return operation.startsWith("balance inquiry");
    }

    @Override
    public List<Transaction> parse(LocalDateTime timestamp, String userId, String operation)
            throws BalanceInquiryException {
        String[] parts = operation.split("\\s+");
        if (parts.length != 3) { // "balance", "inquiry", "amount"
            throw new BalanceInquiryException("Invalid balance inquiry format: " + operation);
        }

        String amountStr = parts[2];

        if (!AMOUNT_PATTERN.matcher(amountStr).matches()) {
            throw new BalanceInquiryException("Invalid amount format in balance inquiry: " + amountStr);
        }

        BigDecimal amount = new BigDecimal(amountStr);
        Transaction transaction = new Transaction(TransactionType.BALANCE_INQUIRY, timestamp, userId,null
                    ,amount);

        List<Transaction> result = new ArrayList<>();
        result.add(transaction);
        return result;
    }
}

