package com.codersact.blocker.blockedlist;

import android.content.Context;

import com.codersact.blocker.model.MobileData;
import com.codersact.blocker.model.NumberData;

import java.util.ArrayList;


public interface BlockedListView {
    void setBlockedList(ArrayList<MobileData> arrayList);
    Context getFragmentContext();
}
