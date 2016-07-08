package ro.octa.greendaosample.dao;

import de.greenrobot.dao.DaoException;

public class DBUser {

    private Long id;
    private String displayName;
    private String phoneNumber;
    private byte[] avatar;
    private String avatarUrl;
    private long isOnline;
    private long detailsId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient DBUserDao myDao;

    private DBUserDetails details;
    private Long details__resolvedKey;


    public DBUser() {
    }

    public DBUser(Long id) {
        this.id = id;
    }

    public DBUser(Long id, String displayName, String phoneNumber, byte[] avatar, long isOnline, long detailsId) {
        this.id = id;
        this.displayName = displayName;
        this.phoneNumber = phoneNumber;
        this.avatar = avatar;
        this.isOnline = isOnline;
        this.detailsId = detailsId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getDBUserDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getDetailsId() {
        return detailsId;
    }

    public void setDetailsId(long detailsId) {
        this.detailsId = detailsId;
    }

    /** To-one relationship, resolved on first access. */
    public DBUserDetails getDetails() {
        long __key = this.detailsId;
        if (details__resolvedKey == null || !details__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DBUserDetailsDao targetDao = daoSession.getDBUserDetailsDao();
            DBUserDetails detailsNew = targetDao.load(__key);
            synchronized (this) {
                details = detailsNew;
                details__resolvedKey = __key;
            }
        }
        return details;
    }

    public void setDetails(DBUserDetails details) {
        if (details == null) {
            throw new DaoException("To-one property 'detailsId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.details = details;
            detailsId = details.getId();
            details__resolvedKey = detailsId;
        }
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public long getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(long isOnline) {
        this.isOnline = isOnline;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
