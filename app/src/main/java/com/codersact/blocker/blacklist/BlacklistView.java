package com.codersact.blocker.blacklist;

import android.content.Context;

import com.codersact.blocker.model.MobileData;
import com.codersact.blocker.model.NumberData;

import java.util.ArrayList;

/**
 * Created by masum on 11/08/2015.
 */
public interface BlacklistView {
    void setBlackList(ArrayList<MobileData> arrayList);
    void setCallLogList(ArrayList<NumberData> arrayList);
    Context getFragmentContext();
}
