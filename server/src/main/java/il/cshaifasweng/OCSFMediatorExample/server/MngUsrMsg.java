package il.cshaifasweng.OCSFMediatorExample.server;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "mng_usr_msgs")
public class MngUsrMsg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    // private int state;// state -1 = new;  0 = approved; 1 = volunteered; 2 = done
    private String title;
    private String description;
    private LocalDateTime created;
    @ManyToOne
    @JoinColumn(name = "from_id", referencedColumnName = "id") // This specifies the foreign key
    private User from;
    @ManyToOne
    @JoinColumn(name = "to_id", referencedColumnName = "id") // This specifies the foreign key
    private User to;


    public MngUsrMsg(String title, String description, User from, User to) {
        super();
        this.title = title;
        this.description = description;
        this.from = from;
        this.to = to;
        this.created = LocalDateTime.now();
    }

    public MngUsrMsg(String title, String description, User from, User to, LocalDateTime created) {
        super();
        this.title = title;
        this.description = description;
        this.from = from;
        this.to = to;
        this.created = created;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public User getFrom() {
        return from;
    }

    public User getTo() {
        return to;
    }
}
