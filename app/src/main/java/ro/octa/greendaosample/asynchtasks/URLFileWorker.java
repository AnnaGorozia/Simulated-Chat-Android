package ro.octa.greendaosample.asynchtasks;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import ro.octa.greendaosample.MainActivity;
import ro.octa.greendaosample.R;
import ro.octa.greendaosample.UsersActivity;
import ro.octa.greendaosample.dao.DBUser;

public class URLFileWorker {
    private String jsonURL;

    private static final String CONTACT_LIST = "contactList";
    private static final String ID = "id";
    private static final String DISPLAY_NAME = "displayName";
    private static final String PHONE_NUMBER = "phoneNumber";
    private static final String AVATAR = "avatarImg";

    public URLFileWorker(String url){
        this.jsonURL = url;
    }

    public void setURL(String URL) {
        this.jsonURL = URL;
    }

    public ArrayList<DBUser> jsonFileParser(JSONObject json){
        ArrayList<DBUser> contactsList = new ArrayList<DBUser>();
        try {
            JSONArray contacts = json.getJSONArray(CONTACT_LIST);
            Log.i("contacts length is: ", ""+contacts.length());
            for(int i = 0; i < contacts.length(); i++){
                JSONObject jsonObject = contacts.getJSONObject(i);
                DBUser contact = new DBUser();
                contact.setAvatar(getDefaultImage());
                contact.setId(Long.parseLong(jsonObject.getString(ID)));
                contact.setDisplayName(jsonObject.getString(DISPLAY_NAME));
                contact.setPhoneNumber(jsonObject.getString(PHONE_NUMBER));
                contact.setAvatarUrl(jsonObject.getString(AVATAR));
                contactsList.add(contact);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return contactsList;
    }

    private byte[] getDefaultImage() {
        Resources res = UsersActivity.getContextOver().getResources();
        Drawable drawable = res.getDrawable(R.drawable.ic_launcher);
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitMapData = stream.toByteArray();
        return bitMapData;
    }

    public InputStream getInputStream(){
        InputStream input = null;
        try {
            URL url = new URL(jsonURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            input = connection.getInputStream();

        }catch (Exception e){
            e.printStackTrace();
        }
        return input;
    }

    public JSONObject getJsonObject(String json){
        JSONObject jObj = null;
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        return jObj;
    }

    public String getJsonFileString(){
        String json = "";
        InputStream inStream = getInputStream();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inStream, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            inStream.close();
            json = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        return json;
    }

}
