package br.pucrio.inf.lac.mhub.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;

import br.pucrio.inf.lac.mhub.components.AppUtils;
import br.pucrio.inf.lac.mhub.models.DeviceModel;
import br.pucrio.inf.lac.mhub.s2pa.base.TechnologyDevice;
import de.greenrobot.event.EventBus;

/**
 * Gets the module for the different M-OBJs when requested,
 * respond with a ResultReceiver to the technology (e.g. BLE)
 */
public class AdaptationService extends Service {
    /** DEBUG */
    private static final String TAG = AdaptationService.class.getSimpleName();

    /** The context object */
    private Context ac;

    /** Adaptive Client Manager */
    //private ClientManager clientManager;

    /** This is the object that receives interactions from clients */
    private final IBinder mBinder = new LocalBinder();

    /** Package of the device structure */
    private static final String DEVICE_PACKAGE = "br.pucrio.inf.lac.mhub.s2pa.technologies.ble.devices.";

    /** It is the ID of the application (the package) used for the extras */
    private static final String PARAMS_APPID = AdaptationService.class.getPackage().getName();

    /** Param keys for the result */
    public static final String EXTRA_RESULT = PARAMS_APPID + "EXTRA_RESULT";

    /** Status code */
    public static final int FAILED  = 0;
    public static final int SUCCESS = 1;

    /**
     * Class for clients to access. Because we know this service always runs in
     * the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public AdaptationService getService() {
            return AdaptationService.this;
        }
    }

    /**
     * It gets called when the service is started.
     *
     * @param i The intent received.
     * @param flags Additional data about this start request.
     * @param startId A unique integer representing this specific request to start.
     * @return Using START_STICKY the service will run again if got killed by
     * the service.
     */
    @Override
    public int onStartCommand(Intent i, int flags, int startId) {
        AppUtils.logger( 'i', TAG, ">> Started" );
        // get the context
        ac = AdaptationService.this;
        // register to event bus
        EventBus.getDefault().register( this );
        // Configurations
        bootstrap();
        // if the service is killed by Android, service starts again
        return START_STICKY;
    }

    /**
     * When the service get destroyed by Android or manually.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        AppUtils.logger( 'i', TAG, ">> Destroyed" );
        // unregister from event bus
        EventBus.getDefault().unregister( this );
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * The bootstrap for the location service
     */
    private void bootstrap() {
        // create the UUID for this device if there is not one
        if( AppUtils.getUuid( ac ) == null ) {
            Boolean saved = AppUtils.createSaveUuid( ac );
            if( !saved )
                AppUtils.logger( 'e', TAG, ">> UUID not saved to SharedPrefs" );
        }
        /*UUID uuid = AppUtils.getUuid( ac );

        // initialize the connection for the adaptation
        try {
            final Map<String, Class<? extends AbstractComponentWrapper<? extends IComponent<?, ?>, ?, ?>>> wrapperMapper = new TreeMap<>();
            wrapperMapper.put( TechnologyDevice.class.getCanonicalName(), TechnologyDeviceWrapper.class );
            clientManager = new ClientManager( wrapperMapper, ConnectionService.getConnection(), uuid, ac );
        } catch(IOException e) {
            e.printStackTrace();
        }*/
    }

    @SuppressWarnings("unused") // it's actually used to receive the device from a technology
    public void onEventAsync( DeviceModel device ) {
        AppUtils.logger( 'i', TAG, ">> NEW_DEVICE_MSG" );
        final String deviceName = device.name;
        final ResultReceiver receiver = device.receiver;

        if( deviceName != null ) {
            try {
                final String componentClass = DEVICE_PACKAGE + deviceName.replaceAll( "\\s+", "" );

                Class<?> c = Class.forName( componentClass );
                TechnologyDevice componentInstance = (TechnologyDevice) c.newInstance();
                        /*clientManager.loadJarByComponentClassName( componentClass, true );
                        final TechnologyDevice componentInstance = clientManager.getAdaptationManager().
                                getComponentManager().createComponentInstance(
                                TechnologyDevice.class.getCanonicalName(),
                                componentClass );*/

                Bundle resultData = new Bundle();
                resultData.putSerializable( EXTRA_RESULT, componentInstance );
                receiver.send( SUCCESS, resultData );
            } catch( Throwable e ) {
                AppUtils.logger( 'e', TAG, e.getMessage() );
                receiver.send( FAILED, null );
            }
        } else
            receiver.send( FAILED, null );
    }
}
