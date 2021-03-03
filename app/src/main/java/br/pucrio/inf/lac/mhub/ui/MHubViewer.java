package br.pucrio.inf.lac.mhub.ui;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;

import br.pucrio.inf.lac.mhub.R;
import br.pucrio.inf.lac.mhub.ui.adapters.MObjectsListAdapter;
import br.pucrio.inf.lac.mhub.components.AppUtils;
import br.pucrio.inf.lac.mhub.components.MOUUID;
import br.pucrio.inf.lac.mhub.models.ConnectionData;
import br.pucrio.inf.lac.mhub.models.locals.LocationData;
import br.pucrio.inf.lac.mhub.models.locals.SensorData;
import de.greenrobot.event.EventBus;

/**
 * Viewer for the Mobile Hub
 * @author Luis Talavera
 */
public class MHubViewer extends Fragment {
    /** DEBUG */
    //private static final String TAG = MHubViewer.class.getSimpleName();

    /** The context object */
    private Context ac;

    /** GUI Controllers */
    private View mRootView;
    private MObjectsListAdapter mObjectsAdapter;
    private ExpandableListView mExpListView;
    private TextView mLatitudeView;
    private TextView mLongitudeView;
    private ImageView mConnStateView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mRootView = inflater.inflate(R.layout.fragment_viewer, container, false);

        setupGUI();
        updateData();

        return mRootView;
	}

    @Override
    public void onStart() {
        super.onStart();
        // register to event bus
        EventBus.getDefault().registerSticky( this );
    }

    @Override
    public void onStop() {
        super.onStop();
        // unregister from event bus
        EventBus.getDefault().unregister( this );
    }

    private void setupGUI() {
        // get the context
        ac = getActivity();
        // set the title
        getActivity().setTitle( R.string.title_fragment_mhub_viewer );

        mExpListView   = (ExpandableListView) mRootView.findViewById(R.id.lvExp);
        mLatitudeView  = (TextView) mRootView.findViewById(R.id.textLatitude);
        mLongitudeView = (TextView) mRootView.findViewById(R.id.textLongitude);
        mConnStateView = (ImageView) mRootView.findViewById(R.id.connectionState);

        if( !AppUtils.getCurrentLocationService( ac ) ) {
            Double latitude  = AppUtils.getLocationLatitude( ac );
            Double longitude = AppUtils.getLocationLongitude( ac );

            if( latitude != null && longitude != null ) {
                mLatitudeView.setText( String.valueOf( latitude ) );
                mLongitudeView.setText( String.valueOf( longitude ) );
            }
        }

        if( AppUtils.isConnected( ac ) )
            mConnStateView.setImageResource( R.drawable.con_on );
    }

    private void updateData() {
        mObjectsAdapter = new MObjectsListAdapter( ac, mExpListView );
        mExpListView.setAdapter( mObjectsAdapter );
    }

    @SuppressWarnings("unused") // it receives events from the Connection Listener
    public void onEventMainThread( ConnectionData connection ) {
        String state = connection.getState();
        if( state.equals( ConnectionData.CONNECTED ) )
            mConnStateView.setImageResource( R.drawable.con_on );
        else if( state.equals( ConnectionData.DISCONNECTED ) )
            mConnStateView.setImageResource( R.drawable.con_off );
    }

    @SuppressWarnings("unused") // it's actually used to receive events from the Location Service
    public void onEventMainThread( LocationData locData ){
        if( locData != null ) {
            mLatitudeView.setText( String.valueOf( locData.getLatitude() ) );
            mLongitudeView.setText( String.valueOf( locData.getLongitude() ) );
        }
    }

    @SuppressWarnings("unused") // it's actually used to receive events from the S2PA Service
    public void onEventMainThread( SensorData sensorData ) {
        if( sensorData != null ) {
            Double latitude = sensorData.getLatitude();
            Double longitude = sensorData.getLongitude();

            if( latitude != null && longitude != null ) {
                mLatitudeView.setText( String.valueOf( latitude ) );
                mLongitudeView.setText( String.valueOf( longitude ) );
            }

            switch( sensorData.getAction() ) {
                case SensorData.CONNECTED:
                    MOUUID mobj = MOUUID.fromString( sensorData.getMouuid() );
                    mObjectsAdapter.addGroup( mobj.toString() );
                    break;

                case SensorData.DISCONNECTED:
                    mobj = MOUUID.fromString( sensorData.getMouuid() );
                    mObjectsAdapter.removeGroup( mobj.toString() );
                    break;

                case SensorData.READ:
                    mobj = MOUUID.fromString( sensorData.getMouuid() );
                    String groupName   = mobj.toString();
                    String childName   = sensorData.getSensorName() + ":";
                    String childValues = Arrays.toString( sensorData.getSensorValue() );
                    Double rssi        = sensorData.getSignal();

                    mObjectsAdapter.addGroup( groupName );
                    mObjectsAdapter.setRSSI( groupName, rssi );
                    mObjectsAdapter.addChild( groupName, childName, childValues );
                    break;
            }
        }
    }
}
