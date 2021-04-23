package domain.validators;

import domain.Friendship;

public class FriendshipValidator implements Validator<Friendship> {

    @Override
    public void validate(Friendship entity) throws ValidationException {
        if(entity.getID().getRight().equals(entity.getID().getLeft()))
            throw new ValidationException("Friendship members cannot be the same");
    }
}
