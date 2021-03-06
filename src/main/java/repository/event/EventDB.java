package repository.event;

import domain.UserEvent;
import domain.validators.Validator;
import exceptions.RepoException;
import repository.JDBCUtils;
import repository.PaginationInfo;
import repository.PagingUtils;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EventDB implements IEventRepository {
    private final Validator<UserEvent> validator;

    private final JDBCUtils dbUtils;

    public EventDB(Validator<UserEvent> validator, Properties properties) {
        this.dbUtils = new JDBCUtils(properties);
        this.validator = validator;
    }

    @Override
    public UserEvent findOne(Long id) {
        // Selects event as a join between the event data and the event participants based on event id equality
        String sql = "SELECT E.event_id, ev_name, ev_date, ev_creator, string_agg(cast(user_id AS VARCHAR(100)), ',') as users, string_agg(cast(get_notifications AS VARCHAR(100)), ',') as notifications " +
                "FROM public.events E JOIN public.event_users EU ON E.event_id=EU.event_id WHERE E.event_id=?" +
                "GROUP BY E.event_id, ev_name, ev_date, ev_creator ORDER BY ev_date";

        try (Connection connection = dbUtils.getConnection()) {

            try (PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setLong(1, id);

                ResultSet resultSet = statement.executeQuery();
                resultSet.next();

                String toStringU = resultSet.getString("users");
                List<Long> toListU = Arrays.stream(toStringU.split(",")).map(Long::parseLong).collect(Collectors.toList());
                String toStringN = resultSet.getString("notifications");
                List<Boolean> toListN = Arrays.stream(toStringN.split(",")).map(Boolean::parseBoolean).collect(Collectors.toList());

                Map<Long, Boolean> users = IntStream.range(0, toListU.size()).boxed().collect(Collectors.toMap(toListU::get, toListN::get));

                UserEvent event = new UserEvent(
                        resultSet.getLong("ev_creator"),
                        resultSet.getObject("ev_date", LocalDate.class),
                        resultSet.getString("ev_name"));
                event.setAttending(users);
                event.setID(resultSet.getLong("msg_id"));

                return event;
            } catch (SQLException throwable) {
                //System.out.println(throwable.getMessage());
                return null;
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    @Override
    public Iterable<UserEvent> findAll() {
        List<UserEvent> events = null;

        try (Connection connection = dbUtils.getConnection()) {

            try {
                Statement statement = connection.createStatement();
                // Selects events as a join between the event data and the event participants based on event id equality
                ResultSet resultSet = statement.executeQuery("SELECT E.event_id as id, ev_name, ev_date, ev_creator, COALESCE(string_agg(cast(user_id AS VARCHAR(100)), ','), '') as users, COALESCE(string_agg(cast(get_notifications AS VARCHAR(100)), ','), '') as notifications " +
                        "FROM public.events E LEFT OUTER JOIN public.event_users EU ON E.event_id=EU.event_id " +
                        "GROUP BY E.event_id, ev_name, ev_date, ev_creator ORDER BY ev_date, E.event_id");

                events = processResultSet(resultSet);
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return events;
    }

    @Override
    public List<UserEvent> getPage(PaginationInfo paginationInfo) {
        // Selects events as a join between the event data and the event participants based on event id equality
        String sql = "SELECT E.event_id as id, ev_name, ev_date, ev_creator, COALESCE(string_agg(cast(user_id AS VARCHAR(100)), ','), '') as users, COALESCE(string_agg(cast(get_notifications AS VARCHAR(100)), ','), '') as notifications " +
                "FROM public.events E LEFT OUTER JOIN public.event_users EU ON E.event_id=EU.event_id " +
                "GROUP BY E.event_id, ev_name, ev_date, ev_creator ORDER BY ev_date, E.event_id LIMIT ? OFFSET ?";
        List<UserEvent> events;
        int pageSize = paginationInfo.getPageSize();
        int pageNumber = paginationInfo.getPageNumber();

        if (!PagingUtils.validatePage(pageSize, pageNumber)) {
            throw new RepoException("Invalid pageSize or pageNumber");
        }

        try (Connection connection = dbUtils.getConnection()) {

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, pageSize);
                statement.setInt(2, pageNumber * pageSize);

                ResultSet resultSet = statement.executeQuery();

                events = processResultSet(resultSet);
            } catch (SQLException throwable) {
                System.out.println(throwable.getMessage());
                events = null;
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            events = null;
        }
        return events;
    }

    private List<UserEvent> processResultSet(ResultSet resultSet) throws SQLException {
        List<UserEvent> events = new LinkedList<>();
        while (resultSet.next()) {
            Map<Long, Boolean> users;
            String toStringU = resultSet.getString("users");
            String toStringN = resultSet.getString("notifications");
            if (!toStringU.equals("") && !toStringN.equals("")) {
                // Maps user IDs and notification preferences to list and subsequently to map
                List<Long> toListUsers = Arrays.stream(toStringU.split(",")).map(Long::parseLong).collect(Collectors.toList());
                List<Boolean> toListNotifications = Arrays.stream(toStringN.split(",")).map(Boolean::parseBoolean).collect(Collectors.toList());

                users = new HashMap<>(IntStream.range(0, toListUsers.size()).boxed().collect(Collectors.toMap(toListUsers::get, toListNotifications::get)));
            } else {
                users = new HashMap<>();
            }

            UserEvent event = new UserEvent(
                    resultSet.getLong("ev_creator"),
                    resultSet.getObject("ev_date", LocalDate.class),
                    resultSet.getString("ev_name"));
            event.setID(resultSet.getLong("id"));
            event.setAttending(users);
            events.add(event);

        }

        if (events.size() == 0) {
            events = null;
        }

        return events;
    }

    @Override
    public int getPageCount(PaginationInfo paginationInfo) {
        int pageCount;
        int temp;

        int pageSize = paginationInfo.getPageSize();
        if(pageSize < 1) {
            throw new RepoException("Invalid pageSize or pageNumber");
        }

        try (Connection connection = dbUtils.getConnection()) {
            try {
                Statement statement = connection.createStatement();

                ResultSet resultSet = statement.executeQuery("SELECT COUNT(event_id) as count FROM public.events");
                resultSet.next();
                temp = resultSet.getInt("count");

            } catch (SQLException ignored) {
                return 0;
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return 0;
        }
        pageCount = temp / pageSize;
        if (temp % pageSize != 0) {
            pageCount++;
        }

        return pageCount;
    }

    @Override
    public UserEvent save(UserEvent entity) {
        validator.validate(entity);
        String sql = "INSERT INTO events(ev_name, ev_creator, ev_date) VALUES(?, ?, ?) RETURNING event_id";
        String sqlSetTo = "INSERT INTO event_users(event_id, user_id, get_notifications) VALUES(?, ?, ?)";

        try (Connection connection = dbUtils.getConnection()) {

            try (PreparedStatement statement = connection.prepareStatement(sql);
                 PreparedStatement setTo = connection.prepareStatement(sqlSetTo)) {

                statement.setString(1, entity.getName());
                statement.setLong(2, entity.getCreator());
                statement.setObject(3, entity.getEventDate());
                ResultSet result = statement.executeQuery();
                result.next();
                entity.setID(result.getLong("event_id"));

                for (Map.Entry<Long, Boolean> entry : entity.getAttending().entrySet()) {
                    setTo.setLong(1, entity.getID());
                    setTo.setLong(2, entry.getKey());
                    setTo.setBoolean(3, entry.getValue());
                    setTo.execute();
                }
            } catch (SQLException throwable) {
                //System.out.println(throwable.getMessage());
                throw new RepoException("EventElement BD fail" + throwable.getMessage());
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return entity;
    }

    @Override
    public UserEvent delete(Long id) {
        return null;
    }

    @Override
    public UserEvent update(UserEvent entity) {
        String sqlDelete = "DELETE FROM event_users WHERE event_id=?";
        String sqlInsert = "INSERT INTO event_users (event_id, user_id, get_notifications) VALUES (?, ?, ?)";

        Connection connection = dbUtils.getConnection();

        try (PreparedStatement insert = connection.prepareStatement(sqlInsert);
             PreparedStatement delete = connection.prepareStatement(sqlDelete)
        ) {
            delete.setLong(1, entity.getID());
            delete.execute();
            for (Map.Entry<Long, Boolean> entry : entity.getAttending().entrySet()) {
                insert.setLong(1, entity.getID());
                insert.setLong(2, entry.getKey());
                insert.setBoolean(3, entry.getValue());
                insert.execute();
            }
            return entity;
        } catch (SQLException throwable) {
            //System.out.println(throwable.getMessage());
            return null;
        }
    }

    @Override
    public List<UserEvent> getBetweenDates(LocalDate start, LocalDate end) {
        String sql = "SELECT E.event_id as id, ev_name, ev_date, ev_creator, COALESCE(string_agg(cast(user_id AS VARCHAR(100)), ','), '') as users, COALESCE(string_agg(cast(get_notifications AS VARCHAR(100)), ','), '') as notifications " +
                "FROM public.events E LEFT OUTER JOIN public.event_users EU ON E.event_id=EU.event_id WHERE ev_date >= ? AND ev_date < ? " +
                "GROUP BY E.event_id, ev_name, ev_date, ev_creator ORDER BY ev_date, E.event_id";
        List<UserEvent> events;

        try (Connection connection = dbUtils.getConnection()) {

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setDate(1, Date.valueOf(start));
                statement.setDate(2, Date.valueOf(end));

                ResultSet resultSet = statement.executeQuery();

                events = processResultSet(resultSet);
            } catch (SQLException throwable) {
                System.out.println(throwable.getMessage());
                events = null;
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            events = null;
        }
        return events;
    }

    @Override
    public List<UserEvent> getOnDate(LocalDate date) {
        return getBetweenDates(date, date.plusDays(1));
    }

    @Override
    public List<UserEvent> getUserEvents(long userID) {
        String sql = "SELECT E.event_id as id, ev_name, ev_date, ev_creator, COALESCE(string_agg(cast(user_id AS VARCHAR(100)), ','), '') as users, COALESCE(string_agg(cast(get_notifications AS VARCHAR(100)), ','), '') as notifications " +
                "FROM public.events E LEFT OUTER JOIN public.event_users EU ON E.event_id=EU.event_id WHERE ev_creator = ? " +
                "GROUP BY E.event_id, ev_name, ev_date, ev_creator ORDER BY ev_date, E.event_id";
        List<UserEvent> events;

        try (Connection connection = dbUtils.getConnection()) {

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, userID);

                ResultSet resultSet = statement.executeQuery();

                events = processResultSet(resultSet);
            } catch (SQLException throwable) {
                System.out.println(throwable.getMessage());
                events = null;
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            events = null;
        }
        return events;
    }

    @Override
    public void setSubscription(UserEvent event, long userID, boolean isSubscribed) {
        String sqlSetNotifications = "UPDATE event_users SET get_notifications = ? WHERE event_id = ? AND user_id = ?";

        try (Connection connection = dbUtils.getConnection()) {

            try (PreparedStatement setNotifications = connection.prepareStatement(sqlSetNotifications)) {
                setNotifications.setBoolean(1, isSubscribed);
                setNotifications.setLong(2, event.getID());
                setNotifications.setLong(3, userID);

                setNotifications.executeUpdate();
            } catch (SQLException throwable) {
                //System.out.println(throwable.getMessage());
                throw new RepoException("EventElement BD fail" + throwable.getMessage());
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }
}
