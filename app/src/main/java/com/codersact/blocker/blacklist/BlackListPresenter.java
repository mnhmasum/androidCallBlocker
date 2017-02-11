package com.codersact.blocker.blacklist;

public class BlackListPresenter {
    private BlacklistView blacklistView;
    private BlackListService blackListService;

    public BlackListPresenter(BlacklistView blacklistView, BlackListService blackListService) {
        this.blacklistView = blacklistView;
        this.blackListService = blackListService;
    }

    public void getBlackList() {
        blacklistView.setBlackList(blackListService.fetchBlackList(blacklistView.getFragmentContext()));
    }

    public void getCallLogList() {
        blacklistView.setCallLogList(blackListService.getCallDetails(blacklistView.getFragmentContext()));
    }

    public void addNewBlackNumber(String name, String number) {
        blackListService.addNewNumber(name, number);
    }
}
