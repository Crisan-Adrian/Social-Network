package domain.validators;

import domain.FriendshipRequest;

public class FriendRequestValidator  implements Validator<FriendshipRequest> {

    @Override
    public void validate(FriendshipRequest entity) throws ValidationException {
        if(entity.getTo().equals(entity.getFrom()))
            throw new ValidationException("Friendship request sender and recipient cannot be the same");
    }
}
