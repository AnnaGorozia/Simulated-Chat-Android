package ro.octa.greendaosample.adapters;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ro.octa.greendaosample.MainActivity;
import ro.octa.greendaosample.R;
import ro.octa.greendaosample.UsersActivity;
import ro.octa.greendaosample.dao.DBMessage;

public class ChatAdapter extends ArrayAdapter<DBMessage> {

    private TextView messageText;
    private Activity context;
    private List<DBMessage> messages = new ArrayList<>();
    private LinearLayout wrapper;

    @Override
    public void add(DBMessage object) {
        messages.add(object);
        super.add(object);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public ChatAdapter(Activity context, ArrayList<DBMessage> messages) {
        super(context, R.layout.chat_list_item, messages);
        this.messages = messages;
        this.context = context;
    }

    public DBMessage getItem(int index) {
        return this.messages.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.chat_list_item, parent, false);
        }

        wrapper = (LinearLayout) row.findViewById(R.id.wrapper);

        DBMessage message = getItem(position);

        messageText = (TextView) row.findViewById(R.id.comment);

        messageText.setText(message.getMessage());

        messageText.setBackgroundResource(message.getFromId() != UsersActivity.getOwnerId() ? R.drawable.bubble_yellow : R.drawable.bubble_green);
        wrapper.setGravity(message.getFromId() != UsersActivity.getOwnerId() ? Gravity.LEFT : Gravity.RIGHT);

        return row;
    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

}