package service;

import domain.*;
import repository.friendship.IFriendRequestRepository;
import repository.friendship.IFriendshipRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FriendshipService {

    //TODO: Comment code where necessary. Document functions. Refactor if needed
    //TODO: Move to one Service Model.

    private final IFriendshipRepository repoFriendships;
    private final IFriendRequestRepository repoRequests;

    public FriendshipService(IFriendshipRepository repoFriendships, IFriendRequestRepository repoRequests) {
        this.repoFriendships = repoFriendships;
        this.repoRequests = repoRequests;
    }

    /**
     * Deletes the friendship between the users with the given ids
     *
     * @param id1 ID of the first user
     * @param id2 ID of the second user
     * @return the friendship if it was successfully deleted
     * {@code null} if the friendship did not exist
     */
    public Friendship deleteFriendship(Long id1, Long id2) {
        if (id1 > id2) {
            Long aux = id1;
            id1 = id2;
            id2 = aux;
        }

        LLTuple id = new LLTuple(id1, id2);
        return repoFriendships.delete(id);
    }

    /**
     * Deletes all of a users friendships
     *
     * @param id the user ID to delete from
     */
    public void deleteAllFriendships(Long id) {
        Iterable<Friendship> allFriendships = repoFriendships.findAll();
        Stack<Friendship> toDelete = new Stack<>();
        for (Friendship p : allFriendships) {
            if (p.getID().getLeft().equals(id) || p.getID().getRight().equals(id)) {
                toDelete.push(p);
            }
        }
        while (!toDelete.isEmpty()) {
            Friendship p = toDelete.pop();
            repoFriendships.delete(p.getID());
        }
    }

    /**
     * Accepts a friendship between from a user
     *
     * @param user the user accepting
     * @param from the sending user
     * @return {@code null} if the friendship request does not exist or the users are already friends
     * the friendship otherwise
     */
    public Friendship answerFriendshipRequest(Long user, Long from, FriendRequestStatus response) {
        FriendshipRequest request = repoRequests.findOne(new LLTuple(user, from));
        if (request != null) {
            if (user > from) {
                Long aux = user;
                user = from;
                from = aux;
            }

            request.setStatus(response);

            repoRequests.update(request);
            if (response == FriendRequestStatus.ACCEPTED) {
                Friendship friendship = new Friendship(user, from, LocalDate.now());

                if (repoFriendships.save(friendship) == null) {
                    return friendship;
                }
            }
        }
        return null;
    }

    public void cancelFriendshipRequest(Long user, Long to)
    {
        FriendshipRequest request = repoRequests.findOne(new LLTuple(to, user));
        if (request != null) {
            repoRequests.delete(new LLTuple(to, user));
        }
    }

    public FriendshipRequest sendFriendshipRequest(Long user, Long to) {
        Long id1, id2;
        if (user > to) {
            id1 = to;
            id2 = user;
        } else {
            id1 = user;
            id2 = to;
        }

        if (repoRequests.findOne(new LLTuple(to, user)) != null
                || repoRequests.findOne(new LLTuple(user, to)) != null
                || repoFriendships.findOne(new LLTuple(id1, id2)) != null) {
            return null;
        }

        FriendshipRequest friendshipRequest = new FriendshipRequest(user, to);
        repoRequests.save(friendshipRequest);
        return friendshipRequest;
    }

    public Friendship createFriendship(Long user1, Long user2) {
        sendFriendshipRequest(user1, user2);
        return answerFriendshipRequest(user2, user1, FriendRequestStatus.ACCEPTED);
    }

    public List<Long> getUserFriendRequests(Long user) {
        Iterable<FriendshipRequest> requests = repoRequests.findAll();
        List<Long> usersRequesting = new LinkedList<>();
        for (FriendshipRequest request : requests) {
            if (request.getTo().equals(user) && request.getStatus() == FriendRequestStatus.PENDING) {
                usersRequesting.add(request.getFrom());
            }
        }
        return usersRequesting;
    }

    public Iterable<FriendshipRequest> getAllRequests()
    {
        return repoRequests.findAll();
    }

    /**
     * Determines the community with the longest friendship chain
     *
     * @return the IDs of the community members
     */
    public Iterable<Long> mostSociableCommunity() {
        // m^2
        List<List<Friendship>> comunitati = getCommunities();
        List<Friendship> longestChainCommunity = new LinkedList<>();
        int longestChain = 0;

        for (List<Friendship> comunitate : comunitati) {
            Map<Long, List<Long>> map = friendMap(comunitate);
            int chainLeght = communityChainLength(map);
            if (chainLeght > longestChain) {
                longestChainCommunity.addAll(comunitate);
                longestChain = chainLeght;
            }
        }

        return getCommunityMembers(longestChainCommunity);
    }

    /**
     * Determines the number of existing communities
     *
     * @return the number of communities
     */
    public int getCommunitiesNumber() {
        return getCommunities().size();
    }

    /**
     * Determines the members of a community given the community's friendship list
     *
     * @param community the list of friendships that define the community
     * @return the IDs of the community members
     */
    private List<Long> getCommunityMembers(List<Friendship> community) {
        List<Long> communityMembers = new LinkedList<>();

        for (Friendship p : community) {
            Long left = p.getID().getLeft();
            Long right = p.getID().getRight();
            if (!communityMembers.contains(left)) {
                communityMembers.add(left);
            }
            if (!communityMembers.contains(right)) {
                communityMembers.add(right);
            }
        }

        return communityMembers;
    }

    /**
     * Determines the communities (connected sub-graphs) in the network
     *
     * @return a list of communities
     */
    private List<List<Friendship>> getCommunities() {
        List<Friendship> allFriendshipsAsList = new LinkedList<>();
        Iterable<Friendship> allFriendships = repoFriendships.findAll();
        allFriendships.forEach(allFriendshipsAsList::add);

        List<List<Friendship>> comunitati = new LinkedList<>();

        Friendship initialFriendship = allFriendshipsAsList.get(0);
        Stack<Long> toVisit = new Stack<>();
        toVisit.push(initialFriendship.getID().getLeft());
        List<Friendship> community = new LinkedList<>();
        while (!allFriendshipsAsList.isEmpty()) {
            Long current = toVisit.pop();
            for (int i = 0; i < allFriendshipsAsList.size(); i++) {
                Friendship p = allFriendshipsAsList.get(i);
                Long left = p.getID().getLeft();
                Long right = p.getID().getRight();
                if (right.equals(current) || left.equals(current)) {
                    community.add(p);
                    if (!right.equals(current) && !toVisit.contains(right)) {
                        toVisit.push(right);
                    }
                    if (!left.equals(current) && !toVisit.contains(left)) {
                        toVisit.push(left);
                    }
                    allFriendshipsAsList.remove(p);
                    i--;
                }
            }

            if (toVisit.isEmpty()) {
                comunitati.add(community);
                community = new LinkedList<>();
                toVisit.push(allFriendshipsAsList.get(0).getID().getLeft());
            }
            if (allFriendshipsAsList.size() == 0) {
                comunitati.add(community);
            }
        }
        return comunitati;
    }

    /**
     * Determines the longest friendship chain of a community
     *
     * @param map the friendship map (adjacency list) of the community
     * @return the lenght of the longest chain
     */
    private int communityChainLength(Map<Long, List<Long>> map) {
        Set<Long> users = map.keySet();
        int longestChainLength = 0;

        for (Long user : users) {
            Map<Long, List<Long>> copy = copy((HashMap<Long, List<Long>>) map);

            Stack<Long> parents = new Stack<>();
            Stack<Long> currentChain = new Stack<>();

            currentChain.add(user);
            parents.add(null);
            while (!currentChain.isEmpty()) {
                Long current = currentChain.lastElement();
                if (parents.lastElement() != null) {
                    Long parent = parents.lastElement();
                    copy.get(current).remove(parent);
                }

                if (copy.get(current).size() != 0) {
                    parents.add(current);
                    currentChain.add(copy.get(current).remove(0));
                } else {
                    if (currentChain.size() > longestChainLength) {
                        longestChainLength = currentChain.size();
                    }
                    currentChain.pop();
                    parents.pop();
                }
            }
        }

        return longestChainLength;
    }

    /**
     * Deep copies a friendMap
     *
     * @param original original FriendMap
     * @return a deep copy of the FriendMap
     */
    public static Map<Long, List<Long>> copy(HashMap<Long, List<Long>> original) {
        HashMap<Long, List<Long>> copy = new HashMap<>();
        for (Map.Entry<Long, List<Long>> entry : original.entrySet()) {
            copy.put(entry.getKey(), new LinkedList<>(entry.getValue()));
        }
        return copy;
    }

    /**
     * Creates the friendship map (adjacency list) of a community (connected sub graph)
     *
     * @param community the friendships that define the community
     * @return the friendship map
     */
    private Map<Long, List<Long>> friendMap(Iterable<Friendship> community) {
        HashMap<Long, List<Long>> map = new HashMap<>();

        for (Friendship p : community) {
            Long idLeft = p.getID().getLeft();
            Long idRight = p.getID().getRight();

            if (!map.containsKey(idLeft)) {
                List<Long> list = new LinkedList<>();
                map.put(idLeft, list);
            }
            if (!map.containsKey(idRight)) {
                List<Long> list = new LinkedList<>();
                map.put(idRight, list);
            }

            List<Long> listLeft = map.get(idLeft);
            List<Long> listRight = map.get(idRight);

            listLeft.add(idRight);
            listRight.add(idLeft);
        }

        return map;
    }

    /**
     * Gets all friendships
     *
     * @return all stored friendships
     */
    public Iterable<Friendship> getAll() {
        return repoFriendships.findAll();
    }

    public List<FriendshipDTO> getUserFriendList(Long userID) {
        Iterable<Friendship> friendships = repoFriendships.findAll();
        List<FriendshipDTO> friendList = new LinkedList<>();

        friendships.forEach(prietenie ->
        {
            if (prietenie.getID().getRight().equals(userID)) {
                friendList.add(new FriendshipDTO(prietenie.getID().getLeft(), prietenie.getDate()));
            } else if (prietenie.getID().getLeft().equals(userID)) {
                friendList.add(new FriendshipDTO(prietenie.getID().getRight(), prietenie.getDate()));
            }
        });

        return friendList;
    }

    public List<FriendshipDTO> getUserFriendList(Long userID, int year, int month) {
        Predicate<FriendshipDTO> isFromPeriod = friendshipDTO ->
                friendshipDTO.getFriendedDate().getYear() == year
                        && friendshipDTO.getFriendedDate().getMonth().getValue() == month;

        List<FriendshipDTO> friendList = getUserFriendList(userID);

        return friendList.stream().filter(isFromPeriod).collect(Collectors.toList());
    }

    public List<Long> getUserSentRequests(Long id) {
        Iterable<FriendshipRequest> requests = repoRequests.findAll();
        List<Long> usersSent = new LinkedList<>();
        for (FriendshipRequest request : requests) {
            if (request.getFrom().equals(id) && request.getStatus() == FriendRequestStatus.PENDING) {
                usersSent.add(request.getTo());
            }
        }
        return usersSent;
    }
}
