package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import domain.User;
import domain.UserEvent;
import javafx.scene.layout.AnchorPane;
import repository.event.IEventRepository;
import util.Observable;
import util.Observer;
import util.ObserverManager;

import java.io.IOException;

public class Event extends AnchorPane implements Observable {

    //TODO: Comment code where necessary. Document functions. Refactor if needed

    public Button sub;
    public Button unsub;
    public Label eventName;
    public Button button;
    UserEvent event;
    User currentUser;
    boolean state;

    private final ObserverManager manager = new ObserverManager();

    public Event() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/customElements/event/eventElement.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void setUp(UserEvent e, User u) {
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

    @FXML
    protected void subscribe() {
        event.addAttending(currentUser.getID());
        unsub.setVisible(true);
        sub.setVisible(false);
        state = true;
        button.setVisible(true);
//        repo.update(event);
    }

    @FXML
    protected void unsubscribe() {
        event.removeAttending(currentUser.getID());
        unsub.setVisible(false);
        sub.setVisible(true);
        button.setVisible(false);
//        repo.update(event);
    }

    @FXML
    protected void changeState() {
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

    public void NotifyObservers()
    {
        manager.NotifyObservers(this);
    }

    public void AddObserver(Observer o)
    {
        manager.AddObserver(o);
    }

    public void RemoveObserver(Observer o)
    {
        manager.RemoveObserver(o);
    }
}
