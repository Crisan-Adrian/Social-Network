package util;

import java.util.HashSet;
import java.util.Set;

public class ObserverManager {
    private final Set<Observer> observers;

    public ObserverManager() {
        observers = new HashSet<>();
    }

    public void NotifyObservers(Observable source) {
        for (Observer o : observers)
        {
            o.update(source);
        }
    }

    public void AddObserver(Observer o) {
        if(o!= null)
        {
            observers.add(o);
        }
    }

    public void RemoveObserver(Observer o) {
        if(o!= null)
        {
            observers.remove(o);
        }
    }
}
