package ro.octa.greendaosample;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import ro.octa.greendaosample.adapters.RecentListAdapter;
import ro.octa.greendaosample.dao.DBMessage;
import ro.octa.greendaosample.dao.DBUser;
import ro.octa.greendaosample.manager.DatabaseManager;
import ro.octa.greendaosample.manager.IDatabaseManager;
import ro.octa.greendaosample.transport.ChatEventListsner;
import ro.octa.greendaosample.transport.ChatTransport;
import ro.octa.greendaosample.transport.FragmentListRefresher;
import ro.octa.greendaosample.transport.TestChatTransport;

public class RecentActivity extends Fragment implements FragmentListRefresher,View.OnClickListener, ChatEventListsner{

    private static final String PREFS_NAME = "MyPrefsFile";
    private static Context context;
    private static ChatTransport chatTransport;
    private ListView list;
    private RecentListAdapter adapter;
    private static ArrayList<DBUser> userList;
    private IDatabaseManager databaseManager;

    public static ChatTransport getChatTransport() {
        return chatTransport;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        chatTransport = new TestChatTransport();
        context = this.getActivity();

        init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main, container, false);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == this.getActivity().RESULT_OK) {
                init();
            }
            if (resultCode == this.getActivity().RESULT_CANCELED) {
                init();

            }
        }
    }

    private void init() {
        databaseManager = new DatabaseManager(getActivity());

        userList = new ArrayList<DBUser>();
        list = (ListView) getView().findViewById(R.id.listView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                DBUser user = (DBUser) list.getItemAtPosition(position);
                if (user != null) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("fromID", UsersActivity.getOwnerId());
                    intent.putExtra("toID", user.getId());
                    startActivityForResult(intent, 1);
                }
            }
        });

        refreshUserList();
    }

    private void refreshUserList() {
        ArrayList<DBUser> tempList = DatabaseManager.getInstance(this.getActivity()).listUsers();
        for (DBUser user : tempList) {
            DBMessage messages = DatabaseManager.getInstance(this.getActivity()).lastMessage(UsersActivity.getOwnerId(), user.getId());
            if (messages != null) {
                userList.add(user);
            }
        }

        if (userList != null) {
            if (adapter == null) {
                adapter = new RecentListAdapter(this.getActivity(), userList);
                list.setAdapter(adapter);
            } else {
                list.setAdapter(null);
                adapter.clear();
                adapter.addAll(userList);
                adapter.notifyDataSetChanged();
                list.setAdapter(adapter);
            }
        }
    }

    @Override
    public void onIncomingMsg(DBMessage m) {

        m = databaseManager.insertMessage(m);
        final long id = m.getFromId();
        final String mes = m.getMessage();
        final String name = databaseManager.getUserById(id).getDisplayName();
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Toast.makeText(getContext(), "message from " + id, Toast.LENGTH_SHORT).show();
                init();
                sendNotification(name, mes);
//                adapter.notifyDataSetChanged();
//                refreshUserList();
            }
        });
    }

    private void sendNotification(String name, String mes) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this.getActivity())
                        .setSmallIcon(R.drawable.bender)
                        .setContentTitle("New Message from " + name)
                        .setContentText(mes);
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this.getActivity(), ChatActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this.getActivity());
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(RecentActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) this.getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());
    }

    @Override
    public void onOutgoingMsg(DBMessage m) {

    }

    @Override
    public void onStatusChanged(String contactId, boolean isOnline) {

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void refreshList() {
        init();
    }
}
