package br.com.fabricio.utils;

public class TextFormat {

    public static String toSnakeCase(String camelCase) {
        return camelCase.replaceAll("((?<=[a-z])[A-Z])", "_$1").toUpperCase();
    }
}
