package repository.message;

import domain.Message;
import repository.IRepository;

import java.util.List;

public interface IMessageRepository extends IRepository<Long, Message> {

    List<Message> getUserMessages(Long userID);

}
