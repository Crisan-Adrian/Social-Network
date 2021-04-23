package repository.friendship;

import domain.FriendshipRequest;
import domain.LLTuple;
import repository.IRepository;

import java.util.List;

public interface IFriendRequestRepository extends IRepository<LLTuple, FriendshipRequest> {

    List<FriendshipRequest> getUserReceivedRequests(Long userID);

    List<FriendshipRequest> getUserSentRequests(Long userID);
}
