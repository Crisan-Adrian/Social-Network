package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import domain.User;
import javafx.scene.layout.AnchorPane;
import service.IFriendshipService;
import util.Observable;
import util.Observer;
import util.ObserverManager;

import java.io.IOException;

public class FriendSearchElement extends AnchorPane implements Observable {
    @FXML
    private Label searchedUser;
    @FXML
    private Button button;

    User user;
    User searchUser;
    IFriendshipService service;

    private final ObserverManager manager = new ObserverManager();

    public FriendSearchElement() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/customElements/friendSearch/friendSearchElement.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void setup(User currentUser, User friendUser, IFriendshipService friendshipService) {
        this.user = currentUser;
        this.searchUser = friendUser;
        this.service = friendshipService;
        searchedUser.setText(
                searchUser.getFirstName() + " " +
                searchUser.getLastName() + " (" +
                searchUser.getEmail() + ")");
    }

    @FXML
    protected void friend() {
        service.sendFriendshipRequest(user.getID(), searchUser.getID());
        NotifyObservers();
    }

    public void hideButton()
    {
        button.setVisible(false);
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
