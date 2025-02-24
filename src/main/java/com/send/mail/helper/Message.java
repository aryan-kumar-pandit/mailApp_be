package com.send.mail.helper;

import java.util.List;

public class Message {

    private String from;
    private String content;
    private List<String> files;
    private String subjects;

    public Message(String content, String from, List<String> files, String subjects) {
        this.content = content;
        this.from = from;
        this.files = files;
        this.subjects = subjects;
    }

    public Message() {
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public String getSubjects() {
        return subjects;
    }

    public void setSubjects(String subjects) {
        this.subjects = subjects;
    }
}
