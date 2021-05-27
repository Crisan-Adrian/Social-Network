package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import domain.Tuple;
import domain.User;
import repository.event.IEventRepository;
import service.FriendshipService;
import service.MessageService;
import service.UserService;
import util.Hasher;

import java.io.IOException;

public class MainPageController {

    //TODO: Comment code where necessary. Document functions. Refactor if needed


    public PasswordField signupPassword;
    public TextField signupFirstName;
    public TextField signupLastName;
    public TextField signupEmail;
    public SplitPane split;
    @FXML
    Button loginButton;

    @FXML
    TextField userEmail;

    @FXML
    PasswordField password;

    private UserService userService;
    private FriendshipService friendshipService;
    private MessageService messageService;


    @FXML
    public void initialize() {
        SplitPane.Divider divider = split.getDividers().get(0);
        divider.positionProperty().addListener((observable, oldvalue, newvalue) -> divider.setPosition(0.5));
    }

    public void setup(UserService userService, FriendshipService friendshipService, MessageService messageService, IEventRepository eventRepo) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
//        this.eventRepo = eventRepo;
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
                if (Hasher.encodePassword(password.getText(), loginUser.getSalt()).equals(loginUser.getPassword())) {
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
//            user.setID(userService.getNextID());
            if(userService.AddUser(user) == null)
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
            loader.setLocation(getClass().getResource("/view/userPageView2.fxml"));
            AnchorPane root = loader.load();

            UserPageController userPageController = loader.getController();
            userPageController.setUser(user);
            userPageController.setServices(userService, friendshipService, messageService);
            userPageController.initialLoad();


            Scene scene = new Scene(root, 800, 700);
            stage.setScene(scene);

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setContentText(e.getLocalizedMessage());

            alert.showAndWait();
        }
    }

    public void focusPassword() {
        password.requestFocus();
    }
}
