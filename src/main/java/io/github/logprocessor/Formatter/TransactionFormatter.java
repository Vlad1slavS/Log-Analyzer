package io.github.logprocessor.Formatter;

import io.github.logprocessor.Model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Класс форматирования транзакций для записи в файл.
 */
public class TransactionFormatter {
    private static final DateTimeFormatter OUTPUT_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public String format(Transaction transaction) {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(formatTimestamp(transaction.getTimestamp())).append("]");
        sb.append(" ").append(transaction.getSender());
        sb.append(" ").append(formatOperation(transaction));
        return sb.toString();
    }


    public String formatFinalBalance(String userId, BigDecimal balance, LocalDateTime timestamp) {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(formatTimestamp(timestamp)).append("]");
        sb.append(" ").append(userId);
        sb.append(" final balance ").append(formatAmount(balance));
        return sb.toString();
    }

    private String formatTimestamp(LocalDateTime timestamp) {
        return timestamp.format(OUTPUT_FORMATTER);
    }

    private String formatOperation(Transaction transaction) {
        switch (transaction.getType()) {
            case BALANCE_INQUIRY:
                return "balance inquiry " + formatAmount(transaction.getAmount());

            case TRANSFER_OUT:
                return "transferred " + formatAmount(transaction.getAmount()) +
                        " to " + transaction.getReceiver();

            case TRANSFER_IN:
                return "received " + formatAmount(transaction.getAmount()) +
                        " from " + transaction.getReceiver();

            case WITHDRAWAL:
                return "withdrew " + formatAmount(transaction.getAmount());

            default:
                throw new IllegalArgumentException("Unknown transaction type: " + transaction.getType());
        }
    }

    private String formatAmount(BigDecimal amount) {
        return amount.stripTrailingZeros().toPlainString();
    }
}
