package repository.user;

import domain.User;
import domain.validators.Validator;
import repository.JDBCUtils;
import repository.PaginationInfo;

import java.sql.*;
import java.util.Dictionary;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class UserDB implements IUserRepository {

    //TODO: Comment code where necessary. Document functions. Refactor if needed
    private final JDBCUtils dbUtils;

    private final Validator<User> validator;

    public UserDB(Validator<User> validator, Properties properties, int pageSize) {
        dbUtils = new JDBCUtils(properties);
        this.validator = validator;
    }

    public User findOne(Long id) {
        String sql = "SELECT * FROM public.users WHERE id=?";

        try (Connection connection = dbUtils.getConnection()) {

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setLong(1, id);

                ResultSet result = preparedStatement.executeQuery();
                result.next();
                User resultUser = new User(
                        result.getString("firstName"),
                        result.getString("lastName"),
                        result.getString("email"));
                resultUser.setID(result.getLong("id"));
                return resultUser;
            } catch (SQLException throwable) {
                return null;
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    public Iterable<User> findAll() {
        List<User> utilizatori = new LinkedList<>();

        Connection connection = dbUtils.getConnection();

        try {
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT * FROM public.users");
            while (resultSet.next()) {
                User user = new User(
                        resultSet.getString("firstName"),
                        resultSet.getString("lastName"),
                        resultSet.getString("email"),
                        resultSet.getString("salt"),
                        resultSet.getString("password"));

                user.setID(resultSet.getLong("id"));
                utilizatori.add(user);
            }

        } catch (SQLException ignored) {

        }
        return utilizatori;
    }

    // TODO: Rework paging, implement matched paged queries
    public List<User> getPage(PaginationInfo paginationInfo) {

        List<User> utilizatori = new LinkedList<>();

        String sql = "SELECT * FROM public.users ORDER BY lastname, firstname, email LIMIT ? OFFSET ?";

        Connection connection = dbUtils.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            int pageSize = paginationInfo.getPageSize();
            int pageNumber = paginationInfo.getPageNumber();
            statement.setInt(1, pageSize);
            statement.setInt(2, pageSize * pageNumber);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                User user = new User(
                        resultSet.getString("firstName"),
                        resultSet.getString("lastName"),
                        resultSet.getString("email"),
                        resultSet.getString("salt"),
                        resultSet.getString("password"));

                user.setID(resultSet.getLong("id"));
                utilizatori.add(user);
            }

            if (utilizatori.size() == 0) {
                utilizatori = null;
            }
        } catch (SQLException ignored) {
            utilizatori = null;
        }
        return utilizatori;
    }

    public List<User> getPageMatched(PaginationInfo paginationInfo) {
        List<User> utilizatori = new LinkedList<>();

        Dictionary<String, Object> matchParams = paginationInfo.getMatcher();

        String sql = "SELECT * FROM public.users ORDER BY lastname, firstname, email LIMIT ? OFFSET ?";

        Connection connection = dbUtils.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            int pageSize = paginationInfo.getPageSize();
            int pageNumber = paginationInfo.getPageNumber();
            statement.setInt(1, pageSize);
            statement.setInt(2, pageSize * pageNumber);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                User user = new User(
                        resultSet.getString("firstName"),
                        resultSet.getString("lastName"),
                        resultSet.getString("email"),
                        resultSet.getString("salt"),
                        resultSet.getString("password"));

                user.setID(resultSet.getLong("id"));
                utilizatori.add(user);
            }

            if (utilizatori.size() == 0) {
                utilizatori = null;
            } else {
                this.page = page;
            }
        } catch (SQLException ignored) {
            utilizatori = null;
        }
        return utilizatori;
    }

    public int getPageCount(PaginationInfo paginationInfo) {
        int pageCount;
        int temp;

        Connection connection = dbUtils.getConnection();

        try {
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT COUNT(id) as count FROM public.users");
            resultSet.next();
            temp = resultSet.getInt("count");

        } catch (SQLException ignored) {
            return 0;
        }

        int pageSize = paginationInfo.getPageSize();
        pageCount = temp / pageSize;
        if (temp % pageSize != 0) {
            pageCount++;
        }

        return pageCount;
    }

    // -----------------------------------------------------------------------------------------------------------------

    public User save(User entity) {
        validator.validate(entity);
        User resultUser = findOne(entity.getID());
        if (resultUser == null) {
            String sql = "INSERT INTO users(firstname, lastName, email, password, salt)" +
                    " VALUES(?, ?, ?, ?, ?) RETURNING id";

            try (Connection connection = dbUtils.getConnection()) {

                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, entity.getFirstName());
                    statement.setString(2, entity.getLastName());
                    statement.setString(3, entity.getEmail());
                    statement.setString(4, entity.getPassword());
                    statement.setString(5, entity.getSalt());
                    ResultSet rs = statement.executeQuery();
                    rs.next();
                    long id = rs.getLong(1);
                    entity.setID(id);
                } catch (SQLException throwable) {
                    return entity;
                }
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        }
        return entity;
    }

    public User delete(Long id) {
        User entity = findOne(id);
        if (entity != null) {
            String sql = "DELETE FROM users WHERE id=?";

            Connection connection = dbUtils.getConnection();

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, id);
                statement.execute();
                return entity;
            } catch (SQLException throwable) {
                return null;
            }
        }
        return null;
    }

    public User update(User entity) {
        return null;
    }

    @Override
    public User findByEmail(String email) {
        //TODO: Implement Method
        return null;
    }
}
