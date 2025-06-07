package io.github.logprocessor.Parser.Patterns;

import java.util.regex.Pattern;

public final class UserPattern {
    public static final Pattern USER_ID_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");
}
