package br.pucrio.inf.lac.mhub.components.actions.contract;

import android.content.Context;

/**
 * Created by hei on 03/11/16.
 * Interface for the actions that the Mobile Hub can execute
 */
public interface Action {
    /**
     * It executes an action
     * @param ac The context of the application
     */
    void execute( Context ac );
}
