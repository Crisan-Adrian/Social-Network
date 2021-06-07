package repository.friendship;

import domain.FriendshipRequest;
import domain.LLTuple;
import repository.IRepository;

import java.util.List;

public interface IFriendRequestRepository extends IRepository<LLTuple, FriendshipRequest> {

    /**
     * Gets all friendship requests received by the user
     * @param userID - the user's ID
     * @return all received friendship requests or {@code null} if an error occurred
     */
    List<FriendshipRequest> getUserReceivedRequests(Long userID);

    /**
     * Gets all friendship requests sent by the user
     * @param userID - the user's ID
     * @return all sent friendship requests or {@code null} if an error occurred
     */
    List<FriendshipRequest> getUserSentRequests(Long userID);
}
