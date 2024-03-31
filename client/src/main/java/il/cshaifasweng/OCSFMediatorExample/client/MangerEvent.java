package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class MangerEvent {
    private Message message;

    public Message getMessage() {
        return message;
    }

    public MangerEvent(Message message) {
        this.message = message;
    }
}
