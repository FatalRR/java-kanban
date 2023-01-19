package ru.yandex.practicum.server.handlers;

public enum QueriesType {
    GET("GET"),
    POST("POST"),
    DELETE("DELETE"),
    NOT_FOUND("NOT FOUND");
    private final String query;

    QueriesType(String query) {
        this.query = query;
    }

    public static QueriesType fromValue (String value) {
        for (final QueriesType queriesType : values()) {
            if (queriesType.query.equalsIgnoreCase(value)) {
                return queriesType;
            }
        }
        return NOT_FOUND;
    }
}