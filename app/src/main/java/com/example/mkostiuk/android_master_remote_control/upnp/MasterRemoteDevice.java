package com.example.mkostiuk.android_master_remote_control.upnp;

import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.ManufacturerDetails;
import org.fourthline.cling.model.meta.ModelDetails;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDN;

/**
 * Created by mkostiuk on 12/05/2017.
 */

public class MasterRemoteDevice {

    public static LocalDevice createDevice(UDN udn) throws ValidationException {

        DeviceType type =
                new UDADeviceType("MasterRemoteControl", 1);

        DeviceDetails details =
                new DeviceDetails(
                        "Master Remote Control",
                        new ManufacturerDetails("IRIT"),
                        new ModelDetails("Vote", "Permet de contrôler le bureau de vote", "v1")
                );

        LocalService commandService =
                new AnnotationLocalServiceBinder().read(MasterCommandController.class);

        commandService.setManager(
                new DefaultServiceManager<>(commandService, MasterCommandController.class)
        );


        return new LocalDevice(
                new DeviceIdentity(udn),
                type,
                details,
                commandService

        );
    }
}

