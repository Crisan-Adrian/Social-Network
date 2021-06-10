package controller;

import domain.Tuple;
import domain.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import service.IEventService;
import service.IFriendshipService;
import service.IMessageService;
import service.IUserService;
import util.Hasher;

import java.io.IOException;
import java.util.Objects;

public class MainPageController {

    @FXML
    private PasswordField signupPassword;
    @FXML
    private TextField signupFirstName;
    @FXML
    private TextField signupLastName;
    @FXML
    private TextField signupEmail;
    @FXML
    private SplitPane split;
    @FXML
    private Button loginButton;
    @FXML
    private TextField userEmail;
    @FXML
    private PasswordField password;

    private IUserService userService;
    private IFriendshipService friendshipService;
    private IMessageService messageService;
    private IEventService eventService;


    @FXML
    public void initialize() {
        SplitPane.Divider divider = split.getDividers().get(0);
        divider.positionProperty().addListener((observable, oldvalue, newvalue) -> divider.setPosition(0.5));
    }

    public void setup(IUserService userService, IFriendshipService friendshipService, IMessageService messageService, IEventService eventService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
        this.eventService = eventService;
    }


    public void tryLogin() {
        if (userEmail.getText().equals("") ||
                password.getText().equals("")) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setContentText("Please input user email and password!");

            alert.showAndWait();
        } else {
            User loginUser = userService.FindUserByEmail(userEmail.getText());
            if (loginUser != null) {
                String encodedPassword = Hasher.encodePassword(password.getText(), loginUser.getSalt());
                if(encodedPassword == null)
                {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Error");
                    alert.setContentText("Error retrieving login data");

                    alert.showAndWait();
                }
                else if (encodedPassword.equals(loginUser.getPassword())) {
                    enterAccount(loginUser);
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Error");
                    alert.setContentText("Wrong user email or password!");

                    alert.showAndWait();
                }
            }
        }
    }

    public void trySignUp() {
        if (signupEmail.getText().equals("") || signupFirstName.getText().equals("") || signupLastName.getText().equals("") || signupPassword.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setContentText("Please fill all text fields!");

            alert.showAndWait();
        } else {
            Tuple<String, String> hashed = Hasher.encodePassword(signupPassword.getText());
            User user = new User(signupFirstName.getText(), signupLastName.getText(), signupEmail.getText(), hashed.getRight(), hashed.getLeft());
            if(userService.AddUser(user).getID() != null)
            {
                enterAccount(user);
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setContentText("User email already registered!");

                alert.showAndWait();
            }
        }
    }

    private void enterAccount(User user) {
        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/userPageView.fxml"));
            AnchorPane root = loader.load();

            UserPageController userPageController = loader.getController();
            userPageController.setUser(user);
            userPageController.setServices(userService, friendshipService, messageService, eventService);
            userPageController.initialLoad();


            Scene scene = new Scene(root, 800, 700);
            scene.getStylesheets().add("/stylesheets/userPage.css");
            stage.setScene(scene);

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setContentText(e.getLocalizedMessage());
            System.out.println(e.getLocalizedMessage());

            alert.showAndWait();
        }
    }

    public void focusPassword() {
        password.requestFocus();
    }
}
