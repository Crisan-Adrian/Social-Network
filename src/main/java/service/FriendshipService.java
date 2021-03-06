package service;

import domain.*;
import repository.friendship.IFriendRequestRepository;
import repository.friendship.IFriendshipRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class FriendshipService implements IFriendshipService {

    private final IFriendshipRepository repoFriendships;
    private final IFriendRequestRepository repoRequests;

    public FriendshipService(IFriendshipRepository repoFriendships, IFriendRequestRepository repoRequests) {
        this.repoFriendships = repoFriendships;
        this.repoRequests = repoRequests;
    }


    @Override
    public void deleteFriendship(Long id1, Long id2) {
        if (id1 > id2) {
            Long aux = id1;
            id1 = id2;
            id2 = aux;
        }

        LLTuple id = new LLTuple(id1, id2);
        repoFriendships.delete(id);
    }


    @Override
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

    @Override
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

    @Override
    public void cancelFriendshipRequest(Long user, Long to) {
        FriendshipRequest request = repoRequests.findOne(new LLTuple(to, user));
        if (request != null) {
            repoRequests.delete(new LLTuple(to, user));
        }
    }

    @Override
    public void sendFriendshipRequest(Long user, Long to) {
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
            return;
        }

        FriendshipRequest friendshipRequest = new FriendshipRequest(user, to);
        repoRequests.save(friendshipRequest);
    }

    @Override
    public Friendship createFriendship(Long user1, Long user2) {
        sendFriendshipRequest(user1, user2);
        return answerFriendshipRequest(user2, user1, FriendRequestStatus.ACCEPTED);
    }

    @Override
    public List<Long> getUserReceivedRequests(Long user) {
        Iterable<FriendshipRequest> requests = repoRequests.findAll();
        List<Long> usersRequesting = new LinkedList<>();
        for (FriendshipRequest request : requests) {
            if (request.getTo().equals(user) && request.getStatus() == FriendRequestStatus.PENDING) {
                usersRequesting.add(request.getFrom());
            }
        }
        return usersRequesting;
    }

    @Override
    public Iterable<FriendshipRequest> getAllRequests() {
        return repoRequests.findAll();
    }


    @Override
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

    @Override
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
    private static Map<Long, List<Long>> copy(HashMap<Long, List<Long>> original) {
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

    @Override
    public Iterable<Friendship> getAllFriendships() {
        return repoFriendships.findAll();
    }

    @Override
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

    public List<FriendshipDTO> getUserFriendsFromPeriod(Long userID, LocalDate start, LocalDate end) {
        List<Friendship> friendships = repoFriendships.getUserFriendsFromPeriod(userID, start, end);

        return getFriendshipDTOS(userID, friendships);
    }

    @Override
    public List<FriendshipDTO> getUserFriendList(Long userID, int year, int month) {
        List<Friendship> friendList = repoFriendships.getUserFriendsFromPeriod(userID, year, month);

        return getFriendshipDTOS(userID, friendList);
    }

    private List<FriendshipDTO> getFriendshipDTOS(Long userID, List<Friendship> friendships) {
        return friendships.stream().map(friendship -> {
            long friend = getFriend(userID, friendship);

            LocalDate friendedDate = friendship.getDate();

            return new FriendshipDTO(friend, friendedDate);
        }).collect(Collectors.toList());
    }

    private long getFriend(Long userID, Friendship friendship) {
        if (friendship.getID().getLeft().equals(userID)) {
            return friendship.getID().getRight();
        } else {
            return friendship.getID().getLeft();
        }
    }

    @Override
    public List<Long> getUserSentRequests(Long user) {
        Iterable<FriendshipRequest> requests = repoRequests.getUserSentRequests(user);
        List<Long> usersSent = new LinkedList<>();
        for (FriendshipRequest request : requests) {
            if (request.getStatus() == FriendRequestStatus.PENDING) {
                usersSent.add(request.getTo());
            }
        }
        return usersSent;
    }
}
