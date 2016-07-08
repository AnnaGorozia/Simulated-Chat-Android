package ro.octa.greendaosample.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

public class DBMessageDao extends AbstractDao<DBMessage, Long> {
    public static final String TABLENAME = "DBMESSAGE";

    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Incoming = new Property(1, long.class, "incoming", false, "INCOMING");
        public final static Property ToId = new Property(2, long.class, "toId", false, "TO_ID");
        public final static Property FromId = new Property(3, long.class, "fromId", false, "FROM_ID");
        public final static Property Message = new Property(4, String.class, "message", false, "MESSAGE");
    }

    ;

    private DaoSession daoSession;


    public DBMessageDao(DaoConfig config) {
        super(config);
    }

    public DBMessageDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /**
     * Creates the underlying database table.
     */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "\"DBMESSAGE\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"INCOMING\" INTEGER NOT NULL ," + // 1: email
                "\"TO_ID\" INTEGER NOT NULL ," + // 2: password
                "\"FROM_ID\" INTEGER NOT NULL ," + // 2: password
                "\"MESSAGE\" TEXT NOT NULL );"); // 2: password
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"DBMESSAGE\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, DBMessage entity) {
        stmt.clearBindings();

        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }

        stmt.bindLong(2, entity.getIncoming());
        stmt.bindLong(3, entity.getToId());
        stmt.bindLong(4, entity.getFromId());
        stmt.bindString(5, entity.getMessage());
    }

    @Override
    protected void attachEntity(DBMessage entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }

    /** @inheritdoc */
    @Override
    public DBMessage readEntity(Cursor cursor, int offset) {
        DBMessage entity = new DBMessage( //
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
                cursor.getLong(offset + 1),
                cursor.getLong(offset + 2),
                cursor.getLong(offset + 3),
                cursor.getString(offset + 4)
        );
        return entity;
    }

    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, DBMessage entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setIncoming(cursor.getLong(offset + 1));
        entity.setToId(cursor.getLong(offset + 2));
        entity.setFromId(cursor.getLong(offset + 3));
        entity.setMessage(cursor.getString(offset + 4));
    }

    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(DBMessage entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }

    /** @inheritdoc */
    @Override
    public Long getKey(DBMessage entity) {
        if (entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /**
     * @inheritdoc
     */
    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }

}
