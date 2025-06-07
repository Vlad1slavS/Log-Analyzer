package io.github.logprocessor.Parser;

import io.github.logprocessor.Exception.LogParserException;
import io.github.logprocessor.Model.Transaction;

import java.time.LocalDateTime;
import java.util.List;

public interface OperationParser {
    boolean canParse(String operation);
    List<Transaction> parse(LocalDateTime timestamp, String userId, String operation)
            throws LogParserException;
}
