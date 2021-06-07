package service;

import domain.Message;

import java.time.LocalDateTime;
import java.util.List;

public interface IMessageService {

    /**
     * Sends a message
     * @param from the sender's ID
     * @param to the receivers IDs
     * @param messageText -
     * @param timestamp -
     * @return the message
     */
    Message sendMessage(Long from, List<Long> to, String messageText, LocalDateTime timestamp);

    /**
     * Sends a message
     * @param from the sender's ID
     * @param to the receivers IDs
     * @param messageText -
     * @param timestamp -
     * @param repliesTo the message it replies to
     * @return the message
     */
    Message sendMessage(Long from, List<Long> to, String messageText, LocalDateTime timestamp, Long repliesTo);

    /**
     * Gets all messages received by the user
     * @param user the user's ID
     * @return the messages received
     */
    List<Message> getUserMessages(Long user);

    /**
     * Gets the messages of a conversation
     * @param members the conversation member's IDs
     * @return the conversation as a string
     */
    List<String> getUsersConversation(List<Long> members);

    /**
     * Gets the messages of a conversation
     * @param members the conversation member's IDs
     * @return the conversation as a list of messages
     */
    List<Message> getUsersConversationMessages(List<Long> members);

    /**
     * Gets the groups of which the user is part of
     * @param id the user's ID
     * @return the groups
     */
    List<List<Long>> getGroups(Long id);
}
