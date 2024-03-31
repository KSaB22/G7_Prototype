package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SimpleServer extends AbstractServer {

    private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();
    private static ArrayList<SubscribedClient> activeVolenteers = new ArrayList<>();
    private static ArrayList<Task> awaitingEnd = new ArrayList<>();

    private static Session session;

    public SimpleServer(int port) {
        super(port);

        try {
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
            session.beginTransaction();
            //generateAll();

        } catch (Exception exception) {
            if (session != null) {
                session.getTransaction().rollback();
            }
            System.err.println("An error occured, changes have been rolled back.");
            exception.printStackTrace();
        } finally {
            session.close();
        }
        checkOnVolunteers();
    }

    public void checkOnVolunteers() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        Runnable toRun = new Runnable() {
            public void run() {
                LocalDateTime currentTime = LocalDateTime.now();
                System.out.println("checked up on Volunteers");
                Message message = new Message(0, "check for update");
                for (int i = 0; i < awaitingEnd.size(); i++) {
                    if (currentTime.isAfter(awaitingEnd.get(i).getCreated())) {
                        try {
                            message.setMessage("check for updates " + i);
                            message.setData(awaitingEnd.toString());
                            activeVolenteers.get(i).getClient().sendToClient(message);

                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            }
        };
        ScheduledFuture<?> handle = scheduler.scheduleAtFixedRate(toRun, 1, 24, TimeUnit.HOURS);
    }

    private static SessionFactory getSessionFactory() throws HibernateException {
        String currentDir = System.getProperty("user.dir");
        System.out.println("Current Working Directory: " + currentDir);
        Configuration configuration = new Configuration();
        File propertiesFile = new File("./hibernate.properties");
        if (propertiesFile.exists()) {
            System.out.println("New config file found in working directory.");
            configuration.setProperties(PropertiesLoader.loadFile(propertiesFile));
        }

        // Add ALL of your entities here. You can also try adding a whole package.
        configuration.addAnnotatedClass(Task.class);
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(EmergencyCall.class);

        ServiceRegistry serviceRegistry = new
                StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();

        return configuration.buildSessionFactory(serviceRegistry);
    }

    protected static void generateAll() {
        String[] temp = {
                PasswordHashing.generateSalt(),
                PasswordHashing.generateSalt(),
                PasswordHashing.generateSalt(),
                PasswordHashing.generateSalt(),
                PasswordHashing.generateSalt(),
                PasswordHashing.generateSalt()
        };
        User[] users = new User[]{
                new User("123456789", "Simon Crespo", PasswordHashing.hashPassword("12345", temp[0]), temp[0], "Haifa", false),
                new User("987654321", "Haxamanish Waller", PasswordHashing.hashPassword("1234", temp[1]), temp[1], "Haifa", true),
                new User("465214987", "Petronius Shain", PasswordHashing.hashPassword("123", temp[2]), temp[2], "Haifa", false),
                new User("548246872", "Shmulik Cohen", PasswordHashing.hashPassword("1234", temp[3]), temp[3], "Tel-aviv", true),
                new User("985642133", "Roy Nissan", PasswordHashing.hashPassword("1234", temp[4]), temp[4], "Tel-aviv", false),
                new User("789525642", "Bar Goldberg", PasswordHashing.hashPassword("1234", temp[5]), temp[5], "Tel-aviv", false)};
        Task[] tasks = new Task[]{
                new Task(0, "fix my washer", LocalDateTime.of(2023, 12, 5, 8, 0), users[0], null),
                new Task(0, "paint my house", LocalDateTime.of(2024, 2, 5, 8, 0), users[2], null),
                new Task(0, "buy me some candy", LocalDateTime.of(2024, 1, 2, 9, 0), users[2], null),
                new Task(0, "dogsit my dog", LocalDateTime.of(2023, 5, 2, 10, 0), users[4], null),
                new Task(0, "come talk to me", LocalDateTime.of(2023, 11, 2, 9, 0), users[5], null)
        };
        EmergencyCall[] emergencyCalls = new EmergencyCall[]{
                new EmergencyCall(users[1]),
                new EmergencyCall(users[4])
        };
        for (User u : users) {
            session.save(u);
            session.flush();
        }
        for (Task t : tasks) {
            session.save(t);
            session.flush();
        }
        for (EmergencyCall ec : emergencyCalls) {
            session.save(ec);
            session.flush();
        }
        session.getTransaction().commit();
    }

    protected static List<Task> getTasks() {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Task> query = builder.createQuery(Task.class);
        query.from(Task.class);
        List<Task> data = session.createQuery(query).getResultList();
        return data;
    }

    protected static List<User> getUsers() {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        query.from(User.class);
        List<User> data = session.createQuery(query).getResultList();
        return data;
    }

    protected static List<EmergencyCall> getEmergencyCalls() {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<EmergencyCall> query = builder.createQuery(EmergencyCall.class);
        query.from(EmergencyCall.class);
        List<EmergencyCall> data = session.createQuery(query).getResultList();
        return data;
    }

    protected static ArrayList<Task> getUnfinishedTasks(List<Task> tasks) {
        ArrayList<Task> temp = new ArrayList<>();
        for (Task t : tasks) {
            if (t.getState() != 2 && t.getState() != -1) {
                temp.add(t);
            }
        }
        return temp;
    }

    protected static ArrayList<Task> getRequests(List<Task> tasks) {
        ArrayList<Task> temp = new ArrayList<>();
        for (Task t : tasks) {
            if (t.getState() == 0) {
                temp.add(t);
            }
        }
        return temp;
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
            List<User> users = getUsers();
            List<EmergencyCall> emergencyCalls = getEmergencyCalls();
            ArrayList<Task> unfinishedTasks = getUnfinishedTasks(tasks);
            if (request.startsWith("pull tasks")) {

                message.setData(stringForList(unfinishedTasks));
                message.setMessage("list of tasks");
                client.sendToClient(message);
            } else if (request.startsWith("give task ")) {
                int index = Integer.parseInt(request.split(" ")[2]);
                if (request.endsWith("all"))
                    message.setData(tasks.get(index).toString());
                else
                    message.setData(unfinishedTasks.get(index).toString());
                message.setMessage("specific task");
                client.sendToClient(message);
            } else if (request.startsWith("volunteer in")) {
                int index = Integer.parseInt(request.split(" ")[2]);
                String userid = request.split(" ")[3];
                if (unfinishedTasks.get(index).getVolunteer() == null) {

                    for (int i = 0; i < users.size(); i++) {
                        if (userid.equals(users.get(i).getId())) {
                            String addvol = "UPDATE Task t SET t.volunteer = :newvol , t.state = 1 WHERE t.num = :whattask";
                            session.createQuery(addvol)
                                    .setString("newvol", users.get(i).getId())
                                    .setInteger("whattask", unfinishedTasks.get(index).getNum())
                                    .executeUpdate();

                            session.flush();

                            unfinishedTasks.get(index).setState(1);
                            unfinishedTasks.get(index).setVolunteer(users.get(i));// i cant update in time so this will do

                            SubscribedClient connection = new SubscribedClient(client);
                            activeVolenteers.add(connection);

                            awaitingEnd.add(unfinishedTasks.get(index));

                            message.setData(stringForList(unfinishedTasks));
                            message.setMessage("list of tasks");
                            sendToAllClients(message);
                            message.setData(unfinishedTasks.get(index).toString());
                            message.setMessage("specific task");
                            client.sendToClient(message);
                        }
                    }
                } else {
                    message.setMessage("already vol");
                    client.sendToClient(message);
                }
            } else if (request.startsWith("finish")) {
                int index = Integer.parseInt(request.split(" ")[1]);
                String userid = request.split(" ")[2];
                if (request.endsWith(userid)) {
                    if (unfinishedTasks.get(index).getVolunteer().getId().equals(userid)) {
                        String addvol = "UPDATE Task t SET t.state = 2 WHERE t.num = :whattask";
                        session.createQuery(addvol)
                                .setInteger("whattask", unfinishedTasks.get(index).getNum())
                                .executeUpdate();

                        session.flush();

                        unfinishedTasks.get(index).setState(2);//doesnt update in time

                        message.setData(unfinishedTasks.get(index).toString());
                        message.setMessage("specific task");
                        client.sendToClient(message);


                        for (int i = 0; i < awaitingEnd.size(); i++) {
                            if (awaitingEnd.get(i).getNum() == unfinishedTasks.get(index).getNum()) {
                                activeVolenteers.remove(i);
                                awaitingEnd.remove(i);
                                break;
                            }
                        }

                        unfinishedTasks.remove(index);

                        message.setData(stringForList(unfinishedTasks));
                        message.setMessage("list of tasks");
                        sendToAllClients(message);

                    } else {
                        message.setMessage("not volunteer");
                        client.sendToClient(message);
                    }
                } else {
                    String addvol = "UPDATE Task t SET t.state = 2 WHERE t.num = :whattask";
                    session.createQuery(addvol)
                            .setInteger("whattask", awaitingEnd.get(index).getNum())
                            .executeUpdate();

                    session.flush();


                    int i;
                    for (i = 0; i < awaitingEnd.size(); i++) {
                        if(awaitingEnd.get(index).getNum() == unfinishedTasks.get(i).getNum()) {
                            unfinishedTasks.remove(i);
                        }
                    }

                    activeVolenteers.remove(index);
                    awaitingEnd.remove(index);

                    message.setData(stringForList(unfinishedTasks));
                    message.setMessage("list of tasks");
                    sendToAllClients(message);
                }
            } else if (request.equals("add client")) {
                SubscribedClient connection = new SubscribedClient(client);
                SubscribersList.add(connection);
                message.setMessage("client added successfully");
                client.sendToClient(message);
            } else if (request.startsWith("login ")) {
                String info = request.substring(6);
                String userid = info.split(" ")[0];
                String password = info.split(" ")[1];
                for (int i = 0; i < users.size(); i++) {
                    if (userid.equals(users.get(i).getId())) {
                        if (PasswordHashing.hashPassword(password, users.get(i).getSalt()).equals(users.get(i).getPassword())) {
                            if (users.get(i).isManger()) {
                                message.setMessage("manager found");
                                message.setData(userid);
                                client.sendToClient(message);
                            } else {
                                message.setMessage("account found");
                                client.sendToClient(message);
                            }
                        } else {
                            message.setMessage("wrong password");
                            client.sendToClient(message);
                        }
                        return;
                    }
                }
                message.setMessage("no user with that id");
                client.sendToClient(message);
            } else if (request.startsWith("emergency")) {
                EmergencyCall temp = new EmergencyCall(users.get(users.size() - 1));
                if (request.length() > "emergency".length()) {
                    String userid = request.split(" ")[1];
                    for (int i = 0; i < users.size(); i++) {
                        if (users.get(i).getId().equals(userid)) {
                            temp = new EmergencyCall(users.get(i));
                        }
                    }
                }

                message.setMessage("emergency prompt");
                message.setData(temp.toString());
                client.sendToClient(message);
                session.save(temp);
                session.flush();
                session.getTransaction().commit();
            } else if (request.startsWith("new task ")) {
                String userid = request.split(" ")[2];
                String info = request.substring(19);
                Task temp = null;
                for (User user : users) {
                    if (user.getId().equals(userid)) {
                        temp = new Task(-1, info, LocalDateTime.now(), user, null);
                        session.save(temp);
                        session.flush();
                        break;
                    }
                }
                message.setMessage("specific task");
                message.setData(temp.toString());
                client.sendToClient(message);
            } else if (request.startsWith("pull requests")) {
                ArrayList<Task> requests = getRequests(tasks);
                message.setData(stringForList(requests));
                message.setMessage("list of tasks");
                client.sendToClient(message);

            } else if (request.startsWith("pull emergency")) {
                StringBuilder temp = new StringBuilder();
                for (EmergencyCall e : emergencyCalls) {
                    temp.append(" Emergency call : ")
                            .append(e.toString())
                            .append(".");
                }
                message.setData(temp.toString());
                message.setMessage("list of tasks");
                client.sendToClient(message);
            }


        } catch (Exception exception) {
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

    private static String stringForList(List<Task> tasks) {
        StringBuilder temp = new StringBuilder();
        for (Task t : tasks) {    //each task look like "Status: 0-2 Task: bla" the . is there to separate for the list
            if (t.getState() == 0) {
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
