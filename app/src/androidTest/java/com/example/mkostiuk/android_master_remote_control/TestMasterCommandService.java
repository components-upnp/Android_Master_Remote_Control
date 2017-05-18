package com.example.mkostiuk.android_master_remote_control;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.mkostiuk.android_master_remote_control.upnp.MasterCommandController;
import com.example.mkostiuk.android_master_remote_control.upnp.Service;
import com.example.mkostiuk.android_master_remote_control.xml.GenerateurXml;

import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.model.meta.LocalService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import static org.junit.Assert.*;

/**
 * Test instrumental, le device Android doit être connecté
 * On teste ici le fonctionnement du service décrit dans MasterCommandController
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class TestMasterCommandService {

    private Service service;
    private ServiceConnection serviceConnection;
    private LocalService<MasterCommandController> masterService;
    private GenerateurXml gen;

    @Before
    public void setUp() throws Exception {
        Context context = InstrumentationRegistry.getContext();

        this.useAppContext();

        service = new Service();
        serviceConnection = service.getService();

        gen = new GenerateurXml();

        context.bindService(new Intent(context, AndroidUpnpServiceImpl.class),
                serviceConnection,
                Context.BIND_AUTO_CREATE);

    }

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.mkostiuk.android_master_remote_control", appContext.getPackageName());
    }

    @Test
    public void testCommandeOK() throws TransformerException, ParserConfigurationException {
        String com = gen.getDocXml("UdnTest", "CommandeTest", "ClefTest");

        service.getMasterCommandService().getManager().getImplementation().setCommande(com);
    }
}
