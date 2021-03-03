package br.pucrio.inf.lac.mhub.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.pucrio.inf.lac.mhub.R;
import br.pucrio.inf.lac.mhub.models.NavigationItem;

/**
 * Created by luis on 25/02/15.
 * Adapter for the drawer
 */
public class NavigationDrawerAdapter extends ArrayAdapter<NavigationItem> {
    /**
     * Default Constructor
     * @param context The context of the activity
     * @param objects The objects of the drawer (navigation items)
     */
    public NavigationDrawerAdapter(Context context, List<NavigationItem> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if( convertView == null ) {
            LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate( R.layout.drawer_item, parent, false );
        }

        // Sets the icon and the name of the item
        ImageView icon = (ImageView) convertView.findViewById( R.id.icon );
        TextView name  = (TextView) convertView.findViewById( R.id.name );

        // Gets the current item and set the values
        NavigationItem item = getItem( position );
        icon.setImageResource( item.getIconId() );
        name.setText( item.getName() );

        return convertView;
    }
}
