package io.github.logprocessor.Parser;

import io.github.logprocessor.Exception.LogParserException;
import io.github.logprocessor.Model.Transaction;

import java.util.List;

public interface LogParser {
    List<Transaction> parseLine(String line) throws LogParserException;
}
