import controller.MainPageController;
import domain.validators.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import repository.event.EventDB;
import repository.event.IEventRepository;
import repository.friendship.FriendshipDB;
import repository.friendship.FriendshipRequestDB;
import repository.friendship.IFriendRequestRepository;
import repository.friendship.IFriendshipRepository;
import repository.message.IMessageRepository;
import repository.message.MessageDB;
import repository.user.IUserRepository;
import repository.user.UserDB;
import service.*;

import java.io.IOException;
import java.util.Properties;

public class MainFX extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        Properties properties = new Properties();
        try {
            properties.load(MainFX.class.getResourceAsStream("/server.properties"));
        } catch (IOException e) {
            System.err.println("Cannot find properties " + e);
            return;
        }

        IUserRepository userRepository = new UserDB(new UserValidator(), properties, 10);
        IFriendshipRepository friendshipRepository = new FriendshipDB(new FriendshipValidator(), properties);
        IFriendRequestRepository friendRequestRepository = new FriendshipRequestDB(new FriendRequestValidator(), properties);
        IMessageRepository messageRepository = new MessageDB(new MessageValidator(), properties);
        IEventRepository eventRepository = new EventDB(new EventValidator(), properties, 10);

        IFriendshipService friendshipService = new FriendshipService(friendshipRepository, friendRequestRepository);
        IUserService userService = new UserService(userRepository);
        IMessageService messageService = new MessageService(messageRepository);
        IEventService eventService = new EventService(eventRepository);

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/landingPageView.fxml"));
        AnchorPane root = loader.load();


        MainPageController mainPageController = loader.getController();
        mainPageController.setup(userService, friendshipService, messageService, eventService);

        Scene scene = new Scene(root, 800, 700);

        primaryStage.initStyle(StageStyle.DECORATED);

        primaryStage.setScene(scene);
        primaryStage.setMinHeight(700);
        primaryStage.setMinWidth(800);
        primaryStage.setTitle("MAPBook");
        primaryStage.show();
        root.requestFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }

}