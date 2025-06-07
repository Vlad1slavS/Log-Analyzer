package io.github.logprocessor.Parser.Patterns;

import java.util.regex.Pattern;

public final class AmountPattern {
    public static final Pattern AMOUNT_PATTERN = Pattern.compile("\\d+(?:\\.\\d+)?");
}
