package com.m4rc3l05.my_flux.Core.Models;

public class Todo {
    private final String _id;
    private final String _text;
    private final boolean _isDone;
    private final String _timestamp;

    private Todo(String id, String text, boolean isDone, String timestamp) {
        this._text = text;
        this._id = id;
        this._isDone = isDone;
        this._timestamp = timestamp;
    }

    public static Todo create(String id, String text, boolean isDone, String timestamp) {
        return new Todo(id, text, isDone, timestamp);
    }

    public String get_id() {
        return _id;
    }

    public String get_text() {
        return _text;
    }

    public String get_timestamp() {
        return _timestamp;
    }

    public boolean is_isDone() {
        return _isDone;
    }
}
