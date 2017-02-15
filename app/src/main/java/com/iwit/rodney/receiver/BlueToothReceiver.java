package com.iwit.rodney.receiver;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.iwit.rodney.App;
import com.iwit.rodney.R;
import com.iwit.rodney.comm.tools.PlayMedia;
import com.iwit.rodney.service.MusicManagerService;
import com.iwit.rodney.ui.view.ArarmDialog;

public class BlueToothReceiver extends BroadcastReceiver
{
	private String btMessage="";
	//震动器̬
	private Vibrator mVibrator;
	private PlayMedia mp;
	@Override
	public void onReceive(Context context, Intent intent)
	{
        String action = intent.getAction();
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        if("com.iwit.test".equals(action)){
        	Log.v("onReceive", "com.iwit.test");
        }    
        Toast.makeText(context, device.getName() + " 发现设备", Toast.LENGTH_LONG).show();
        if(device == null || device.getName() == null){
        	return;
        }
        
        if(BluetoothDevice.ACTION_FOUND.equals(action)) 
        {
            short rssi = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI); 
            Toast.makeText(context, device.getName() + " 发现设备:"+rssi, Toast.LENGTH_LONG).show();
            btMessage=device.getName()+"发现设备";
        }
        else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) 
        {
            Toast.makeText(context, device.getName() + "连接设备", Toast.LENGTH_LONG).show();
            btMessage=device.getName()+"连接设备";
        }
        
        else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) 
        {
            Toast.makeText(context, device.getName() + "正在连接设备", Toast.LENGTH_LONG).show();
            btMessage=device.getName()+"正在连接设备";
        }
        else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) 
        {
        	if(device.getName().contains("超级飞侠穿戴故事机")){
        		Toast.makeText(context, device.getName() + "设备断开",
                		Toast.LENGTH_LONG).show();
        		boolean isBlueOpen;
        		Object blue = App.getSp().get("blue_ararm_switch");
        		if(blue == null){
        			isBlueOpen = true;
        		}else{
        			isBlueOpen = Boolean.valueOf(String.valueOf(blue));
        		}
        		if(!isBlueOpen){
        			return;
        		}
                btMessage=device.getName()+"设备断开";
                mVibrator = ( Vibrator ) context.getSystemService(Service.VIBRATOR_SERVICE);  
                mVibrator.vibrate( new long[]{100,10,100,15000},-1);  
                mp = PlayMedia.getInstance(context);
                mp.playMusicMedia(R.raw.ararm);
                final ArarmDialog dialog = new ArarmDialog(context);
            	dialog.show();
            	Intent pauseIntent = new Intent(action);
    			pauseIntent.setAction(MusicManagerService.action);
    			pauseIntent.putExtra("action", MusicManagerService.PAUSE);
    			context.sendBroadcast(pauseIntent);
        	}
        } else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){
        	 short rssi = intent.getExtras().getShort( BluetoothDevice.EXTRA_RSSI); 
        	 Toast.makeText(context, device.getName() + "信号强度："+rssi,Toast.LENGTH_LONG).show();
        	 Log.v("qh", "信号强度："+rssi);
        }
	}
	//动画对话框
	
}
