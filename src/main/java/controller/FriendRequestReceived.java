package controller;

import domain.FriendRequestStatus;
import domain.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import service.IFriendshipService;
import util.Observable;
import util.Observer;
import util.ObserverManager;

import java.io.IOException;

public class FriendRequestReceived extends AnchorPane implements Observable {
    @FXML
    private Label from;

    private User user;
    private User fromUser;
    private IFriendshipService service;

    private final ObserverManager manager = new ObserverManager();

    public FriendRequestReceived() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/customElements/friendRequestReceived/friendRequestReceivedElement.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void setup(User currentUser, User from, IFriendshipService friendshipService) {
        this.user = currentUser;
        this.fromUser = from;
        this.service = friendshipService;

        this.from.setText(
                fromUser.getFirstName() + " " +
                fromUser.getLastName() + " \n(" +
                fromUser.getEmail() + ")");
    }

    @FXML
    protected void reject() {
        service.answerFriendshipRequest(user.getID(), fromUser.getID(), FriendRequestStatus.REJECTED);
        NotifyObservers();
    }

    @FXML
    protected void accept() {
        service.answerFriendshipRequest(user.getID(), fromUser.getID(), FriendRequestStatus.ACCEPTED);
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
