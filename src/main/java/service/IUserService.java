package service;

import domain.User;
import exceptions.UnknownUserException;

import java.util.List;
import java.util.Map;

public interface IUserService {

    /**
     * Adds a user
     *
     * @param user the user to add
     * @return {@code null} if the user was saved
     * the user otherwise (the user ID is already registered)
     */
    User AddUser(User user);

    /**
     * Deletes a user
     *
     * @param id the user ID to delete
     * @return the user if he was deleted
     * {@code null} otherwise
     */
    User DeleteUser(Long id);

    /**
     * Returns the user names registered with the given ids
     *
     * @param ids the user IDs
     * @return the user names
     * @throws UnknownUserException if a user ID is not registered
     */
    Iterable<String> GetUserNames(Iterable<Long> ids) throws UnknownUserException;

    /**
     * Returns the user names registered with the given ids
     *
     * @param userIDs the user IDs
     * @return Users matched to the given IDs
     * @throws UnknownUserException if a user ID is not registered
     */
    List<User> GetUsers(List<Long> userIDs) throws UnknownUserException;

    /**
     * Gets the user with the given ID
     *
     * @param id the user ID
     * @return the requested user
     * {@code null} if the user does not exist
     */
    User GetOne(Long id);

    /**
     * Gets all registered users
     *
     * @return all registered users
     */
    Iterable<User> GetAllUsers();

    /**
     * Gets the user with the given email
     *
     * @param email the email the user registered with
     * @return the requested user
     * {@code null} if the user does not exist
     */
    User FindUserByEmail(String email);


    /**
     * Gets the first page of users using current paging information
     *
     * @return the first page of users
     * @throws exceptions.ServiceException if paging information is not set
     */
    List<User> getFirstPage();

    /**
     * Gets page of given number and moves cursor to it
     * @param pageNumber the page number
     * @return the page of users
     * @throws exceptions.ServiceException if page number is invalid or paging information is not set
     */
    List<User> getPage(int pageNumber);

    /**
     * Gets the number of pages available
     *
     * @return the number of pages
     * @throws exceptions.ServiceException if paging information is not set
     */
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
    List<User> getPrevPage();

    /**
     * Get the previous page
     * @return the page of events
     * @throws exceptions.ServiceException if there is no previous page or if paging information is not set
     */
    List<User> getNextPage();

    /**
     * Sets a matcher for paginated search. Valid match keys are "firstname", "lastname", "email"
     * @param matcher new match criteria
     * @throws exceptions.ServiceException if matcher is invalid
     */
    void setMatcher(Map<String, String> matcher);
}
