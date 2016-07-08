package ro.octa.greendaosample.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import de.greenrobot.dao.async.AsyncOperation;
import de.greenrobot.dao.async.AsyncOperationListener;
import de.greenrobot.dao.async.AsyncSession;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;
import ro.octa.greendaosample.dao.DBMessage;
import ro.octa.greendaosample.dao.DBMessageDao;
import ro.octa.greendaosample.dao.DBUser;
import ro.octa.greendaosample.dao.DBUserDao;
import ro.octa.greendaosample.dao.DBUserDetails;
import ro.octa.greendaosample.dao.DBUserDetailsDao;
import ro.octa.greendaosample.dao.DaoMaster;
import ro.octa.greendaosample.dao.DaoSession;

/**
 * @author Octa
 */
public class DatabaseManager implements IDatabaseManager, AsyncOperationListener {

    /**
     * Class tag. Used for debug.
     */
    private static final String TAG = DatabaseManager.class.getCanonicalName();
    /**
     * Instance of DatabaseManager
     */
    private static DatabaseManager instance;
    /**
     * The Android Activity reference for access to DatabaseManager.
     */
    private Context context;
    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase database;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private AsyncSession asyncSession;
    private List<AsyncOperation> completedOperations;

    /**
     * Constructs a new DatabaseManager with the specified arguments.
     *
     * @param context The Android {@link android.content.Context}.
     */
    public DatabaseManager(final Context context) {
        this.context = context;
        mHelper = new DaoMaster.DevOpenHelper(this.context, "sample-database", null);
        completedOperations = new CopyOnWriteArrayList<AsyncOperation>();
    }

    /**
     * @param context The Android {@link android.content.Context}.
     * @return this.instance
     */
    public static DatabaseManager getInstance(Context context) {

        if (instance == null) {
            instance = new DatabaseManager(context);
        }

        return instance;
    }

    @Override
    public void onAsyncOperationCompleted(AsyncOperation operation) {
        completedOperations.add(operation);
    }

    private void assertWaitForCompletion1Sec() {
        asyncSession.waitForCompletion(1000);
        asyncSession.isCompleted();
    }

    /**
     * Query for readable DB
     */
    public void openReadableDb() throws SQLiteException {
        database = mHelper.getReadableDatabase();
        daoMaster = new DaoMaster(database);
        daoSession = daoMaster.newSession();
        asyncSession = daoSession.startAsyncSession();
        asyncSession.setListener(this);
    }

    /**
     * Query for writable DB
     */
    public void openWritableDb() throws SQLiteException {
        database = mHelper.getWritableDatabase();
        daoMaster = new DaoMaster(database);
        daoSession = daoMaster.newSession();
        asyncSession = daoSession.startAsyncSession();
        asyncSession.setListener(this);
    }

    @Override
    public void closeDbConnections() {
        if (daoSession != null) {
            daoSession.clear();
            daoSession = null;
        }
        if (database != null && database.isOpen()) {
            database.close();
        }
        if (mHelper != null) {
            mHelper.close();
            mHelper = null;
        }
        if (instance != null) {
            instance = null;
        }
    }

