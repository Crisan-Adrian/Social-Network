package service;

import domain.User;
import exceptions.ServiceException;
import exceptions.UnknownUserException;
import repository.PaginationInfo;
import repository.PaginationInfoBuilder;
import repository.user.IUserRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class UserService implements IUserService {

    private final IUserRepository repo;
    private final PaginationInfo paginationInfo;

    public UserService(IUserRepository repo) {
        this.repo = repo;
        paginationInfo = new PaginationInfoBuilder().create().setPageSize(10).setPageNumber(0).build();

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
        return repo.findByEmail(email);
    }

    @Override
    public List<User> getFirstPage() {
        paginationInfo.setPageNumber(0);
        return repo.getPage(paginationInfo);
    }

    @Override
    public List<User> getPage(int pageNumber) {
        paginationInfo.setPageNumber(pageNumber);
        return repo.getPage(paginationInfo);
    }

    @Override
    public int getPageCount() {
        return repo.getPageCount(paginationInfo);
    }

    @Override
    public boolean hasPrevPage() {
        return paginationInfo.getPageNumber() > 0;
    }

    @Override
    public boolean hasNextPage() {
        return paginationInfo.getPageNumber() < (repo.getPageCount(paginationInfo) - 1);
    }

    @Override
    public int getPageNumber() {
        return paginationInfo.getPageNumber();
    }

    @Override
    public List<User> getPrevPage() {
        if (hasPrevPage()) {
            paginationInfo.decrementPageNumber();
            return repo.getPage(paginationInfo);
        }
        throw new ServiceException("There is no previous page");
    }

    @Override
    public List<User> getNextPage() {
        if (hasNextPage()) {
            paginationInfo.incrementPageNumber();
            return repo.getPage(paginationInfo);
        }
        throw new ServiceException("There is no next page");
    }

    @Override
    public void setMatcher(Map<String, String> matcher) {
        paginationInfo.setMatcher(matcher);
        if(!repo.validateMatcher(paginationInfo)){
            paginationInfo.setMatcher(null);
            throw new ServiceException("Invalid matcher");
        }
    }


}
