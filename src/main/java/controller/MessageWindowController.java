package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import domain.Message;
import domain.User;
import service.MessageService;
import service.UserService;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class MessageWindowController {

    //TODO: Comment code where necessary. Document functions. Refactor if needed


    @FXML
    public ScrollPane scroll;
    @FXML
    public VBox messageBox;
    @FXML
    public TextArea messageText;
    @FXML
    public Label user;

    MessageService messageService;
    UserService userService;
    private User friendUser;
    private User currentUser;
    List<Long> members;

    public void setup(MessageService messageService, UserService userService, User currentUser, User friendUser) {
        this.messageService = messageService;
        this.userService = userService;
        this.currentUser = currentUser;
        this.friendUser = friendUser;
        this.members = new LinkedList<>();

        members.add(friendUser.getID());
        members.add(currentUser.getID());
        System.out.println("YO!");
        scroll.vvalueProperty().bind(messageBox.heightProperty());
        user.setText(friendUser.getFirstName() + " " + friendUser.getLastName());
        loadMessages();
    }

    private void loadMessages() {
        messageBox.getChildren().clear();
        for (Message m : messageService.getUsersConversationsM(members)) {
            addMessage(m);
        }
    }

    public void sendMessageE(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            sendMessage();
        }
    }

    public void sendMessage() {
        if (!messageText.getText().equals("")) {
            List<Long> members = new LinkedList<>();
            members.add(friendUser.getID());
            Message message = messageService.sendMessage(currentUser.getID(), members, messageText.getText(), LocalDateTime.now());
            messageText.clear();
            addMessage(message);
        }
    }

    private void addMessage(Message message) {
        User sender = userService.GetOne(message.getFrom());
        messageBox.getChildren().add(new Label(sender.getFirstName() + " " + sender.getLastName() + " - " + message.getMessage()));
    }
}
