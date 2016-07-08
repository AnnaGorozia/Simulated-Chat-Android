package ro.octa.greendaosample.asynchtasks;

import java.util.ArrayList;

import ro.octa.greendaosample.UsersActivity;
import ro.octa.greendaosample.dao.DBUser;
import ro.octa.greendaosample.manager.DatabaseManager;

public class DBContactListDownloaderTask extends ContactListDownloaderTask {
    @Override
    protected ArrayList<DBUser> doInBackground(Void... params) {
        return DatabaseManager.getInstance(UsersActivity.getContextOver()).listUsers();
    }

}
