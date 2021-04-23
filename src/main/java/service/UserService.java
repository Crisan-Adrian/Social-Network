package service;

import domain.User;
import exceptions.UnknownUserException;
import repository.user.IUserRepository;

import java.util.LinkedList;
import java.util.List;

public class UserService {

    //TODO: Comment code where necessary. Document functions. Refactor if needed

    private final IUserRepository repo;

    public UserService(IUserRepository repo) {
        this.repo = repo;
    }

    /**
     * Adds a user
     *
     * @param user the user to add
     * @return {@code null} if the user was saved
     * the user otherwise (the user ID is already registered)
     */
    public User addUtilizator(User user) {
        return repo.save(user);
    }

    /**
     * Deletes a user
     *
     * @param id the user ID to delete
     * @return the user if he was deleted
     * {@code null} otherwise
     */
    public User deleteUtilizator(Long id) {
        return repo.delete(id);
    }

    /**
     * Returns the user names registered with the given ids
     *
     * @param ids the user IDs
     * @return the user names
     * @throws UnknownUserException if a user ID is not registered
     */
    public Iterable<String> getUserNames(Iterable<Long> ids) {
        List<String> names = new LinkedList<>();
        for (Long id : ids) {
            User user = repo.findOne(id);
            if (user == null) {
                throw new UnknownUserException("User ID does not exist");
            }
            names.add(user.getFirstName() + " " + user.getLastName());
        }
        return names;
    }


    public List<User> getUsers(List<Long> userIDs) {
        List<User> users = new LinkedList<>();
        for (Long userID : userIDs) {
            User user = repo.findOne(userID);
            if (user == null) {
                throw new UnknownUserException("User ID does not exist");
            }
            users.add(user);
        }
        return users;
    }

    /**
     * Gets the user with the given ID
     *
     * @param id the user ID
     * @return the requested user
     * {@code null} if the user does not exist
     */
    public User getOne(Long id) {
        return repo.findOne(id);
    }

    /**
     * Gets all registered users
     *
     * @return all registered users
     */
    public Iterable<User> getAll() {
        return repo.findAll();
    }

    public User findUserEmail(String email) {
        List<User> page = repo.getFirstPage();
        do{
            for (User u : page) {
                if (u.getEmail().equals(email))
                    return u;
            }
            page = repo.getNextPage();
        }while (repo.hasNextPage());
        return null;
    }
}
