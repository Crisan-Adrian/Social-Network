package repository.user;

import domain.User;
import repository.IRepository;
import repository.PaginationInfo;

import java.util.List;

public interface IUserRepository extends IRepository<Long, User> {

    User findByEmail(String email);
    List<User> getPage(PaginationInfo paginationInfo);
    List<User> getPageMatched(PaginationInfo paginationInfo);
    int getPageCount(PaginationInfo paginationInfo);

}
