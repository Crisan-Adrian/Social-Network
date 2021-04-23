package domain;

import java.time.LocalDate;
import java.util.Objects;

public class Friendship extends Entity<LLTuple> {

    private LocalDate date;
    private Long u1, u2;

    public Friendship(Long u1, Long u2, LocalDate date) {
        this.u1 = u1;
        this.u2 = u2;
        setID(new LLTuple(u1, u2));
        this.date = date;
    }

    public Friendship(Long u1, Long u2) {
        this.u1 = u1;
        this.u2 = u2;
        setID(new LLTuple(u1, u2));
    }

    /**
     * @return the date when the friendship was created
     */
    public LocalDate getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friendship that = (Friendship) o;
        return u1.equals(that.u1) &&
                u2.equals(that.u2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(u1, u2);
    }
}
