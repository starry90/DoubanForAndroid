package com.starry.douban.db;


import android.content.ContentValues;
import android.database.Cursor;

/**
 * Description: 数据库实体类基类 <br>
 * Date: 19-11-20 上午11:10 <br>
 * Author: Starry Jerry  <br>
 */
public interface BaseDBModel {

    void read(Cursor cursor);

    ContentValues contentValues();

}
