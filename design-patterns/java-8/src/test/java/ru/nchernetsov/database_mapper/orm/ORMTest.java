package ru.nchernetsov.database_mapper.orm;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.nchernetsov.database_mapper.entity.User;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ORMTest {

    private static ORM orm = null;

    @BeforeClass
    public static void setup() throws SQLException {
        orm = new ORM();
    }

    @Before
    public void clearTable() throws SQLException {
        orm.execQuery("TRUNCATE TABLE users;");
        orm.clearIdentityMapForClass(User.class);
    }

    @Test
    public void insertSomeUsersInTableTest() {
        orm.save(new User(1, "Ivan", 25));
        orm.save(new User(5, "Maria", 19));

        try {
            int IvanID = orm.execQuery("SELECT id FROM users WHERE name = 'Ivan'", resultSet -> {
                resultSet.next();
                return resultSet.getInt(1);
            });

            int MariaID = orm.execQuery("SELECT id FROM users WHERE name = 'Maria'", resultSet -> {
                resultSet.next();
                return resultSet.getInt(1);
            });

            assertEquals(1, IvanID);
            assertEquals(5, MariaID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadUserTest() {
        orm.save(new User(2, "User2", 2));
        orm.save(new User(3, "User3", 3));

        User user2 = orm.load(2, User.class);
        User user3 = orm.load(3, User.class);

        assertEquals("User2", user2.getName());
        assertEquals("User3", user3.getName());
    }

    @Test
    public void loadAllUsersTest() {
        List<User> insertUsersList = Arrays.asList(
            new User(1, "User1", 1),
            new User(2, "User2", 2),
            new User(3, "User3", 3));

        for (User insertUser : insertUsersList) {
            orm.save(insertUser);
        }

        List<User> loadedUsers = orm.loadAll(User.class);

        assertEquals(3, loadedUsers.size());

        assertTrue(loadedUsers.containsAll(insertUsersList));
    }

    @Test
    public void identityMapTest() {
        orm.save(new User(111, "User111", 111));

        Map<Class<?>, Map<Long, Object>> identityMap = orm.getIdentityMap();

        assertEquals(1, identityMap.get(User.class).size());

        Object identityMapUser = identityMap.get(User.class).get(111L);

        assertEquals("User111", ((User) identityMapUser).getName());
        assertEquals(111, ((User) identityMapUser).getAge());
    }

}
