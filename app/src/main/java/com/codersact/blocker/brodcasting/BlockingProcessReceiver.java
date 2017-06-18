package com.codersact.blocker.brodcasting;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;
import com.codersact.blocker.R;
import com.codersact.blocker.db.DataBaseUtil;
import com.codersact.blocker.main.MainActivity;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by masum on 30/07/2015.
 */
public class BlockingProcessReceiver extends BroadcastReceiver {
    Integer notificationId = 1207, requestId = 1208;
    String msgBody;
    Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        SharedPreferences.Editor editor = context.getSharedPreferences("L", Context.MODE_PRIVATE).edit();

        ITelephony telephonyService;
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        // If, the received action is not a type of "Phone_State", ignore it
        if (!intent.getAction().equals("android.intent.action.PHONE_STATE"))
            return;

            // Else, try to do some action
        else {
            if(telephony.getCallState() == telephony.CALL_STATE_RINGING) {
                SharedPreferences prefs = context.getSharedPreferences("L", Context.MODE_PRIVATE);

                int idName = prefs.getInt("idName", 0); //0 is the default value.
                if (idName == 1) {
                    Log.i("No Need block", ".......");
                } else {
                    Log.i("Ringing" , ".......");
                    // Fetch the number of incoming call
                    String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    SimpleDateFormat df = new SimpleDateFormat("MMM d, yyyy hh:mm:ss");
                    Calendar c = Calendar.getInstance();
                    String formattedDate = df.format(c.getTime());
                    checkBlackList(number, null, context, formattedDate);
                    Log.i("income number", "" + number);
                }

            } else if (telephony.getCallState() == telephony.CALL_STATE_IDLE) {
                Log.i("Idle" , ".......");
                editor.putInt("idName", 0);
                editor.commit();

            } else if (telephony.getCallState() == telephony.CALL_STATE_OFFHOOK) {
                Log.i("OFFHOOK", ".......");
                editor.putInt("idName", 1);
                editor.commit();
            }

        }

    }


    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void disconnectPhoneiTelephony(Context context) {
        ITelephony telephonyService;
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class c = Class.forName(telephony.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            telephonyService = (ITelephony) m.invoke(telephony);
            telephonyService.endCall();

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void saveIncomingBlockedNumber(Context context, String name, String number, String formattedDate) {
        try {
            DataBaseUtil dataBaseUtil = new DataBaseUtil(context);
            dataBaseUtil.open();
            dataBaseUtil.saveBlockedNumber(formattedDate, name, number);
            dataBaseUtil.close();

        } catch (Exception e) {
            Log.d("addToSMS_BlackList", "***" + e.getMessage());
            Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_LONG).show();

        }

    }

    private void checkBlackList(final String mobileNumber, final Long threadId, final Context context, String createdDate) {
        try {
            DataBaseUtil dataBaseUtil = new DataBaseUtil(context);
            dataBaseUtil.open();
            if(dataBaseUtil.isAlreadyExistNumber(mobileNumber)) {

                disconnectPhoneiTelephony(context); // call disconnect
                saveIncomingBlockedNumber(context, "Call blocked ", mobileNumber, createdDate);
                pushNotification(mobileNumber);

                Log.d("SMSBlockingProcess", " checkBlackList Ended");

            }

            dataBaseUtil.close();

        } catch (Exception e){
            Log.e("SMSBlocking excep", " " + e.getMessage());
        }

    }

    private void pushNotification(String fromAddress){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentTitle("Call Blocked")
                        .setContentText(fromAddress);

        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(100, mBuilder.build());
    }


}
