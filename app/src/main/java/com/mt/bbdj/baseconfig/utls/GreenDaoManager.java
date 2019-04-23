package com.mt.bbdj.baseconfig.utls;

import android.content.Context;

import com.mt.bbdj.baseconfig.db.gen.DaoMaster;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;

import org.greenrobot.greendao.identityscope.IdentityScopeType;

/**
 * Author : ZSK
 * Date : 2019/1/2
 * Description :  数据库管理类
 */
public class GreenDaoManager {

    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    private static GreenDaoManager mInstance;//单例

    private GreenDaoManager() {
    }

    public  void init(Context context) {
        //创建数据库
        // DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context.getApplicationContext(),"bbdj.db", null);
        MyOpenHelper helper = new MyOpenHelper(context.getApplicationContext(),"bbdj.db",null);
        //创建可读写数据库
        //  mDaoMaster = new DaoMaster(helper.getEncryptedWritableDb("7334453"));
        mDaoMaster = new DaoMaster(helper.getWritableDatabase());
        mDaoSession = mDaoMaster.newSession(IdentityScopeType.None);
    }

    private static final class GreenDaoManagerHolder{
        private static final GreenDaoManager mInstance = new GreenDaoManager();
    }

    //静态内部类实现单例，保证只有一个daoSesson
    public static GreenDaoManager getInstance(){
        return GreenDaoManagerHolder.mInstance;
    }

    public DaoMaster getMaster() {
        return mDaoMaster;
    }

    public DaoSession getSession() {
        return mDaoSession;
    }

    public DaoSession getNewSession() {
        return mDaoSession;
    }

}
