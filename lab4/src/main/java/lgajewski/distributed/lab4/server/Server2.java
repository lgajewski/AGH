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

public class Server2 {
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
                    "tcp -h localhost -p 10000:udp -h localhost -p 10000:ssl -h localhost -p 10001");

            // 3-4. Stworzenie serwanta/serwant�w, dodanie wpis�w do ASM, konfiguracja mechanizm�w zarz�dzania serwantami
            CalcI servant1 = new CalcI();
            CalcI servant2 = new CalcI();
            CalcI servant3 = new CalcI();
            CalcI servant4 = new CalcI();

            Ice.ServantLocator mylocator = new ServantLocator1("A", servant3); //TODO, example only

            adapter.add(servant2, new Identity("name", "category")); //TODO, example only

            adapter.addServantLocator(mylocator, "category"); //TODO, example only
            adapter.addDefaultServant(servant3, "category"); //TODO, example only


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
        Server2 app = new Server2();
        app.t1(args);
    }
}
