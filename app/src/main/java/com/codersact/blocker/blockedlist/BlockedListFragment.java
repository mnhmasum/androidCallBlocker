package com.codersact.blocker.blockedlist;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.codersact.blocker.R;
import com.codersact.blocker.adapter.BlockedAdapter;
import com.codersact.blocker.adapter.LogNumberAdapter;
import com.codersact.blocker.blacklist.BlackListFragment;
import com.codersact.blocker.db.DataBaseUtil;
import com.codersact.blocker.inbox.InboxService;
import com.codersact.blocker.model.MobileData;
import com.codersact.blocker.model.NumberData;

import java.util.ArrayList;

public class BlockedListFragment extends Fragment implements View.OnClickListener, BlockedListView {
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView recyclerView;
    private TextView textView;
    private ArrayList<MobileData> mobileDatas = new ArrayList<>();
    private BlockedListPresenter blockedListPresenter;
    private FloatingActionButton floatingActionButton;
    private BlockedAdapter mAdapter;
    private DataBaseUtil dataBaseUtil;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_blocked_list, container, false);
        dataBaseUtil = new DataBaseUtil(getActivity());
        dataBaseUtil.open();
        initView(rootView);
        blockedListPresenter = new BlockedListPresenter(this, new BlockedListService());
        blockedListPresenter.getBlockedList();
        return rootView;
    }

    private void initView(View rootView) {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv);
        textView = (TextView) rootView.findViewById(R.id.textView);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(this);

    }

    public void setEmptyMessage(int size) {
        if (size > 0) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setBlockedList(ArrayList<MobileData> arrayList) {
        mAdapter = new BlockedAdapter(arrayList, getActivity());
        recyclerView.setAdapter(mAdapter);
        setEmptyMessage(arrayList.size());
        setAdapterChangeListener();

    }

    private void setAdapterChangeListener() {
        mAdapter.setOnDataChangeListener(new BlockedAdapter.OnDataChangeListener() {
            @Override
            public void onDataChanged(int size) {
                setEmptyMessage(size);
            }
        });
    }

    @Override
    public Context getFragmentContext() {
        return getActivity();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floatingActionButton:
                openActionDialog();
                break;

        }
    }

    private void blackListFragment() {
        android.app.Fragment fragment = null;
        fragment = new BlackListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        FragmentManager frgManager = getFragmentManager();
        android.app.FragmentTransaction ft = frgManager.beginTransaction();
        ft.replace(R.id.content_frame, fragment, "SEARCH_FRAGMENT");
        ft.commit();
    }

    private void openManualEntryDilaog(String message, String okButton, String cancelButton) {
        final Dialog dialog = new Dialog(getActivity(), R.style.AlertDialogCustom_Destructive);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_retry);
        dialog.setCanceledOnTouchOutside(false);

        TextView txtViewPopupMessage = (TextView) dialog.findViewById(R.id.txtViewPopupMessage);
        ImageButton imgBtnClose = (ImageButton) dialog.findViewById(R.id.imgBtnClose);
        final EditText editText = (EditText) dialog.findViewById(R.id.editText);

        Button btnAccept = (Button) dialog.findViewById(R.id.btnAdd);
        btnAccept.setText(okButton);
        txtViewPopupMessage.setText(message);

        // if button is clicked, close the custom dialog
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //new CommonDbMethod(getActivity()).addToNumberBlacklist("Name", editText.getText().toString().trim());
                dialog.dismiss();
                blackListFragment();
            }

        });

        imgBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }

        });

        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        btnCancel.setText(cancelButton);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }

        });

        dialog.show();
    }

    private void openDilaogInbox(String cancelButton) {
        final Dialog dialog = new Dialog(getActivity(), R.style.AlertDialogCustom_Destructive);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_inbox);
        dialog.setCanceledOnTouchOutside(false);

        ListView listView = (ListView) dialog.findViewById(R.id.listViewInbox);
        final ArrayList<MobileData> mobileDatas = new InboxService().fetchInboxSms(getActivity());

        final ArrayList<NumberData> numberDatas = new ArrayList<>();
        for (int i = 0; i < mobileDatas.size(); i++) {
            NumberData numberData = new NumberData();
            numberData.setSenderNumber(mobileDatas.get(i).getMobileNumber());
            numberDatas.add(numberData);
        }

        LogNumberAdapter inboxNumberAdapter = new LogNumberAdapter(getActivity(), numberDatas);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);

        btnCancel.setText(cancelButton);
        listView.setAdapter(inboxNumberAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                dataBaseUtil.saveNewNumber("", mobileDatas.get(position).getSmsThreadNo(), numberDatas.get(position).getSenderNumber());
                //new CommonDbMethod(getActivity()).addToNumberBlacklist(mobileDatas.get(position).getSmsThreadNo(), numberDatas.get(position).getSenderNumber());
                dialog.dismiss();
                blackListFragment();
                //Toast.makeText(getActivity(), "Position" + numberDatas.get(position).getSenderNumber(), Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }

        });

        dialog.show();
    }

    private void openActionDialog() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity(), R.style.AlertDialogCustom_Destructive);
        //builderSingle.setIcon(R.drawable.about);
        builderSingle.setTitle("Add From Sender");
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_selectable_list_item);
        arrayAdapter.add("Inbox");
        arrayAdapter.add("Manual Entry");

        builderSingle.setNegativeButton("cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(arrayAdapter,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == 0) {
                            openDilaogInbox("Cancel");
                        } else {
                            openManualEntryDilaog("Number", "Add", "Cancel");
                        }

                    }
                });
        builderSingle.show();
    }


}
