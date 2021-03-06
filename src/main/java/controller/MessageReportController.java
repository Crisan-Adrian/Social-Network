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
import domain.Message;
import domain.User;
import service.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.LinkedList;

public class MessageReportController {

    @FXML
    private TextArea text;
    @FXML
    private BarChart chart;

    private IMessageService messageService;
    private IUserService userService;
    private IFriendshipService friendshipsService;
    private User currentUser;
    private User friend;
    private LocalDate start;
    private LocalDate end;
    String log;

    public void load() {
        StringBuilder logBuilder = new StringBuilder();
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();

        chart.setTitle("Message Report");
        xAxis.setLabel("Day of Week");
        yAxis.setLabel("Value");

        XYChart.Series<String, Integer> messages = new XYChart.Series<>();
        messages.setName("Messages");

        LocalDate current;
        current = start;
        java.util.List<Message> messageList = messageService.getUserMessages(currentUser.getID());
        int[] dayOfWeek = new int[7];

        while (!current.equals(end.plusDays(1))) {
            int messagesRecv = 0;
            java.util.List<Message> toRemove = new LinkedList<>();
            for (Message m : messageList) {
                if (m.getTimestamp().toLocalDate().equals(current)) {
                    dayOfWeek[current.getDayOfWeek().getValue() - 1]++;
                    messagesRecv++;
                    toRemove.add(m);
                    logBuilder.append(m.getTimestamp().toLocalDate().toString()).append(" - ").append(m.getMessage().replace("\n", "")).append("\n");
                }
            }
            messageList.removeAll(toRemove);
            if (messagesRecv != 0) {
                logBuilder.append(current).append(" - Day activities: ").append(messagesRecv).append(" messages received.\n\n");
            }

            current = current.plusDays(1);
        }
        int i = 1;
        for (int day : dayOfWeek) {
            messages.getData().add(new XYChart.Data<>(DayOfWeek.of(i).toString(), day));
            i++;
        }

        chart.getData().addAll(messages);
        Runnable r = () -> {
            for (Node n : chart.lookupAll(".default-color0.chart-bar")) {
                n.setStyle("-fx-bar-fill: #149fd6;");
            }
            for (Node n : chart.lookupAll(".bar-legend-symbol.default-color0")) {
                n.setStyle("-fx-background-color: " + "#149fd6;");
            }
        };
        Platform.runLater(r);
        log = logBuilder.toString();
        chart.setBarGap(1);
        chart.setCategoryGap(0);
        chart.setPrefWidth(400);
        text.setText(log);
    }

    public void export() throws DocumentException, FileNotFoundException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream("messageReport.pdf"));
        document.open();
        Font font = FontFactory.getFont(FontFactory.COURIER, 20, BaseColor.BLACK);
        Paragraph paragraph = new Paragraph("Message report", font);
        paragraph.setAlignment(2);
        document.add(paragraph);
        font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
        paragraph = new Paragraph("Period: " + start.toString() + " - " + end.toString() + "\nFriend: " + friend.getFirstName() + " " + friend.getLastName() + "\n", font);
        paragraph.setAlignment(2);
        document.add(paragraph);
        font = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);
        paragraph = new Paragraph(log, font);
        document.add(paragraph);
        document.close();
    }

    public void setup(IMessageService messageService, IUserService userService, IFriendshipService friendshipService, User currentUser, LocalDate start, LocalDate end, User friendTarget) {
        this.messageService = messageService;
        this.userService = userService;
        this.friendshipsService = friendshipService;
        this.currentUser = currentUser;
        this.friend = friendTarget;
        this.start = start;
        this.end = end;
    }
}
