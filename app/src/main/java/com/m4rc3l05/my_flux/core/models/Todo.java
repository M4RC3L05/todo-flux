package com.m4rc3l05.my_flux.core.models;

public class Todo {
    private final String id;
    private final String text;
    private final boolean isDone;
    private final String timestamp;

    private Todo(String id, String text, boolean isDone, String timestamp) {
        this.text = text;
        this.id = id;
        this.isDone = isDone;
        this.timestamp = timestamp;
    }

    public static Todo create(String id, String text, boolean isDone, String timestamp) {
        return new Todo(id, text, isDone, timestamp);
    }

    public String get_id() {
        return id;
    }

    public String get_text() {
        return text;
    }

    public String get_timestamp() {
        return timestamp;
    }

    public boolean is_isDone() {
        return isDone;
    }
}
