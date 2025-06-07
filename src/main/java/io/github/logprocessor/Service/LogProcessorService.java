package io.github.logprocessor.Service;

import io.github.logprocessor.Calculator.BalanceCalculator;
import io.github.logprocessor.Exception.LogParserException;
import io.github.logprocessor.Formatter.TransactionFormatter;
import io.github.logprocessor.Model.Transaction;
import io.github.logprocessor.Parser.Impl.LogParserImpl;
import io.github.logprocessor.Service.Impl.FileServiceImpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Основной сервис обработки логов.
 */
public class LogProcessorService {

    private final FileServiceImpl fileService;
    private final LogParserImpl logParserImpl;
    private final TransactionFormatter formatter;
    private final BalanceCalculator balanceCalculator;

    public LogProcessorService() {
        this.fileService = new FileServiceImpl();
        this.formatter = new TransactionFormatter();
        this.balanceCalculator = new BalanceCalculator();
        this.logParserImpl = new LogParserImpl();
    }

    /**
     * Основной метод для обработки логов.
     * 1) Поиск файлов логов в заданной директории,
     * 2) Парсинг всех транзакций,
     * 3) Создание выходной директории,
     * 4) Генерация отдельных файлов логов для каждого пользователя.
     *
     * @param inputDirectory путь к директории с файлами логов
     * @throws IOException если возникнет ошибка при работе с файловой системой
     */
    public void processLogs(Path inputDirectory) throws IOException {

        List<Path> logFiles = fileService.findLogFiles(inputDirectory);
        if (logFiles.isEmpty()) {
            System.out.println("No log files found in directory: " + inputDirectory);
            return;
        }

        System.out.println("Found " + logFiles.size() + " log files");

        Map<String, List<Transaction>> transactionsByUser = parseAllTransactions(logFiles);

        if (transactionsByUser.isEmpty()) {
            System.out.println("No valid transactions found");
            return;
        }

        Path outputDirectory = inputDirectory.resolve("transactions_by_users");
        fileService.createDirectory(outputDirectory);

        generateUserLogFiles(transactionsByUser, outputDirectory);

        System.out.println("Generated user log files in: " + outputDirectory);
    }

    /**
     * Парсит все строки из списка файлов логов и группирует транзакции по пользователям.
     * @param logFiles список путей к файлам логов
     * @return Map, ключ — пользователь (sender), значение — список транзакций
     * @throws IOException если при чтении файлов возникнет ошибка
     */
    private Map<String, List<Transaction>> parseAllTransactions(List<Path> logFiles) throws IOException {
        Map<String, List<Transaction>> transactionsByUser = new HashMap<>();

        for (Path logFile : logFiles) {
            System.out.println("Processing file: " + logFile.getFileName());

            List<String> lines = fileService.readAllLines(logFile);

            for (String line : lines) {
                try {
                    List<Transaction> transactions = logParserImpl.parseLine(line);

                    for (Transaction transaction : transactions) {
                        String sender = transaction.getSender();
                        if (!transactionsByUser.containsKey(sender)) {
                            transactionsByUser.put(sender, new ArrayList<>());
                        }
                        List<Transaction> userTransactions = transactionsByUser.get(sender);
                        userTransactions.add(transaction);
                    }
                } catch (LogParserException e) {
                    System.err.println("Warning: Failed to parse line in " +
                            logFile.getFileName() + ": " + e.getMessage());
                }
            }
        }

        return transactionsByUser;
    }

    /**
     * Генерирует отдельные файлы логов по пользователям:
     * @param transactionsByUser Map транзакций, сгруппированных по пользователям
     * @param outputDirectory    путь к выходной директории для файлов пользователей
     * @throws IOException если при записи файлов возникнет ошибка
     */
    private void generateUserLogFiles(Map<String, List<Transaction>> transactionsByUser,
                                      Path outputDirectory) throws IOException {

        for (Map.Entry<String, List<Transaction>> entry : transactionsByUser.entrySet()) {
            String userId = entry.getKey();
            List<Transaction> transactions = entry.getValue();

            transactions.sort(Comparator.comparing(Transaction::getTimestamp));

            List<String> outputLines = transactions.stream()
                    .map(formatter::format)
                    .collect(Collectors.toList());

            BigDecimal finalBalance = balanceCalculator.calculateFinalBalance(transactions);
            String finalBalanceLine = formatter.formatFinalBalance(userId, finalBalance, LocalDateTime.now());
            outputLines.add(finalBalanceLine);

            Path userLogFile = outputDirectory.resolve(userId + ".log");
            fileService.writeLines(userLogFile, outputLines);

            System.out.println("Generated: " + userLogFile.getFileName() +
                    " (" + transactions.size() + " transactions, final balance: " + finalBalance + ")");
        }
    }
}
