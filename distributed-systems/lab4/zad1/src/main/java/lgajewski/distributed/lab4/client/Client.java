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
import demo.UserPrx;
import demo.UserPrxHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

    private static final Logger log = Logger.getGlobal();

    public static void main(String[] args) {

        Ice.Communicator communicator = null;

        try {
            // ICE initialization
            communicator = Ice.Util.initialize(args);

            // read adapter properties
            Properties properties = new Properties();
            properties.load(new FileInputStream(new File("config/config.client")));
            String proxy = properties.getProperty("Adapter.Endpoints");

            String line;
            java.io.BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

            do {
                System.out.println("Please type servant ID.");
                System.out.print("==> ");
                System.out.flush();
                line = in.readLine();

                if (line == null) {
                    break;
                }

                if (line.matches(".*\\/.*")) {
                    Ice.ObjectPrx base = communicator.stringToProxy(line + ":" + proxy);

                    UserPrx user = UserPrxHelper.checkedCast(base);
                    if (user == null) {
                        System.out.println("Unable to find servant on server! Proxy is null.");
                        continue;
                    }

                    do {
                        System.out.print("\t==> ");
                        System.out.flush();
                        line = in.readLine();

                        if (line.equals("changeName")) {
                            System.out.println("Type first name:");
                            String firstName = in.readLine();

                            System.out.println("Type second name:");
                            String lastName = in.readLine();

                            user.changeName(new Name(firstName, lastName));
                        }

                        if (line.equals("timestamp")) {
                            System.out.println("T:" + user.getTimestamp());
                        }

                        if (line.equals("getName")) {
                            System.out.println("Name: " + user.getName().firstName + " " + user.getName().lastName);
                        }

                        if (line.equals("id")) {
                            System.out.println("ID: " + user.getId());
                        }
                    } while (!line.equals("q"));

                } else {
                    System.out.println("Wrong id!");
                }


            } while (!line.equals("x"));

        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        if (communicator != null) {
            try {
                communicator.destroy();
            } catch (Exception e) {
                log.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

}