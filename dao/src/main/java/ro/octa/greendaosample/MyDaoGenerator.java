package ro.octa.greendaosample;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

/**
 * @author Octa
 */
public class MyDaoGenerator {

    private static final String PROJECT_DIR = System.getProperty("user.dir").replace("\\", "/");
    private static final String OUT_DIR = PROJECT_DIR + "/app/src/main/java";

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(1, "ro.octa.greendaosample.dao");

        addTables(schema);

        new DaoGenerator().generateAll(schema, OUT_DIR);
    }

    /**
     * Create tables and the relationships between them
     */
    private static void addTables(Schema schema) {
        /* entities */
        Entity user = addUser(schema);
        Entity userDetails = addUserDetails(schema);
        Entity message = addMessage(schema);

        /* properties */
        Property userIdForUserDetails = userDetails.addLongProperty("userId").notNull().getProperty();
        Property userDetailsIdForUser = user.addLongProperty("detailsId").notNull().getProperty();

        /* relationships between entities */
        userDetails.addToOne(user, userIdForUserDetails, "user");    // one-to-one (user.getDetails)
        user.addToOne(userDetails, userDetailsIdForUser, "details"); // one-to-one (user.getUser)

    }

    /**
     * Create user's Properties
     *
     * @return DBUser entity
     */
    private static Entity addUser(Schema schema) {
        Entity user = schema.addEntity("DBUser");
        user.addIdProperty().primaryKey().autoincrement();
        user.addStringProperty("displayName").notNull().unique();
        user.addStringProperty("phoneNumber").notNull();
        user.addByteArrayProperty("avatar");
        user.addLongProperty("isOnline").notNull();
        return user;
    }

    /**
     * Create user details Properties
     *
     * @return DBUserDetails entity
     */
    private static Entity addUserDetails(Schema schema) {
        Entity userDetails = schema.addEntity("DBUserDetails");
        userDetails.addIdProperty().primaryKey().autoincrement();
        return userDetails;
    }

    private static Entity addMessage(Schema schema) {
        Entity message = schema.addEntity("DBMessage");
        message.addIdProperty().primaryKey().autoincrement();
        message.addLongProperty("incoming").notNull();
        message.addLongProperty("toId").notNull();
        message.addLongProperty("fromId").notNull();
        message.addStringProperty("message").notNull();
        return message;
    }

}
