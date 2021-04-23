package domain.validators;

import domain.User;

public class UserValidator implements Validator<User> {
    //TODO: Refactor exception messages

    @Override
    public void validate(User entity) throws ValidationException {
        if (!entity.getFirstName().matches("^[A-Z][a-z]+$"))
            throw new ValidationException("Illegal user name");
        if (!entity.getLastName().matches("^[A-Z][a-z]+$"))
            throw new ValidationException("Illegal user name");
    }
}
