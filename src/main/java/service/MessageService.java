package service;

import domain.Message;
import repository.message.IMessageRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class MessageService implements IMessageService {

    IMessageRepository repoMessage;

    public MessageService(IMessageRepository repoMessage) {
        this.repoMessage = repoMessage;
    }

    @Override
    public Message sendMessage(Long from, List<Long> to, String messageText, LocalDateTime timestamp) {
        return sendMessage(from, to, messageText, timestamp, null);
    }

    @Override
    public Message sendMessage(Long from, List<Long> to, String messageText, LocalDateTime timestamp, Long repliesTo) {
        Message message = new Message(from, to, messageText, timestamp, repliesTo);

        return repoMessage.save(message);
    }

    @Override
    public List<Message> getUserMessages(Long user) {
        return repoMessage.getUserMessages(user);
    }

    @Override
    public List<String> getUsersConversation(List<Long> members) {
        List<Message> messages = (List<Message>) repoMessage.findAll();
        List<String> conversation = new LinkedList<>();

        class SortByTime implements Comparator<Message> {
            public int compare(Message a, Message b) {
                return a.getTimestamp().compareTo(b.getTimestamp());
            }
        }

        messages.sort(new SortByTime());

        for (Message message : messages) {
            if (isSameConversation(message, members)) {
                conversation.add(/*message.getId() + " - " + */message.getFrom() + " - " + message.getMessage() + " - "
                        + DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).format(message.getTimestamp()));
            }
        }

        return conversation;
    }

    private boolean isSameConversation(Message message, List<Long> members) {
        List<Long> messageMembers = new LinkedList<>();
        messageMembers.add(message.getFrom());
        messageMembers.addAll(message.getTo());

        boolean sameConv = true;
        for (Long member : members) {
            if (!messageMembers.contains(member)) {
                sameConv = false;
                break;
            }
        }
        if (members.size() != messageMembers.size())
            sameConv = false;
        return sameConv;
    }

    @Override
    public List<Message> getUsersConversationMessages(List<Long> members) {
        List<Message> messages = (List<Message>) repoMessage.findAll();
        List<Message> conversation = new LinkedList<>();

        class SortByTime implements Comparator<Message> {
            public int compare(Message a, Message b) {
                return a.getTimestamp().compareTo(b.getTimestamp());
            }
        }

        messages.sort(new SortByTime());

        for (Message message : messages) {
            if (isSameConversation(message, members)) {
                conversation.add(message);
            }
        }

        return conversation;
    }

    @Override
    public List<List<Long>> getGroups(Long id) {
        List<List<Long>> groups = new LinkedList<>();
        List<Message> messages = (List<Message>) repoMessage.findAll();

        for (Message message : messages) {
            List<Long> members = message.getTo();
            members.add(message.getFrom());
            if(!members.contains(id)) {
                continue;
            }
            if (!foundGroup(groups, members) && members.size() > 2) {
                groups.add(members);
            }
        }

        return groups;
    }

    private boolean foundGroup(List<List<Long>> groups, List<Long> members) {
        boolean foundGroup = false;
        for (List<Long> group : groups) {
            if (members.size() == group.size() && group.containsAll(members)) {
                foundGroup = true;
            }
        }
        return foundGroup;
    }
}
