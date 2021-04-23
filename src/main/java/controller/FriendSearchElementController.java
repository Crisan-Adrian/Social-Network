package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import domain.User;
import service.FriendshipService;
import util.Observable;

public class FriendSearchElementController extends Observable {

    //TODO: Comment code where necessary. Document functions. Refactor if needed


    public Label searchedUser;
    public Button button;
    User user;
    User searchUser;
    FriendshipService service;

    @FXML
    public void initialize() {
    }

    public void setup(User currentUser, User friendUser, FriendshipService friendshipService) {
        this.user = currentUser;
        this.searchUser = friendUser;
        this.service = friendshipService;

        loadElement();
    }

    private void loadElement() {
        searchedUser.setText(searchUser.getFirstName() + " " + searchUser.getLastName() + " (" + searchUser.getEmail() + ")");
    }

    public void friend() {
        service.sendFriendshipRequest(user.getID(), searchUser.getID());
        notifyObservers();
    }

    public void hideButton()
    {
        button.setVisible(false);
    }
}
