package controller;

import domain.*;
import domain.validators.ValidationException;
import exceptions.InvalidCommand;
import service.FriendshipService;
import service.MessageService;
import service.UserService;
import ui.ConsoleUI;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Long.parseLong;

/**
 * Controller class that mediates between UI and services
 */
public class Controller {

    //TODO: Comment code where necessary. Document functions.

    UserService userService;
    FriendshipService friendshipService;
    MessageService messageService;
    User currentUser;

    public Controller(UserService userService, FriendshipService friendshipService, MessageService messageService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
    }

    /**
     * @param input the input words to interpret into commands.
     *              input[0] should be the command name followed
     *              by the command arguments.
     * @return the result of the given command.
     * @throws InvalidCommand if the given command is unknown or if the
     *                        given arguments do not fit the given command.
     */
    public String processCommand(List<String> input, ConsoleUI ui) {

        if (currentUser.getFirstName().equals("admin")) {
            return processAdminCommand(input, ui);
        } else {
            return processUserCommand(input, ui);
        }

    }

    ///TODO: Documentation
    private String processUserCommand(List<String> input, ConsoleUI ui) {
        String returnString;

        String command = input.remove(0);

        input.add(0, currentUser.getID().toString());

        switch (command) {
            case "delete":
                if (input.size() != 1)
                    throw new InvalidCommand("Wrong use of 'delete' command\n Correct use: delete");
                returnString = deleteUser(input);
                break;
            case "friend":
                if (input.size() != 2)
                    throw new InvalidCommand("Wrong use of 'friend' command\n Correct use: friend [id]");
                returnString = addFriendshipRequest(input);
                break;
            case "unfriend":
                if (input.size() != 2)
                    throw new InvalidCommand("Wrong use of 'unfriend' command\n Correct use: unfriend [id]");
                returnString = deleteFriendship(input);
                break;
            case "cancelRequest":
                returnString = "WIP";
                break;
            case "friendList":
                if (input.size() == 1) {
                    returnString = friendList(input);
                } else if (input.size() == 3) {

                    returnString = friendListFromPeriod(input);
                } else {
                    throw new InvalidCommand("Wrong use of 'friendList' command\n Correct use: 'friendList' or 'friendList [month] [year]'");
                }
                break;
            case "ar":
            case "answerRequest":
                if (input.size() != 3) {
                    throw new InvalidCommand("Wrong use of 'answerRequest' command\n Correct use: 'answerRequest [id] [response]'");
                }
                returnString = answerFriendshipRequest(input);
                break;
            case"gr":
            case "getRequests":
                if(input.size() != 1)
                {
                    throw new InvalidCommand("Wrong use of 'getRequests' command\n Correct use: 'getRequests'");
                }
                returnString = getUserFriendRequests(input);
                break;
            case "message":
                if(!(input.size()>1))
                {
                    throw new InvalidCommand("Wrong use of 'message' command\n Correct use: 'message [email] [email] ...'");
                }
                returnString = sendMessage(input, ui);
                break;
            case "gc":
            case "getConversations":
                if(!(input.size()>1))
                {
                    throw new InvalidCommand("Wrong use of 'getConversations' command\n Correct use: 'getConversation [email] [email] ...'");
                }
                returnString = getConversation(input);
                break;
            case "r":
            case "reply":
                if(!(input.size()>1))
                {
                    throw new InvalidCommand("Wrong use of 'reply' command\n Correct use: 'reply [email] [email] ...'");
                }
                returnString = replyToConversation(input, ui);
                break;
            default:
                throw new InvalidCommand("Unknown command");
        }
        return returnString;
    }

