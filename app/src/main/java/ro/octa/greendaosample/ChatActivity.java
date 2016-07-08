package ro.octa.greendaosample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import ro.octa.greendaosample.adapters.ChatAdapter;
import ro.octa.greendaosample.adapters.UserListAdapter;
import ro.octa.greendaosample.dao.DBMessage;
import ro.octa.greendaosample.dao.DBUser;
import ro.octa.greendaosample.manager.DatabaseManager;
import ro.octa.greendaosample.manager.IDatabaseManager;
import ro.octa.greendaosample.transport.ChatEventListsner;

public class ChatActivity extends Activity implements ChatEventListsner {

	private ChatAdapter adapter;
	private ListView lv;
	private ImageView back;
	private TextView displayName;
	private EditText editText1;
	private static ArrayList<DBMessage> messageList;
	private static Random random;

	private long fromID;
	private DBUser user;
	private IDatabaseManager databaseManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		init();
		UsersActivity.getChatTransport().addChatEventListsner(this, user.getId());
		lv = (ListView) findViewById(R.id.listView1);

//		displayName = (TextView) findViewById(R.id.display_name);
//		displayName.setText(user.getDisplayName());


		if (getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);

//		back = (ImageView) findViewById(R.id.back_home);
//		back.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent returnIntent = new Intent();
//                setResult(RESULT_CANCELED, returnIntent);
//               	finish();
//			}
//		});

		refreshMessageList();

		editText1 = (EditText) findViewById(R.id.editText1);
		editText1.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
					DBMessage message = new DBMessage();
					message.setFromId(fromID);
					message.setToId(user.getId());
					message.setMessage(String.valueOf(editText1.getText()));
					message.setIncoming(1);
					message = databaseManager.insertMessage(message);
					messageList.add(message);
					adapter.notifyDataSetChanged();
					refreshMessageList();
					editText1.setText("");
					UsersActivity.getChatTransport().sendMessage(message);
					return true;
				}
				return false;
			}
		});
	}

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent returnIntent = new Intent();
                setResult(RESULT_CANCELED, returnIntent);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

	private void refreshMessageList() {
		messageList = DatabaseManager.getInstance(this).listMessages(user.getId(), fromID);
		if (messageList != null) {
			int i = 0;
			for (DBMessage m : messageList) {
				if (m.getIncoming() == 1) {
					m.setIncoming(0);
					messageList.set(i, m);
					databaseManager.updateMessage(m);
					i++;
				}
			}
			if (adapter == null) {
				adapter = new ChatAdapter(ChatActivity.this, messageList);
				lv.setAdapter(adapter);
			} else {
				lv.setAdapter(null);
				adapter.clear();
				adapter.addAll(messageList);
				adapter.notifyDataSetChanged();
				lv.setAdapter(adapter);
			}
		}
		if (messageList == null) messageList = new ArrayList<>();
		if (adapter == null)adapter = new ChatAdapter(ChatActivity.this, messageList);
	}

	private void init() {
		// init database manager

		databaseManager = new DatabaseManager(this);

		long userId = getIntent().getLongExtra("toID", -1L);
		this.fromID = getIntent().getLongExtra("fromID", -1L);
		if (userId != -1) {
			user = databaseManager.getUserById(userId);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		RecentActivity.getChatTransport().removeChatEventListsner(this, user.getId());
	}

	@Override
	public void onBackPressed() {
		Intent returnIntent = new Intent();
		setResult(RESULT_CANCELED, returnIntent);
		finish();
	}

	@Override
	public void onIncomingMsg(DBMessage m) {
		final Handler handler = new Handler();
		m.setIncoming(0);
		m = databaseManager.insertMessage(m);
		final DBMessage tempM = m;
		Random random = new Random();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {

				messageList.add(tempM);
				adapter.notifyDataSetChanged();
				refreshMessageList();
			}
		}, random.nextInt(10) * 1000
		);

	}

	@Override
	public void onOutgoingMsg(DBMessage m) {

	}

	@Override
	public void onStatusChanged(String contactId, boolean isOnline) {
		// TODO Auto-generated method stub
		
	}

}
