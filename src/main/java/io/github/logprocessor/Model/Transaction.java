package io.github.logprocessor.Model;

import io.github.logprocessor.Model.Enum.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private final TransactionType type;
    private final LocalDateTime timestamp;
    private final String sender;
    private final String receiver;
    private final BigDecimal amount;

    public Transaction(TransactionType type, LocalDateTime timestamp, String sender, String receiver, BigDecimal amount) {
        this.type = type;
        this.timestamp = timestamp;
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
    }

    public Transaction(LocalDateTime timestamp, String sender, TransactionType type, BigDecimal amount, String receiver) {
        this.type = type;
        this.timestamp = timestamp;
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "type=" + type +
                ", timestamp=" + timestamp +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", amount=" + amount +
                '}';
    }


}
