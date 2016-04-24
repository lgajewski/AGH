// **********************************************************************
//
// Copyright (c) 2003-2011 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

package lgajewski.distributed.lab4.client;

import demo.Name;
import demo.UserManagementPrx;
import demo.UserPrx;
import demo.UserManagementPrxHelper;

public class Client2 {
    public static void main(String[] args) {
        int status = 0;
        Ice.Communicator communicator = null;

        try {
            // 1. Inicjalizacja ICE
            communicator = Ice.Util.initialize(args);

            // 2. Uzyskanie referencji obiektu na podstawie linii w pliku konfiguracyjnym
            // Ice.ObjectPrx base = communicator.propertyToProxy("Calc1.Proxy");
            // 2. To samo co powy?ej, ale mniej ?adnie
            Ice.ObjectPrx base1 = communicator.stringToProxy("users/um11:tcp -h localhost -p 10000:udp -h localhost -p 10000:ssl -h localhost -p 10001");

            // 3. Rzutowanie, zaw?anie
            UserManagementPrx um1 = UserManagementPrxHelper.checkedCast(base1);
            if (um1 == null) throw new Error("Invalid proxy");

            UserManagementPrx um1_oneway = (UserManagementPrx) um1.ice_oneway();
            UserManagementPrx um1_batch_oneway = (UserManagementPrx) um1.ice_batchOneway();
            UserManagementPrx um1_datagram = (UserManagementPrx) um1.ice_datagram();
            UserManagementPrx um1_batch_datagram = (UserManagementPrx) um1.ice_batchDatagram();

            // 4. Wywolanie zdalnych operacji

            String line = null;
            java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));

            UserPrx u1 = null;
            do {
                try {
                    System.out.print("==> ");
                    System.out.flush();
                    line = in.readLine();

                    if (line == null) {
                        break;
                    }
                    if (line.equals("c")) {
                        u1 = um1.createUser();
                        System.out.println("User created, RESULT (syn) = " + u1);
                    }
                    if (line.equals("us")) {
                        Name n = new Name();
                        n.firstName = "Jan";
                        n.lastName = "Kowalski";
                        u1.changeName(n);
                        System.out.println("Username set (syn)");
                    }
                    if (line.equals("ug")) {
                        Name n = u1.getName();
                        System.out.println("Username got (syn): " + n.firstName + " " + n.lastName);
                    } else if (line.equals("x")) {
                        // Nothing to do
                    }
                } catch (java.io.IOException ex) {
                    System.err.println(ex);
                }
            }
            while (!line.equals("x"));


        } catch (Ice.LocalException e) {
            e.printStackTrace();
            status = 1;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            status = 1;
        }
        if (communicator != null) {
            // Clean up
            //
            try {
                communicator.destroy();
            } catch (Exception e) {
                System.err.println(e.getMessage());
                status = 1;
            }
        }
        System.exit(status);
    }

}