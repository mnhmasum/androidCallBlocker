package com.codersact.blocker.blacklist;

import android.app.Dialog;
import android.app.Fragment;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codersact.blocker.R;
import com.codersact.blocker.adapter.BlackListAdapter;
import com.codersact.blocker.adapter.LogNumberAdapter;
import com.codersact.blocker.inbox.InboxService;
import com.codersact.blocker.model.MobileData;
import com.codersact.blocker.model.NumberData;

import java.util.ArrayList;


public class BlackListFragment extends Fragment implements View.OnClickListener, BlacklistView {
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private BlackListPresenter blackListPresenter;
    private RelativeLayout relative_help;
    private TextView textView;
    private BlackListAdapter mAdapter;

    private ArrayList<NumberData> arrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_black_list, container, false);
        initView(rootView);
        blackListPresenter = new BlackListPresenter(this, new BlackListService(getActivity()));
        blackListPresenter.getBlackList();
        blackListPresenter.getCallLogList();
        return rootView;
    }

    public void setMessage(int size) {
        if (size > 0) {
            relative_help.setVisibility(View.INVISIBLE);

        } else {
            relative_help.setVisibility(View.VISIBLE);

        }
    }

    private void initView(View rootView) {
        getActivity().setTitle("Black List");

        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(this);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv);
        textView = (TextView) rootView.findViewById(R.id.textView);

        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        relative_help = (RelativeLayout) rootView.findViewById(R.id.relative_help);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floatingActionButton:
                openActionDialog();
                break;
        }
    }

    @Override
    public void setBlackList(ArrayList<MobileData> arrayList) {
        mAdapter = new BlackListAdapter(arrayList, getActivity());
        recyclerView.setAdapter(mAdapter);
        setMessage(arrayList.size());
        setAdapterChangeListener();

    }

    @Override
    public void setCallLogList(ArrayList<NumberData> arrayList) {
        this.arrayList = arrayList;
    }

    @Override
    public Context getFragmentContext() {
        return getActivity();
    }

    private void setAdapterChangeListener() {
        mAdapter.setOnDataChangeListener(new BlackListAdapter.OnDataChangeListener() {
            @Override
            public void onDataChanged(int size) {
                setMessage(size);
            }
        });
    }

    private void openActionDialog() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setTitle("Add From Sender");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_selectable_list_item);
        arrayAdapter.add("     From Inbox");
        arrayAdapter.add("     From Log");
        arrayAdapter.add("     Manual Entry");

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
                            openDialogInbox("Cancel");

                        } else if (which == 1) {
                            openDialogLog("Cancel");

                        } else if (which == 2) {
                            openManualEntryDialog("Number", "Add", "Cancel");
                        }

                    }
                });

        builderSingle.show();
    }

    private void openManualEntryDialog(String message, String okButton, String cancelButton) {
        final Dialog dialog = new Dialog(getActivity());
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
                blackListPresenter.addNewBlackNumber("", editText.getText().toString().trim());
                blackListPresenter.getBlackList();
                dialog.dismiss();
                Toast.makeText(getActivity(), "" + editText.getText().toString().trim() + " is added to the block list", Toast.LENGTH_SHORT).show();
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

    private void openDialogInbox(String cancelButton) {
        final Dialog dialog = new Dialog(getActivity());
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
                blackListPresenter.addNewBlackNumber("", numberDatas.get(position).getSenderNumber().trim());
                blackListPresenter.getBlackList();
                dialog.dismiss();
                Toast.makeText(getActivity(), "" + numberDatas.get(position).getSenderNumber().trim() + " is added to the block list", Toast.LENGTH_SHORT).show();
//                blackListPresenter.addNewBlackNumber("", );
//                blackListPresenter.getBlackList();
//                getActivity().setTitle("Black List");
//                dialog.dismiss();
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

    private void openDialogLog(String cancelButton) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_inbox);
        dialog.setCanceledOnTouchOutside(false);

        TextView view = (TextView) dialog.findViewById(R.id.view);
        ListView listView = (ListView) dialog.findViewById(R.id.listViewInbox);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);

        LogNumberAdapter inboxNumberAdapter = new LogNumberAdapter(getActivity(), arrayList);
        btnCancel.setText(cancelButton);
        listView.setAdapter(inboxNumberAdapter);
        view.setText("Log");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                blackListPresenter.addNewBlackNumber("", arrayList.get(position).getSenderNumber());
                blackListPresenter.getBlackList();
                getActivity().setTitle("Black List");
                dialog.dismiss();
                Toast.makeText(getActivity(), "" + arrayList.get(position).getSenderNumber() + " is added to the block list", Toast.LENGTH_SHORT).show();

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

}