    private String replyToConversation(List<String> input, ConsoleUI ui) {
        Long userID;
        List<Long> members = new LinkedList<>();
        try {
            userID = parseLong(input.remove(0));
        } catch (NumberFormatException exception) {
            return "User ID must be a number";
        }
        for(String email : input)
        {
            Long ID = userService.findUserEmail(email).getID();
            if(ID != null)
                if(!members.contains(ID))
                    members.add(ID);
                else
                    throw new InvalidCommand("An user can't be a member of a conversation twice");
            else
            {
                throw new InvalidCommand("One of the conversation members doesn't exist");
            }
        }
        User user = userService.getOne(userID);
        if (user == null) {
            return "Utilizatorul nu exista";
        } else {
            input.add(0, userID.toString());
            ui.display(getConversation(input));

            String messageIDString = ui.getInput("Message ID to reply to:");
            Long messageID;
            try {
                messageID = parseLong(messageIDString);

                String replyBody = ui.getInput("Reply:");
                try {
                    messageService.sendMessage(userID, members, replyBody, LocalDateTime.now(), messageID);

                    return "You replied";
                }
                catch (ValidationException exception)
                {
                    return exception.getLocalizedMessage();
                }
            } catch (NumberFormatException exception) {
                return "User ID must be a number";
            }
        }
    }

    private String getConversation(List<String> input) {
        Long userID;
        List<Long> members = new LinkedList<>();
        try {
            userID = parseLong(input.remove(0));
        } catch (NumberFormatException exception) {
            return "User ID must be a number";
        }
        for(String email : input)
        {
            Long ID = userService.findUserEmail(email).getID();
            if(ID != null)
                if(!members.contains(ID))
                    members.add(ID);
                else
                    throw new InvalidCommand("An user can't be a member of a conversation twice");
            else
            {
                throw new InvalidCommand("One of the conversation members doesn't exist");
            }
        }
        User user = userService.getOne(userID);
        if (user == null) {
            return "Utilizatorul nu exista";
        } else {
            members.add(userID);
            List<String> conversationLines = messageService.getUsersConversations(members);
            StringBuilder conversation = new StringBuilder();
            if(conversationLines.size() > 0)
            {
                conversation.append("Members: ").append(user.getEmail());
                for(String email: input)
                {
                    conversation.append(", ").append(email);
                }
                conversation.append("\n" +
                        "Conversation:\n");
                for(String line : conversationLines)
                {
                    conversation.append(line).append("\n");
                }
                return conversation.toString();
            }
            else
            {
                return "No conversation exists";
            }
        }
    }

    private String processAdminCommand(List<String> input, ConsoleUI ui) {
        String returnString;

        String command = input.remove(0);

        switch (command) {
            case "add":
                if (input.size() != 4)
                    throw new InvalidCommand("Wrong use of 'add' command\n Correct use: add [id] [firstName] [lastName] [email]");
                returnString = addUser(input);
                break;
            case "delete":
                if (input.size() != 1)
                    throw new InvalidCommand("Wrong use of 'delete' command\n Correct use: delete [id]");
                returnString = deleteUser(input);
                break;
            case "friend":
                if (input.size() != 2)
                    throw new InvalidCommand("Wrong use of 'friend' command\n Correct use: friend [id1] [id2]");
                returnString = addFriendship(input);
                break;
            case "unfriend":
                if (input.size() != 2)
                    throw new InvalidCommand("Wrong use of 'unfriend' command\n Correct use: unfriend [id1] [id2]");
                returnString = deleteFriendship(input);
                break;
            case "communitiesNumber":
                returnString = comunitiesNumber();
                break;
            case "mostSociable":
                returnString = mostSociable();
                break;
            case "friendList":
                if (input.size() == 1) {
                    returnString = friendList(input);
                } else if (input.size() == 3) {

                    returnString = friendListFromPeriod(input);
                } else {
                    throw new InvalidCommand("Wrong use of 'friendList' command\n Correct use: 'friendList [id]' or 'friendList [id] [month] [year]'");
                }
                break;
            case "getRequests":
                if(input.size() != 1)
                {
                    throw new InvalidCommand("Wrong use of 'getRequests' command\n Correct use: 'getRequests [id]'");
                }
                returnString = getUserFriendRequests(input);
                break;
            case "answerRequest":
                if (input.size() != 3) {
                    throw new InvalidCommand("Wrong use of 'answerRequest' command\n Correct use: 'answerRequest [id1] [id2] [response]'");
                }
                returnString = answerFriendshipRequest(input);
                break;
            case "gc":
            case "getConversations":
                if(!(input.size()>2))
                {
                    throw new InvalidCommand("Wrong use of 'getConversations' command\n Correct use: 'getConversation [id] [email] [email] ...'");
                }
                returnString = getConversation(input);
                break;
            default:
                throw new InvalidCommand("Unknown command");
        }
        return returnString;
    }

