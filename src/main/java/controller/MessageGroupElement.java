package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import util.Observable;
import util.Observer;
import util.ObserverManager;

import java.io.IOException;

public class MessageGroupElement extends AnchorPane implements Observable {

    //TODO: Implement. Comment code where necessary. Document functions. Refactor if needed

    private final ObserverManager manager = new ObserverManager();

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
    }



    @FXML
    protected void message(ActionEvent event) {
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
