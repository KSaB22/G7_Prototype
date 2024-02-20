package il.cshaifasweng.OCSFMediatorExample.server;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

public class MySQLContext {
    private static MySQLContext instance = null;
    private Connection connection = null;

    public static MySQLContext getInstance() {
        if (instance == null) {
            instance = new MySQLContext();
        }

        return instance;
    }

    private MySQLContext() {
    }

    public void connect(String host, int port, String user, String password, String schema) throws SQLException, IOException {
        this.connection = DriverManager.getConnection(String.format("jdbc:mysql://%s:%d/?allowMultiQueries=true&serverTimezone=UTC&user=%s&password=%s", host, port, user, password));
        String sanitizedSchema = schema.replaceAll("[^a-zA-Z0-9_]+", "");
        sanitizedSchema = sanitizedSchema.replaceAll("(UNION|SELECT|INSERT|DROP|UPDATE|ALTER)+", "");
        Statement initStatement = this.connection.createStatement();
        initStatement.execute(String.format("DROP DATABASE IF EXISTS %s; CREATE DATABASE %s;", sanitizedSchema, sanitizedSchema));
        this.connection.close();
        this.connection = DriverManager.getConnection(String.format("jdbc:mysql://%s:%d/%s?allowMultiQueries=true&serverTimezone=UTC&user=%s&password=%s", host, port, sanitizedSchema, user, password));
        Alert successAlert = new Alert(AlertType.CONFIRMATION, "Logged in and ready to test.", new ButtonType[]{ButtonType.OK});
        successAlert.showAndWait();
        //test3
    }

    public ResultSet executeQuery(String query) {
        try {
            Statement statement = this.connection.createStatement();
            return statement.executeQuery(query);
        } catch (SQLException var3) {
            var3.printStackTrace();
            //Errors.showError(var3.getMessage());
            return null;
        }
    }

    public Connection getConnection() {
        return this.connection;
    }

    public List<String> getTables() throws SQLException {
        ResultSet rs = this.executeQuery("show tables");
        List<String> ret = new ArrayList();

        while(rs.next()) {
            ret.add(rs.getString(1));
        }

        return ret;
    }
}

