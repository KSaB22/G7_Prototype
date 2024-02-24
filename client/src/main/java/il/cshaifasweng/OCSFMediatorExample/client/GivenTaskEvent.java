package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class GivenTaskEvent {
    private Message message;

    public Message getMessage() {
        return message;
    }

    public GivenTaskEvent(Message message) {
        this.message = message;
    }
}
