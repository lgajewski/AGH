// **********************************************************************
//
// Copyright (c) 2003-2011 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

package lgajewski.distributed.lab4.server;


import Ice.Identity;
import lgajewski.distributed.lab4.impl.CalcI;
import lgajewski.distributed.lab4.impl.UserManagementI;

public class Server1 {
    public void t1(String[] args) {
        int status = 0;
        Ice.Communicator communicator = null;

        try {
            // 1. Inicjalizacja ICE
            communicator = Ice.Util.initialize(args);

            // 2. Konfiguracja adaptera
            // METODA 1 (polecana): Konfiguracja adaptera Adapter1 jest w pliku konfiguracyjnym podanym jako parametr uruchomienia serwera
            //Ice.ObjectAdapter adapter = communicator.createObjectAdapter("Adapter1");

            // METODA 2 (niepolecana): Konfiguracja adaptera Adapter1 jest w kodzie �r�d�owym
            Ice.ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints("Adapter1",
                    "tcp -h localhost -p 10000:udp -h localhost -p 10000");

            // 3. Stworzenie serwanta/serwant�w

            CalcI calcServant1 = new CalcI();

            UserManagementI umServant1 = new UserManagementI(adapter);


            // zad07
            //WorkQueue workQueue = new WorkQueue();
            //CalcI calcServant2 = new CalcI(workQueue);
            //workQueue.start();


            // 4. Dodanie wpis�w do ASM
            adapter.add(calcServant1, new Identity("calc11", "calc"));
            //adapter.add(calcServant2, new Identity("calc77", "calc"));

            adapter.add(umServant1, new Identity("um11", "users"));


            // 5. Aktywacja adaptera i przej�cie w p�tl� przetwarzania ��da�
            adapter.activate();
            System.out.println("Entering event processing loop...");
            communicator.waitForShutdown();
        } catch (Exception e) {
            System.err.println(e);
            status = 1;
        }
        if (communicator != null) {
            // Clean up
            try {
                communicator.destroy();
            } catch (Exception e) {
                System.err.println(e);
                status = 1;
            }
        }
        System.exit(status);
    }


    public static void main(String[] args) {
        Server1 app = new Server1();
        app.t1(args);
    }
}
