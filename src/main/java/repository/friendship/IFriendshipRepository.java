package repository.friendship;

import domain.Friendship;
import domain.LLTuple;
import repository.IRepository;

import java.time.LocalDate;
import java.util.List;

public interface IFriendshipRepository extends IRepository<LLTuple, Friendship> {

    List<Friendship> getUserFriends(Long userID);

    List<Friendship> getUserFriendsFromPeriod(Long userID, LocalDate start, LocalDate end);

    List<Friendship> getUserFriendsFromPeriod(Long userID, int year, int month);

}
