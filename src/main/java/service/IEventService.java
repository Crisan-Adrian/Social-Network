package service;

import domain.User;
import domain.UserEvent;

import java.util.List;

public interface IEventService {

    /**
     * Gets the first page of events using current paging information
     *
     * @return the first page of events
     * @throws exceptions.ServiceException if paging information is not set
     */
    List<UserEvent> getFirstPage();

    /**
     * Gets page of given number and moves cursor to it
     * @param pageNumber the page number
     * @return the page of events
     * @throws exceptions.ServiceException if page number is invalid or paging information is not set
     */
    List<UserEvent> getPage(int pageNumber);


    int getPageCount();

    /**
     * Checks if there is a page after current page
     *
     * @return
     * true - if a next page exists
     * false - otherwise
     * @throws exceptions.ServiceException if paging information is not set
     */
    boolean hasPrevPage();

    /**
     * Checks if there is a page before current page
     *
     * @return
     * true - if a previous page exists
     * false - otherwise
     * @throws exceptions.ServiceException if paging information is not set
     */
    boolean hasNextPage();

    /**
     * Gets the number of the current page
     * @return the number of the current page
     */
    int getPageNumber();

    /**
     * Get the next page
     * @return the page of events
     * @throws exceptions.ServiceException if there is no next page or if paging information is not set
     */
    List<UserEvent> getNextPage();

    /**
     * Get the previous page
     * @return the page of events
     * @throws exceptions.ServiceException if there is no previous page or if paging information is not set
     */
    List<UserEvent> getPrevPage();

    /**
     * Save an event
     * @param newEvent the event to save
     */
    void save(UserEvent newEvent);

    /**
     * Changes the notification preferences for an event for the given user
     * @param user the user to update preferences of
     * @param event the event to update
     * @param isNotified the new notification preferences
     * @return the updated event
     */
    UserEvent changeNotificationState(User user, UserEvent event, boolean isNotified);

    /**
     * Changes the attendance for an event for the given user
     * @param user the user to update attendance of
     * @param event the event to update
     * @param isAttending the new attendance status
     * @return the updated event
     */
    UserEvent changeAttendance(User user, UserEvent event, boolean isAttending);
}
