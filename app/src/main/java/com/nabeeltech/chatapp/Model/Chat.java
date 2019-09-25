package com.nabeeltech.chatapp.Model;

public class Chat
{
    private String sender;
    private String receiver;
    private String message;
    private String status;
    private boolean isssen;

    public Chat()
    {

    }

    public Chat(String sender, String receiver, String message, String status, boolean isssen) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.status = status;
        this.isssen = isssen;
    }


    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isIsssen() {
        return isssen;
    }

    public void setIsssen(boolean isssen) {
        this.isssen = isssen;
    }
}
