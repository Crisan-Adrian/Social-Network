package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import domain.User;
import javafx.scene.layout.AnchorPane;
import service.FriendshipService;
import util.Observable;
import util.Observer;
import util.ObserverManager;

import java.io.IOException;

public class FriendRequestSent extends AnchorPane implements Observable {

    //TODO: Comment code where necessary. Document functions. Refactor if needed

    public Label to;
    User user;
    User toUser;
    FriendshipService service;

    private final ObserverManager manager = new ObserverManager();

    public FriendRequestSent() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/customElements/friendRequestSent/friendRequestSentElement.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void setup(User currentUser, User to, FriendshipService friendshipService) {
        this.user = currentUser;
        this.toUser = to;
        this.service = friendshipService;

        loadElement();
    }

    private void loadElement() {
        to.setText(toUser.getFirstName() + " " + toUser.getLastName());
    }

    @FXML
    protected void cancel() {
        service.cancelFriendshipRequest(user.getID(), toUser.getID());
        NotifyObservers();
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
