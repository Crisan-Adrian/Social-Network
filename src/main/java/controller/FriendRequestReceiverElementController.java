package controller;

import javafx.scene.control.Label;
import domain.FriendRequestStatus;
import domain.User;
import service.FriendshipService;
import util.Observable;

public class FriendRequestReceiverElementController implements Observable {


    //TODO: Comment code where necessary. Document functions. Refactor if needed

    public Label from;
    User user;
    User fromUser;
    FriendshipService service;


    public void setup(User currentUser, User from, FriendshipService friendshipService) {
        this.user = currentUser;
        this.fromUser = from;
        this.service = friendshipService;

        loadElement();
    }

    private void loadElement() {
        from.setText(fromUser.getFirstName() + " " + fromUser.getLastName() + " \n(" + fromUser.getEmail() + ")");
    }

    public void reject() {
        service.answerFriendshipRequest(user.getID(), fromUser.getID(), FriendRequestStatus.REJECTED);
        NotifyObservers();
    }

    public void accept() {
        service.answerFriendshipRequest(user.getID(), fromUser.getID(), FriendRequestStatus.ACCEPTED);
        NotifyObservers();
    }
}
