package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import domain.User;
import domain.UserEvent;
import javafx.scene.layout.AnchorPane;
import service.IEventService;
import util.Observable;
import util.Observer;
import util.ObserverManager;

import java.io.IOException;

public class EventElement extends AnchorPane implements Observable {

    @FXML
    private Button sub;
    @FXML
    private Button unsub;
    @FXML
    private Label eventName;
    @FXML
    private Button button;

    private IEventService service;
    private UserEvent event;
    private User currentUser;
    boolean state;

    private final ObserverManager manager = new ObserverManager();

    public EventElement() {
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

    public void setup(UserEvent e, User u, IEventService service) {
        event = e;
        currentUser = u;
        this.service = service;
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
        event = service.changeAttendance(currentUser, event, true);
    }

    @FXML
    protected void unsubscribe() {
        unsub.setVisible(false);
        sub.setVisible(true);
        button.setVisible(false);
        event = service.changeAttendance(currentUser, event, false);
    }

    @FXML
    protected void changeState() {
        state = !state;
        if (state) {
            // &#128276;
            button.setText("\uD83D\uDD14");
        } else {
            // &#128277;
            button.setText("\uD83D\uDD15");
        }
        event = service.changeNotificationState(currentUser, event, state);
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
