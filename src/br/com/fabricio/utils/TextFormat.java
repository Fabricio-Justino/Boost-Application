package br.com.fabricio.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TextFormat {

    public static String toSnakeCase(String camelCase) {
        return camelCase.replaceAll("((?<=[a-z])[A-Z])", "_$1").toUpperCase();
    }

    public static String toCamelCase(String snakeCase) {
        List<String> strings = Arrays.stream(snakeCase.toLowerCase().split("_"))
                .map(str -> str.substring(0, 1).toUpperCase() + str.substring(1, str.length()))
                .toList();

        return strings.get(0).toLowerCase() + strings.stream().skip(1)
                .collect(Collectors.joining(""));
    }

    public static String splitJoin(String text, String splitRegex, String delimiter) {
        return Arrays.stream(text.split(splitRegex)).collect(Collectors.joining(delimiter));
    }
    public static String splitJoin(String text, String splitRegex) {
        return splitJoin(text, splitRegex, ", ");
    }

    public static String splitSkipJoin(String text, String splitRegex, String delimiter, int skipCount) {
        return Arrays.stream(text.split(splitRegex)).skip(skipCount).collect(Collectors.joining(delimiter));
    }
    public static String splitSkipJoin(String text, String splitRegex, int skipCount) {
        return Arrays.stream(text.split(splitRegex)).skip(skipCount).collect(Collectors.joining(", "));
    }

    public static String title(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1, text.length());
    }
}
