package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import org.greenrobot.eventbus.EventBus;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;

public class SimpleClient extends AbstractClient {
	
	private static SimpleClient client = null;

	private SimpleClient(String host, int port) {
		super(host, port);
	}

	@Override
	protected void handleMessageFromServer(Object msg) {
		Message message = (Message) msg;
		if(message.getMessage().equals("list of tasks")){
			EventBus.getDefault().post(new TaskMessageEvent(message));
		}else if(message.getMessage().equals("client added successfully")){
			EventBus.getDefault().post(new NewSubscriberEvent(message));
		}else if(message.getMessage().equals("specific task")){
			EventBus.getDefault().post(new GivenTaskEvent(message));
		}else if(message.getMessage().equals("already vol")){
			EventBus.getDefault().post(new ErrorEvent(message));
		} else if (message.getMessage().equals("account found")) {
			EventBus.getDefault().post(new LoginEvent(message));
		} else if(message.getMessage().equals("wrong password")){
			EventBus.getDefault().post(new ErrorEvent(message));
		}else if(message.getMessage().equals("no user with that id")){
			EventBus.getDefault().post(new ErrorEvent(message));
		} else if (message.getMessage().equals("emergency prompt")) {
			EventBus.getDefault().post(new ErrorEvent(message));
		} else if (message.getMessage().equals("manger found")) {
			EventBus.getDefault().post(new MangerEvent(message));
		}

	}
	
	public static SimpleClient getClient() {
		if (client == null) {
			client = new SimpleClient("localhost", 3000);
		}
		return client;
	}

}
