package ro.octa.greendaosample.asynchtasks;

import java.util.ArrayList;

import android.os.AsyncTask;

import ro.octa.greendaosample.dao.DBUser;
import ro.octa.greendaosample.transport.NetworkEventListener;

public abstract class ContactListDownloaderTask extends AsyncTask<Void, Void, ArrayList<DBUser>>{
	private NetworkEventListener networkEventListener;

	public void setNetworkEventListener(NetworkEventListener networkEventListener) {
		this.networkEventListener = networkEventListener;
	}
	
	@Override
	protected void onPostExecute(ArrayList<DBUser> contacts) {
		super.onPostExecute(contacts);
		networkEventListener.onContactListDownloaded(contacts);
	}
}
