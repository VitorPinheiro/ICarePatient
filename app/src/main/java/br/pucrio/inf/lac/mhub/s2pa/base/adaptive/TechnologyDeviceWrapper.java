package br.pucrio.inf.lac.mhub.s2pa.base.adaptive;

import java.util.List;
import java.util.UUID;

import br.pucrio.inf.lac.mhub.s2pa.base.TechnologyDevice;
import br.pucrio.inf.lac.mhub.s2pa.base.TechnologySensor;
import component.AbstractComponentWrapper;

/**
 * Created by Luis on 25/02/15.
 * Wrapper for the device adaptive middleware
 */
public class TechnologyDeviceWrapper extends AbstractComponentWrapper<TechnologyDevice, Object, Object> implements TechnologyDevice {
    @Override
    public TechnologySensor getServiceByName(final String serviceName) {
        super.readLock();

        final TechnologySensor result = this.componentInstance.getServiceByName( serviceName );

        super.readUnlock();

        return result;
    }

    @Override
    public List<TechnologySensor> getServiceByUUID(final UUID uuid) {
        super.readLock();

        final List<TechnologySensor> result = this.componentInstance.getServiceByUUID( uuid );

        super.readUnlock();

        return result;
    }

    @Override
    public TechnologySensor getCharacteristicByUUID( UUID uuid ) throws UnsupportedOperationException {
        super.readLock();

        final TechnologySensor result = this.componentInstance.getCharacteristicByUUID( uuid );

        super.readUnlock();

        return result;
    }
}
