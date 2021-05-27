package service;

import domain.FriendRequestStatus;
import domain.Friendship;
import domain.FriendshipDTO;
import domain.FriendshipRequest;

import java.util.List;

public interface IFriendshipService {
    Friendship deleteFriendship(Long id1, Long id2);

    void deleteAllFriendships(Long id);

    Friendship answerFriendshipRequest(Long user, Long from, FriendRequestStatus response);

    void cancelFriendshipRequest(Long user, Long to);

    FriendshipRequest sendFriendshipRequest(Long user, Long to);

    Friendship createFriendship(Long user1, Long user2);

    List<Long> getUserFriendRequests(Long user);

    Iterable<FriendshipRequest> getAllRequests();

    Iterable<Long> mostSociableCommunity();

    int getCommunitiesNumber();

    Iterable<Friendship> getAllFriendships();

    List<FriendshipDTO> getUserFriendList(Long userID);

    List<FriendshipDTO> getUserFriendList(Long userID, int year, int month);

    List<Long> getUserSentRequests(Long id);
}
