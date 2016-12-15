package hva.flashdiscount.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import hva.flashdiscount.R;
import hva.flashdiscount.fragment.MapViewFragment;
import hva.flashdiscount.model.Category;

/**
 * Created by chrisvanderheijden on 13/12/2016.
 */

public class CategoryAdapter extends BaseAdapter implements View.OnClickListener {

    private ArrayList<Category> categoryArrayList;
    private final LayoutInflater inf;
    private Context thisContext;
    private MapViewFragment mapViewFragment;
    private Category category;

    public CategoryAdapter(Context context, int resource, ArrayList<Category> categoryArrayList, MapViewFragment mapViewFragment) {
        //super(context, resource, categoryArrayList);
        super();
        this.mapViewFragment = mapViewFragment;
        thisContext = context;
        this.categoryArrayList = new ArrayList<Category>();
        this.categoryArrayList.addAll(categoryArrayList);
        inf = LayoutInflater.from(context);
        inf.inflate(resource, null);
    }

    @Override
    public void onClick(View view) {

        ArrayList<Category> categoryList = categoryArrayList;
        for(int i=0;i < categoryList.size();i++){
            category = categoryList.get(i);
            if(category.isSelected()){
                Log.i("CAT", category.getName());
            }
        }
    }

    private class ViewHolder{
        TextView code;
        CheckBox name;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {

            category = categoryArrayList.get(position);
            LayoutInflater vi = (LayoutInflater) thisContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.category_list_child, null);

            holder = new ViewHolder();
            holder.code = (TextView) convertView.findViewById(R.id.category_name);
            holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
            holder.code.setText(category.getName());

            convertView.setTag(holder);

            holder.name.setOnClickListener( new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox checkBox = (CheckBox) v ;

                    category = (Category) checkBox.getTag();

                    category.setSelected(checkBox.isChecked());
                    if(checkBox.isChecked()){
                        mapViewFragment.displaySelectedMarkers();

                    } else {
                        mapViewFragment.displaySelectedMarkers();
                    }
                }
            });
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        category = categoryArrayList.get(position);
        //holder.code.setText(category.getCategoryName());
        holder.code.setText(category.getName());

        holder.name.setChecked(category.isSelected());
        holder.name.setTag(category);

        return convertView;

    }

    public int getCount(){
        return categoryArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return categoryArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return categoryArrayList.get(i).getCategoryId();
    }

    public boolean getItemChecked(int i){
        return categoryArrayList.get(i).isSelected();
    }


}
