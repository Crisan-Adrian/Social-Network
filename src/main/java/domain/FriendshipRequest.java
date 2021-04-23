package domain;

import java.util.Objects;

public class FriendshipRequest extends Entity<LLTuple> {
    Long from;
    Long to;
    FriendRequestStatus status;

    public FriendshipRequest(Long from, Long to, FriendRequestStatus status) {
        this.status = status;
        this.from = from;
        this.to = to;
        super.setID(new LLTuple(to, from));
    }

    public FriendshipRequest(Long from, Long to) {
        this.from = from;
        this.to = to;
        this.status = FriendRequestStatus.PENDING;
        super.setID(new LLTuple(to, from));
    }

    public FriendRequestStatus getStatus() {
        return status;
    }

    public void setStatus(FriendRequestStatus status) {
        this.status = status;
    }

    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from;
    }

    public Long getTo() {
        return to;
    }

    public void setTo(Long to) {
        this.to = to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendshipRequest request = (FriendshipRequest) o;
        return from.equals(request.from) &&
                to.equals(request.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }
}
