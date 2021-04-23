package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import domain.User;
import service.FriendshipService;
import service.MessageService;
import service.UserService;
import util.Observable;

import java.io.IOException;
import java.util.Optional;

public class FriendElementController extends Observable {

    //TODO: Comment code where necessary. Document functions. Refactor if needed

    User user;
    User friendUser;
    FriendshipService service;
    MessageService messageService;
    UserService userService;
    Boolean openedMessageWindow;
    Stage messageWindow;

    @FXML
    Label from;
    @FXML
    private CheckBox check;

    public void setup(User currentUser, User friendUser, FriendshipService friendshipService, MessageService messageService, UserService userService) {
        this.user = currentUser;
        this.friendUser = friendUser;
        this.service = friendshipService;
        this.messageService = messageService;
        this.userService = userService;
        openedMessageWindow = false;

        loadElement();
    }

    private void loadElement() {
        from.setText(friendUser.getFirstName() + " " + friendUser.getLastName());
    }

    public void delete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Delete");
        alert.setContentText("Do you want to delete friend?");

        Optional<ButtonType> result = alert.showAndWait();
        if (((Optional<?>) result).get() == ButtonType.OK) {
            service.deleteFriendship(user.getID(), friendUser.getID());
            notifyObservers();
        }
    }

    public void message() throws IOException {
        if (!openedMessageWindow) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/messageWindowView.fxml"));
            AnchorPane root = loader.load();


            MessageWindowController controller = loader.getController();
            controller.setup(messageService, userService, user, friendUser);

            // New window (Stage)
            Stage messageWindow = new Stage();
            messageWindow.setScene(new Scene(root, 300, 500));
            messageWindow.setMinWidth(300);
            //messageWindow.setMaxWidth(300);
            messageWindow.setMinHeight(300);
            messageWindow.setTitle(friendUser.getFirstName() + " " + friendUser.getLastName());

            // Set position of second window, related to primary window.
            messageWindow.setX(300);
            messageWindow.setY(300);

            messageWindow.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closedMessageWindow);

            openedMessageWindow = true;
            this.messageWindow = messageWindow;
            messageWindow.show();
        } else {
            messageWindow.requestFocus();
        }
    }

    public void closedMessageWindow(WindowEvent event) {
        openedMessageWindow = false;
        messageWindow = null;
        System.out.println("Closed!");
    }

    public void setCheckVisible(boolean b)
    {
        check.setVisible(b);
    }

    public boolean getCheck()
    {
        return check.isSelected();
    }
}
