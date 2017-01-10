package utilities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

import www.ntej.com.worldclocks.R;

/**
 * Created by navatejareddy on 1/6/17.
 * Credits https://gist.github.com/fjfish/3024308
 */

public class SearchableAdapter extends ArrayAdapter<String> implements Filterable {

   // private Context context;

    private ArrayList<String> originalData = null;
    private ArrayList<String>filteredData = null;
    private ItemFilter mFilter = new ItemFilter();

    private int layoutResource;

    public SearchableAdapter(Context context,int layoutResource, ArrayList<String> data)
    {
        super(context,layoutResource,data);

        this.filteredData = data;
        this.originalData = data;
        this.layoutResource = layoutResource;
    }


    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return filteredData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        ViewHolder holder;

        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutResource,null);

            holder = new ViewHolder();

            holder.text = (TextView)convertView.findViewById(R.id.textView);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.text.setText(filteredData.get(position));

        return convertView;
    }

    static class ViewHolder
    {
        TextView text;
    }
    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public class ItemFilter extends Filter
    {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            String filterString = charSequence.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final ArrayList<String> list = originalData;

            int count = list.size();
            final ArrayList<String> nlist =new ArrayList<String>(count);

            String filterableString;

            for(int i =0; i< count; i++)
            {
                filterableString = list.get(i);
                if(filterableString.toLowerCase().contains(filterString))
                {
                    nlist.add(filterableString);
                }
            }
            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            filteredData = (ArrayList<String>) filterResults.values;
            notifyDataSetChanged();

        }
    }
}
