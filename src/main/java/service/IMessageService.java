package service;

import domain.Message;

import java.time.LocalDateTime;
import java.util.List;

public interface IMessageService {
    //TODO: Add doc

    Message sendMessage(Long from, List<Long> to, String messageText, LocalDateTime timestamp);

    Message sendMessage(Long from, List<Long> to, String messageText, LocalDateTime timestamp, Long repliesTo);

    List<Message> getUserMessages(Long user);

    List<String> getUsersConversations(List<Long> members);

    List<Message> getUsersConversationsM(List<Long> members);

    List<List<Long>> getGroups(Long id);
}
