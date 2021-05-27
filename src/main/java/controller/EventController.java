package controller;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import domain.User;
import domain.UserEvent;
import repository.event.IEventRepository;
import util.Observable;

public class EventController implements Observable {

    //TODO: Comment code where necessary. Document functions. Refactor if needed

    public Button sub;
    public Button unsub;
    public Label eventName;
    public Button button;
    UserEvent event;
    User currentUser;
    boolean state;

    public void setUp(UserEvent e, User u, IEventRepository repo) {
        event = e;
        currentUser = u;
//        this.repo = repo;
        if (e.getAttending().get(u.getID()) == null) {
            state = false;
        } else {
            state = e.getAttending().get(u.getID());
        }

        eventName.setText(e.getName() + " on " + e.getEventDate());
        if (state) {
            button.setText("\uD83D\uDD14");
        } else {
            button.setText("\uD83D\uDD15");
        }

        if (e.getAttending().get(u.getID()) == null) {
            unsub.setVisible(false);
            sub.setVisible(true);
            button.setVisible(false);
        } else {
            unsub.setVisible(true);
            sub.setVisible(false);
            button.setVisible(true);
        }
    }

    public void subscribe() {
        event.addAttending(currentUser.getID());
        unsub.setVisible(true);
        sub.setVisible(false);
        state = true;
        button.setVisible(true);
//        repo.update(event);
    }

    public void unsubscribe() {
        event.removeAttending(currentUser.getID());
        unsub.setVisible(false);
        sub.setVisible(true);
        button.setVisible(false);
//        repo.update(event);
    }

    public void changeState() {
        state = !state;
        if (state) {
            // &#128276;
            button.setText("\uD83D\uDD14");
            event.subscribe(currentUser.getID());
        } else {
            // &#128277;
            button.setText("\uD83D\uDD15");
            event.unsubscribe(currentUser.getID());
        }
//        repo.update(event);
    }
}
