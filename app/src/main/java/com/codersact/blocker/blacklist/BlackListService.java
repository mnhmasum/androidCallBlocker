package com.codersact.blocker.blacklist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.SystemClock;
import android.provider.CallLog;
import android.provider.Settings;
import android.util.Log;

import com.codersact.blocker.db.DataBaseUtil;
import com.codersact.blocker.model.MobileData;
import com.codersact.blocker.model.NumberData;

import java.util.ArrayList;
import java.util.Date;

import static com.codersact.blocker.db.DataBaseUtil.KEY_CREATED_TIME;
import static com.codersact.blocker.db.DataBaseUtil.KEY_NAME;
import static com.codersact.blocker.db.DataBaseUtil.KEY_NUMBER;

public class BlackListService {
    private static final String TAG = "BlackListService";
    DataBaseUtil dataBaseUtil;

    BlackListService(Context context) {
        this.dataBaseUtil = new DataBaseUtil(context);
    }

    public ArrayList<MobileData> fetchBlackList(Context context) {
        ArrayList<MobileData> mobileDatas = new ArrayList<>();

        try {
            dataBaseUtil.open();
            mobileDatas = dataBaseUtil.getBlackList();
            dataBaseUtil.close();

        } catch (Exception e) {
            Log.e(TAG, "Get black list error: " + e.getMessage());
        }

        return mobileDatas;
    }

    public ArrayList<NumberData> getCallDetails(Context context) {
        final ArrayList<NumberData> numberDatas = new ArrayList<>();

        try {
            StringBuffer sb = new StringBuffer();
            Cursor managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC");
            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
            sb.append("Call Details :");

            while (managedCursor.moveToNext()) {
                String callType = managedCursor.getString(type);
                if (Integer.parseInt(callType) == CallLog.Calls.INCOMING_TYPE || Integer.parseInt(callType) == CallLog.Calls.MISSED_TYPE) {
                    String phNumber = managedCursor.getString(number);

                    String callDate = managedCursor.getString(date);
                    Date callDayTime = new Date(Long.valueOf(callDate));
                    String callDuration = managedCursor.getString(duration);
                    String dir = null;
                    int dircode = Integer.parseInt(callType);
                    switch (dircode) {
                        case CallLog.Calls.OUTGOING_TYPE:
                            dir = "OUTGOING";
                            break;

                        case CallLog.Calls.INCOMING_TYPE:
                            dir = "INCOMING";
                            break;

                        case CallLog.Calls.MISSED_TYPE:
                            dir = "MISSED";
                            break;
                    }

                    NumberData numberData = new NumberData();
                    numberData.setSenderNumber(phNumber);
                    numberDatas.add(numberData);

                    sb.append("\n Phone Number:--- " + phNumber + " \n Call Type:--- " + dir + " \n Call Date:--- " + callDayTime + " \n Call duration in sec :--- " + callDuration);
                    sb.append("\n----------------------------------");
                }


            }

            managedCursor.close();

        } catch (Exception e) {

        }

        return numberDatas;

    }

    public int addNewNumber(String name, String number) {
        int status = 0;
        try {

            dataBaseUtil.open();
            if (!dataBaseUtil.isAlreadyExistNumber(number)) {
                dataBaseUtil.saveNewNumber("" + SystemClock.currentThreadTimeMillis(), name, number);
                status = 1;
            } else {
                status = 0;
            }

            dataBaseUtil.close();

        } catch (Exception e) {
            Log.e(TAG, "Get black list error: " + e.getMessage());
            status = -1;
        }

        return status;
    }
}
