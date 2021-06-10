package controller;

import domain.User;
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
import service.*;
import util.Observable;
import util.Observer;
import util.ObserverManager;

import java.io.IOException;
import java.util.Optional;

public class FriendElement extends AnchorPane implements Observable {
    private final ObserverManager manager = new ObserverManager();

    @FXML
    private Label userName;
    @FXML
    private CheckBox check;

    private User user;
    private User friendUser;
    private IFriendshipService service;
    private IMessageService messageService;
    private IUserService userService;
    private Boolean openedMessageWindow;
    private Stage messageWindow;

    public FriendElement() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/customElements/friend/friendElement.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void setup(User currentUser, User friendUser, IFriendshipService friendshipService, IMessageService messageService, IUserService userService) {
        this.user = currentUser;
        this.friendUser = friendUser;
        this.service = friendshipService;
        this.messageService = messageService;
        this.userService = userService;
        openedMessageWindow = false;
        userName.setText(friendUser.getFirstName() + " " + friendUser.getLastName());
    }

    @FXML
    protected void delete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Delete");
        alert.setContentText("Do you want to delete friend?");

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent()) {
            if (result.get() == ButtonType.OK) {
                service.deleteFriendship(user.getID(), friendUser.getID());
                NotifyObservers();
            }
        }
    }

    @FXML
    protected void message() throws IOException {
        System.out.println("Check");
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

    private void closedMessageWindow(WindowEvent event) {
        openedMessageWindow = false;
        messageWindow = null;
        System.out.println("Closed!");
    }

    public Long getFriendID() {
        return friendUser.getID();
    }

    public void setCheckVisible(boolean b) {
        check.setVisible(b);
    }

    public boolean getCheck() {
        return check.isSelected();
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
