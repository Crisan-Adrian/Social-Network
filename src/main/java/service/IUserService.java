package service;

import domain.User;
import exceptions.UnknownUserException;

import java.util.List;

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
}
