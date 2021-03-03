package br.pucrio.inf.lac.mhub.s2pa.technologies.ble.operations;

/**
 * Indicates that no more operations are going to be executed
 */
public class BLENone extends BLEOperation {
    public BLENone() {
        super( null );
    }

    @Override
    public boolean execute() {
        return false;
    }
}
