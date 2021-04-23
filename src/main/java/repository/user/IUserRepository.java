package repository.user;

import domain.User;
import repository.IRepository;

import java.util.List;

public interface IUserRepository extends IRepository<Long, User> {

    User findByEmail(String email);

    List<User> getPage();
    List<User> getFirstPage();
    List<User> getNextPage();
    List<User> getPrevPage();
    int getPageNumber();
    int getPageCount();
    boolean hasPrevPage();
    boolean hasNextPage();


    List<User> getPageM();
    List<User> getNextPageM();
    List<User> getPrevPageM();
    int getPageNumberM();
    boolean hasPrevPageM();
    boolean hasNextPageM();

}
