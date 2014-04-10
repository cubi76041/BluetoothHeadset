package com.learn2crack.speech;

import android.content.Context;

public interface Bluetooth {
    public void setContext(Context appContext);
    public void obtainProxy() throws Exception;
    public void releaseProxy() throws Exception;
    public void startVoiceRecognition();
    public void stopVoiceRecognition();
    public boolean isAvailable();
    
    public static final String BLUETOOTH_STATE = "cc.gtank.bt.BLUETOOTH_STATE";
}
