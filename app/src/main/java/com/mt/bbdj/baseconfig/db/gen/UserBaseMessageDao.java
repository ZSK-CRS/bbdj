package com.mt.bbdj.baseconfig.db.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.mt.bbdj.baseconfig.db.UserBaseMessage;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "user_base".
*/
public class UserBaseMessageDao extends AbstractDao<UserBaseMessage, Long> {

    public static final String TABLENAME = "user_base";

    /**
     * Properties of entity UserBaseMessage.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property MainId = new Property(0, Long.class, "mainId", true, "_id");
        public final static Property User_id = new Property(1, String.class, "user_id", false, "user_id");
        public final static Property User_type = new Property(2, String.class, "user_type", false, "user_type");
        public final static Property Headimg = new Property(3, String.class, "headimg", false, "headimg");
        public final static Property Mingcheng = new Property(4, String.class, "mingcheng", false, "mingcheng");
        public final static Property Contacts = new Property(5, String.class, "contacts", false, "contacts");
        public final static Property Contact_number = new Property(6, String.class, "contact_number", false, "contact_number");
        public final static Property Contact_email = new Property(7, String.class, "contact_email", false, "contact_email");
        public final static Property Birthday = new Property(8, String.class, "birthday", false, "birthday");
        public final static Property Balance = new Property(9, String.class, "balance", false, "balance");
    }


    public UserBaseMessageDao(DaoConfig config) {
        super(config);
    }
    
    public UserBaseMessageDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"user_base\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: mainId
                "\"user_id\" TEXT," + // 1: user_id
                "\"user_type\" TEXT," + // 2: user_type
                "\"headimg\" TEXT," + // 3: headimg
                "\"mingcheng\" TEXT," + // 4: mingcheng
                "\"contacts\" TEXT," + // 5: contacts
                "\"contact_number\" TEXT," + // 6: contact_number
                "\"contact_email\" TEXT," + // 7: contact_email
                "\"birthday\" TEXT," + // 8: birthday
                "\"balance\" TEXT);"); // 9: balance
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"user_base\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, UserBaseMessage entity) {
        stmt.clearBindings();
 
        Long mainId = entity.getMainId();
        if (mainId != null) {
            stmt.bindLong(1, mainId);
        }
 
        String user_id = entity.getUser_id();
        if (user_id != null) {
            stmt.bindString(2, user_id);
        }
 
        String user_type = entity.getUser_type();
        if (user_type != null) {
            stmt.bindString(3, user_type);
        }
 
        String headimg = entity.getHeadimg();
        if (headimg != null) {
            stmt.bindString(4, headimg);
        }
 
        String mingcheng = entity.getMingcheng();
        if (mingcheng != null) {
            stmt.bindString(5, mingcheng);
        }
 
        String contacts = entity.getContacts();
        if (contacts != null) {
            stmt.bindString(6, contacts);
        }
 
        String contact_number = entity.getContact_number();
        if (contact_number != null) {
            stmt.bindString(7, contact_number);
        }
 
        String contact_email = entity.getContact_email();
        if (contact_email != null) {
            stmt.bindString(8, contact_email);
        }
 
        String birthday = entity.getBirthday();
        if (birthday != null) {
            stmt.bindString(9, birthday);
        }
 
        String balance = entity.getBalance();
        if (balance != null) {
            stmt.bindString(10, balance);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, UserBaseMessage entity) {
        stmt.clearBindings();
 
        Long mainId = entity.getMainId();
        if (mainId != null) {
            stmt.bindLong(1, mainId);
        }
 
        String user_id = entity.getUser_id();
        if (user_id != null) {
            stmt.bindString(2, user_id);
        }
 
        String user_type = entity.getUser_type();
        if (user_type != null) {
            stmt.bindString(3, user_type);
        }
 
        String headimg = entity.getHeadimg();
        if (headimg != null) {
            stmt.bindString(4, headimg);
        }
 
        String mingcheng = entity.getMingcheng();
        if (mingcheng != null) {
            stmt.bindString(5, mingcheng);
        }
 
        String contacts = entity.getContacts();
        if (contacts != null) {
            stmt.bindString(6, contacts);
        }
 
        String contact_number = entity.getContact_number();
        if (contact_number != null) {
            stmt.bindString(7, contact_number);
        }
 
        String contact_email = entity.getContact_email();
        if (contact_email != null) {
            stmt.bindString(8, contact_email);
        }
 
        String birthday = entity.getBirthday();
        if (birthday != null) {
            stmt.bindString(9, birthday);
        }
 
        String balance = entity.getBalance();
        if (balance != null) {
            stmt.bindString(10, balance);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public UserBaseMessage readEntity(Cursor cursor, int offset) {
        UserBaseMessage entity = new UserBaseMessage( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // mainId
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // user_id
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // user_type
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // headimg
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // mingcheng
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // contacts
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // contact_number
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // contact_email
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // birthday
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9) // balance
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, UserBaseMessage entity, int offset) {
        entity.setMainId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUser_id(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setUser_type(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setHeadimg(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setMingcheng(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setContacts(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setContact_number(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setContact_email(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setBirthday(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setBalance(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(UserBaseMessage entity, long rowId) {
        entity.setMainId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(UserBaseMessage entity) {
        if(entity != null) {
            return entity.getMainId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(UserBaseMessage entity) {
        return entity.getMainId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
