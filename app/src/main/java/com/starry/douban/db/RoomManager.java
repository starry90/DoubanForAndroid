package com.starry.douban.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;

import com.starry.douban.db.dao.MovieItemDao;
import com.starry.douban.env.AppWrapper;
import com.starry.douban.model.MovieItemBean;

/**
 * @author Starry Jerry
 * @since 2021/9/18.
 */
@Database(entities = {MovieItemBean.class}, version = 1, exportSchema = false)
public abstract class RoomManager extends RoomDatabase {

    /**
     * 关于AppDataBase 的使用
     */
    public static RoomManager getDatabase() {
        return SingletonHolder.INSTANCE;
    }

    private static final class SingletonHolder {
        /**
         * HttpManager 只有一个实例
         */
        private static final RoomManager INSTANCE = Room.databaseBuilder(AppWrapper.getContext(), RoomManager.class, RoomDBTable.DB_NAME)
                // 设置是否允许在主线程做查询操作
                .allowMainThreadQueries()
                // 设置数据库升级(迁移)的逻辑
                .build();
    }

    public abstract MovieItemDao getMovieItemDao();

}