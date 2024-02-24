package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class TakenSpotEvent {
    private Message message;

    public Message getMessage() {
        return message;
    }

    public TakenSpotEvent(Message message) {
        this.message = message;
    }
}
