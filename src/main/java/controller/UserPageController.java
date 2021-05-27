package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;
import domain.FriendshipDTO;
import domain.User;
import domain.UserEvent;
import service.FriendshipService;
import service.MessageService;
import service.UserService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import util.Observable;
import util.Observer;

public class UserPageController extends Observer {

    //TODO: Comment code where necessary. Document functions. Refactor if needed. Remove repo calls from controller


    @FXML
    private Button activateCheck;
    @FXML
    private Button createGroup;
    @FXML
    private TitledPane friendsPane;
    @FXML
    private Accordion accordion;
    @FXML
    private VBox groups;
    @FXML
    private Button activity;
    @FXML
    private VBox searchResults;
    @FXML
    private Button prevPage;
    @FXML
    private Button nextPage;
    @FXML
    private VBox events;
    @FXML
    private TextField pageNumberSelector;
    @FXML
    private Label pageCounter;
    @FXML
    private AnchorPane root;
    @FXML
    private Button createEventButton;
    @FXML
    private VBox eventsBox;
    @FXML
    private Button prevPageE;
    @FXML
    private TextField pageNumberSelectorE;
    @FXML
    private Label pageCounterE;
    @FXML
    private Button nextPageE;
    @FXML
    private TabPane tabPane;
    @FXML
    private SplitPane split;
    @FXML
    private Accordion accordion2;


    private User currentUser;
    private UserService userService;
    private FriendshipService friendshipService;
    private MessageService messageService;
    private List<FriendRequestReceived> requestMap;
    private List<FriendRequestSent> requestSentMap;
    private List<FriendElement> friendMap;
    private Map<Observable, AnchorPane> searchMap;

    private List<Long> friendships;
    private List<Long> requestsR;
    private List<Long> requestsS;

    @FXML
    private Button logout;
    @FXML
    private VBox requestsRecv;
    @FXML
    private VBox requestsSent;
    @FXML
    private VBox friends;
    @FXML
    private TextField name;
    @FXML
    private Button searchButton;
    @FXML
    private Label userGreeting;

    @FXML
    public void initialize() {
        friendMap = new ArrayList<>();
        requestMap = new ArrayList<>();
        searchMap = new HashMap<>();
        requestSentMap = new ArrayList<>();

        // Force the field to be numeric only
        pageNumberSelector.setText("1");
        pageNumberSelector.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                pageNumberSelector.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        createEventButton.setTooltip(new Tooltip("Create a new event"));
    }

    public void setUser(User user) {
        currentUser = user;
        userGreeting.setText("Hello, " + currentUser.getFirstName() + " " + currentUser.getLastName() + "!");
    }

    public void setServices(UserService userService, FriendshipService friendshipService, MessageService messageService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
    }

    public void initialLoad() {
        loadReceivedRequests();
        loadFriends();
        loadSentRequests();
        loadEvents(eventRepo.getFirstPage());
        search();

        Platform.runLater(this::showNotifications);
    }

    public void logout() {
        Stage stage = (Stage) logout.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/landingPageView.fxml"));
        try {
            AnchorPane root = loader.load();

            MainPageController mainPageController = loader.getController();
            mainPageController.setup(userService, friendshipService, messageService);

            stage.setScene(new Scene(root, 800, 700));
        } catch (IOException ignored) {
        }
    }

