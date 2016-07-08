package ro.octa.greendaosample;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ro.octa.greendaosample.dao.DBUser;
import ro.octa.greendaosample.dao.DBUserDetails;
import ro.octa.greendaosample.manager.DatabaseManager;
import ro.octa.greendaosample.manager.IDatabaseManager;
import ro.octa.greendaosample.transport.FragmentListRefresher;

public class UserDetailsActivity extends Fragment implements FragmentListRefresher {


    private TextView displayName;
    private TextView phoneNumber;
    private ImageView userImage;
    private DBUser user;
    private IDatabaseManager databaseManager;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        this.getActivity().setContentView(R.layout.activity_user_details);
        init();
        setupDefaults();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_user_details, container, false);
    }

    private void init() {
        // init database manager
        databaseManager = new DatabaseManager(this.getActivity());

        long userId = UsersActivity.getOwnerId();//getIntent().getLongExtra("userID", -1L);
        if (userId != -1) {
            user = databaseManager.getUserById(userId);
        }

//        if (getActionBar() != null)
//            getActionBar().setDisplayHomeAsUpEnabled(true);

        displayName = (TextView) getView().findViewById(R.id.user_display_name);
        phoneNumber = (TextView) getView().findViewById(R.id.user_phone_number);
        userImage = (ImageView) getView().findViewById(R.id.user_image);
    }

    private void setupDefaults() {
        if (user != null) {
            displayName.setText(user.getDisplayName());
            phoneNumber.setText(user.getPhoneNumber());
            userImage.setImageBitmap(BitmapFactory.decodeByteArray(user.getAvatar(), 0, user.getAvatar().length));
            DBUserDetails userDetails = user.getDetails();
            if (userDetails != null) {
            }
        }
    }

    @Override
    public void refreshList() {

    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        switch (item.getItemId()) {
//            // Respond to the action bar's Up/Home button
//            case android.R.id.home:
//                Intent returnIntent = new Intent();
//                this.getActivity().setResult(this.getActivity().RESULT_CANCELED, returnIntent);
//                this.getActivity().finish();
//                return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
