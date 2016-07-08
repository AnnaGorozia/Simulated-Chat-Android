package ro.octa.greendaosample.adapters;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ro.octa.greendaosample.R;
import ro.octa.greendaosample.UsersActivity;
import ro.octa.greendaosample.dao.DBMessage;
import ro.octa.greendaosample.dao.DBUser;
import ro.octa.greendaosample.manager.DatabaseManager;

public class RecentListAdapter extends ArrayAdapter<DBUser> {

    private final Activity context;
    private final ArrayList<DBUser> users;

    public RecentListAdapter(Activity context, ArrayList<DBUser> users) {
        super(context, R.layout.raw_layout_user, users);
        this.users = users;
        this.context = context;
    }

    @Override
    public int getViewTypeCount() {
        if (getCount() == 0) return 1;
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.raw_layout_user, null);
            // configure view holder
            UserViewHolder viewHolder = new UserViewHolder();

            if (rowView != null) {
                viewHolder.userId = (TextView) rowView.findViewById(R.id.user_id);
                viewHolder.userEmail = (TextView) rowView.findViewById(R.id.user_email);
                viewHolder.imageView = (ImageView) rowView.findViewById(R.id.image_view);
                rowView.setTag(viewHolder);
            }
        }

        // fill data
        if (rowView != null) {
            final UserViewHolder holder = (UserViewHolder) rowView.getTag();
            final DBUser user = users.get(position);
            if (user != null) {

                holder.userId.setText(String.valueOf(user.getId()));
                holder.userEmail.setText(user.getDisplayName());
                holder.imageView.setImageBitmap(BitmapFactory.decodeByteArray(user.getAvatar(), 0, user.getAvatar().length));

                DBMessage message = DatabaseManager.getInstance(UsersActivity.getContextOver()).lastMessage(UsersActivity.getOwnerId(), user.getId());
                if (message == null || message.getIncoming() != 1) return rowView;
                rowView.setBackgroundColor(Color.RED);
            }
        }

        return rowView;
    }

    @Override
    public long getItemId(int position) {
        DBUser item = getItem(position);
        return item.getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    static class UserViewHolder {
        public TextView userId;
        public ImageView imageView;
        public TextView userEmail;
    }

}
