package com.codersact.blocker.blockedlist;

public class BlockedListPresenter {
    BlockedListView blacklistView;
    BlockedListService blackListService;

    public BlockedListPresenter(BlockedListView blacklistView, BlockedListService blackListService) {
        this.blacklistView = blacklistView;
        this.blackListService = blackListService;
    }

    public void getBlockedList() {
        blacklistView.setBlockedList(blackListService.fetchBlockedList(blacklistView.getFragmentContext())) ;
    }

}
