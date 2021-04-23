package domain.validators;

import domain.UserEvent;

public class EventValidator implements Validator<UserEvent> {
    //TODO: Refactor validation criterion

    @Override
    public void validate(UserEvent entity) throws ValidationException {
        if(entity.getName().length() < 5)
        {
            throw new ValidationException("Event name too short");
        }
    }
}