    public void loadReceivedRequests() {
        requestMap.clear();
        requestsRecv.getChildren().clear();
        int i = 0;
        for (Long fromID : friendshipService.getUserFriendRequests(currentUser.getID())) {
            try {
                FriendRequestReceived friendRequest = new FriendRequestReceived();
                User from = userService.GetOne(fromID);
                friendRequest.setup(currentUser, from, friendshipService);
                friendRequest.setLayoutX(10);
                friendRequest.setLayoutY(10 + 30 * i);

                requestMap.add(friendRequest);
                friendRequest.AddObserver(this);

                requestsRecv.getChildren().add(friendRequest);
                i++;
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadSentRequests() {
        requestSentMap.clear();
        requestsSent.getChildren().clear();
        int i = 0;
        for (Long toID : friendshipService.getUserSentRequests(currentUser.getID())) {
            try {
                FriendRequestSent friendRequest = new FriendRequestSent();
                User to = userService.GetOne(toID);
                friendRequest.setup(currentUser, to, friendshipService);
                friendRequest.setLayoutX(10);
                friendRequest.setLayoutY(10 + 30 * i);

                requestSentMap.add(friendRequest);
                friendRequest.AddObserver(this);

                requestsSent.getChildren().add(friendRequest);
                i++;
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }


    private void loadFriends() {
        friends.getChildren().clear();
        friendMap.clear();
        for (FriendshipDTO friendshipDTO : friendshipService.getUserFriendList(currentUser.getID())) {
            try {
                User from = userService.GetOne(friendshipDTO.getFriend());
                FriendElement friendElement = new FriendElement();
                friendElement.setup(currentUser, from, friendshipService, messageService, userService);

                friendMap.add(friendElement);
                friendElement.AddObserver(this);

                friends.getChildren().add(friendElement);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void update(Observable o) {
        boolean found = false;
        if (o instanceof FriendRequestReceived) {
            if (requestsRecv.getChildren().remove(requestMap.remove(o)))
                found = true;
            loadFriends();
        }
        if (o instanceof FriendElement) {
            if (friends.getChildren().remove(friendMap.remove(o)))
                found = true;
        }
        if (o instanceof FriendRequestSent) {
            if (requestsSent.getChildren().remove(requestSentMap.remove(o)))
                found = true;
            loadSentRequests();
        }
        if (!found) {
            searchMap.remove(o).setDisable(true);
        }
        Platform.runLater(() -> {
            friendships = friendshipService.getUserFriendList(currentUser.getID()).stream().map(FriendshipDTO::getFriend).collect(Collectors.toList());
            requestsR = friendshipService.getUserFriendRequests(currentUser.getID());
            requestsS = friendshipService.getUserSentRequests(currentUser.getID());
            loadSentRequests();
            loadFriends();
            loadReceivedRequests();
        });
    }

    public void loadPage(List<User> page) {
        searchResults.getChildren().clear();
        searchMap.clear();

        if (page == null) {
            searchResults.getChildren().add(new Label("No results"));
        } else {
            try {
                for (User user : page) {
                    if (friendships.contains(user.getID())) {
                        // Load friend element

                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/customElements/friend/friendElement.fxml"));
                        AnchorPane friend = fxmlLoader.load();
                        FriendElement elementController = fxmlLoader.getController();
                        elementController.setup(currentUser, user, friendshipService, messageService, userService);

                        searchMap.put(elementController, friend);
                        elementController.AddObserver(this);

                        searchResults.getChildren().add(friend);
                    } else if (requestsS.contains(user.getID())) {
                        // Load sent request element

                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/customElements/friendRequestSent/friendRequestSentElement.fxml"));
                        AnchorPane friend = fxmlLoader.load();
                        FriendRequestSent elementController = fxmlLoader.getController();
                        elementController.setup(currentUser, user, friendshipService);

                        searchMap.put(elementController, friend);
                        elementController.AddObserver(this);

                        searchResults.getChildren().add(friend);
                    } else if (requestsR.contains(user.getID())) {
                        // Load recv request element

                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/customElements/friendRequestReceived/friendRequestReceivedElement.fxml"));
                        AnchorPane friend = fxmlLoader.load();
                        FriendRequestReceived elementController = fxmlLoader.getController();
                        elementController.setup(currentUser, user, friendshipService);

                        searchMap.put(elementController, friend);
                        elementController.AddObserver(this);

                        searchResults.getChildren().add(friend);
                    } else {
                        //Load search element
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/friendSearchElement.fxml"));
                        AnchorPane friend = fxmlLoader.load();
                        FriendSearchElementController elementController = fxmlLoader.getController();
                        elementController.setup(currentUser, user, friendshipService);

                        if(user.getID().equals(currentUser.getID()))
                        {
                            elementController.hideButton();
                        }

                        searchMap.put(elementController, friend);
                        elementController.AddObserver(this);

                        searchResults.getChildren().add(friend);
                    }
                }

                prevPage.setDisable(!repo.hasPrevPageM());
                nextPage.setDisable(!repo.hasNextPageM());
                pageNumberSelector.setText(String.valueOf(repo.getPageNumberM() + 1));
                root.requestFocus();
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setContentText(e.getMessage());

                alert.showAndWait();
            }
        }
    }

    public void search() {
        repo.setMatcher(name.getText().split(" ")[0]);

        pageCounter.setText("/ " + repo.getPageCountM());

        friendships = friendshipService.getUserFriendList(currentUser.getID()).stream().map(FriendshipDTO::getFriend).collect(Collectors.toList());
        requestsR = friendshipService.getUserFriendRequests(currentUser.getID());
        requestsS = friendshipService.getUserSentRequests(currentUser.getID());

        List<User> page = repo.getPageM();
        loadPage(page);
    }

    public void loadPrevPage() {
        List<User> page = repo.getPrevPageM();
        loadPage(page);
    }

    public void loadNextPage() {
        List<User> page = repo.getNextPageM();
        loadPage(page);
    }


    public void loadPage() {
        int pageNumber = Integer.parseInt(pageNumberSelector.getText()) - 1;
        if (pageNumber < repo.getPageCount()) {
            List<User> page = repo.getPage(pageNumber);
            loadPage(page);
        } else {
            pageNumberSelector.setText(String.valueOf(repo.getPageNumber() + 1));
        }
    }

    public void onEnterFirst() {
        searchButton.fire();
    }

    public void activateCheck() {
        friendsPane.setExpanded(true);
        friendMap.keySet().forEach(k -> k.setCheckVisible(true));
        activateCheck.setVisible(false);
        createGroup.setVisible(true);
    }

    public void createGroup() {
        List<Long> members = new LinkedList<>();
        members.add(currentUser.getID());
        friendMap.keySet().forEach(k -> {
            k.setCheckVisible(false);
            if (k.getCheck())
                members.add(k.friendUser.getID());
        });
        if (members.size() > 2) {
            groups.getChildren().add(new Label(members.toString()));
        }
        System.out.println(members);
        activateCheck.setVisible(true);
        createGroup.setVisible(false);
    }

    public void activityReport() throws IOException {
        Dialog<Pair<LocalDate, LocalDate>> reportPeriodDialog = new Dialog<>();
        reportPeriodDialog.setTitle("Report Period");

        ButtonType generateReport = new ButtonType("Generate Report", ButtonBar.ButtonData.OK_DONE);
        reportPeriodDialog.getDialogPane().getButtonTypes().addAll(generateReport, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        DatePicker startDate = new DatePicker();
        DatePicker endDate = new DatePicker();

        grid.add(new Label("Start:"), 0, 0);
        grid.add(startDate, 1, 0);
        grid.add(new Label("End:"), 0, 1);
        grid.add(endDate, 1, 1);


        Node generateReportButton = reportPeriodDialog.getDialogPane().lookupButton(generateReport);
        generateReportButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        startDate.valueProperty().addListener((observable, oldValue, newValue) -> generateReportButton.setDisable(startDate.getValue() == null || endDate.getValue() == null));
        endDate.valueProperty().addListener((observable, oldValue, newValue) -> generateReportButton.setDisable(startDate.getValue() == null || endDate.getValue() == null));

        reportPeriodDialog.getDialogPane().setContent(grid);

        reportPeriodDialog.setResultConverter(dialogButton -> {
            if (dialogButton == generateReport) {
                return new Pair<>(startDate.getValue(), endDate.getValue());
            }
            return null;
        });

        Optional<Pair<LocalDate, LocalDate>> result = reportPeriodDialog.showAndWait();

        LocalDate start;
        LocalDate end;

        if (result.isPresent()) {
            start = result.get().getKey();
            end = result.get().getValue();

            if (start.compareTo(end) >= 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setHeaderText("Invalid date");
                alert.setContentText("The dates you selected are invalid");

                alert.showAndWait();
                return;
            }

            //Report Code
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/activityReportView.fxml"));
            AnchorPane root = loader.load();


            ActivityReportController controller = loader.getController();
            controller.setup(messageService, userService, friendshipService, currentUser, start, end);

            // New window (Stage)
            Stage reportWindow = new Stage();
            reportWindow.setScene(new Scene(root, 300, 500));
            reportWindow.setMinWidth(500);
            reportWindow.setMinHeight(300);
            reportWindow.setTitle("Activity Report Window");

            Platform.runLater(controller::load);

            // Set position of second window, related to primary window.
            reportWindow.setX(200);
            reportWindow.setY(200);

            reportWindow.show();
        }
    }

    public void messageReport() throws IOException {
        Dialog<Triplet<LocalDate, LocalDate, String>> reportPeriodDialog = new Dialog<>();
        reportPeriodDialog.setTitle("Configure Report");

        ButtonType generateReport = new ButtonType("Generate Report", ButtonBar.ButtonData.OK_DONE);
        reportPeriodDialog.getDialogPane().getButtonTypes().addAll(generateReport, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        DatePicker startDate = new DatePicker();
        DatePicker endDate = new DatePicker();
        ComboBox<String> friend = new ComboBox<>();

        friend.setPromptText("Friend...");
        for (FriendshipDTO f : friendshipService.getUserFriendList(currentUser.getID())) {
            friend.getItems().add(userService.GetOne(f.getFriend()).getEmail());
        }

        grid.add(new Label("Start:"), 0, 0);
        grid.add(startDate, 1, 0);
        grid.add(new Label("End:"), 0, 1);
        grid.add(endDate, 1, 1);
        grid.add(new Label("Friend:"), 0, 2);
        grid.add(friend, 1, 2);


        Node generateReportButton = reportPeriodDialog.getDialogPane().lookupButton(generateReport);
        generateReportButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        startDate.valueProperty()
                .addListener((observable, oldValue, newValue) -> generateReportButton.setDisable(startDate.getValue() == null
                        || endDate.getValue() == null || friend.getValue() == null));

        endDate.valueProperty()
                .addListener((observable, oldValue, newValue) -> generateReportButton.setDisable(startDate.getValue() == null
                        || endDate.getValue() == null || friend.getValue() == null));

        friend.valueProperty()
                .addListener(observable -> generateReportButton.setDisable(startDate.getValue() == null
                        || endDate.getValue() == null || friend.getValue() == null));

        reportPeriodDialog.getDialogPane().setContent(grid);

        reportPeriodDialog.setResultConverter(dialogButton -> {
            if (dialogButton == generateReport) {
                return new Triplet<>(startDate.getValue(), endDate.getValue(), friend.getValue());
            }
            return null;
        });

        Optional<Triplet<LocalDate, LocalDate, String>> result = reportPeriodDialog.showAndWait();

        LocalDate start;
        LocalDate end;
        User friendTarget;

        if (result.isPresent()) {
            start = result.get().e1;
            end = result.get().e2;
            friendTarget = userService.FindUserByEmail(result.get().e3);

            System.out.println(friendTarget.getEmail());

            if (start.compareTo(end) >= 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setHeaderText("Invalid date");
                alert.setContentText("The dates you selected are invalid");

                alert.showAndWait();
                return;
            }

            //Report Code
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/messageReportView.fxml"));
            AnchorPane root = loader.load();


            MessageReportController controller = loader.getController();
            controller.setup(messageService, userService, friendshipService, currentUser, start, end, friendTarget);

            // New window (Stage)
            Stage reportWindow = new Stage();
            reportWindow.setScene(new Scene(root, 300, 500));
            reportWindow.setMinWidth(500);
            reportWindow.setMinHeight(300);
            reportWindow.setTitle("Message Report Window");

            Platform.runLater(controller::load);

            // Set position of second window, related to primary window.
            reportWindow.setX(200);
            reportWindow.setY(200);

            reportWindow.show();
        }
    }

    public void createEvent() {
        Dialog<Pair<String, LocalDate>> createEventDialog = new Dialog<>();
        createEventDialog.setTitle("Create Event");

        ButtonType createEvent = new ButtonType("Create Event", ButtonBar.ButtonData.OK_DONE);
        createEventDialog.getDialogPane().getButtonTypes().addAll(createEvent, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField eventName = new TextField();
        DatePicker eventDate = new DatePicker();

        grid.add(new Label("Name:"), 0, 0);
        grid.add(eventName, 1, 0);
        grid.add(new Label("Date:"), 0, 1);
        grid.add(eventDate, 1, 1);


        Node createEventButton = createEventDialog.getDialogPane().lookupButton(createEvent);
        createEventButton.setDisable(true);

        eventDate.valueProperty().
                addListener((observable, oldValue, newValue) -> createEventButton.setDisable(eventDate.getValue() == null
                        || eventName.getText().equals("") || eventName.getText().length() < 5));
        eventName.textProperty().
                addListener((observable, oldValue, newValue) -> createEventButton.setDisable(eventDate.getValue() == null
                        || eventName.getText().equals("") || eventName.getText().length() < 5));

        createEventDialog.getDialogPane().setContent(grid);

        createEventDialog.setResultConverter(dialogButton -> {
            if (dialogButton == createEvent) {
                return new Pair<>(eventName.getText(), eventDate.getValue());
            }
            return null;
        });

        Optional<Pair<String, LocalDate>> result = createEventDialog.showAndWait();

        String name;
        LocalDate date;

        if (result.isPresent()) {
            name = result.get().getKey();
            date = result.get().getValue();

            if (date.compareTo(LocalDate.now()) < 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setHeaderText("Invalid date");
                alert.setContentText("The date you selected has already passed");

                alert.showAndWait();
                return;
            }

            UserEvent newEvent = new UserEvent(currentUser.getID(), date, name);
            eventRepo.save(newEvent);
            loadEvents(eventRepo.getPage(eventRepo.getPageNumber()));
        }
    }

    public void loadPrevPageE() {
        List<UserEvent> page = eventRepo.getPrevPage();
        loadEvents(page);
    }

    public void loadEvents(List<UserEvent> page) {
        pageCounterE.setText("/ " + eventRepo.getPageCount());

        eventsBox.getChildren().clear();
        for (UserEvent event : page) {
            if (event.getEventDate().compareTo(LocalDate.now()) >= 0) {
                try {
                    Event eventElement = new Event();
                    eventElement.setUp(event, currentUser);
                    eventElement.AddObserver(this);
                    eventsBox.getChildren().add(eventElement);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }

        prevPageE.setDisable(!eventRepo.hasPrevPage());
        nextPageE.setDisable(!eventRepo.hasNextPage());
        pageNumberSelectorE.setText(String.valueOf(eventRepo.getPageNumber() + 1));
        root.requestFocus();
    }

    public void loadNextPageE() {
        List<UserEvent> page = eventRepo.getNextPage();
        loadEvents(page);
    }

    public void loadEvents() {
        int pageNumber = Integer.parseInt(pageNumberSelectorE.getText()) - 1;
        if (pageNumber < eventRepo.getPageCount()) {
            List<UserEvent> page = eventRepo.getPage(pageNumber);
            loadEvents(page);
        } else {
            pageNumberSelectorE.setText(String.valueOf(eventRepo.getPageNumber() + 1));
        }
    }

    public void showNotifications() {
        List<UserEvent> closeEvents = new LinkedList<>();
        List<UserEvent> temp = eventRepo.getFirstPage();
        do {
            if (temp != null) {
                for (UserEvent u : temp) {
                    if (LocalDate.now().plusDays(7).compareTo(u.getEventDate()) >= 0 && u.getAttending().get(currentUser.getID()) != null) {
                        if(u.getAttending().get(currentUser.getID()))
                            closeEvents.add(u);
                    }
                }
            }
            temp = eventRepo.getNextPage();
        } while (eventRepo.hasNextPage());

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Upcoming events");

        if (closeEvents.size() > 0) {
            alert.setHeaderText("You have upcoming events:");
            StringBuilder notification = new StringBuilder();
            for (UserEvent event : closeEvents) {
                notification.append(event.getName()).append(" on ").append(event.getEventDate()).append(".\n");
            }
            alert.setContentText(notification.toString());
        } else {
            alert.setHeaderText("You have no event notifications");
        }

        alert.showAndWait();
    }


    private class Triplet<T1, T2, T3> {
        public T1 e1;
        public T2 e2;
        public T3 e3;

        public Triplet(T1 e1, T2 e2, T3 e3) {
            this.e1 = e1;
            this.e2 = e2;
            this.e3 = e3;
        }
    }
}
