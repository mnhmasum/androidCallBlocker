package com.codersact.blocker.utility;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.codersact.blocker.R;
import com.codersact.blocker.blacklist.BlackListFragment;


/**
 * Created by masum on 11/08/2015.
 */
public class UtilityMethod {

    public static void blackListFragment(Activity activity) {
        android.app.Fragment fragment = null;
        fragment = new BlackListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        FragmentManager frgManager = activity.getFragmentManager();
        android.app.FragmentTransaction ft = frgManager.beginTransaction();
        ft.replace(R.id.content_frame, fragment, "SEARCH_FRAGMENT");
        ft.commit();
    }

    public static boolean isConnectingToInternet(Context _context) {
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;


    }

}
