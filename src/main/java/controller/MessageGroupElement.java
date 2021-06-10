package controller;

import domain.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import service.IMessageService;
import service.IUserService;
import util.Observable;
import util.Observer;
import util.ObserverManager;

import java.io.IOException;
import java.util.List;

public class MessageGroupElement extends AnchorPane implements Observable {

    @FXML
    private Text from;

    private final ObserverManager manager = new ObserverManager();
    private Boolean openedMessageWindow;
    private Stage messageWindow;

    private IMessageService messageService;
    private IUserService userService;
    private List<Long> members;
    private User currentUser;

    public MessageGroupElement() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/customElements/messageGroup/messageGroupElement.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        openedMessageWindow = false;
        from.getStyleClass().add("my-text");
    }

    public void setup (User user, List<Long> members, IUserService userService, IMessageService messageService) {
        this.currentUser = user;
        this.members = members;
        this.userService = userService;
        this.messageService = messageService;

        StringBuilder text = new StringBuilder();
        boolean first = true;
        int count = 0;
        for(Long memberID: members) {
            if(count == 5) {
                text.append(" ...");
                break;
            }
            if(!first) {
                text.append(", ");
            }
            User member = userService.GetOne(memberID);
            text.append(member.getFirstName()).append(" ").append(member.getLastName());
            first = false;
            count++;
        }

        from.setText(text.toString());
    }

    @FXML
    public void message() throws IOException {
        if (!openedMessageWindow) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/messageWindowView.fxml"));
            AnchorPane root = loader.load();


            MessageWindowController controller = loader.getController();
            controller.setup(messageService, userService, currentUser, members);

            // New window (Stage)
            Stage messageWindow = new Stage();
            messageWindow.setScene(new Scene(root, 300, 500));
            messageWindow.setMinWidth(300);
            messageWindow.setMinHeight(300);
            messageWindow.setTitle("Group Conversation");

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