    private String sendMessage(List<String> input, ConsoleUI ui) {
        Long from;
        List<Long> to = new LinkedList<>();
        String messageText = ui.getInput("Enter message: ");
        try {
            from = parseLong(input.remove(0));
        } catch (NumberFormatException exception) {
            return "User ID must be a number";
        }
        for(String email: input)
        {
            Long ID = userService.findUserEmail(email).getID();
            if(ID != null)
                if(!to.contains(ID))
                    to.add(ID);
                else
                    throw new InvalidCommand("You can't send the message to the same user twice");
            else
            {
                throw new InvalidCommand("One of the message receivers doesn't exist");
            }
        }
        User user = userService.getOne(from);
        if (user == null) {
            return "Utilizatorul nu exista";
        } else {
            LocalDateTime timestamp = LocalDateTime.now();
            try {

                Message message = messageService.sendMessage(from, to, messageText, timestamp);
                if(message == null)
                    return "Message not sent";
                else
                    return "Message sent";
            }
            catch (ValidationException exception)
            {
                return exception.getLocalizedMessage();
            }
        }
    }

    /**
     * Returns a users friendlist
     *
     * @param input list of inputs; id-the given user ID
     * @return the formatted friend list
     */
    private String friendList(List<String> input) {
        Long id;
        try {
            id = Long.parseLong(input.get(0));
        } catch (NumberFormatException exception) {
            return "User ID must be a number";
        }
        List<FriendshipDTO> friendsDTO = friendshipService.getUserFriendList(id);
        return friendsDTO.stream().map(friendID -> {
            User u = userService.getOne(friendID.getFriend());
            return u.getFirstName() + " | " + u.getLastName() + " | " + friendID.getFriendedDate();
        }).collect(Collectors.joining("\n"));
    }

    /**
     * Returns a users friendlist
     *
     * @param input list of inputs; id-the given user ID, month(int), year(int)
     * @return the formatted friend list
     */
    private String friendListFromPeriod(List<String> input) {
        Long id;
        int year;
        int month;
        try {
            id = Long.parseLong(input.get(0));
            month = Integer.parseInt(input.get(1));
            year = Integer.parseInt(input.get(2));
        } catch (NumberFormatException exception) {
            return "User ID must be a number";
        }
        List<FriendshipDTO> friendsDTO = friendshipService.getUserFriendList(id, year, month);
        return friendsDTO.stream().map(friendID -> {
            User u = userService.getOne(friendID.getFriend());
            return u.getFirstName() + " | " + u.getLastName() + " | " + friendID.getFriendedDate();
        }).collect(Collectors.joining("\n"));
    }

    /**
     * Determines the community with the largest friendship chain
     *
     * @return the String containing the names of the members
     */
    private String mostSociable() {
        Iterable<Long> ids = friendshipService.mostSociableCommunity();
        Iterable<String> names = userService.getUserNames(ids);

        StringBuilder community = new StringBuilder("Cea mai sociabila comunitate:\n");
        for (String name : names) {
            community.append(name).append("\n");
        }

        return community.toString();
    }

    /**
     * Determines the number of communities. A community is a group
     * of at least 2 users who form a connected subgraph.
     *
     * @return the String containing the number of communities
     */
    private String comunitiesNumber() {
        int number = friendshipService.getCommunitiesNumber();
        return "Number of comunities: " + number;
    }

