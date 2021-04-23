package domain;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class UserEvent extends Entity<Long> {
    private Long creator;
    private LocalDate eventDate;
    private Map<Long, Boolean> attending;
    private String name;

    public UserEvent(Long creator, LocalDate eventDate, String name) {
        this.creator = creator;
        this.eventDate = eventDate;
        this.name = name;
        attending = new HashMap<>();
        attending.put(creator, true);
    }

    public void setAttending(Map<Long, Boolean> attending) {
        this.attending = attending;
    }

    public Long getCreator() {
        return creator;
    }

    public String getName() {
        return name;
    }

    public Map<Long, Boolean> getAttending() {
        return attending;
    }

    public void addAttending(Long user) {
        attending.put(user, true);
    }

    public void removeAttending(Long user)
    {
        attending.remove(user);
    }

    public void subscribe(Long user){
        attending.put(user, true);
    }
    public void unsubscribe(Long user){
        attending.put(user, false);
    }

    public LocalDate getEventDate() {
        return eventDate;
    }
}
