package io.openliberty.guides.application.models;

public class Article {
    private String title;
    private String body;
    private String key;

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getKey() {
        return key;
    }

    public void setTitle(String tString) {
        title = tString;
    }

    public void setBody(String bString) {
        body = bString;
    }

    public void setKey(String k) {
        key = k;
    }
}