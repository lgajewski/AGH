package pl.gajewski.chatapp.adapters.drawer;

import android.app.Fragment;

public class NavItem {

    private String mTitle;
    private String mSubtitle;
    private Fragment mFragmentLayout;
    private int mIcon;
 
    public NavItem(String title, String subtitle, Fragment layout, int icon) {
        mTitle = title;
        mSubtitle = subtitle;
        mIcon = icon;
        mFragmentLayout = layout;
    }

    public NavItem(String title, Fragment layout, int icon) {
        this(title, "", layout, icon);
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSubtitle() {
        return mSubtitle;
    }

    public int getIcon() {
        return mIcon;
    }

    public Fragment getFragmentLayout() {
        return mFragmentLayout;
    }
}