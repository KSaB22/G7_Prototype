package il.cshaifasweng.OCSFMediatorExample.server;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table( name = "Emergencys")
public class EmergencyCall {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private int num;
    @ManyToOne
    private User creator;
    private LocalDateTime time;
    public EmergencyCall() {
        super();
        this.creator = null;
        this.time = LocalDateTime.now();
    }

    public EmergencyCall(User creator) {
        super();
        this.creator = creator;
        this.time = LocalDateTime.now();
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Time of emergency: " + time.toString() +
                "\nName: " + creator.getUsername() +
                "\nID: " + creator.getId() +
                "\nCommunity: " + creator.getCommunity();
    }
}
