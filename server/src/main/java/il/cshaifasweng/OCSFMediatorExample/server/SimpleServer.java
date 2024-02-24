package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SimpleServer extends AbstractServer {

	private static Session session;

	public SimpleServer(int port) {
		super(port);
		try {
			SessionFactory sessionFactory = getSessionFactory();
			session = sessionFactory.openSession();
			session.beginTransaction();
			generateAll();

		} catch (Exception exception){
			if (session != null) {
				session.getTransaction().rollback();
			}
			System.err.println("An error occured, changes have been rolled back.");
			exception.printStackTrace();
		} finally {
			session.close();
		}
	}

	private static SessionFactory getSessionFactory() throws HibernateException {
		Configuration configuration = new Configuration();

		// Add ALL of your entities here. You can also try adding a whole package.
		configuration.addAnnotatedClass(Task.class);
		configuration.addAnnotatedClass(User.class);

		ServiceRegistry serviceRegistry = new
				StandardServiceRegistryBuilder()
				.applySettings(configuration.getProperties())
				.build();

		return configuration.buildSessionFactory(serviceRegistry);
	}

	protected static void generateAll(){
		User[] users = new User[]{
				new User("123456789", "Simon Crespo", "12345",PasswordHashing.generateSalt(), "Haifa", false),
				new User("987654321", "Haxamanish Waller", "1234", PasswordHashing.generateSalt(), "Haifa", true),
				new User("465214987", "Petronius Shain", "123",PasswordHashing.generateSalt(), "Haifa", false),
				new User("548246872", "Shmulik Cohen", "1234",PasswordHashing.generateSalt(), "Tel-aviv", true),
				new User("985642133", "Roy Nissan", "1234",PasswordHashing.generateSalt(), "Tel-aviv", false),
				new User("789525642", "Bar Goldberg", "1234",PasswordHashing.generateSalt(), "Tel-aviv", false)};
		Task[] tasks = new Task[]{
				new Task(0, "fix my washer", users[0]),
				new Task(0, "paint my house", users[2]),
				new Task(0, "buy me some candy", users[2]),
				new Task(0, "dogsit my dog", users[4]),
				new Task(0, "come talk to me", users[5])
		};
		for(User u : users){
			session.save(u);
			session.flush();
		}
		for (Task t: tasks){
			session.save(t);
			session.flush();
		}
		session.getTransaction().commit();
	}
	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		Message message = (Message) msg;
		String request = message.getMessage();
		try {
			//we got an empty message, so we will send back an error message with the error details.
			if (request.isBlank()){
				message.setMessage("Error! we got an empty message");
				client.sendToClient(message);
			}
			else if(request.startsWith("change submitters IDs:")){
				message.setData(request.substring(23));
				message.setMessage("update submitters IDs");
				sendToAllClients(message);
			}
			else{
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void sendToAllClients(Message message) {
		try {
			for (SubscribedClient SubscribedClient : SubscribersList) {
				SubscribedClient.getClient().sendToClient(message);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
