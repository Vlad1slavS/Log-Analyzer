package io.github.logprocessor.Parser.Transactions;

import io.github.logprocessor.Exception.TransferException;
import io.github.logprocessor.Model.Enum.TransactionType;
import io.github.logprocessor.Model.Transaction;
import io.github.logprocessor.Parser.OperationParser;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static io.github.logprocessor.Parser.Patterns.AmountPattern.AMOUNT_PATTERN;
import static io.github.logprocessor.Parser.Patterns.UserPattern.USER_ID_PATTERN;

public class TransferParser implements OperationParser {

    @Override
    public boolean canParse(String operation) {
        return operation.startsWith("transferred") && operation.contains("to");
    }

    @Override
    public List<Transaction> parse(LocalDateTime timestamp, String userId, String operation)
            throws TransferException {
        String[] parts = operation.split("\\s+");
        if (parts.length != 4 || !parts[2].equals("to")) {
            throw new TransferException("Invalid transfer format: " + operation);
        }

        String amountStr = parts[1];
        String recipientId = parts[3];

        if (!AMOUNT_PATTERN.matcher(amountStr).matches()) {
            throw new TransferException("Invalid amount format in transfer: " + amountStr);
        }

        if (!USER_ID_PATTERN.matcher(recipientId).matches()) {
            throw new TransferException("Invalid recipient ID format: " + recipientId);
        }

        BigDecimal amount = new BigDecimal(amountStr);

        List<Transaction> result = new ArrayList<>();

        result.add(new Transaction(TransactionType.TRANSFER_OUT, timestamp, userId, recipientId,
                    amount));

        result.add(new Transaction(TransactionType.TRANSFER_IN, timestamp, recipientId, userId,
                    amount));

        return result;
    }
}

