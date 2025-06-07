package io.github.logprocessor.Parser.Transactions;

import io.github.logprocessor.Exception.WithdrawalException;
import io.github.logprocessor.Model.Enum.TransactionType;
import io.github.logprocessor.Model.Transaction;
import io.github.logprocessor.Parser.OperationParser;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static io.github.logprocessor.Parser.Patterns.AmountPattern.AMOUNT_PATTERN;

public class WithdrawalParser implements OperationParser {

    @Override
    public boolean canParse(String operation) {
        return operation.startsWith("withdrew");
    }

    @Override
    public List<Transaction> parse(LocalDateTime timestamp, String userId, String operation)
            throws WithdrawalException {
        String[] parts = operation.split("\\s+");
        if (parts.length != 2) {
            throw new WithdrawalException("Invalid withdrawal format: " + operation);
        }

        String amountStr = parts[1];

        if (!AMOUNT_PATTERN.matcher(amountStr).matches()) {
            throw new WithdrawalException("Invalid amount format in withdrawal: " + amountStr);
        }

        BigDecimal amount = new BigDecimal(amountStr);
        Transaction transaction = new Transaction(TransactionType.WITHDRAWAL, timestamp, userId, null,
                amount);

        List<Transaction> result = new ArrayList<>();
        result.add(transaction);
        return result;
    }
}
