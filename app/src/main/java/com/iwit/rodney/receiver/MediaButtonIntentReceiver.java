package com.iwit.rodney.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.iwit.rodney.App;
import com.iwit.rodney.service.MusicManagerService;

public class MediaButtonIntentReceiver extends BroadcastReceiver {

    private KeyEvent event;

    @Override
    public void onReceive(Context context, Intent intent) {
        String intentAction = intent.getAction();
//        Toast.makeText(context, "蓝牙拦截了", Toast.LENGTH_SHORT).show();
        if (!Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
            return;
        }
        event = (KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
        if (event == null) {
            return;
        }
        try {
            int keyCode = event.getKeyCode();
            
            switch (keyCode) {
			case KeyEvent.KEYCODE_MEDIA_PLAY:
				break;
			case KeyEvent.KEYCODE_MEDIA_PAUSE:
				break;
			case KeyEvent.KEYCODE_MEDIA_NEXT:
			//	App.nextMusic();
//				Toast.makeText(context, "上一首:"+keyCode, Toast.LENGTH_SHORT).show();
				Intent nextIntent = new Intent("com.iwit.receiver.notification");
				nextIntent.putExtra("action", 4000);
				context.sendBroadcast(nextIntent);
				
				break;
			case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
			//	App.priorMusic();
//				Toast.makeText(context, "下一首:"+keyCode, Toast.LENGTH_SHORT).show();
				Intent preIntent = new Intent("com.iwit.receiver.notification");
				preIntent.putExtra("action", 1000);
				context.sendBroadcast(preIntent);
				break;
			}
            abortBroadcast();
        } catch (Exception e) {
            Log.d("TEST", "THIS IS NOT GOOD");
        }
      
    }
}