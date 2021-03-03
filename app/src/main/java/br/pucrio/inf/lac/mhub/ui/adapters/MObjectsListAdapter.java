package br.pucrio.inf.lac.mhub.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import br.pucrio.inf.lac.mhub.R;

/**
 * Created by luis on 11/03/15.
 * Adapter for the ListView (Mobile Objects)
 */
public class MObjectsListAdapter extends BaseExpandableListAdapter {
	/** The context object */
	private Context ac;
	
	/** Data */
    private List<String> listDataHeader; // header titles for parent
    private HashMap<String, String> listRSSIValues;
    private HashMap<String, List<String>> listDataChild;
    private HashMap<String, String> listDataValues;
    
    /** GUI */
    private ExpandableListView expListView;
 
    public MObjectsListAdapter(Context context, ExpandableListView expListView) {
        this.ac = context;
        this.expListView = expListView;

        listDataHeader = new ArrayList<>();
        listDataChild  = new HashMap<>();
        listDataValues = new HashMap<>();
        listRSSIValues = new HashMap<>();
    }
 
    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return listDataChild.get( listDataHeader.get( groupPosition ) ).get( childPosititon );
    }
 
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
 
	@Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
    	final String childText = (String) getChild( groupPosition, childPosition );
    	final String childRaw  = listDataValues.get( listDataHeader.get( groupPosition ) + childText );
 
        if( convertView == null ) {
            LayoutInflater inflaInflater = (LayoutInflater) ac.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            convertView = inflaInflater.inflate( R.layout.exp_list_item, parent, false );
        }

        // Parse the data of the child
        String childValue = "";
        try {
			JSONArray data = new JSONArray( childRaw );
			if( data.length() != 0 ) {
				DecimalFormat format = new DecimalFormat( "0.00" );
				childValue += format.format( data.get( 0 ) );
				for( int i = 1; i < data.length(); i++ )
					childValue += " | " + format.format( data.get( i ) );
			}
		} catch( JSONException e ) {
			e.printStackTrace();
		}

        // Gets the current item and set the values
        TextView txtListChild = (TextView) convertView.findViewById( R.id.lblListItem );
        TextView txtListValue = (TextView) convertView.findViewById( R.id.lblListItemValue );

        txtListChild.setText( childText );
        txtListValue.setText( childValue );
        
        return convertView;
    }
 
    @Override
    public int getChildrenCount(int groupPosition) {
        return listDataChild.get( listDataHeader.get( groupPosition ) ).size();
    }
 
    @Override
    public Object getGroup(int groupPosition) {
        return listDataHeader.get( groupPosition );
    }
 
    @Override
    public int getGroupCount() {
        return listDataHeader.size();
    }
 
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
 
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final String headerTitle = (String) getGroup( groupPosition );
        final String rssi        = listRSSIValues.get( headerTitle );

        if( convertView == null ) {
            LayoutInflater inflaInflater = (LayoutInflater) ac.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            convertView = inflaInflater.inflate( R.layout.exp_list_group, parent, false );
        }

        // Get the current item and set its values
        TextView lblListHeader = (TextView) convertView.findViewById( R.id.lblListHeader );
        TextView lblListInfo = (TextView) convertView.findViewById( R.id.lblListInfo );

        lblListHeader.setText( headerTitle );

        // RSSI can be null, so we only set it when we have its value
        if( rssi != null ) {
        	lblListInfo.setText( rssi );
        }

        return convertView;
    }
 
    @Override
    public boolean hasStableIds() {
        return false;
    }
 
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    /**
     * Adds a group to the expandable list
     * @param name The name (Mobile Object's name)
     */
    public void addGroup( String name ) {
        // Only add the first time
    	if( listDataHeader.contains( name ) )
    		return;
    	
    	listDataHeader.add( name );
    	listDataChild.put( name, new ArrayList<String>() );
    	expListView.expandGroup( listDataHeader.indexOf( name ) );
    	
    	notifyDataSetChanged();
    }

    /**
     * Remove a group from the expandable list
     * @param name Removes a Mobile Object by its anem
     */
    public void removeGroup(String name) {
    	List<String> childs = listDataChild.get( name );
    	
    	listDataHeader.remove( name );
    	listRSSIValues.remove( name );
    	listDataChild.remove( name );
    	
    	if( childs != null ) {
            for( String child : childs )
                listDataValues.remove( name + child );
    	}
    	
        notifyDataSetChanged();
    }

    /**
     * Add a child (sensor data) to the Mobile Object
     * @param groupName The name of the Mobile Object
     * @param childName The name of the sensor
     * @param childValues The values of the sensor
     */
    public void addChild( String groupName, String childName, String childValues ) {
    	if( !listDataHeader.contains( groupName ) )
    		return;
    	
    	List<String> childs = listDataChild.get( groupName );
    	if( !childs.contains( childName ) )
    		childs.add( childName );

        Collections.sort( childs );
    	listDataValues.put( groupName + childName, childValues );
    	
    	notifyDataSetChanged();
    }

    /**
     * Sets the RSSI(signal) to a Mobile Object
     * @param groupName The name of the Mobile Object
     * @param rssi The rssi level
     */
    public void setRSSI( String groupName, Double rssi ) {
    	if( !listDataHeader.contains( groupName ) || rssi == null )
    		return;
    	
    	listRSSIValues.put( groupName, rssi.toString() );
    	notifyDataSetChanged();
    }

    /**
     * Clear all the elements
     * */
    public void clear() {
    	listDataHeader.clear();
        listDataChild.clear();
        listDataValues.clear();
        listRSSIValues.clear();
        
        notifyDataSetChanged();
    }
}
