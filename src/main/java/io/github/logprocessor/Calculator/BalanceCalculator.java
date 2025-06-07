package io.github.logprocessor.Calculator;

import io.github.logprocessor.Model.Transaction;
import io.github.logprocessor.Model.Enum.TransactionType;

import java.math.BigDecimal;
import java.util.List;

public class BalanceCalculator {

    /**
     * Вычисляет итоговый баланс пользователя на основе истории его транзакций.
     * Логика:
     * - Запросы баланса отражают известный баланс на момент запроса
     * - Исходящие переводы уменьшают баланс
     * - Входящие переводы увеличивают баланс
     * - Снятия средств уменьшают баланс
     */
    public BigDecimal calculateFinalBalance(List<Transaction> transactions) {
        if (transactions.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal baseBalance = findLastBalance(transactions);

        return applyTransactionsAfterLastInquiry(transactions, baseBalance);
    }

    private BigDecimal findLastBalance(List<Transaction> transactions) {
        for (int i = transactions.size() - 1; i >= 0; i--) {
            Transaction transaction = transactions.get(i);
            if (transaction.getType() == TransactionType.BALANCE_INQUIRY) {
                return transaction.getAmount();
            }
        }

        return BigDecimal.ZERO;
    }

    private BigDecimal applyTransactionsAfterLastInquiry(List<Transaction> transactions,
                                                         BigDecimal baseBalance) {
        BigDecimal balance = baseBalance;
        boolean foundLastInquiry = false;

        // Если запроса баланса нет, применяем все транзакции
        if (baseBalance.equals(BigDecimal.ZERO) && !hasBalanceInquiry(transactions)) {
            foundLastInquiry = true;
        }

        for (Transaction transaction : transactions) {
            if (!foundLastInquiry) {
                if (transaction.getType() == TransactionType.BALANCE_INQUIRY &&
                        transaction.getAmount().equals(baseBalance)) {
                    foundLastInquiry = true;
                }
                continue;
            }

            balance = applyTransaction(balance, transaction);
        }

        return balance;
    }

    private boolean hasBalanceInquiry(List<Transaction> transactions) {
        for (Transaction transaction : transactions) {
            if (transaction.getType() == TransactionType.BALANCE_INQUIRY) {
                return true;
            }
        }
        return false;
    }

    private BigDecimal applyTransaction(BigDecimal currentBalance, Transaction transaction) {
        switch (transaction.getType()) {
            case BALANCE_INQUIRY:
                return currentBalance;

            case TRANSFER_OUT, WITHDRAWAL:
                return currentBalance.subtract(transaction.getAmount());

            case TRANSFER_IN:
                return currentBalance.add(transaction.getAmount());

            default:
                throw new IllegalArgumentException("Неизвестный тип транзакции: " +
                        transaction.getType());
        }
    }
}