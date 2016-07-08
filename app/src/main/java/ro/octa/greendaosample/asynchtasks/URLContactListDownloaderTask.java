package ro.octa.greendaosample.asynchtasks;



import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import ro.octa.greendaosample.DataObject;
import ro.octa.greendaosample.dao.DBUser;

public class URLContactListDownloaderTask extends ContactListDownloaderTask {
    private static String JSON_URL="https://dl.dropboxusercontent.com/u/28030891/FreeUni/Android/assinments/contacts.json/";
    private ArrayList<DBUser> contacts;
	@Override
	protected ArrayList<DBUser> doInBackground(Void... params){
        InputStream inStream = null;
        URLFileWorker jsonFileWorker = new URLFileWorker(JSON_URL);
        String json = jsonFileWorker.getJsonFileString();
        JSONObject jObj = jsonFileWorker.getJsonObject(json);
        ArrayList<DBUser> contactsList = jsonFileWorker.jsonFileParser(jObj);
        this.contacts = contactsList;
        System.out.println("----------------contacts size: " + contacts.size());
        Log.i("URLContactList", "HERE");
        return contactsList;
	}
}
