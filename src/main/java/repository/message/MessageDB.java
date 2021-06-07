package repository.message;

import domain.Message;
import domain.validators.Validator;
import exceptions.RepoException;
import repository.JDBCUtils;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class MessageDB implements IMessageRepository {
    private final JDBCUtils dbUtils;
    private final Validator<Message> validator;

    public MessageDB(Validator<Message> validator, Properties properties) {
        dbUtils = new JDBCUtils(properties);
        this.validator = validator;
    }

    @Override
    public Message findOne(Long id) {
        // Selects messages as a join between the message data and the message receivers based on message id equality
        String sql = "SELECT msg_id, msg_body, msg_time, from_user, string_agg(cast(u_id AS VARCHAR(100)), ',') as to_users, reply " +
                "FROM public.messages JOIN public.message_user ON msg_id=m_id WHERE msg_id=?" +
                "GROUP BY msg_id, msg_body, msg_time, from_user, reply ORDER BY msg_time";

        Connection connection = dbUtils.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();

            String toString = resultSet.getString("to_users");
            List<String> toList = Arrays.asList(toString.split(","));
            List<Long> toUsers = toList.stream().map(Long::parseLong).collect(Collectors.toList());

            Message message = new Message(
                    resultSet.getLong("from_user"),
                    toUsers,
                    resultSet.getString("msg_body"),
                    resultSet.getObject("msg_time", LocalDateTime.class),
                    resultSet.getLong("reply"));
            message.setID(resultSet.getLong("msg_id"));

            return message;
        } catch (SQLException throwable) {
            //System.out.println(throwable.getMessage());
            return null;
        }
    }

    @Override
    public Iterable<Message> findAll() {
        List<Message> messages;

        Connection connection = dbUtils.getConnection();

        try {
            Statement statement = connection.createStatement();

            // Selects messages as a join between the message data and the message receivers based on message id equality
            ResultSet resultSet = statement.executeQuery("SELECT msg_id, msg_body, msg_time, from_user," +
                    " string_agg(cast(u_id AS VARCHAR(100)), ',') as to_users, reply FROM public.messages" +
                    " JOIN public.message_user ON msg_id=m_id GROUP BY msg_id, msg_body, msg_time, from_user, reply");

            messages = ProcessResultSet(resultSet);

        } catch (SQLException throwable) {
            //System.out.println(throwable.getMessage());
            messages = null;
        }
        return messages;
    }

    private List<Message> ProcessResultSet(ResultSet resultSet) throws SQLException {
        List<Message> messages = new ArrayList<>();
        while (resultSet.next()) {

            String toString = resultSet.getString("to_users");
            List<String> toList = Arrays.asList(toString.split(","));
            List<Long> toUsers = toList.stream().map(Long::parseLong).collect(Collectors.toList());

            Message message = new Message(
                    resultSet.getLong("from_user"),
                    toUsers,
                    resultSet.getString("msg_body"),
                    resultSet.getObject("msg_time", LocalDateTime.class),
                    resultSet.getLong("reply"));
            message.setID(resultSet.getLong("msg_id"));
            messages.add(message);

        }
        return messages;
    }

    @Override
    public Message save(Message entity) {
        validator.validate(entity);
        String sql = "INSERT INTO messages(msg_body, from_user, msg_time) VALUES(?, ?, ?) RETURNING msg_id";
        String sqlReply = "INSERT INTO messages(msg_body, from_user, msg_time, reply) VALUES(?, ?, ?, ?) RETURNING msg_id";
        String sqlSetTo = "INSERT INTO message_user(m_id, u_id) VALUES(?, ?)";

        Connection connection = dbUtils.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(sql);
             PreparedStatement statementReply = connection.prepareStatement(sqlReply);
             PreparedStatement setTo = connection.prepareStatement(sqlSetTo)) {

            if (entity.getReply() != null) {
                statementReply.setString(1, entity.getMessage());
                statementReply.setLong(2, entity.getFrom());
                statementReply.setObject(3, entity.getTimestamp());
                statementReply.setLong(4, entity.getReply());
                ResultSet result = statementReply.executeQuery();
                result.next();
                entity.setID(result.getLong("msg_id"));
            } else {
                statement.setString(1, entity.getMessage());
                statement.setLong(2, entity.getFrom());
                statement.setObject(3, entity.getTimestamp());
                ResultSet result = statement.executeQuery();
                result.next();
                entity.setID(result.getLong("msg_id"));
            }

            for (Long to : entity.getTo()) {
                setTo.setLong(1, entity.getID());
                setTo.setLong(2, to);
                setTo.execute();
            }
        } catch (SQLException throwable) {
            //System.out.println(throwable.getMessage());
            throw new RepoException("Message BD fail");
        }
        return entity;
    }

    @Override
    public Message delete(Long aLong) {
        return null;
    }

    @Override
    public Message update(Message entity) {
        return null;
    }

    @Override
    public List<Message> getUserMessages(Long userID) {
        List<Message> messages = new LinkedList<>();

        // Selects messages that have been addressed to the user
        // as a join between the message data and the message receivers based on message id equality
        String sql = "SELECT msg_id, msg_body, msg_time, from_user," +
                " string_agg(cast(u_id AS VARCHAR(100)), ',') as to_users, reply FROM public.messages" +
                " JOIN public.message_user ON msg_id=m_id WHERE EXISTS (SELECT m_id FROM public.message_user WHERE u_id = ?)" +
                " GROUP BY msg_id, msg_body, msg_time, from_user, reply";

        try (Connection connection = dbUtils.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, userID);

                ResultSet resultSet = statement.executeQuery();

                ProcessResultSet(resultSet);
            } catch (SQLException throwable) {
                throwable.printStackTrace();
                messages = null;
            }

        } catch (SQLException throwable) {
            throwable.printStackTrace();
            messages = null;
        }
        return messages;
    }
}
