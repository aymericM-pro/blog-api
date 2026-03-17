package com.example.blog.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ArticleCategory {
    ALL("all"),
    DEV("dev"),
    DESIGN("design"),
    JAPAN("japan"),
    GAMING("gaming");

    private final String value;

    ArticleCategory(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ArticleCategory fromValue(String value) {
        for (ArticleCategory category : values()) {
            if (category.value.equalsIgnoreCase(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown category: " + value);
    }
}
