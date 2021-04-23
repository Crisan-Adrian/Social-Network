package repository.user;

import domain.User;
import domain.validators.Validator;
import repository.JDBCUtils;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class UserDB implements IUserRepository {

    //TODO: Comment code where necessary. Document functions. Refactor if needed
    //TODO: Change to use generated IDs
    private final JDBCUtils dbUtils;

    private final int pageSize;
    private int page;
    private int pageM;
    private String matcher;
    private final Validator<User> validator;

    public UserDB(Validator<User> validator, Properties properties, int pageSize) {
        this.pageSize = pageSize;
        page = 0;
        pageM = 0;
        matcher = "";
        dbUtils = new JDBCUtils(properties);
        this.validator = validator;
    }

    public User findOne(Long id) {
        String sql = "SELECT * FROM public.users WHERE id=?";

        Connection connection = dbUtils.getConnection();

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

    public boolean hasPrevPage() {
        return page != 0;
    }

    public boolean hasNextPage() {
        return page < getPageCount() - 1;
    }

    public List<User> getPage() {
        return getPage(page);
    }

    public List<User> getPage(int page) {
        List<User> utilizatori = new LinkedList<>();

        String sql = "SELECT * FROM public.users ORDER BY lastname, firstname, email LIMIT ? OFFSET ?";

        Connection connection = dbUtils.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, pageSize);
            statement.setInt(2, pageSize * page);

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
            else
            {
                this.page = page;
            }
        } catch (SQLException ignored) {
            utilizatori = null;
        }
        return utilizatori;
    }

    public List<User> getNextPage() {
        return getPage(page + 1);
    }

    public List<User> getPrevPage() {
        return getPage(page - 1);
    }


    public List<User> getFirstPage() {
        return getPage(0);
    }

    public int getPageCount() {
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

        pageCount = temp/pageSize;
        if (temp % pageSize != 0) {
            pageCount++;
        }

        return pageCount;
    }

    public int getPageNumber() {
        return page;
    }

    // -----------------------------------------------------------------------------------------------------------------

    // Matcher w/ Paging

    public void setMatcher(String newMatcher) {
        newMatcher = newMatcher.strip();
        if (newMatcher.equals("")) {
            matcher = "";
        } else {
            matcher = newMatcher.split(" ")[0];
        }
        pageM = 0;
    }

    public boolean hasPrevPageM() {
        return pageM != 0;
    }

    public boolean hasNextPageM() {
        return pageM < getPageCountM() - 1;
    }

    public List<User> getPageM(int pageM) {
        List<User> utilizatori = new LinkedList<>();

        String sql = "SELECT * FROM public.users WHERE firstname ilike '%' || ? || '%' OR lastname ilike '%' || ? || '%' ORDER BY lastname, firstname, email LIMIT ? OFFSET ?";

        Connection connection = dbUtils.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, matcher);
            statement.setString(2, matcher);
            statement.setInt(3, pageSize);
            statement.setInt(4, pageSize * pageM);

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
            else
            {
                this.pageM = pageM;
            }
        } catch (SQLException ignored) {
            utilizatori = null;
        }
        return utilizatori;
    }

    public List<User> getPageM() {
        return getPageM(pageM);
    }

    public List<User> getNextPageM() {
        return getPageM(pageM + 1);
    }

    public List<User> getPrevPageM() {
        return getPageM(pageM - 1);
    }

    public int getPageCountM() {
        int pageCount;
        int temp;

        String sql = "SELECT COUNT(id) as count FROM public.users WHERE firstname ilike '%' || ? || '%' OR lastname ilike '%' || ? || '%'";

        Connection connection = dbUtils.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, matcher);
            statement.setString(2, matcher);

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            temp = resultSet.getInt("count");

        } catch (SQLException ignored) {
            return 0;
        }

        pageCount = temp/pageSize;
        if (temp % pageSize != 0) {
            pageCount++;
        }

        return pageCount;
    }


    public int getPageNumberM() {
        return pageM;
    }

    // -----------------------------------------------------------------------------------------------------------------

    public User save(User entity) {
        User resultUser = findOne(entity.getID());
        validator.validate(entity);
        if (resultUser == null) {
            String sql = "INSERT INTO users(id, firstname, lastName, email, password, salt) VALUES(?, ?, ?, ?, ?, ?)";

            Connection connection = dbUtils.getConnection();

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, entity.getID());
                statement.setString(2, entity.getFirstName());
                statement.setString(3, entity.getLastName());
                statement.setString(4, entity.getEmail());
                statement.setString(5, entity.getPassword());
                statement.setString(6, entity.getSalt());
                statement.execute();
                return null;
            } catch (SQLException throwable) {
                return entity;
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
