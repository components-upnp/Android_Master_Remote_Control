package com.example.mkostiuk.android_master_remote_control.upnp;

import org.fourthline.cling.binding.annotations.UpnpAction;
import org.fourthline.cling.binding.annotations.UpnpInputArgument;
import org.fourthline.cling.binding.annotations.UpnpService;
import org.fourthline.cling.binding.annotations.UpnpServiceId;
import org.fourthline.cling.binding.annotations.UpnpServiceType;
import org.fourthline.cling.binding.annotations.UpnpStateVariable;

import java.beans.PropertyChangeSupport;

/**
 * Created by mkostiuk on 12/05/2017.
 */

@UpnpService(
        serviceType = @UpnpServiceType(value = "MasterCommandService"),
        serviceId = @UpnpServiceId("MasterCommandService")
)
public class MasterCommandController {
    private final PropertyChangeSupport propertyChangeSupport;

    public MasterCommandController() {
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    @UpnpStateVariable(defaultValue = "")
    private String commande = "";

    public void setCommande( String newCommandeValue) {
        commande = newCommandeValue;
        getPropertyChangeSupport().firePropertyChange("Commande","",commande);
    }

}
