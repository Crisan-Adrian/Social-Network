package domain.validators;

import domain.Message;

public class MessageValidator implements Validator<Message> {

    @Override
    public void validate(Message entity) throws ValidationException {
        if(entity.getTo().contains(entity.getFrom()))
            throw new ValidationException("Sender cannot be in the receiving user list");
        if(entity.getMessage().length() >= 250)
        {
            throw new ValidationException("Message too long");
        }
        if(entity.getMessage().length() < 1)
        {
            throw new ValidationException("Message too short");
        }
    }
}
