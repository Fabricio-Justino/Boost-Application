package br.com.fabricio.CRUD;

import br.com.fabricio.utils.ReadProperties;

import java.util.*;

public abstract class Dialect {

    protected enum PropertiesManager {
        ALTER_LIMIT, INSERT_OR_IGNORE, CREATE_IF_NOT_EXISTS;

        protected boolean isAllowed;
        protected String value;

        public boolean isAllowed() {
            return this.isAllowed;
        }

        public String getValue() {
            return this.value;
        }
    }

    private Map<Class<?>, String> dialect;

    public Dialect() {
        this.configurePropertiesValues();
        this.dialect = new HashMap<>();
        this.configure(this.dialect);
    }

    public Optional<String> get(Class<?> key) {
        return Optional.ofNullable(this.dialect.get(key));
    }

    protected abstract void configure(Map<Class<?>, String> dialect);

    protected void allowAllProperties() {
        for (var prop : PropertiesManager.values())
            prop.isAllowed = true;
    }

    protected void disallowAllProperties() {
        for (var prop : PropertiesManager.values())
            prop.isAllowed = false;
    }

    protected List<PropertiesManager> getAllowedProperties() {
        return Arrays.stream(PropertiesManager.values()).filter(prop -> prop.isAllowed).toList();
    }

    public Optional<String> getPropertyValueIfIsAllowed(String property) {
        try {
            return getAllowedProperties()
                    .parallelStream()
                    .filter(p -> p.name().equals(property))
                    .map(PropertiesManager::getValue)
                    .findAny();
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    public Optional<String> getPropertyNameIfItsValueIsExpected(String property, String expectedValue) {
        try {
            return getAllowedProperties()
                    .parallelStream()
                    .filter(p -> p.name().equals(property))
                    .findAny()
                    .filter(p -> p.getValue().equals(expectedValue))
                    .map(Enum::name);

        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    private void configurePropertiesValues() {
        for (var property : PropertiesManager.values()) {
            ReadProperties.INSTANCE.getIfExistsKey(property.name())
                    .ifPresent(value -> {
                        property.value = value;
                    });
        }
    }
}
