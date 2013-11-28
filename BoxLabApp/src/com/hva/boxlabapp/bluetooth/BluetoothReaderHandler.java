package com.hva.boxlabapp.bluetooth;

import android.os.Handler;
import android.os.Message;

public class BluetoothReaderHandler extends Handler{
	
	private String data;
	
	@Override
	public void handleMessage(Message msg){
		data = msg.getData().getString(ConnectToRaspberryPi.JSON);
	}
	
	public String getData(){
		return data;
	}
}
