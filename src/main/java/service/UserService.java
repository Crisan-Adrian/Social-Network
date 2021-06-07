package service;

import domain.User;
import exceptions.UnknownUserException;
import repository.user.IUserRepository;

import java.util.LinkedList;
import java.util.List;

public class UserService implements IUserService {

    private final IUserRepository repo;

    public UserService(IUserRepository repo) {
        this.repo = repo;
    }

    @Override
    public User AddUser(User user) {
        return repo.save(user);
    }

    @Override
    public User DeleteUser(Long id) {
        return repo.delete(id);
    }

    @Override
    public Iterable<String> GetUserNames(Iterable<Long> ids) throws UnknownUserException {
        String unknownIDs = "";
        List<String> names = new LinkedList<>();
        for (Long id : ids) {
            User user = repo.findOne(id);
            if (user == null) {
                unknownIDs = id + " ";
            } else {
                names.add(user.getFirstName() + " " + user.getLastName());
            }
        }
        if (!unknownIDs.equals("")) {
            throw new UnknownUserException("The following User IDs do not exist: " + unknownIDs);
        }
        return names;
    }

    @Override
    public List<User> GetUsers(List<Long> userIDs) throws UnknownUserException {
        String unknownIDs = "";
        List<User> users = new LinkedList<>();
        for (Long userID : userIDs) {
            User user = repo.findOne(userID);
            if (user == null) {
                unknownIDs = userID + " ";
            } else {
                users.add(user);
            }
        }
        if (!unknownIDs.equals("")) {
            throw new UnknownUserException("User ID does not exist");
        }
        return users;
    }

    @Override
    public User GetOne(Long id) {
        return repo.findOne(id);
    }

    @Override
    public Iterable<User> GetAllUsers() {
        return repo.findAll();
    }

    @Override
    public User FindUserByEmail(String email) {
//        List<User> page = repo.getFirstPage();
//        do {
//            for (User u : page) {
//                if (u.getEmail().equals(email)) {
//                    return u;
//                }
//            }
//            page = repo.getNextPage();
//        } while (repo.hasNextPage());
        return null;
    }
}
