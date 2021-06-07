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

    /**
     * Finds a user with registered with the given email address
     * @param email the email address to check
     * @return {@code null} if no user is registered with the given email, the user otherwise.
     */
    User findByEmail(String email);

    /**
     * Gets a page of users.
     * @param paginationInfo defines page size, page number and match parameters to be used.
     * @return a page of users or null if an error occurred
     */
    List<User> getPage(PaginationInfo paginationInfo);

    /**
     * Gets the number of pages that exist.
     * @param paginationInfo defines page size, page number and match parameters to be used when counting pages.
     * @return the number of pages that exist or 0 if an error occurred
     */
    int getPageCount(PaginationInfo paginationInfo);

}
