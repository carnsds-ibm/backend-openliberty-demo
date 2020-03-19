package io.openliberty.guides.application.models;

public class Article {
    private String title;
    private String body;

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public void setTitle(String tString) {
        title = tString;
    }

    public void setBody(String bString) {
        body = bString;
    }
}