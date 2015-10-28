package pl.gajewski.chatapp.adapters.users;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pl.gajewski.chatapp.R;

public class UserListAdapter extends ArrayAdapter<UserItem> {

    private final Activity activity;
    private final List<UserItem> list;
    private Filter filter;


    public UserListAdapter(Activity activity, List<UserItem> list) {
        super(activity, R.layout.user_item, list);
        this.activity = activity;
        this.list = list;
    }

    class ViewHolder {
        public TextView username;
        public ImageView icon;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.user_item, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.username = (TextView) rowView.findViewById(R.id.user_item_title);
            viewHolder.icon = (ImageView) rowView.findViewById(R.id.user_item_icon);
            rowView.setTag(viewHolder);
        }

        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();
        UserItem userItem = list.get(position);
        holder.username.setText(userItem.getUsername());
        holder.icon.setImageResource(userItem.getIcon());
        return rowView;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new UserItemFilter();
        }
        return filter;
    }

    private class UserItemFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint.toString().length() > 0) {
                ArrayList<UserItem> filteredItems = new ArrayList<>();

                synchronized (list) {
                    for (int i = 0; i < list.size(); i++) {
                        UserItem userItem = list.get(i);
                        if (userItem.getUsername().toLowerCase().contains(constraint))
                            filteredItems.add(userItem);
                    }
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            } else {
                synchronized (this) {
                    result.values = new ArrayList<>(list);
                    result.count = list.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            List<UserItem> filteredItems = (ArrayList<UserItem>) results.values;
            notifyDataSetChanged();
            clear();

            for (int i = 0, l = filteredItems.size(); i < l; i++)
                add(filteredItems.get(i));

            notifyDataSetInvalidated();
        }
    }

}