    /**
     * Adds a user to the network
     *
     * @param userData the list of strings that define the user,
     *                 id-Long firstName-string lastName-string email-string
     * @return "Account created" if the user was added
     * "User already registered" if the user id or email was already registered
     */
    private String addUser(List<String> userData) {
        Long id;
        try {
            id = parseLong(userData.get(0));
        } catch (NumberFormatException exception) {
            return "User ID must be a number";
        }
        String firstName = userData.get(1);
        String lastName = userData.get(2);
        String email = userData.get(3);
        User user = new User(firstName, lastName, email);
        user.setID(id);
        try {

            user = userService.addUtilizator(user);
            if (user == null && userService.findUserEmail(email) == null) {
                return "Account created";
            } else {
                return "User already registered";
            }
        } catch (ValidationException exception) {
            return exception.getLocalizedMessage();
        }
    }

    /**
     * Removes a user to the network and deletes all his friendships
     *
     * @param userData the list of strings that define the user,
     *                 id-Long
     * @return "Stergere efectuata" if the user was deleted,
     * "Utilizatorul nu exista" if the user id was not registered
     */
    private String deleteUser(List<String> userData) {
        Long id;
        try {
            id = parseLong(userData.get(0));
        } catch (NumberFormatException exception) {
            return "User ID must be a number";
        }
        User user = userService.deleteUtilizator(id);
        if (user == null) {
            return "Utilizatorul nu exista";
        } else {
            friendshipService.deleteAllFriendships(id);
            return "Stergere efectuata";
        }
    }

    /**
     * Creates a friendship between 2 existing users
     *
     * @param userIDs strings containg the 2 users' ids
     * @return "Prietenia a fost adaugata" if the friendship was added
     * "Utilizatorii erau deja prieteni" if the users were already friends
     * @throws InvalidCommand if at least one of the user ids were not registered
     */
    private String addFriendship(List<String> userIDs) {
        Long user1, user2;
        try {
            user1 = parseLong(userIDs.get(0));
            user2 = parseLong(userIDs.get(1));
        } catch (NumberFormatException exception) {
            throw new InvalidCommand("User ids must be integers");
        }

        User u1 = userService.getOne(user1);
        User u2 = userService.getOne(user2);
        if (u1 == null || u2 == null) {
            throw new InvalidCommand("Not all given user ids are registered");
        }

        try {
            Friendship request = friendshipService.createFriendship(user1, user2);

            if (request != null) {
                return "Prieteniea a fost creata";
            } else {
                return "Utilizatorii sunt deja prieteni";
            }
        } catch (ValidationException exception) {

            return exception.getLocalizedMessage();
        }
    }

    private String addFriendshipRequest(List<String> userIDs) {
        Long user, to;
        try {
            user = parseLong(userIDs.get(0));
            to = parseLong(userIDs.get(1));
        } catch (NumberFormatException exception) {
            throw new InvalidCommand("User ids must be integers");
        }

        User u1 = userService.getOne(user);
        User u2 = userService.getOne(to);
        if (u1 == null || u2 == null) {
            throw new InvalidCommand("Not all given user ids are registered");
        }

        try {
            FriendshipRequest request = friendshipService.sendFriendshipRequest(user, to);

            if (request != null) {
                return "Crerea de prietenie a fost trimisa";
            } else {
                return "Utilizatorii sunt deja prieteni sau o cerere exista deja";
            }
        } catch (ValidationException exception) {
            return exception.getLocalizedMessage();
        }
    }

    private String answerFriendshipRequest(List<String> input) {
        Long user, to;
        try {
            user = parseLong(input.get(0));
            to = parseLong(input.get(1));
        } catch (NumberFormatException exception) {
            throw new InvalidCommand("User ids must be integers");
        }

        String responseString = input.get(2);
        FriendRequestStatus response;
        switch (responseString) {
            case "accept":
                response = FriendRequestStatus.ACCEPTED;
                break;
            case "reject":
                response = FriendRequestStatus.REJECTED;
                break;
            default:
                throw new InvalidCommand("Invalid response, choose either accept or reject");
        }

        User u1 = userService.getOne(user);
        User u2 = userService.getOne(to);
        if (u1 == null || u2 == null) {
            throw new InvalidCommand("Not all given user ids are registered");
        }

        Friendship friendship = friendshipService.answerFriendshipRequest(user, to, response);

        if (friendship != null) {
            return "Prieteniea a fost creata";
        } else {
            return "Ai raspuns cererii";
        }
    }

