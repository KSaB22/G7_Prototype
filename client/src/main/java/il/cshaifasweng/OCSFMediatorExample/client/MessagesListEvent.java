package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class MessagesListEvent {
    private Message message;

    public Message getMessage() {
        return message;
    }

    public MessagesListEvent(Message message) {
        this.message = message;
    }
}
