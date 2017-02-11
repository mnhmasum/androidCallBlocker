package com.codersact.blocker.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.codersact.blocker.model.MobileData;

import java.util.ArrayList;

public class DataBaseUtil {

    //Database Info
    private static final String TAG = "DataBaseUtil";
    private static final String DATABASE_NAME = "db_call_blocker";
    private static final String DATABASE_TABLE_BLACK_LIST = "tbl_blocked_list";
    private static final String DATABASE_TABLE_BLOCKED_LOG = "tbl_blocked_log";
    private static final int DATABASE_VERSION = 2;

    // Table Columns
    public static final String KEY_ROWID = "_id";
    public static final String KEY_CREATED_TIME = "created_time";
    public static final String KEY_BLOCKED_TIME = "blocked_time";
    public static final String KEY_NAME = "name";
    public static final String KEY_NUMBER = "number";

    //Column
    public String[] columns_black_list = {KEY_ROWID, KEY_CREATED_TIME, KEY_NAME, KEY_NUMBER};
    public String[] columns_block_log = {KEY_ROWID, KEY_BLOCKED_TIME, KEY_NAME, KEY_NUMBER};

    private static final String CREATE_TABLE_BLACK_LIST = "create table "
            + DATABASE_TABLE_BLACK_LIST + " (" + KEY_ROWID + " integer primary key autoincrement, "
            + KEY_CREATED_TIME + " text not null, "
            + KEY_NAME + " text not null, "
            + KEY_NUMBER + " text not null);";

    private static final String CREATE_TABLE_BLOCKED_LOG = "create table "
            + DATABASE_TABLE_BLOCKED_LOG + " (" + KEY_ROWID + " integer primary key autoincrement, "
            + KEY_BLOCKED_TIME + " text not null, "
            + KEY_NAME + " text not null, "
            + KEY_NUMBER + " text not null);";

    private final Context context;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase sqlDb;

    public DataBaseUtil(Context ctx) {
        this.context = ctx;
    }

    public DataBaseUtil open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        sqlDb = dbHelper.getWritableDatabase();
        // Log.i("DB WORK","YES");
        return this;
    }

    public void close() {
        sqlDb.close();
    }

    /**
     * Insert cart data
     */
    public long saveNewNumber(String date, String name, String number) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_CREATED_TIME, date);
        cv.put(KEY_NAME, name);
        cv.put(KEY_NUMBER, number);

        return sqlDb.insert(DATABASE_TABLE_BLACK_LIST, null, cv);
    }

    public long saveBlockedNumber(String date, String name, String number) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_BLOCKED_TIME, date);
        cv.put(KEY_NAME, name);
        cv.put(KEY_NUMBER, number);

        return sqlDb.insert(DATABASE_TABLE_BLOCKED_LOG, null, cv);
    }

    /**
     * Update cart data
     */
    public long updateNumber(String itemId, int itemQty) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_NUMBER, itemQty);
        return sqlDb.update(DATABASE_TABLE_BLACK_LIST, cv, KEY_NUMBER + "= ?", new String[]{String.valueOf(itemId)});
    }


    /**
     * Delete cart data
     */
    public boolean deleteNumber(String itemId) {
        sqlDb.execSQL("DELETE FROM " + DATABASE_TABLE_BLACK_LIST + " WHERE " + KEY_ROWID + "= " + Integer.parseInt(itemId));
        return true;
    }

    public boolean deleteSingleLog(String itemId) {
        Log.e(TAG,"Log delete number: " + itemId);
        sqlDb.execSQL("DELETE FROM " + DATABASE_TABLE_BLOCKED_LOG + " WHERE " + KEY_NUMBER + "= '" + itemId + "'");
        return true;
    }

    /**
     * Update cart data
     */
    public int countBlackListNumber() {
        Cursor cur = sqlDb.rawQuery("SELECT SUM(" + KEY_NUMBER + ") FROM " + DATABASE_TABLE_BLACK_LIST, null);
        if (cur.moveToFirst()) {
            return cur.getInt(0);
        }
        return 0;
    }

    /**
     * Fetch all cart data
     */
    public ArrayList<MobileData> getBlackList() {
        ArrayList<MobileData> callerDatas = new ArrayList<>();
        // query(table, column, where, selection, groupby, having, order)
        Cursor cursor = sqlDb.query(DATABASE_TABLE_BLACK_LIST, columns_black_list, null, null, null, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            MobileData callerInfo = new MobileData();
            Log.i("ITEM_REST_ID", "* " + cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            callerInfo.setRowId(cursor.getString(cursor.getColumnIndex(KEY_ROWID)));
            callerInfo.setCallerName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            callerInfo.setCreatedTime(cursor.getString(cursor.getColumnIndex(KEY_CREATED_TIME)));
            callerInfo.setMobileNumber(cursor.getString(cursor.getColumnIndex(KEY_NUMBER)));

            callerDatas.add(callerInfo);

            cursor.moveToNext();
        }

        cursor.close();
        return callerDatas;
    }

    public ArrayList<MobileData> getBlockedList() {
        ArrayList<MobileData> callerDatas = new ArrayList<>();
        // query(table, column, where, selection, groupby, having, order)
        Cursor cursor = sqlDb.query(DATABASE_TABLE_BLOCKED_LOG, columns_block_log, null, null, null, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            MobileData callerInfo = new MobileData();
            Log.i("ITEM_REST_ID", "* " + cursor.getString(cursor.getColumnIndex(KEY_NAME)));

            callerInfo.setRowId(cursor.getString(cursor.getColumnIndex(KEY_ROWID)));
            callerInfo.setCallerName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            callerInfo.setCreatedTime(cursor.getString(cursor.getColumnIndex(KEY_BLOCKED_TIME)));
            callerInfo.setMobileNumber(cursor.getString(cursor.getColumnIndex(KEY_NUMBER)));
            Log.e(TAG, "BLOCKED NUMBER " + callerInfo.getMobileNumber());

            callerDatas.add(callerInfo);

            cursor.moveToNext();
        }

        cursor.close();
        return callerDatas;
    }

    public boolean isAlreadyExistNumber(String itemId) {
        // query(table, column, where, selection, groupby, having, order)
        Cursor cursor = sqlDb.query(DATABASE_TABLE_BLACK_LIST, new String[] {KEY_NUMBER}, KEY_NUMBER + "= ?", new String[]{String.valueOf(itemId)}, null, null, null, null);
        cursor.moveToFirst();
        int itemCount = cursor.getCount();

        if (itemCount > 0) {
            cursor.close();
            return true;
        }

        cursor.close();
        return false;
    }

    public boolean deleteFullLog() {
        // query(table, column, where, selection, groupby, having, order)
        return sqlDb.delete(CREATE_TABLE_BLOCKED_LOG, null, null) > 0;
    }

    /**
     * Database Helper class for creating table
     */
    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            // TODO Auto-generated constructor stub
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_BLACK_LIST);
            db.execSQL(CREATE_TABLE_BLOCKED_LOG);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            Log.w(TAG, "Upgrading Database from version " + oldVersion + " to " + newVersion + " version.");
        }
    }
}
