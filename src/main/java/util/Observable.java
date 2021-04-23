package util;

import java.util.LinkedList;
import java.util.List;

public abstract class Observable {

    //TODO: Comment code where necessary. Document functions. Refactor if needed

    private List<Observer> observers;

    public Observable() {
        observers = new LinkedList<>();
    }

    public void notifyObservers()
    {
        for (Observer o : observers)
        {
            o.update(this);
        }
    }

    public void addObserver(Observer o)
    {
        if(o!= null && !observers.contains(o))
        {
            observers.add(o);
        }
    }

    public void removeObserver(Observer o)
    {
        if(o!= null && observers.contains(o))
        {
            observers.remove(o);
        }
    }
}
