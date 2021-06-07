package repository.friendship;

import domain.FriendRequestStatus;
import domain.FriendshipRequest;
import domain.LLTuple;
import domain.validators.Validator;
import exceptions.RepoException;
import repository.JDBCUtils;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class FriendshipRequestDB implements IFriendRequestRepository {

    private final JDBCUtils dbUtils;
    private final Validator<FriendshipRequest> validator;

    public FriendshipRequestDB(Validator<FriendshipRequest> validator, Properties properties) {
        dbUtils = new JDBCUtils(properties);
        this.validator = validator;
    }

    @Override
    public FriendshipRequest findOne(LLTuple id) {
        String sql = "SELECT * FROM friend_requests WHERE to_user=? AND from_user=?";

        Connection connection = dbUtils.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id.getLeft());
            statement.setLong(2, id.getRight());
            ResultSet result = statement.executeQuery();
            result.next();
            FriendRequestStatus status = switch (result.getString("status")) {
                case "ACCEPTED" -> FriendRequestStatus.ACCEPTED;
                case "REJECTED" -> FriendRequestStatus.REJECTED;
                case "CANCELED" -> FriendRequestStatus.CANCELED;
                default -> FriendRequestStatus.PENDING;
            };

            return new FriendshipRequest(
                    result.getLong("from_user"),
                    result.getLong("to_user"),
                    status);
        } catch (SQLException throwable) {
            //System.out.println(throwable.getMessage());
            return null;
        }
    }

    @Override
    public Iterable<FriendshipRequest> findAll() {
        List<FriendshipRequest> requests = new LinkedList<>();

        Connection connection = dbUtils.getConnection();

        try {
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT * FROM public.friend_requests ");
            while (resultSet.next()) {
                FriendRequestStatus status = switch (resultSet.getString("status")) {
                    case "ACCEPTED" -> FriendRequestStatus.ACCEPTED;
                    case "REJECTED" -> FriendRequestStatus.REJECTED;
                    case "CANCELED" -> FriendRequestStatus.CANCELED;
                    default -> FriendRequestStatus.PENDING;
                };

                FriendshipRequest request = new FriendshipRequest(
                        resultSet.getLong("from_user"),
                        resultSet.getLong("to_user"),
                        status);
                requests.add(request);
            }

        } catch (SQLException throwable) {
            //System.out.println(throwable.getMessage());
        }
        return requests;
    }

    @Override
    public FriendshipRequest save(FriendshipRequest entity) {
        validator.validate(entity);
        FriendshipRequest request = findOne(entity.getID());
        if(request == null)
        {
            String sql = "INSERT INTO friend_requests(to_user, from_user, status) VALUES(?, ?, ?)";

            Connection connection = dbUtils.getConnection();

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                String requestStatus = switch (entity.getStatus()) {
                    case PENDING -> "PENDING";
                    case ACCEPTED -> "ACCEPTED";
                    case REJECTED -> "REJECTED";
                    case CANCELED -> "CANCELED";
                };

                statement.setLong(1, entity.getTo());
                statement.setLong(2, entity.getFrom());
                statement.setString(3, requestStatus);
                statement.execute();
            } catch (SQLException throwable) {
                //System.out.println(throwable.getMessage());
                throw new RepoException("Request already registered");
            }
        }
        return request;
    }

    @Override
    public FriendshipRequest delete(LLTuple id) {
        FriendshipRequest request = findOne(id);
        if (request != null) {
            String sql = "DELETE FROM friend_requests WHERE to_user=? AND from_user=?";

            Connection connection = dbUtils.getConnection();

            try (PreparedStatement statement = connection.prepareStatement(sql))
            {
                statement.setLong(1, request.getTo());
                statement.setLong(2, request.getFrom());
                statement.execute();
            } catch (SQLException throwable) {
                //System.out.println(throwable.getMessage());
                return null;
            }
        }
        return request;
    }

    @Override
    public FriendshipRequest update(FriendshipRequest entity) {
        validator.validate(entity);
        FriendshipRequest request = findOne(entity.getID());
        if(request != null)
        {
        String sql = "UPDATE friend_requests SET status = ? WHERE to_user=? AND from_user = ?";

            Connection connection = dbUtils.getConnection();

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                String requestStatus = switch (entity.getStatus()) {
                    case PENDING -> "PENDING";
                    case ACCEPTED -> "ACCEPTED";
                    case REJECTED -> "REJECTED";
                    case CANCELED -> "CANCELED";
                };

                statement.setLong(2, entity.getTo());
                statement.setLong(3, entity.getFrom());
                statement.setString(1, requestStatus);
                statement.execute();
                return null;
            } catch (SQLException throwable) {
                //System.out.println(throwable.getMessage());
                return entity;
            }
        }
        return null;
    }

    @Override
    public List<FriendshipRequest> getUserReceivedRequests(Long userID) {
        List<FriendshipRequest> requests = new LinkedList<>();

        String sql = "SELECT * FROM public.friend_requests WHERE to_user = ?";

        try (Connection connection = dbUtils.getConnection()) {

            try (PreparedStatement statement = connection.prepareStatement(sql))
            {
                statement.setLong(1, userID);

                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    FriendRequestStatus status = switch (resultSet.getString("status")) {
                        case "ACCEPTED" -> FriendRequestStatus.ACCEPTED;
                        case "REJECTED" -> FriendRequestStatus.REJECTED;
                        case "CANCELED" -> FriendRequestStatus.CANCELED;
                        default -> FriendRequestStatus.PENDING;
                    };

                    FriendshipRequest request = new FriendshipRequest(
                            resultSet.getLong("from_user"),
                            resultSet.getLong("to_user"),
                            status);
                    requests.add(request);
                }

            } catch (SQLException throwable) {
                throwable.printStackTrace();
                requests = null;
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            requests = null;
        }
        return requests;
    }

    @Override
    public List<FriendshipRequest> getUserSentRequests(Long userID) {
        List<FriendshipRequest> requests = new LinkedList<>();

        String sql = "SELECT * FROM public.friend_requests WHERE from_user = ?";

        try (Connection connection = dbUtils.getConnection()) {

            try (PreparedStatement statement = connection.prepareStatement(sql))
            {
                statement.setLong(1, userID);

                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    FriendRequestStatus status = switch (resultSet.getString("status")) {
                        case "ACCEPTED" -> FriendRequestStatus.ACCEPTED;
                        case "REJECTED" -> FriendRequestStatus.REJECTED;
                        case "CANCELED" -> FriendRequestStatus.CANCELED;
                        default -> FriendRequestStatus.PENDING;
                    };

                    FriendshipRequest request = new FriendshipRequest(
                            resultSet.getLong("from_user"),
                            resultSet.getLong("to_user"),
                            status);
                    requests.add(request);
                }

            } catch (SQLException throwable) {
                throwable.printStackTrace();
                requests = null;
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            requests = null;
        }
        return requests;
    }
}
