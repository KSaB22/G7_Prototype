package il.cshaifasweng.OCSFMediatorExample.server;


import javax.persistence.*;

@Entity
@Table (name = "users")
public class User {

    @Id
    private String id;
    private String username;
    @Column (name = "passwrd")
    private String password;
    private String salt;
    private String community;
    private boolean isManger;

    public User(){}

    public User(String id, String username, String password, String salt, String community, boolean isManger) {
        super();
        this.id = id;
        this.username = username;
        this.password = password;
        this.salt = salt;
        this.community = community;
        this.isManger = isManger;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public boolean isManger() {
        return isManger;
    }

    public void setManger(boolean manger) {
        isManger = manger;
    }

    @Override
    public String toString() {
        return "Username: " + username +
                "\nID: " + id +
                "\nCommunity: " + getCommunity();
    }
}
