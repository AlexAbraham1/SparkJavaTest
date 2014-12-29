package main.java.models;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
 * Database Connection
 */
public class DBC {

    private static String databaseURL = "jdbc:mysql://127.0.0.1:8889/spark";
    private static String databaseUser = "root";
    private static String databasePassword = "root";

    private static ConnectionSource connectionSource;
    private static Dao<User, String> userDao;


    public static List<User> queryUser(String key, Object value)
    {
        try {
            List<User> userList = userDao.queryForEq(key, value);
            return userList;
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static void addUser(User user)
    {
        try {
            userDao.create(user);
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static void updateUser(User user)
    {
        try {
            userDao.update(user);
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static void initialize()
    {
        try {
            ConnectionSource connectionSource = getConnectionSource();
            userDao = DaoManager.createDao(connectionSource, User.class); //Create Data Access Object
            TableUtils.createTableIfNotExists(connectionSource, User.class); //Create users table iff it doesn't exist
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }

    }

    private static ConnectionSource getConnectionSource() throws SQLException {


        connectionSource = new JdbcConnectionSource(databaseURL);
        ((JdbcConnectionSource)connectionSource).setUsername(databaseUser);
        ((JdbcConnectionSource)connectionSource).setPassword(databasePassword);
        return connectionSource;

    }

    public static void terminate()
    {
        try {
            connectionSource.close();
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
