package com.codersact.blocker.blockedlist;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.codersact.blocker.db.DataBaseUtil;
import com.codersact.blocker.model.MobileData;

import java.util.ArrayList;

/**
 * Created by masum on 11/08/2015.
 */
public class BlockedListService {

    public ArrayList<MobileData> fetchBlockedList(Context context) {
        ArrayList<MobileData> mobileDatas = new ArrayList<>();

        try {
            DataBaseUtil dataBaseUtil = new DataBaseUtil(context);
            dataBaseUtil.open();
            mobileDatas = dataBaseUtil.getBlockedList();
            dataBaseUtil.close();

        } catch (Exception e) {

        }

        return mobileDatas;
    }
}
