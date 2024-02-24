package il.cshaifasweng.OCSFMediatorExample.server;

import javax.persistence.*;
import java.security.PublicKey;

@Entity
@Table( name = "tasks")
public class Task {
    @Id
    @GeneratedValue (strategy =  GenerationType.IDENTITY)
    private int num;
    private int state;
    private String info;
    @ManyToOne
    private User creator;

    public Task(){}
    public Task(int state, String info, User creator) {
        super();
        this.state = state;
        this.info = info;
        this.creator = creator;
    }

    public int getNum() {
        return num;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }
}
