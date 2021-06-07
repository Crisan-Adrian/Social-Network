package repository.user;

import domain.User;
import domain.validators.Validator;
import exceptions.RepoException;
import repository.JDBCUtils;
import repository.PaginationInfo;
import repository.PagingUtils;

import java.sql.*;
import java.util.*;

public class UserDB implements IUserRepository {

    //TODO: Comment code where necessary. Document functions. Refactor if needed
    private final JDBCUtils dbUtils;

    private final Validator<User> validator;

    private static final List<String> validMatchKeys = new ArrayList<>(Set.of("email", "lastname", "firstname"));

    public UserDB(Validator<User> validator, Properties properties) {
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

    public List<User> getPage(PaginationInfo paginationInfo) {

        List<User> utilizatori = new LinkedList<>();
        Map<String, String> matchParams = paginationInfo.getMatcher();
        if (!PagingUtils.validateMatchers(matchParams, validMatchKeys)) {
            throw new RepoException("Invalid match parameters");
        }
        int matchersNo = matchParams.size();
        String matchers = PagingUtils.buildMatcher(matchParams.keySet());
        List<String> matchValues = (List<String>) matchParams.values();

        String sql = "SELECT * FROM public.users ORDER BY lastname, firstname, email " + matchers + " LIMIT ? OFFSET ?";
        int pageSize = paginationInfo.getPageSize();
        int pageNumber = paginationInfo.getPageNumber();

        if (!PagingUtils.validatePage(pageSize, pageNumber)) {
            throw new RepoException("Invalid pageSize or pageNumber");
        }

        try (Connection connection = dbUtils.getConnection()) {

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                for (int i = 1; i <= matchersNo; i++) {
                    statement.setString(i, "%" + matchValues.get(i - 1) + "%");
                }
                statement.setInt(matchersNo + 1, pageSize);
                statement.setInt(matchersNo + 2, pageSize * pageNumber);

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
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            utilizatori = null;
        }
        return utilizatori;
    }

    public int getPageCount(PaginationInfo paginationInfo) {
        int pageCount;
        int temp;

        int pageSize = paginationInfo.getPageSize();
        if(pageSize > 1) {
            throw new RepoException("Invalid pageSize or pageNumber");
        }

        Map<String, String> matchParams = paginationInfo.getMatcher();
        if (!PagingUtils.validateMatchers(matchParams, validMatchKeys)) {
            throw new RepoException("Invalid match parameters");
        }
        int matchersNo = matchParams.size();
        String matchers = PagingUtils.buildMatcher(matchParams.keySet());
        List<String> matchValues = (List<String>) matchParams.values();

        String sql = "SELECT COUNT(id) as count FROM public.users " + matchers;

        try (Connection connection = dbUtils.getConnection()) {

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                for (int i = 1; i <= matchersNo; i++) {
                    statement.setString(i, "%" + matchValues.get(i - 1) + "%");
                }

                ResultSet resultSet = statement.executeQuery();
                resultSet.next();
                temp = resultSet.getInt("count");


            } catch (SQLException throwable) {
                throwable.printStackTrace();
                return 0;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return 0;
        }
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
