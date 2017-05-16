package com.example.mkostiuk.android_master_remote_control.upnp;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.model.types.UDN;

/**
 * Created by mkostiuk on 12/05/2017.
 */

public class Service {

    private AndroidUpnpService upnpService;
    private UDN udnDevice;
    private ServiceConnection serviceConnection;

    public Service() {


        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                upnpService = (AndroidUpnpService) service;


                LocalService<MasterCommandController> remoteControlService = getMasterCommandService();

                // Register the device when this activity binds to the service for the first time
                if (remoteControlService == null) {
                    try {
                        System.err.println("CREATION DEVICE!!!");
                        udnDevice = new SaveUDN().getUdn();
                        LocalDevice remoteDevice = MasterRemoteDevice.createDevice(udnDevice);

                        upnpService.getRegistry().addDevice(remoteDevice);

                    } catch (Exception ex) {
                        System.err.println("Creating Android remote controller device failed !!!");
                        ex.printStackTrace();
                        return;
                    }
                }

                System.out.println("Creation device reussie...");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                upnpService = null;
            }
        };
    }

    public LocalService<MasterCommandController> getMasterCommandService() {
        if (upnpService == null)
            return null;

        LocalDevice remoteDevice;

        if ((remoteDevice = upnpService.getRegistry().getLocalDevice(udnDevice, true)) == null)
            return null;

        return (LocalService<MasterCommandController>)
                remoteDevice.findService(new UDAServiceType("MasterCommandService", 1));
    }

    public ServiceConnection getService() {
        return serviceConnection;
    }

    public void stop() {
        upnpService.get().shutdown();
    }

    public UDN getUdn() {
        return udnDevice;
    }
}
