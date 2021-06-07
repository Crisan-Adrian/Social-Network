package domain.validators;

import domain.UserEvent;

public class EventValidator implements Validator<UserEvent> {

    @Override
    public void validate(UserEvent entity) throws ValidationException {
        if(entity.getName().length() < 5)
        {
            throw new ValidationException("Event name too short");
        }
        if(entity.getCreator() == null)
        {
            throw new ValidationException("Event must have a creator");
        }
    }
}
