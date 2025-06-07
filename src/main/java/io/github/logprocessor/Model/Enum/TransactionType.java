package io.github.logprocessor.Model.Enum;

/**
 * Enum для определения типа транзакции
 */
public enum TransactionType {
    BALANCE_INQUIRY("balance inquiry"),
    TRANSFER_OUT("transfer out"),
    TRANSFER_IN("transfer in"),
    WITHDRAWAL("withdrawal");

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
