package repository.event;

import domain.User;
import domain.UserEvent;
import repository.IRepository;

import java.time.LocalDate;
import java.util.List;

public interface IEventRepository extends IRepository<Long, UserEvent> {

    List<UserEvent> getBetweenDates(LocalDate start, LocalDate end);

    List<UserEvent> getOnDate(LocalDate date);

    List<UserEvent> getUserEvents(long userID);

    void changeSubscription(UserEvent event, long userID, boolean isSubscribed);

    List<UserEvent> getPage();
    List<UserEvent> getFirstPage();
    List<UserEvent> getNextPage();
    List<UserEvent> getPrevPage();
    int getPageNumber();
    int getPageCount();
    boolean hasPrevPage();
    boolean hasNextPage();
}
