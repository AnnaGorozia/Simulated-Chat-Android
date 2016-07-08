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
import java.util.List;
import java.util.UUID;

import ro.octa.greendaosample.adapters.UserListAdapter;
import ro.octa.greendaosample.asynchtasks.ContactImageDownloaderTask;
import ro.octa.greendaosample.asynchtasks.ContactListDownloaderTask;
import ro.octa.greendaosample.asynchtasks.DBContactListDownloaderTask;
import ro.octa.greendaosample.asynchtasks.URLContactListDownloaderTask;
import ro.octa.greendaosample.dao.DBMessage;
import ro.octa.greendaosample.dao.DBUser;
import ro.octa.greendaosample.dao.DBUserDetails;
import ro.octa.greendaosample.manager.DatabaseManager;
import ro.octa.greendaosample.manager.IDatabaseManager;
import ro.octa.greendaosample.transport.ChatEventListsner;
import ro.octa.greendaosample.transport.ChatTransport;
import ro.octa.greendaosample.transport.FragmentListRefresher;
import ro.octa.greendaosample.transport.NetworkEventListener;
import ro.octa.greendaosample.transport.TestChatTransport;

public class UsersActivity extends Fragment implements FragmentListRefresher,NetworkEventListener,View.OnClickListener, ChatEventListsner {

    private static final String PREFS_NAME = "MyPrefsFile";
    private static Context context;
    private static ChatTransport chatTransport;
    private static ArrayList contacts;
    private ListView list;
    private UserListAdapter adapter;
    private static ArrayList<DBUser> userList;
    private IDatabaseManager databaseManager;
    private static long ownerId;

    public static ArrayList getContactList() {
        return contacts;
    }

