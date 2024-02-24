package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class TaskMessageEvent {
    private Message message;

    public Message getMessage() {
        return message;
    }

    public TaskMessageEvent(Message message) {
        this.message = message;
    }
}
