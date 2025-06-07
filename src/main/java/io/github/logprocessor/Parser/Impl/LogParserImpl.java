package io.github.logprocessor.Parser.Impl;

import io.github.logprocessor.Exception.LogParserException;
import io.github.logprocessor.Model.Transaction;
import io.github.logprocessor.Parser.*;
import io.github.logprocessor.Parser.Transactions.BalanceInquiryParser;
import io.github.logprocessor.Parser.Transactions.TransferParser;
import io.github.logprocessor.Parser.Transactions.WithdrawalParser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static io.github.logprocessor.Parser.Patterns.TimestampPatterns.*;

/**
 * Парсер логов
 */
public class LogParserImpl implements LogParser {


    private final List<OperationParser> operationParsers;

    public LogParserImpl() {
        this.operationParsers = new ArrayList<>();
        initializeParsers();
    }

    private void initializeParsers() {
        operationParsers.add(new BalanceInquiryParser());
        operationParsers.add(new TransferParser());
        operationParsers.add(new WithdrawalParser());
    }


    /**
     * Парсит строку лога и возвращает список транзакций
     */
    public List<Transaction> parseLine(String line) throws LogParserException {
        if (line == null || line.trim().isEmpty()) {
            return new ArrayList<>();
        }

        line = line.trim();

        if (!line.startsWith("[") || !line.contains("]")) {
            throw new LogParserException("Invalid log format - missing timestamp brackets: " + line);
        }

        int timestampEnd = line.indexOf(']');
        if (timestampEnd == -1) {
            throw new LogParserException("Invalid log format - unclosed timestamp bracket: " + line);
        }

        String timestampStr = line.substring(1, timestampEnd);
        String remaining = line.substring(timestampEnd + 1).trim();

        if (!isValidTimestampFormat(timestampStr)) {
            throw new LogParserException("Invalid timestamp format: " + timestampStr +
                    ". Supported formats: yyyy-MM-dd HH:mm:ss, yyyy/MM/dd HH:mm:ss, dd-MM-yyyy HH:mm:ss, etc.");
        }

        String[] parts = remaining.split("\\s+", 2);

        if (parts.length < 2) {
            throw new LogParserException("Invalid log format - missing userId or operation: " + line);
        }

        String userId = parts[0];
        String operation = parts[1];

        LocalDateTime timestamp = parseTimestamp(timestampStr);

        for (OperationParser parser : operationParsers) {
            if (parser.canParse(operation)) {
                return parser.parse(timestamp, userId, operation);
            }
        }

        throw new LogParserException("Unknown operation format: " + operation);
    }

    private boolean isValidTimestampFormat(String timestampStr) {
        return REGEXPS.stream()
                .anyMatch(pattern -> pattern.matcher(timestampStr).matches());
    }

    private LocalDateTime parseTimestamp(String timestampStr) throws LogParserException {
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                return LocalDateTime.parse(timestampStr, formatter);
            } catch (Exception ignored) {

            }
        }

        throw new LogParserException("Unable to parse timestamp: " + timestampStr +
                ". Supported formats: yyyy-MM-dd HH:mm:ss, yyyy/MM/dd HH:mm:ss, dd-MM-yyyy HH:mm:ss, " +
                "yyyy-MM-dd'T'HH:mm:ss, yyyy-MM-dd HH:mm:ss.SSS, MMM dd, yyyy HH:mm:ss");
    }

}
