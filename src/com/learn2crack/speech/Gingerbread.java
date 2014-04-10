/*
 * The dirty, unsupported version. Targets API Level 10, Android 2.3.3
 */
package com.learn2crack.speech;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class Gingerbread implements Bluetooth {
    
    private Class<?> baseHeadset;
    private Context appContext;
    private Object btHeadset;
    private boolean connected;
    
    public Gingerbread() {
        try {
            baseHeadset = Class.forName("android.bluetooth.BluetoothHeadset");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public void setContext(Context context) {
        appContext = context;
    }
    
    public void obtainProxy() throws Exception {
        if(appContext != null) {
            try {
                Class<?> serviceListener = baseHeadset.getDeclaredClasses()[0]; //android.bluetooth.BluetoothHeadset$ServiceListener
                Object listenerProxy = java.lang.reflect.Proxy.newProxyInstance(serviceListener.getClassLoader(), 
                            new Class[] { serviceListener },
                            new InvocationHandler() {
                                @Override
                                public Object invoke(Object proxy, Method method,
                                        Object[] args) throws Throwable {
                                    String methodName = method.getName();
                                    if(methodName.equals("onServiceConnected")) {    
                                        connected = true;
                                    } else if(methodName.equals("onServiceDisconnected")) {
                                        connected = false;
                                    }
                                    return null;
                                }
                        
                    });
                Constructor<?> constructor = baseHeadset.getConstructor(Context.class, serviceListener);
                btHeadset = constructor.newInstance(appContext, listenerProxy);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new Exception("No application context supplied!");
        }
    }
    
    public void releaseProxy() {
        if(btHeadset != null) {
            try {
                Method close = btHeadset.getClass().getMethod("close");
                close.invoke(btHeadset, (Object[])null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public void startVoiceRecognition() {
        if(connected) {
            try {
                Method startVoice = btHeadset.getClass().getMethod("startVoiceRecognition");
                boolean started = (Boolean) startVoice.invoke(btHeadset, (Object[])null);
                if(started) {
                    Intent btState = new Intent(BLUETOOTH_STATE);
                    btState.putExtra("bluetooth_connected", true);
                    appContext.sendBroadcast(btState);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stopVoiceRecognition() {
        if(connected) {
            try {
                Method stopVoice = btHeadset.getClass().getMethod("stopVoiceRecognition");
                boolean stopped = (Boolean) stopVoice.invoke(btHeadset, (Object[])null);
                if(stopped) {
                    Intent btState = new Intent(BLUETOOTH_STATE);
                    btState.putExtra("bluetooth_connected", false);
                    appContext.sendBroadcast(btState);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public boolean isAvailable() {
        try {
            Method getCurrentHeadset = baseHeadset.getDeclaredMethod("getCurrentHeadset");
            BluetoothDevice btDevice = (BluetoothDevice) getCurrentHeadset.invoke(btHeadset, (Object[])null);
            return btDevice != null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
