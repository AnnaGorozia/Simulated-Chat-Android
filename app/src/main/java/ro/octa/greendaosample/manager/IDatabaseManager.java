package ro.octa.greendaosample.manager;

import java.util.ArrayList;
import java.util.Set;

import ro.octa.greendaosample.dao.DBMessage;
import ro.octa.greendaosample.dao.DBUser;
import ro.octa.greendaosample.dao.DBUserDetails;

/**
 * Interface that provides methods for managing the database inside the Application.
 *
 * @author Octa
 */
public interface IDatabaseManager {

    /**
     * Closing available connections
     */
    void closeDbConnections();

    /**
     * Delete all tables and content from our database
     */
    void dropDatabase();

    /**
     * Insert a user into the DB
     *
     * @param user to be inserted
     */
    DBUser insertUser(DBUser user);

    /**
     * Insert a message into the DB
     *
     * @param message to be inserted
     */
    DBMessage insertMessage(DBMessage message);

    /**
     * List all the users from the DB
     *
     * @return list of users
     */
    ArrayList<DBUser> listUsers();

    /**
     * List messages from the DB
     *
     * @return list of messages
     */
    ArrayList<DBMessage> listMessages(long toId, long fromId);

    /**
     * Delete message history from the DB
     */
    void deleteMessages(long toId, long fromId);

    /**
     * Update a user from the DB
     *
     * @param user to be updated
     */
    void updateUser(DBUser user);

    /**
     * Delete all users with a certain email from the DB
     *
     * @param email of users to be deleted
     */
    void deleteUserByEmail(String email);

    /**
     * Delete a user with a certain id from the DB
     *
     * @param userId of users to be deleted
     */
    boolean deleteUserById(Long userId);

    /**
     * @param userId - of the user we want to fetch
     * @return Return a user by its id
     */
    DBUser getUserById(Long userId);

    /**
     * Delete all the users from the DB
     */
    void deleteUsers();

    /**
     * Insert or update a userDetails object into the DB
     *
     * @param userDetails to be inserted/updated
     */
    DBUserDetails insertOrUpdateUserDetails(DBUserDetails userDetails);

    /**
     * Delete a user by name and gender
     */
//    void deleteUserByFirstNameAndGender(String firstName, String gender);

    void updateMessage(DBMessage message);

    DBMessage lastMessage(long toId, long fromId);

}
