// **********************************************************************
//
// Copyright (c) 2003-2011 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

package lgajewski.distributed.lab4.server;

import Ice.ServantLocator;
import demo.Name;
import lgajewski.distributed.lab4.impl.UserI;
import lgajewski.distributed.lab4.server.locator.ServantEvictor5;
import lgajewski.distributed.lab4.server.locator.ServantLocator1;
import lgajewski.distributed.lab4.server.locator.ServantLocator2;
import lgajewski.distributed.lab4.server.locator.ServantLocator3;
import lgajewski.distributed.lab4.server.serialize.Serializer;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * K1 - Zapewnienie mechanizmu polaczenia. Odzyskiwanie danych z ostatniej sesji.
 * K2 - Obsluga rekordu studenta uczelni.
 * K3 - Realizacja 'ciezkiego' zadania obliczeniowego.
 * K4 - Mechanizm logowania. (debug mode)
 * K5 - Zarzadzanie duza iloscia danych, wczytywanie ograniczonej ilosci i operowanie na nich
 */

public class Server implements Runnable {

    private static final Logger log = Logger.getGlobal();

    private final String[] args;

    private Serializer serializer = new Serializer();

    public Server(String[] args) {
        this.args = args;
    }

    @Override
    public void run() {
        Ice.Communicator communicator = null;

        try {
            // ICE initialization
            communicator = Ice.Util.initialize(args);

            // configure an adapter using configuration file
            // --Ice.Config=config/config.server
            Ice.ObjectAdapter adapter = communicator.createObjectAdapter("Adapter");

            // create servants
            UserI user1 = new UserI(1, new Name("Jan", "Kowalski"));
            UserI user2 = new UserI(2, new Name("Adam", "Nowak"));
            UserI user3 = new UserI(3);

            // serialize into db
            serializer.serialize("user1", user1);
            serializer.serialize("user2", user2);
            serializer.serialize("user3", user3);

            // register servant locators
            ServantLocator locator1 = new ServantLocator1(adapter);
            ServantLocator locator2 = new ServantLocator2();
            ServantLocator locator3 = new ServantLocator3();
            ServantLocator evictor5 = new ServantEvictor5();

            /** Zapewnienie mechanizmu polaczenia. Odzyskiwanie danych z ostatniej sesji. */
            adapter.addServantLocator(locator1, "K1");
            /** Obsluga rekordu studenta uczelni. */
            adapter.addServantLocator(locator2, "K2");
            /** Realizacja 'ciezkiego' zadania obliczeniowego. */
            adapter.addServantLocator(locator3, "K3");
            /** Mechanizm logowania (debug mode). */
            adapter.addDefaultServant(user1, "K4");
            /** Zarzadzanie duza iloscia danych, wczytywanie ograniczonej ilosci i operowanie na nich */
            adapter.addServantLocator(evictor5, "K5");

            // activate adapter and enter event processing loop
            adapter.activate();

            log.info("Entering event processing loop...");
            communicator.waitForShutdown();
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            // clean up
            if (communicator != null) {
                try {
                    communicator.destroy();
                } catch (Exception e) {
                    log.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server(args);
        server.run();
    }

}
