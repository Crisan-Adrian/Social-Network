package domain;

import java.time.LocalDate;

public class FriendshipDTO {
    Long friend;
    LocalDate friendedDate;

    public FriendshipDTO(Long friend, LocalDate friendedDate) {
        this.friend = friend;
        this.friendedDate = friendedDate;
    }

    public Long getFriend() {
        return friend;
    }

    public void setFriend(Long friend) {
        this.friend = friend;
    }

    public LocalDate getFriendedDate() {
        return friendedDate;
    }

    public void setFriendedDate(LocalDate friendedDate) {
        this.friendedDate = friendedDate;
    }
}
