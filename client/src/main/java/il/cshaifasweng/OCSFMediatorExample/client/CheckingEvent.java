package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class CheckingEvent {
    private Message message;

    public Message getMessage() {
        return message;
    }

    public CheckingEvent(Message message) {
        this.message = message;
    }
}
