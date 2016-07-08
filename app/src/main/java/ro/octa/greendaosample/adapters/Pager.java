package ro.octa.greendaosample.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import ro.octa.greendaosample.RecentActivity;
import ro.octa.greendaosample.Tab1;
import ro.octa.greendaosample.Tab2;
import ro.octa.greendaosample.Tab3;
import ro.octa.greendaosample.UserDetailsActivity;
import ro.octa.greendaosample.UsersActivity;

public class Pager extends FragmentStatePagerAdapter {

    int tabCount;

    public Pager(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount= tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
//                Tab1 tab1 = new Tab1();
                UsersActivity tab2 = new UsersActivity();
//                UsersActivity tab1 = new UsersActivity();
                return tab2;
            case 1:
//                Tab2 tab2 = new Tab2();
                RecentActivity tab1 = new RecentActivity();
                return tab1;
            case 2:
//                Tab3 tab3 = new Tab3();
                UserDetailsActivity tab3 = new UserDetailsActivity();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}