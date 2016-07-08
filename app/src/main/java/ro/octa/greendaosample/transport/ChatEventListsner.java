package ro.octa.greendaosample.transport;


import ro.octa.greendaosample.dao.DBMessage;

public interface ChatEventListsner {
	public void onIncomingMsg(DBMessage m);
	public void onOutgoingMsg(DBMessage m);
	public void onStatusChanged(String contactId, boolean isOnline);
}
