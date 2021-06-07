package repository.message;

import domain.Message;
import repository.IRepository;

import java.util.List;

public interface IMessageRepository extends IRepository<Long, Message> {

    /**
     * Gets all messages that have the user in the receiving list
     * @param userID the user's ID
     * @return the list of messages or {@code null} if an error occurred
     */
    List<Message> getUserMessages(Long userID);

}
