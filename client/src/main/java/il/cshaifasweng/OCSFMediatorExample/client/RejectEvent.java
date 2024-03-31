package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class RejectEvent {
    private Message message;

    public Message getMessage() {
        return message;
    }

    public RejectEvent(Message message) {
        this.message = message;
    }
}