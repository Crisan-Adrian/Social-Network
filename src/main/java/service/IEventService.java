package service;

import domain.UserEvent;

import java.util.List;

public interface IEventService {
    //TODO: Add paged functions and fields for current paging in implementation.

    /**
     * Gets the first page of events using current paging information
     *
     * @return the first page of events
     * @throws exceptions.ServiceException if paging information is not set
     */
    List<UserEvent> getFirstPage();

    /**
     * Gets page of current number and moves cursor to it
     * @param pageNumber the page number
     * @return the page of events
     * @throws exceptions.ServiceException if page number is invalid or paging information is not set
     */
    List<UserEvent> getPage(int pageNumber);

    /**
     * Gets the number of pages available
     *
     * @return the number of pages
     * @throws exceptions.ServiceException if paging information is not set
     */
    int getPageCount();

    /**
     * Checks is there exits a page after current page
     *
     * @return
     * true - if a next page exists
     * false - otherwise
     * @throws exceptions.ServiceException if paging information is not set
     */
    boolean hasPrevPage();

    /**
     * Checks is there exits a page before current page
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

    List<UserEvent> getNextPage();

    List<UserEvent> getPrevPage();

    void save(UserEvent newEvent);
}
