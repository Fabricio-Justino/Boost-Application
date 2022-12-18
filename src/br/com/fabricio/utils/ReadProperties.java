package br.com.fabricio.utils;

import java.io.File;
import java.io.IOException;
import java.util.*;

public enum ReadProperties {
    INSTANCE;
    final private Map<String, String> propertiesMap;
    private File propertiesFile;
    ReadProperties() {
        this.propertiesMap = new HashMap<>();
        this.propertiesFile = new File("");

        configureFile(this.propertiesFile);
        configureMap(this.propertiesMap);
    }

    private void configureFile(File file) {
        final String DIRECTORY_NAME = "project_setting";
        createDirectoryIfNotExists(DIRECTORY_NAME);
        File fl = new File(DIRECTORY_NAME + "\\application.prop");
            try {
                if (!fl.exists())
                    fl.createNewFile();

                this.propertiesFile = fl;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }

    private void createDirectoryIfNotExists(String directoryName) {
        File directory = new File(".\\" + directoryName);
        if (!directory.exists())
            directory.mkdirs();
    }

    private void configureMap(Map<String, String> propertiesMap) {
        final int KEY = 0;
        final int VALUE = 1;

        try (Scanner reader = new Scanner(this.propertiesFile);) {
            do {
                String line = reader.nextLine();
                List<String> list = List.of(line.split("=")).stream().map(String::trim).toList();
                propertiesMap.put(list.get(KEY), list.get(VALUE));
            } while (reader.hasNextLine());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public String get(String key) {
        return this.propertiesMap.get(key);
    }

    public Optional<String> getIfExistsKey(String key) {
        return Optional.ofNullable(this.propertiesMap.get(key));
    }
}
