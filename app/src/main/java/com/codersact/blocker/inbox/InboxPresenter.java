package com.codersact.blocker.inbox;

import com.codersact.blocker.model.MobileData;

import java.util.ArrayList;

/**
 * Created by masum on 11/08/2015.
 */
public class InboxPresenter {
    InboxView inboxView;
    InboxService inboxService;

    public InboxPresenter (InboxView inboxView, InboxService inboxService) {
        this.inboxView = inboxView;
        this.inboxService = inboxService;

    }

    public ArrayList<MobileData> onFetchList() {
        ArrayList<MobileData> mobileDatas = inboxService.fetchInboxSms(inboxView.getContext());
        return mobileDatas;
    }

}
