package repository.friendship;

import domain.Friendship;
import domain.LLTuple;
import repository.IRepository;

import java.time.LocalDate;
import java.util.List;

public interface IFriendshipRepository extends IRepository<LLTuple, Friendship> {

    /**
     * Gets all friends of a user
     * @param userID the user ID
     * @return the friendships or {@code null} if an error occurred
     */
    List<Friendship> getUserFriends(Long userID);

    /**
     * Gets all friendships formed between the start date inclusive and end date exclusive
     * @param userID -
     * @param start -
     * @param end -
     * @return the friendships the fit the search criteria or {@code null} if an error occurred
     */
    List<Friendship> getUserFriendsFromPeriod(Long userID, LocalDate start, LocalDate end);

    /**
     * Gets all friendships formed between in the interval between the start and the end of the month int the year
     * @param userID -
     * @param year -
     * @param month -
     * @return the friendships the fit the search criteria or {@code null} if an error occurred
     */
    List<Friendship> getUserFriendsFromPeriod(Long userID, int year, int month);

}
