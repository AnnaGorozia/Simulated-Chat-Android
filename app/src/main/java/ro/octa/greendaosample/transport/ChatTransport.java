package ro.octa.greendaosample.transport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ro.octa.greendaosample.dao.DBMessage;


public abstract class ChatTransport {
	protected List<ChatEventListsner> listeners = new ArrayList<ChatEventListsner>();
	protected Map<Long, ChatEventListsner> listsnerMap = new HashMap<>();

	public abstract void start();
	public abstract void sendMessage(DBMessage m);
	
	public void addChatEventListsner(ChatEventListsner listener, long id) {
//		if (!listeners.contains(listener))
//			listeners.add(listener);

		if (!listsnerMap.containsKey(id)) {
			listsnerMap.put(id, listener);
		}
	}
	
	public void removeChatEventListsner(ChatEventListsner listener, long id) {
//		if (listeners.contains(listener))
//			listeners.remove(listener);

		if (listsnerMap.containsKey(id)) {
			listsnerMap.remove(id);
		}
	}
}
