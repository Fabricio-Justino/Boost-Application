package br.com.fabricio;

import br.com.fabricio.anottation.Entity;
import sun.reflect.ReflectionFactory;

import java.util.Arrays;

public class BoostApplication {
    public static void run(Class<?> loader) {
        createTables(loader);
    }

    private static void createTables(Class<?> loader) {
        System.out.println(loader.getResource("br\\"));
    }
}
