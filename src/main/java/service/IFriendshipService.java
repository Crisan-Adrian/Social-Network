package service;

import domain.FriendRequestStatus;
import domain.Friendship;
import domain.FriendshipDTO;
import domain.FriendshipRequest;

import java.time.LocalDate;
import java.util.List;

public interface IFriendshipService {

    /**
     * Deletes the friendship between the users with the given ids
     *
     * @param id1 ID of the first user
     * @param id2 ID of the second user
     */
    void deleteFriendship(Long id1, Long id2);

    /**
     * Deletes all of a users friendships
     *
     * @param id the user ID to delete from
     */
    void deleteAllFriendships(Long id);

    /**
     * Accepts a friendship between from a user
     *
     * @param user the user accepting
     * @param from the sending user
     * @return {@code null} if the friendship request does not exist or the users are already friends
     * the friendship otherwise
     */
    Friendship answerFriendshipRequest(Long user, Long from, FriendRequestStatus response);

    /**
     * Cancels a sent friend request
     *
     * @param user the sending user
     * @param to   the receiving user
     */
    void cancelFriendshipRequest(Long user, Long to);

    /**
     * Creates a friendship request
     *
     * @param user the user sending the request
     * @param to   the user receiving the request
     */
    void sendFriendshipRequest(Long user, Long to);

    /**
     * Creates a friendship between 2 users at the current date
     *
     * @param user1 user ID
     * @param user2 user ID
     * @return the friendship created
     */
    Friendship createFriendship(Long user1, Long user2);

    /**
     * Gets unanswered friendship request received by the user
     *
     * @param user the user's ID
     * @return the list of user ID's who sent a request
     */
    List<Long> getUserReceivedRequests(Long user);

    Iterable<FriendshipRequest> getAllRequests();

    /**
     * Determines the community with the longest friendship chain
     *
     * @return the IDs of the community members
     */
    Iterable<Long> mostSociableCommunity();

    /**
     * Determines the number of existing communities
     *
     * @return the number of communities
     */
    int getCommunitiesNumber();

    /**
     * Gets all friendships
     *
     * @return all friendships
     */
    Iterable<Friendship> getAllFriendships();

    /** Gets all the friendships of the user
     * @param userID -
     * @return the list of friendships
     */
    List<FriendshipDTO> getUserFriendList(Long userID);

    /**
     * Gets all the friendships of the user formed between the start date inclusive and the end date exclusive
     * @param userID -
     * @param start -
     * @param end -
     * @return the list of friendships the fit the criteria
     */
    List<FriendshipDTO> getUserFriendsFromPeriod(Long userID, LocalDate start, LocalDate end);

    /**
     * Gets all the friendships of the user formed in the given month and the given year
     * @param userID the user's ID
     * @param year -
     * @param month -
     * @return the list of friendships the fit the criteria
     */
    List<FriendshipDTO> getUserFriendList(Long userID, int year, int month);

    /**
     * Gets unanswered friendship request sent by the user
     *
     * @param user the user's ID
     * @return the list of user ID's who received a request
     */
    List<Long> getUserSentRequests(Long user);
}
