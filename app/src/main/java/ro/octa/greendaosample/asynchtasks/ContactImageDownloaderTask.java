package ro.octa.greendaosample.asynchtasks;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;

import ro.octa.greendaosample.MainActivity;
import ro.octa.greendaosample.UsersActivity;
import ro.octa.greendaosample.dao.DBUser;
import ro.octa.greendaosample.transport.NetworkEventListener;

public class ContactImageDownloaderTask extends AsyncTask<List<DBUser>, Object, Void>{
	private NetworkEventListener networkEventListener;
	
	public ContactImageDownloaderTask() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Void doInBackground(List<DBUser>... params) {
        ArrayList<DBUser> contacts = UsersActivity.getContactList();
        URLFileWorker urlFileWorker = new URLFileWorker(null);
        for(DBUser contact : contacts){
            urlFileWorker.setURL(contact.getAvatarUrl());
            InputStream inputStream = urlFileWorker.getInputStream();
            Bitmap bm = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            byte[] byteArray = outStream.toByteArray();
            contact.setAvatar(byteArray);
			publishProgress(byteArray, "" + contact.getId());
        }
        return null;
	}
	
	public void setNetworkEventListener(NetworkEventListener networkEventListener) {
		this.networkEventListener = networkEventListener;
	}
	
	@Override
	protected void onProgressUpdate(Object... values) {
		super.onProgressUpdate(values);
		if (networkEventListener != null)
			networkEventListener.onAvatarDownloaded((byte[])values[0], (String)values[1]);
	}
	
}
