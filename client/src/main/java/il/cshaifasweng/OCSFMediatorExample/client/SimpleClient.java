package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import org.greenrobot.eventbus.EventBus;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class SimpleClient extends AbstractClient {
	
	private static SimpleClient client = null;
	private static String host = "";

	private SimpleClient(String host, int port) {
		super(host, port);
	}

	@Override
	protected void handleMessageFromServer(Object msg) {
		Message message = (Message) msg;
		System.out.println("Recieved from server: " + message.getMessage());
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
		} else if (message.getMessage().equals("manager found")) {
			EventBus.getDefault().post(new MangerEvent(message));
		} else if(message.getMessage().startsWith("check for update")){
			EventBus.getDefault().post(new CheckingEvent(message));
		} else if (message.getMessage().startsWith("task not found")) {
			EventBus.getDefault().post(new ErrorEvent(message));
		} else if (message.getMessage().startsWith("request rejected")) {
			EventBus.getDefault().post((new ErrorEvent(message)));
		} else if (message.getMessage().equals("list of messages")) {
			EventBus.getDefault().post(new MessagesListEvent(message));
		} else if (message.getMessage().equals("creator cannot be volunteer")) {
			EventBus.getDefault().post(new ErrorEvent(message));
		}

	}
	
	public static SimpleClient getClient() {
		if (client == null) {
			if(host == "") {
				client = new SimpleClient("localhost" , 3000);
			} else {
				client = new SimpleClient(host , 3000);
			}
		}
		return client;
	}

	public static void setSimpleClientHost(String host) {
		SimpleClient.host = host;
	}

}
