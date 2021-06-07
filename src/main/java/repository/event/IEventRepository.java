package repository.event;

import domain.UserEvent;
import repository.IRepository;
import repository.PaginationInfo;

import java.time.LocalDate;
import java.util.List;

/**
 * Paged repository for the UserEvent class. Paging functions use Pagination info helper class.
 * Valid pageSize is positive non zero integer and Valid pageNumber is positive integer. Does not currently support matched paging.
 */
public interface IEventRepository extends IRepository<Long, UserEvent> {

    /**
     * Gets all events occurring between start inclusive and end exclusive
     * @param start interval start
     * @param end interval end
     * @return the events, or {@code null} if an error occurred
     */
    List<UserEvent> getBetweenDates(LocalDate start, LocalDate end);

    /**
     * Gets all events occurring on a given date
     * @param date the date to search
     * @return the events, or {@code null} if an error occurred
     */
    List<UserEvent> getOnDate(LocalDate date);

    /**
     * Gets all events created by a user
     * @param userID the event creator
     * @return the events, or {@code null} if an error occurred
     */
    List<UserEvent> getUserEvents(long userID);

    /**
     * Sets the notification status for a user and a given event
     * @param event event to set notification status on
     * @param userID user to set status for
     * @param isSubscribed the new notification status
     */
    void setSubscription(UserEvent event, long userID, boolean isSubscribed);

    /**
     * Gets a page of events.
     * @param paginationInfo defines page size, page number and match parameters to be used.
     * @return a page of events or null if an error occurred
     */
    List<UserEvent> getPage(PaginationInfo paginationInfo);

    /**
     * Gets the number of pages that exist.
     * @param paginationInfo defines page size, page number and match parameters to be used when counting pages.
     * @return the number of pages that exist or 0 if an error occurred
     */
    int getPageCount(PaginationInfo paginationInfo);
}