    @Override
    public synchronized void dropDatabase() {
        try {
            openWritableDb();
            DaoMaster.dropAllTables(database, true); // drops all tables
            mHelper.onCreate(database);              // creates the tables
            asyncSession.deleteAll(DBUser.class);    // clear all elements from a table
            asyncSession.deleteAll(DBUserDetails.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized DBUser insertUser(DBUser user) {
        try {
            if (user != null) {
                openWritableDb();
                DBUserDao userDao = daoSession.getDBUserDao();
                userDao.insert(user);
                Log.d(TAG, "Inserted user: " + user.getDisplayName() + " to the schema.");
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public synchronized DBMessage insertMessage(DBMessage message) {
        try {
            if (message != null) {
                openWritableDb();
                DBMessageDao messageDao = daoSession.getDbMessageDao();
                messageDao.insert(message);
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

    @Override
    public synchronized ArrayList<DBUser> listUsers() {
        List<DBUser> users = null;
        try {
            openReadableDb();
            DBUserDao userDao = daoSession.getDBUserDao();
            users = userDao.loadAll();

            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (users != null) {
            return new ArrayList<>(users);
        }
        return null;
    }

    @Override
    public DBMessage lastMessage(long toId, long fromId) {
        List<DBMessage> messages = null;
        try {
            openWritableDb();
            DBMessageDao dao = daoSession.getDbMessageDao();
            WhereCondition condition1 = dao.queryBuilder().and(DBMessageDao.Properties.FromId.eq(fromId),
                    DBMessageDao.Properties.ToId.eq(toId));
            WhereCondition condition2 = dao.queryBuilder().and(DBMessageDao.Properties.FromId.eq(toId),
                    DBMessageDao.Properties.ToId.eq(fromId));
            WhereCondition condition = dao.queryBuilder().or(condition1,
                    condition2);


            QueryBuilder<DBMessage> queryBuilder = dao.queryBuilder().where(condition).orderAsc(DBMessageDao.Properties.Id).limit(1);
            messages = queryBuilder.list();
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (messages != null) {
            if (messages.size() > 0) return messages.get(0);
        }
        return null;
    }

    @Override
    public ArrayList<DBMessage> listMessages(long toId, long fromId) {
        List<DBMessage> messages = null;
        try {
            openWritableDb();
            DBMessageDao dao = daoSession.getDbMessageDao();
            WhereCondition condition1 = dao.queryBuilder().and(DBMessageDao.Properties.FromId.eq(fromId),
                    DBMessageDao.Properties.ToId.eq(toId));
            WhereCondition condition2 = dao.queryBuilder().and(DBMessageDao.Properties.FromId.eq(toId),
                    DBMessageDao.Properties.ToId.eq(fromId));
            WhereCondition condition = dao.queryBuilder().or(condition1,
                    condition2);


            QueryBuilder<DBMessage> queryBuilder = dao.queryBuilder().where(condition);
            messages = queryBuilder.list();
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (messages != null) {
            if (messages.size() > 0) return (ArrayList<DBMessage>) messages;
        }
        return null;
    }

    @Override
    public void deleteMessages(long toId, long fromId) {
        try {
            openWritableDb();
            DBMessageDao dao = daoSession.getDbMessageDao();
            WhereCondition condition = dao.queryBuilder().and(DBMessageDao.Properties.FromId.eq(fromId),
                    DBMessageDao.Properties.ToId.eq(toId));
            QueryBuilder<DBMessage> queryBuilder = dao.queryBuilder().where(condition);
            dao.deleteInTx(queryBuilder.list());
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void updateUser(DBUser user) {
        try {
            if (user != null) {
                openWritableDb();
                daoSession.update(user);
                Log.d(TAG, "Updated user: " + user.getDisplayName() + " from the schema.");
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void updateMessage(DBMessage message) {
        try {
            if (message != null) {
                openWritableDb();
                daoSession.update(message);
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void deleteUserByEmail(String email) {
        try {
            openWritableDb();
            DBUserDao userDao = daoSession.getDBUserDao();
            QueryBuilder<DBUser> queryBuilder = userDao.queryBuilder().where(DBUserDao.Properties.DisplayName.eq(email));
            List<DBUser> userToDelete = queryBuilder.list();
            for (DBUser user : userToDelete) {
                userDao.delete(user);
            }
            daoSession.clear();
            Log.d(TAG, userToDelete.size() + " entry. " + "Deleted user: " + email + " from the schema.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized boolean deleteUserById(Long userId) {
        try {
            openWritableDb();
            DBUserDao userDao = daoSession.getDBUserDao();
            userDao.deleteByKey(userId);
            daoSession.clear();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public synchronized DBUser getUserById(Long userId) {
        DBUser user = null;
        try {
            openReadableDb();
            DBUserDao userDao = daoSession.getDBUserDao();
            user = userDao.loadDeep(userId);
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public synchronized void deleteUsers() {
        try {
            openWritableDb();
            DBUserDao userDao = daoSession.getDBUserDao();
            userDao.deleteAll();
            daoSession.clear();
            Log.d(TAG, "Delete all users from the schema.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized DBUserDetails insertOrUpdateUserDetails(DBUserDetails userDetails) {
        try {
            if (userDetails != null) {
                openWritableDb();
                daoSession.insertOrReplace(userDetails);
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userDetails;
    }

//    @Override
//    public synchronized void deleteUserByFirstNameAndGender(String firstName, String gender) {
//        try {
//            openWritableDb();
//            DBUserDetailsDao dao = daoSession.getDBUserDetailsDao();
//            WhereCondition condition = dao.queryBuilder().and(DBUserDetailsDao.Properties.FirstName.eq(firstName),
//                    DBUserDetailsDao.Properties.Gender.eq(gender));
//            QueryBuilder<DBUserDetails> queryBuilder = dao.queryBuilder().where(condition);
//            dao.deleteInTx(queryBuilder.list());
//            daoSession.clear();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
