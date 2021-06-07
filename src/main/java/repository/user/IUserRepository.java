package repository.user;

import domain.User;
import repository.IRepository;
import repository.PaginationInfo;

import java.util.List;

/**
 * Paged repository for the User class. Paging functions use Pagination info helper class.
 * Valid match keys are "email", "firstname", "lastname", valid pageSize and pageNumber are positive integers.
 */
public interface IUserRepository extends IRepository<Long, User> {

    User findByEmail(String email);
    List<User> getPage(PaginationInfo paginationInfo);
    int getPageCount(PaginationInfo paginationInfo);

}
