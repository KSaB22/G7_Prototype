package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class SimpleServer extends AbstractServer {

	private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();
	private static Session session;

	public SimpleServer(int port) {
		super(port);
		try {
			SessionFactory sessionFactory = getSessionFactory();
			session = sessionFactory.openSession();
			session.beginTransaction();
			generateAll();
		} catch (Exception exception) {
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
				new Task(0, "fix my washer", LocalDateTime.of(2023,12,5,8,0), users[0], null),
				new Task(0, "paint my house",LocalDateTime.of(2024,2,5,8,0), users[2],null),
				new Task(0, "buy me some candy", LocalDateTime.of(2024,1,2,9,0), users[2],null),
				new Task(0, "dogsit my dog", LocalDateTime.of(2023,5,2,10,0), users[4],null),
				new Task(0, "come talk to me", LocalDateTime.of(2023,11,2,9,0), users[5],null)
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

	protected static List<Task> getTasks(){
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Task> query = builder.createQuery(Task.class);

		query.from(Task.class);
		List<Task> data = session.createQuery(query).getResultList();
		return data;
	}
	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		Message message = (Message) msg;
		String request = message.getMessage();
		try {
			SessionFactory sessionFactory = getSessionFactory();
			session = sessionFactory.openSession();
			session.beginTransaction();


			List<Task> tasks = getTasks();
			if(request.startsWith("pull tasks")){

				message.setData(stringForList(tasks));
				message.setMessage("list of tasks");
				client.sendToClient(message);
			}
			else if(request.startsWith("give task ")){
				int index =Integer.parseInt(request.split(" ")[2]);
				StringBuilder taskdata  = new StringBuilder();
				if(tasks.get(index).getState() == 0){
					taskdata.append("Status: Request");
				} else if (tasks.get(index).getState() == 1) {
					taskdata.append("Status: Pre-execution");
				} else {
					taskdata.append("Status: Done");
				}
				taskdata.append("\nTask: ")
						.append(tasks.get(index).getInfo());
				taskdata.append("\nCreated by: ")
						.append(tasks.get(index).getCreator().getUsername());
				taskdata.append("\nDate created: ")
						.append(tasks.get(index).getCreated().toString());
				if(tasks.get(index).getVolunteer() != null){
					taskdata.append("\nVolunteering now: ")
							.append(tasks.get(index).getVolunteer().getUsername());
				}
				message.setData(taskdata.toString());
				message.setMessage("specific task");
				client.sendToClient(message);
			}
			else if (request.startsWith("volunteer in")){
				int index =Integer.parseInt(request.split(" ")[2]);
				if(tasks.get(index).getVolunteer() == null){
					CriteriaBuilder builder = session.getCriteriaBuilder();
					CriteriaQuery<User> query = builder.createQuery(User.class);
					query.from(User.class);
					List<User> users = session.createQuery(query).getResultList();//pulling all users

					Random random = new Random();
					int randomUser = random.nextInt(users.size());

					String addvol = "UPDATE Task t SET t.volunteer = :newvol , t.state = 1 WHERE t.num = :whattask";
					session.createQuery(addvol)
							.setString("newvol",users.get(randomUser).getId())
							.setInteger("whattask" , tasks.get(index).getNum())
							.executeUpdate();

					session.flush();

					tasks.get(index).setState(1);
					tasks.get(index).setVolunteer(users.get(random.nextInt(users.size())));// i cant update in time so this will do

					message.setData(stringForList(tasks));
					message.setMessage("list of tasks");
					sendToAllClients(message);
				} else {
					message.setMessage("already vol");
					client.sendToClient(message);
				}
			}
			else if (request.equals("add client")){
				SubscribedClient connection = new SubscribedClient(client);
				SubscribersList.add(connection);
				message.setMessage("client added successfully");
				client.sendToClient(message);
			}
		} catch (Exception exception){
			if (session != null) {
				session.getTransaction().rollback();
			}
			System.err.println("An error occured, changes have been rolled back.");
			System.err.println(exception.getMessage());
			exception.printStackTrace();
		} finally {
			session.close();
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

	private static String stringForList(List<Task> tasks){
		StringBuilder temp  = new StringBuilder();
		for(Task t: tasks){	//each task look like "Status: 0-2 Task: bla" the . is there to separate for the list
			if(t.getState() == 0){
				temp.append("Status: Request");
			} else if (t.getState() == 1) {
				temp.append("Status: Pre-execution");
			} else {
				temp.append("Status: Done");
			}

			temp.append(" Task : ")
					.append(t.getInfo())
					.append(".");
		}
		return temp.toString();
	}

}
