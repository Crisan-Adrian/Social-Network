package domain.validators;

import domain.User;

public class UserValidator implements Validator<User> {

    @Override
    public void validate(User entity) throws ValidationException {
        if (!entity.getFirstName().matches("^[A-Z][a-z]+$"))
            throw new ValidationException("Illegal user name");
        if (!entity.getLastName().matches("^[A-Z][a-z]+$"))
            throw new ValidationException("Illegal user name");
        if(!entity.getEmail().matches("^.{3,}\\@.+\\..+$"))
            throw new ValidationException("Invalid email address");
    }
}
