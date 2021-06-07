package repository.friendship;

import domain.Friendship;
import domain.LLTuple;
import domain.validators.Validator;
import exceptions.RepoException;
import repository.JDBCUtils;

import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class FriendshipDB implements IFriendshipRepository {

    private final JDBCUtils dbUtils;
    private final Validator<Friendship> validator;

    public FriendshipDB(Validator<Friendship> validator, Properties properties) {
        dbUtils = new JDBCUtils(properties);
        this.validator = validator;
    }

    @Override
    public Friendship findOne(LLTuple longLongTuple) {
        String sql = "SELECT * FROM friendships WHERE u_id_1=? AND u_id_2=?";

        Connection connection = dbUtils.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, longLongTuple.getLeft());
            statement.setLong(2, longLongTuple.getRight());
            ResultSet result = statement.executeQuery();
            result.next();

            return new Friendship(
                    result.getLong("u_id_1"),
                    result.getLong("u_id_2"),
                    result.getDate("friendDate").toLocalDate());

        } catch (SQLException throwable) {
            //System.out.println(throwable.getMessage());
            return null;
        }
    }

    @Override
    public Iterable<Friendship> findAll() {
        List<Friendship> friendships = new LinkedList<>();

        Connection connection = dbUtils.getConnection();

        try {
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT * FROM public.friendships ");
            while (resultSet.next()) {
                Friendship friendship = new Friendship(
                        resultSet.getLong("u_id_1"),
                        resultSet.getLong("u_id_2"),
                        resultSet.getDate("friendDate").toLocalDate());
                friendships.add(friendship);
            }

        } catch (SQLException throwable) {
            //System.out.println(throwable.getMessage());
        }
        return friendships;
    }

    @Override
    public Friendship save(Friendship entity) {
        validator.validate(entity);
        Friendship friendship = findOne(entity.getID());
        if (friendship == null) {
            String sql = "INSERT INTO friendships(u_id_1, u_id_2, friendDate) VALUES(?, ?, ?)";

            Connection connection = dbUtils.getConnection();

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, entity.getID().getLeft());
                statement.setLong(2, entity.getID().getRight());
                statement.setDate(3, Date.valueOf(entity.getDate()));
                statement.execute();
            } catch (SQLException throwable) {
                //System.out.println(throwable.getMessage());
                throw new RepoException("User ID already registered");
            }
        }
        return friendship;
    }

    @Override
    public Friendship delete(LLTuple id) {
        Friendship friendship = findOne(id);
        if (friendship != null) {
            String sql = "DELETE FROM friendships WHERE u_id_1=? AND u_id_2=?";

            Connection connection = dbUtils.getConnection();

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, id.getLeft());
                statement.setLong(2, id.getRight());
                statement.execute();
            } catch (SQLException throwable) {
                //System.out.println(throwable.getMessage());
                return null;
            }
        }
        return friendship;
    }

    @Override
    public Friendship update(Friendship entity) {
        return null;
    }

    @Override
    public List<Friendship> getUserFriends(Long userID) {
        List<Friendship> friendships = new LinkedList<>();

        String sql = "SELECT * FROM public.friendships WHERE u_id_1 = ? OR u_id_2 = ?";

        try (Connection connection = dbUtils.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, userID);
                statement.setLong(2, userID);

                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    Friendship friendship = new Friendship(
                            resultSet.getLong("u_id_1"),
                            resultSet.getLong("u_id_2"),
                            resultSet.getDate("friendDate").toLocalDate());
                    friendships.add(friendship);
                }

            } catch (SQLException throwable) {
                throwable.printStackTrace();
                friendships = null;
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            friendships = null;
        }
        return friendships;
    }

    @Override
    public List<Friendship> getUserFriendsFromPeriod(Long userID, LocalDate start, LocalDate end) {
        List<Friendship> friendships = new LinkedList<>();

        String sql = "SELECT * FROM public.friendships WHERE u_id_1 = ? OR u_id_2 = ? AND frienddate >= ? AND frienddate < ?";

        try (Connection connection = dbUtils.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, userID);
                statement.setLong(2, userID);
                statement.setDate(3, Date.valueOf(start));
                statement.setDate(4, Date.valueOf(end));


                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    Friendship friendship = new Friendship(
                            resultSet.getLong("u_id_1"),
                            resultSet.getLong("u_id_2"),
                            resultSet.getDate("friendDate").toLocalDate());
                    friendships.add(friendship);
                }

            } catch (SQLException throwable) {
                throwable.printStackTrace();
                friendships = null;
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            friendships = null;
        }
        return friendships;
    }

    @Override
    public List<Friendship> getUserFriendsFromPeriod(Long userID, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = LocalDate.of(year, month, start.lengthOfMonth());
        return getUserFriendsFromPeriod(userID, start, end);
    }
}
