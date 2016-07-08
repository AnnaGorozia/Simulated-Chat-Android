package ro.octa.greendaosample.transport;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import ro.octa.greendaosample.MainActivity;
import ro.octa.greendaosample.UsersActivity;
import ro.octa.greendaosample.dao.DBMessage;

public class TestChatTransport extends ChatTransport {
	private ChatEventListsner chatEventListsner;
	
	@Override
	public void start() {
		this.chatEventListsner = listsnerMap.get(UsersActivity.getOwnerId());

		Timer timer = new Timer ();
		TimerTask hourlyTask = new TimerTask () {
			@Override
			public void run () {
				Random random = new Random();
				DBMessage message = new DBMessage();
				message.setFromId(random.nextInt(100));
				message.setToId(UsersActivity.getOwnerId());
				message.setMessage("Hello World!!!");
				message.setIncoming(1);
				chatEventListsner.onIncomingMsg(message);
			}
		};

		timer.schedule (hourlyTask, 0l, 1000*60);
	}

	@Override
	public void sendMessage(DBMessage m) {
		DBMessage message = new DBMessage();
		message.setFromId(m.getToId());
		message.setToId(m.getFromId());
		message.setMessage(String.valueOf("Hey - " + m.getMessage() + " - good bye!"));
		message.setIncoming(1);
		listsnerMap.get(m.getToId()).onIncomingMsg(message);
	}

}
