package ro.octa.greendaosample.transport;

import java.util.List;

import ro.octa.greendaosample.dao.DBUser;

public interface NetworkEventListener {
	public void onContactListDownloaded(List<DBUser> contacts);
	public void onAvatarDownloaded(byte[] imgData, String contactId);
	public void onError(int errorCode, String errorMsg);
}
