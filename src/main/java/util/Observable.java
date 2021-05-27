package util;

public interface Observable {

    void NotifyObservers();

    void AddObserver(Observer o);

    void RemoveObserver(Observer o);
}
