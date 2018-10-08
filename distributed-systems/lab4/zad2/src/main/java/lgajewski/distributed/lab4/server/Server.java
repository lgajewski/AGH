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
import demo.AuthorizationException;
import demo.Investment;
import demo.Loan;
import demo.Name;
import lgajewski.distributed.lab4.impl.CustomerI;
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

            // serialize into db
            initCustomers();

            // register servant locators
            ServantLocator evictor = new ServantEvictor();

            adapter.addServantLocator(evictor, "customer");

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

    private void initCustomers() throws AuthorizationException {
        CustomerI customer1 = new CustomerI(1, new Name("Jan", "Kowalski"));
        customer1.login();
        customer1.addInvestment(new Investment(36, 2000, 200));
        customer1.addInvestment(new Investment(24, 1000, 100));
        customer1.addLoan(new Loan(12, 1000, 1.2f));
        customer1.addLoan(new Loan(14, 1200, 1.4f));
        customer1.logout();

        CustomerI customer2 = new CustomerI(2, new Name("Adam", "Nowak"));

        serializer.serialize("customer1", customer1);
        serializer.serialize("customer2", customer2);
    }

}
