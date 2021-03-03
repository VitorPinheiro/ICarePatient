package br.pucrio.inf.lac.mhub.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import br.pucrio.inf.lac.mhub.R;
import br.pucrio.inf.lac.mhub.models.locals.EventData;

/**
 * Created by luis on 11/03/15.
 * Adapter for the ListView (Events)
 */
public class EventsListAdapter extends ArrayAdapter<EventData> {
    /** The context object */
    private Context ac;

    /** GUI details */
    private int mLayoutResourceId;

    /** Data */
    private List<EventData> objects;

    public EventsListAdapter(Context mContext, int layoutResourceId, List<EventData> objects) {
        super(mContext, layoutResourceId, objects);
        this.ac = mContext;
        this.mLayoutResourceId = layoutResourceId;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if( convertView == null ) {
            LayoutInflater inflater = (LayoutInflater) ac.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            convertView = inflater.inflate( mLayoutResourceId, parent, false );
        }

        EventData event = objects.get( position );

        // Get the current item and set its values
        TextView eventText = (TextView) convertView.findViewById( R.id.eventText );
        eventText.setText( event.toString() );

        return convertView;
    }
}
