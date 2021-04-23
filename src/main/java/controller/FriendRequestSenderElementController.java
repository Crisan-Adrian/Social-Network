package controller;

import javafx.scene.control.Label;
import domain.User;
import service.FriendshipService;
import util.Observable;

public class FriendRequestSenderElementController extends Observable {

    //TODO: Comment code where necessary. Document functions. Refactor if needed

    public Label to;
    User user;
    User toUser;
    FriendshipService service;

    public void setup(User currentUser, User to, FriendshipService friendshipService) {
        this.user = currentUser;
        this.toUser = to;
        this.service = friendshipService;

        loadElement();
    }

    private void loadElement() {
        to.setText(toUser.getFirstName() + " " + toUser.getLastName());
    }

    public void cancel() {
        service.cancelFriendshipRequest(user.getID(), toUser.getID());
        notifyObservers();
    }
}