    public static long getOwnerId() {
        return ownerId;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        //Returning the layout file after inflating
        //Change R.layout.tab1 in you classes
        super.onActivityCreated(savedInstanceState);
        chatTransport = new TestChatTransport();
        context = this.getActivity();

        // init database manager
        databaseManager = new DatabaseManager(getActivity());

        initApp();

        userList = new ArrayList<DBUser>();
        list = (ListView) getView().findViewById(R.id.listView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                DBUser user = (DBUser) list.getItemAtPosition(position);
                if (user != null) {
//                    Intent intent = new Intent(MainActivity.this, UserDetailsActivity.class);
//                    intent.putExtra("userID", user.getId());
//                    startActivityForResult(intent, 1);
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("fromID", ownerId);
                    intent.putExtra("toID", user.getId());
                    startActivityForResult(intent, 1);
                }
            }
        });

        refreshUserList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main, container, false);
    }

    private void initApp() {
        ContactListDownloaderTask contactListDownloaderTask = null;
        SharedPreferences settings = this.getActivity().getSharedPreferences(PREFS_NAME, 0);
        if (settings.getBoolean("my_first_time", true)) {
            contactListDownloaderTask = new URLContactListDownloaderTask();
        }else{
            contactListDownloaderTask = new DBContactListDownloaderTask();
        }
        contactListDownloaderTask.setNetworkEventListener(this);
        contactListDownloaderTask.execute();
    }

    private byte[] getDefaultImage() {
        Resources res = this.getActivity().getResources();
        Drawable drawable = res.getDrawable(R.drawable.bender);
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitMapData = stream.toByteArray();
        return bitMapData;
    }

    private void createMobileUser() {
        DBUser user = new DBUser();
        user.setDisplayName("Anna Gorozia");
        user.setPhoneNumber("555123123");
        user.setAvatar(getDefaultImage());
        user.setIsOnline(1);

        user = databaseManager.insertUser(user);

        DBUserDetails userDetails = createRandomUserDetails();
        userDetails.setUserId(user.getId());
        userDetails.setUser(user);

        userDetails = databaseManager.insertOrUpdateUserDetails(userDetails);

        user.setDetailsId(userDetails.getId());
        user.setDetails(userDetails);
        databaseManager.updateUser(user);
        SharedPreferences settings = this.getActivity().getSharedPreferences(PREFS_NAME, 0);
        settings.edit().putLong("mobile_user_id", user.getId()).commit();
    }

    private void executeImgDownloaderTask() {
        ContactImageDownloaderTask contactImageDownloaderTask = new ContactImageDownloaderTask();
        contactImageDownloaderTask.setNetworkEventListener(this);
        contactImageDownloaderTask.execute();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == this.getActivity().RESULT_OK) {
                refreshUserList();
            }
            if (resultCode == this.getActivity().RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    public static Context getContextOver(){
        return context;
    }

    private DBUser createRandomUser() {
        DBUser user = new DBUser();
        user.setDisplayName(UUID.randomUUID().toString() + "@email.com");
        user.setPhoneNumber("defaultPass");
        byte[] arr = new byte[4];
        arr[0] = 1;
        arr[1] = 2;
        arr[2] = 3;
        arr[3] = 4;
        user.setAvatar(arr);
        user.setIsOnline(1);
        return user;
    }

    private DBUserDetails createRandomUserDetails() {
        DBUserDetails userDetails = new DBUserDetails();
        return userDetails;
    }

    private void onCreateUserClick() {
        // create a random user object
        DBUser user = createRandomUser();

        // insert that user object to our DB
        user = databaseManager.insertUser(user);

        // Create a random userDetails object
        DBUserDetails userDetails = createRandomUserDetails();
        userDetails.setUserId(user.getId());
        userDetails.setUser(user);

        // insert or update this userDetails object to our DB
        userDetails = databaseManager.insertOrUpdateUserDetails(userDetails);

        // link userDetails Key to user
        user.setDetailsId(userDetails.getId());
        user.setDetails(userDetails);
        databaseManager.updateUser(user);

        try {
            // add the user object to the list
            userList.add(user);
            adapter.notifyDataSetChanged();
            list.post(new Runnable() {
                @Override
                public void run() {
                    // Select the last row so it will scroll into view...
                    list.setSelection(adapter.getCount() - 1);
                }
            });
        } catch (UnsupportedOperationException e) {

        }
    }

    @Override
    public void onContactListDownloaded(List<DBUser> contacts) {
        UsersActivity.contacts = (ArrayList)contacts;
        System.out.println("----------------contacts size: " + contacts.size());
//        MainActivity.userList = (ArrayList)contacts;
//        refreshUserList();
        SharedPreferences settings = this.getActivity().getSharedPreferences(PREFS_NAME, 0);
        long ownerId = settings.getLong("mobile_user_id", 0);
        this.ownerId = ownerId;
        for (DBUser user : contacts) {
            if (user.getId() == ownerId) continue;
            // insert that user object to our DB
            user = databaseManager.insertUser(user);

            // Create a random userDetails object
            DBUserDetails userDetails = createRandomUserDetails();
            userDetails.setUserId(user.getId());
            userDetails.setUser(user);

            // insert or update this userDetails object to our DB
            userDetails = databaseManager.insertOrUpdateUserDetails(userDetails);

            // link userDetails Key to user
            user.setDetailsId(userDetails.getId());
            user.setDetails(userDetails);
            databaseManager.updateUser(user);

            try {
                // add the user object to the list
                userList.add(user);
                adapter.notifyDataSetChanged();
                list.post(new Runnable() {
                    @Override
                    public void run() {
                        // Select the last row so it will scroll into view...
                        list.setSelection(adapter.getCount() - 1);
                    }
                });
            } catch (UnsupportedOperationException e) {

            }
        }
        if (settings.getBoolean("my_first_time", true)) {
            executeImgDownloaderTask();
            createMobileUser();
            settings.edit().putBoolean("my_first_time", false).commit();
        }

        getChatTransport().addChatEventListsner(this, ownerId);
        getChatTransport().start();


    }

    @Override
    public void onAvatarDownloaded(byte[] imgData, String contactId) {
        DBUser user = databaseManager.getUserById(Long.parseLong(contactId));
        user.setAvatar(imgData);
        databaseManager.updateUser(user);
//        refreshUserList();
    }

    @Override
    public void onError(int errorCode, String errorMsg) {

    }

    /**
     * Display all the users from the DB into the listView
     */
    private void refreshUserList() {
        userList = DatabaseManager.getInstance(this.getActivity()).listUsers();
        if (userList != null) {
            if (adapter == null) {
                adapter = new UserListAdapter(this.getActivity(), userList);
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

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    @Override
    public void onIncomingMsg(DBMessage m) {

        SharedPreferences settings = this.getActivity().getSharedPreferences(PREFS_NAME, 0);
        m = databaseManager.insertMessage(m);
//            long id = m.getFromId();
//            if (getViewByPosition((int) id, list) != null)
//                getViewByPosition((int) id, list).setBackgroundColor(Color.BLUE);
//
        final long id = m.getFromId();
        final String mes = m.getMessage();
        final String name = databaseManager.getUserById(id).getDisplayName();
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Toast.makeText(getContext(), "message from " + id, Toast.LENGTH_SHORT).show();
                sendNotification(id, name, mes);
//                refreshUserList();
            }
        });
        System.out.println("------------------------message from " + m.getFromId());
    }

    private void sendNotification(long id, String name, String mes) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this.getActivity())
                        .setSmallIcon(R.drawable.bender)
                        .setContentTitle("New Message from " + name)
                        .setContentText(mes);
        Intent resultIntent = new Intent(this.getActivity(), ChatActivity.class);
        resultIntent.putExtra("fromID", UsersActivity.getOwnerId());
        resultIntent.putExtra("toID", id);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this.getActivity());
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) this.getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }

    @Override
    public void onOutgoingMsg(DBMessage m) {

    }

    @Override
    public void onStatusChanged(String contactId, boolean isOnline) {

    }

    public static ChatTransport getChatTransport() {
        return chatTransport;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void refreshList() {

    }
}