package com.h.msgservice.bean;

import java.io.Serializable;

public class Message implements Serializable {
    private String from;
    private String to;
    private String msg;
    private String type;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Message(String from, String to, String msg, String type) {
        this.from = from;
        this.to = to;
        this.msg = msg;
        this.type = type;
    }

    public Message() {
    }

    @Override
    public String toString() {
        return "Message{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", msg='" + msg + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
