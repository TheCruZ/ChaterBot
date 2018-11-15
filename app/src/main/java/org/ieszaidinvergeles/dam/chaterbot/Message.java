package org.ieszaidinvergeles.dam.chaterbot;

public class Message {
    String from,message;
    long id,when;

    public Message(long id, String from, String message, long when) {
        this.from = from;
        this.message = message;
        this.id = id;
        this.when = when;
    }

    public Message(String from, String message, long when) {
        this.from = from;
        this.message = message;
        this.when = when;
    }

    public Message(){}

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getWhen() {
        return when;
    }

    public void setWhen(long when) {
        this.when = when;
    }

    @Override
    public String toString() {
        return getFrom()+"> "+getMessage();
    }
}
