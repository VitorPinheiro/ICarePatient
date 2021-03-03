package br.pucrio.inf.lac.mhub.ui;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import br.pucrio.inf.lac.mhub.R;
import br.pucrio.inf.lac.mhub.ui.adapters.EventsListAdapter;
import br.pucrio.inf.lac.mhub.components.AppUtils;
import br.pucrio.inf.lac.mhub.models.locals.EventData;
import de.greenrobot.event.EventBus;

/**
 * This fragment handle the events detected by the MEPA Service
 */
public class MHubEvents extends Fragment {
    /** DEBUG */
    private static final String TAG = MHubEvents.class.getSimpleName();

    /** The context object */
    private Context ac;

    /** GUI Controllers */
    private View mRootView;
    private EventsListAdapter mEventsAdapter;
    private ListView mListView;

    /** Events */
    private List<EventData> mEvents;

    /** SKS time to dismiss milliseconds */
    private static final int TIME_TO_DISMISS_EVENT = 5000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRootView = inflater.inflate(R.layout.fragment_events, container, false);

        setupGUI();
        updateData();

        return mRootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        // register to event bus
        EventBus.getDefault().register( this );
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
        getActivity().setTitle( R.string.title_fragment_mhub_events );

        mListView = (ListView) mRootView.findViewById( R.id.eventsListView );
    }

    private void updateData() {
        mEvents = new ArrayList<>();
        mEventsAdapter = new EventsListAdapter( ac, R.layout.list_item, mEvents );
        mListView.setAdapter( mEventsAdapter );
    }

    @SuppressWarnings("unused") // it's actually used to receive events from the MEPA Service
    public void onEventMainThread( final EventData eventData ){
        AppUtils.logger( 'i', TAG, ">> NEW_EVENT_MSG" );

        if( eventData != null && !mEvents.contains( eventData ) ) {
            mEvents.add( eventData );
            mEventsAdapter.notifyDataSetChanged();

            // Destroy the event after some interval of time
            final Handler t = new Handler();
            t.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mEvents.remove( eventData );
                    mEventsAdapter.notifyDataSetChanged();
                }
            }, TIME_TO_DISMISS_EVENT );
        }
    }
}
