package repository.user;

import domain.User;
import repository.IRepository;
import repository.PaginationInfo;

import java.util.List;

/**
 * Paged repository for the User class. Paging functions use Pagination info helper class.
 * Valid match keys are "email", "firstname", "lastname", valid pageSize is positive non zero integer
 * and Valid pageNumber is positive integer.
 */
public interface IUserRepository extends IRepository<Long, User> {

    User findByEmail(String email);
    List<User> getPage(PaginationInfo paginationInfo);
    int getPageCount(PaginationInfo paginationInfo);

}
