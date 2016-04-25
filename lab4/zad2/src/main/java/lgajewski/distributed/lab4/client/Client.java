// **********************************************************************
//
// Copyright (c) 2003-2011 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

package lgajewski.distributed.lab4.client;

import demo.CustomerPrx;
import demo.CustomerPrxHelper;
import demo.Investment;
import demo.Loan;

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

                if (line.matches("[0-9]+")) {
                    Ice.ObjectPrx base = communicator.stringToProxy("customer/" + line + ":" + proxy);

                    CustomerPrx user = CustomerPrxHelper.checkedCast(base);
                    if (user == null) {
                        System.out.println("Unable to find servant on server! Proxy is null.");
                        continue;
                    }

                    do {
                        System.out.print("\t==> ");
                        System.out.flush();
                        line = in.readLine();

                        if(line.equals("login")) {
                            user.login();
                            System.out.println("Logged in!");
                        }

                        if(line.equals("logout")) {
                            user.login();
                            System.out.println("Logged out!");
                        }

                        if (line.equals("getName")) {
                            System.out.println("Name: " + user.getName().firstName + " " + user.getName().lastName);
                        }

                        if (line.equals("id")) {
                            System.out.println("ID: " + user.getUniqueId());
                        }

                        if (line.equals("loans")) {
                            for (Loan loan : user.getLoans()) {
                                System.out.println("\tLoan: " + loan.amount);
                            }
                        }

                        if (line.equals("investments")) {
                            for (Investment investment : user.getInvestments()) {
                                System.out.println("\tInvestment: " + investment.amount);
                            }
                        }

                        if (line.equals("loan")) {
                            System.out.println("\tType period:");
                            String period = in.readLine();

                            System.out.println("\tLoan calculated: " + user.calculateLoan(Integer.parseInt(period)));
                        }

                        if (line.equals("investment")) {
                            System.out.println("\tType period:");
                            String period = in.readLine();

                            System.out.println("\tType amount:");
                            String amount = in.readLine();

                            System.out.println("\tLoan calculated: " + user.calculateInvestment(
                                    Integer.parseInt(period), Integer.parseInt(amount)));
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