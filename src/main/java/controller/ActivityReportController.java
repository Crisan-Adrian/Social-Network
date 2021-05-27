package controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextArea;
import domain.FriendshipDTO;
import domain.Message;
import domain.User;
import service.FriendshipService;
import service.MessageService;
import service.UserService;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;

public class ActivityReportController {

    @FXML
    private BarChart<String, Integer> chart;
    @FXML
    private TextArea text;

    private UserService userService;
    private FriendshipService friendshipService;
    private MessageService messageService;

    private User currentUser;
    private LocalDate start;
    private LocalDate end;
    private String log;

    /***
     * loads data into report window elements
     */
    public void load() {
        log = "";
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();

        chart.setTitle("Activity Report");
        xAxis.setLabel("Day");
        yAxis.setLabel("Value");

        XYChart.Series<String, Integer> messages = new XYChart.Series<>();
        messages.setName("Messages");
        XYChart.Series<String, Integer> friendships = new XYChart.Series<>();
        friendships.setName("New Friends");

        LocalDate current;
        current = start;
        List<Message> messageList = messageService.getUserMessages(currentUser.getID());
        List<FriendshipDTO> friendshipsList = friendshipService.getUserFriendList(currentUser.getID());
        // Iterates through days from start date to end date
        while (!current.equals(end.plusDays(1))) {
            int messagesRecv = 0;
            int friendsMade = 0;
            List<Message> toRemove = new LinkedList<>();
            for (Message m : messageList) {
                // Messages from current date are logged and marked to be removed
                if (m.getTimestamp().toLocalDate().equals(current)) {
                    messagesRecv++;
                    toRemove.add(m);
                    log += m.getTimestamp().toLocalDate().toString() + " - Recieved: " + m.getMessage().replace("\n", "") + " from " + userService.GetOne(m.getFrom()).getLastName() + " " + userService.GetOne(m.getFrom()).getFirstName() + "\n";
                }
            }
            List<FriendshipDTO> toRemoveF = new LinkedList<>();
            for (FriendshipDTO f : friendshipsList) {
                // Friendships created on current date are logged and marked to be removed
                if (f.getFriendedDate().equals(current)) {
                    friendsMade++;
                    toRemoveF.add(f);
                    log += f.getFriendedDate().toString() + " - Friended: " + userService.GetOne(f.getFriend()).getLastName() + " " + userService.GetOne(f.getFriend()).getFirstName() + "\n";
                }
            }
            // Data added to charts and messages and friendships removed from search lists
            friendships.getData().add(new XYChart.Data<>(current.toString(), friendsMade));
            messages.getData().add(new XYChart.Data<>(current.toString(), messagesRecv));
            friendshipsList.removeAll(toRemoveF);
            messageList.removeAll(toRemove);

            // If more than 0 friends made/messages received log a summary
            if(friendsMade != 0 || messagesRecv != 0)
                log += current.toString() + " - Day activities: " + friendsMade + " friends made, " + messagesRecv + " messages received.\n\n";

            current = current.plusDays(1);
        }

        // Add all data to chart and show
        chart.getData().addAll(messages, friendships);
        Runnable r = () -> {
            for(Node n:chart.lookupAll(".default-color0.chart-bar")) {
                n.setStyle("-fx-bar-fill: #6495ed;");
            }
            for (Node n : chart.lookupAll(".bar-legend-symbol.default-color0")) {
                n.setStyle("-fx-background-color: " + "#6495ed;");
            }
            for(Node n:chart.lookupAll(".default-color1.chart-bar")) {
                n.setStyle("-fx-bar-fill: #228b22;");
            }
            for (Node n : chart.lookupAll(".bar-legend-symbol.default-color1")) {
                n.setStyle("-fx-background-color: " + "#228b22;");
            }
        };
        Platform.runLater(r);
        chart.setBarGap(1);
        chart.setCategoryGap(0);
        chart.setPrefWidth(Math.max(ChronoUnit.DAYS.between(start, end) * 25, 400));
        text.setText(log);
    }

    /***
     * Initial setup of controller
     * @param messageService service object to use
     * @param userService service object to use
     * @param friendshipService service object to use
     * @param currentUser current user for report
     * @param start start date of repost
     * @param end end date of report
     */
    public void setup(MessageService messageService, UserService userService, FriendshipService friendshipService, User currentUser, LocalDate start, LocalDate end) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
        this.currentUser = currentUser;

        this.start = start;
        this.end = end;
    }

    /***
     * Export function for report data
     * Saves report data to .pdf file
     */
    public void export() throws IOException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream("activityReport.pdf"));
        document.open();

        // Write report header
        Font font = FontFactory.getFont(FontFactory.COURIER, 20, BaseColor.BLACK);
        Paragraph paragraph = new Paragraph("Activity report", font);
        paragraph.setAlignment(2);
        document.add(paragraph);
        font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
        paragraph = new Paragraph("Period: " + start.toString() + " - " + end.toString() + "\n", font);
        paragraph.setAlignment(2);
        document.add(paragraph);

        // Write report body
        font = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);
        paragraph = new Paragraph(log, font);
        document.add(paragraph);
        document.close();
    }
}
