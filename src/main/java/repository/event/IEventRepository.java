package repository.event;

import domain.User;
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

    List<UserEvent> getBetweenDates(LocalDate start, LocalDate end);

    List<UserEvent> getOnDate(LocalDate date);

    List<UserEvent> getUserEvents(long userID);

    void changeSubscription(UserEvent event, long userID, boolean isSubscribed);

    List<UserEvent> getPage(PaginationInfo paginationInfo);
    int getPageCount(PaginationInfo paginationInfo);
}
