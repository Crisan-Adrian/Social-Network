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
import service.IMessageService;
import service.IUserService;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class MessageWindowController {
    @FXML
    private ScrollPane scroll;
    @FXML
    private VBox messageBox;
    @FXML
    private TextArea messageText;
    @FXML
    private Label user;

    private IMessageService messageService;
    private IUserService userService;
    private User friendUser;
    private User currentUser;
    private List<Long> members;
    private boolean isGroup;

    public void setup(IMessageService messageService, IUserService userService, User currentUser, User friendUser) {
        this.messageService = messageService;
        this.userService = userService;
        this.currentUser = currentUser;
        this.friendUser = friendUser;
        this.isGroup = false;

        this.members = new LinkedList<>();
        members.add(friendUser.getID());

        scroll.vvalueProperty().bind(messageBox.heightProperty());
        loadTitle();
        loadMessages();
    }

    public void setup(IMessageService messageService, IUserService userService, User currentUser, List<Long> members) {
        this.messageService = messageService;
        this.userService = userService;
        this.currentUser = currentUser;
        this.members = members;
        this.isGroup = true;

        scroll.vvalueProperty().bind(messageBox.heightProperty());
        loadTitle();
        loadMessages();
    }

    private void loadTitle() {
        if(isGroup) {
            user.setText("Group Conversation");
        } else {
            user.setText(friendUser.getFirstName() + " " + friendUser.getLastName());
        }
    }

    private void loadMessages() {
        messageBox.getChildren().clear();
        members.add(currentUser.getID());
        for (Message m : messageService.getUsersConversationMessages(members)) {
            addMessage(m);
        }
        members.remove(currentUser.getID());
    }

    public void sendMessageE(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            sendMessage();
        }
    }

    public void sendMessage() {
        if (!messageText.getText().equals("")) {
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