    private String getUserFriendRequests(List<String> input) {
        Long userID;
        try {
            userID = parseLong(input.get(0));
        } catch (NumberFormatException exception) {
            throw new InvalidCommand("User ids must be integers");
        }

        User u = userService.getOne(userID);
        if (u == null) {
            throw new InvalidCommand("Not all given user ids are registered");
        }

        List<Long> userIDs = friendshipService.getUserFriendRequests(userID);

        if (userIDs.size() == 0) {
            return "User doesn't have any friend requests";
        } else {
            List<User> users = userService.getUsers(userIDs);

            StringBuilder returnString = new StringBuilder();
            for (User user : users) {
                returnString.append("ID:").append(user.getID()).append(", ").append(user.getFirstName()).append(" ")
                        .append(user.getLastName()).append(", email: ").append(user.getEmail()).append("\n");
            }
            return returnString.toString();
        }
    }

    /**
     * Removes the friendship between the given users
     *
     * @param userIDs strings containg the 2 users' ids
     * @return "Utilizatorii nu erau prieteni" if the friendship did not exist
     * "Prietenia a fost stearsa" if the friendship was removed
     * @throws InvalidCommand if at least one of the user ids were not registered
     */
    private String deleteFriendship(List<String> userIDs) {
        Long id1, id2;
        try {
            id1 = parseLong(userIDs.get(0));
            id2 = parseLong(userIDs.get(1));
        } catch (NumberFormatException exception) {
            throw new InvalidCommand("User ids must be integers");
        }

        User u1 = userService.getOne(id1);
        User u2 = userService.getOne(id2);
        if (u1 == null || u2 == null) {
            throw new InvalidCommand("Not all given user ids are registered");
        }

        Friendship friendship = friendshipService.deleteFriendship(id1, id2);

        if (friendship == null) {
            return "Utilizatorii nu erau prieteni";
        } else {
            return "Prietenia a fost stearsa";
        }
    }

    /**
     * Logs a user into his account or logs in as superuser
     *
     * @param inputWords list of input; 0-user email or 'admin' for superuser account
     * @return login action result: true if login was successful, false if it wasn't
     */
    public boolean processLogin(List<String> inputWords) {
        if (inputWords.get(0).equalsIgnoreCase("admin")) {
            currentUser = new User("admin");
            return true;
        } else {
            User user = userService.findUserEmail(inputWords.get(0));
            if (user != null) {
                currentUser = user;
            }
            return user != null;
        }
    }

    public String getCurrentUser() {
        return currentUser.getFirstName();
    }

    /**
     * Creates a user account
     *
     * @param input the list of strings that define the user,
     *              id-Long firstName-string lastName-string email-string
     * @return "Account created" if the user was added
     * "User already registered" if the user id or email was already registered
     * "User ID must be a number" if an invalid user ID was given
     */
    public String processSignUp(List<String> input) {
        if (input.size() != 4)
            throw new InvalidCommand("Wrong use of 'add' command\n Correct use: add [id] [firstName] [lastName] [email]");
        else {
            Long id;
            try {
                id = parseLong(input.get(0));
            } catch (NumberFormatException exception) {
                return "User ID must be a number";
            }
            String firstName = input.get(1);
            String lastName = input.get(2);
            String email = input.get(3);
            User user = new User(firstName, lastName, email);
            user.setID(id);
            try {
                user = userService.addUtilizator(user);
                if (user == null && userService.findUserEmail(email) == null) {
                    return "Account created";
                } else {
                    return "User already registered";
                }
            } catch (ValidationException exception) {
                return exception.getLocalizedMessage();
            }
        }
    }
}
