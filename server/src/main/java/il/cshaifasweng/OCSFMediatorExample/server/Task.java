package il.cshaifasweng.OCSFMediatorExample.server;

import javax.persistence.*;
import java.security.PublicKey;
import java.time.LocalDateTime;

@Entity
@Table( name = "tasks")
public class Task {
    @Id
    @GeneratedValue (strategy =  GenerationType.IDENTITY)
    private int num;
    private int state;
    private String info;
    private LocalDateTime created;
    @ManyToOne
    private User creator;
    @ManyToOne
    private User volunteer;

    public Task(){}

    public Task(int state, String info, LocalDateTime created, User creator, User volunteer) {
        super();
        this.state = state;
        this.info = info;
        this.created = created;
        this.creator = creator;
        this.volunteer = volunteer;
    }

    public int getNum() {
        return num;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public User getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(User volunteer) {
        this.volunteer = volunteer;
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
