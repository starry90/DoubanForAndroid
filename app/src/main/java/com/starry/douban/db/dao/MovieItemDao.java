package com.starry.douban.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.starry.douban.db.RoomDBTable;
import com.starry.douban.model.MovieItemBean;

import java.util.List;

/**
 * @author Starry Jerry
 * @since 2021/9/18.
 */
@Dao
public interface MovieItemDao {

    @Query("SELECT * FROM " + RoomDBTable.Movie.ITEM)
    List<MovieItemBean> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<MovieItemBean> list);

}
