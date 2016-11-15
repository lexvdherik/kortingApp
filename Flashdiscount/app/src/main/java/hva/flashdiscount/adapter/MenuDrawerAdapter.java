package hva.flashdiscount.adapter;

import android.app.Activity;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;

import hva.flashdiscount.R;
import hva.flashdiscount.model.User;

/**
 * Created by Maiko on 15-11-2016.
 */

public class MenuDrawerAdapter  implements Adapter {

    private final LayoutInflater inf;
    private User user;

    public MenuDrawerAdapter(User user, Activity activity){
        this.user = user;
        inf = LayoutInflater.from(activity);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            view = inf.inflate(R.layout.nav_header, viewGroup, false);
        }
        ((TextView)view.findViewById(R.id.naam)).setText(user.getName());
        ((TextView)view.findViewById(R.id.email)).setText(user.getEmailAddress());

        return null;
    }

    @Override
    public int getItemViewType(int i) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
