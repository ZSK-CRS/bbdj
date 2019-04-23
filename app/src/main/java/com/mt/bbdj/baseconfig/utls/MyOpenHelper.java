package com.mt.bbdj.baseconfig.utls;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.mt.bbdj.baseconfig.db.gen.BluetoothMessageDao;
import com.mt.bbdj.baseconfig.db.gen.CityDao;
import com.mt.bbdj.baseconfig.db.gen.CountyDao;
import com.mt.bbdj.baseconfig.db.gen.DaoMaster;
import com.mt.bbdj.baseconfig.db.gen.ExpressLogoDao;
import com.mt.bbdj.baseconfig.db.gen.MingleAreaDao;
import com.mt.bbdj.baseconfig.db.gen.ProvinceDao;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.db.gen.WaillMessageDao;

import org.greenrobot.greendao.database.Database;

/**
 * Author : ZSK
 * Date : 2019/4/22
 * Description :
 */
public class MyOpenHelper extends DaoMaster.OpenHelper {

    public MyOpenHelper(Context context, String name) {
        super(context, name);
    }

    public MyOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    /**
     * 数据库升级
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        //操作数据库的更新 有几个表升级都可以传入到下面
        MigrationHelper.getInstance().migrate(db, BluetoothMessageDao.class);
        MigrationHelper.getInstance().migrate(db, CityDao.class);
        MigrationHelper.getInstance().migrate(db, CountyDao.class);
        MigrationHelper.getInstance().migrate(db, ExpressLogoDao.class);
        MigrationHelper.getInstance().migrate(db, MingleAreaDao.class);
        MigrationHelper.getInstance().migrate(db, ProvinceDao.class);
        MigrationHelper.getInstance().migrate(db, UserBaseMessageDao.class);
        MigrationHelper.getInstance().migrate(db, WaillMessageDao.class);
    }
}