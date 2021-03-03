package br.pucrio.inf.lac.mhub.components.actions;

import android.content.Context;
import android.media.MediaPlayer;

import br.pucrio.inf.lac.mhub.R;
import br.pucrio.inf.lac.mhub.components.actions.contract.Action;

/**
 * Created by hei on 03/11/16.
 * Implements an action that starts a sound in the Mobile Hub
 */
public class SoundAction implements Action {
    @Override
    public void execute( Context ac ) {
        MediaPlayer mp = MediaPlayer.create( ac, R.raw.successful );

        if( mp != null ) {
            mp.setOnCompletionListener( new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion( MediaPlayer mp ) {
                    mp.release();
                }
            });
            mp.start();
        }
    }
}
