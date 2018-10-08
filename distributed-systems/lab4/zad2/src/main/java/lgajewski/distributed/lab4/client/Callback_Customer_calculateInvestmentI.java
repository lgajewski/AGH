package lgajewski.distributed.lab4.client;

import Ice.LocalException;
import Ice.UserException;
import demo.Callback_Customer_calculateInvestment;

public class Callback_Customer_calculateInvestmentI extends Callback_Customer_calculateInvestment {
    @Override
    public void exception(UserException e) {
        e.printStackTrace();
    }

    @Override
    public void response(int i) {
        System.out.println("\tCalculated investment: " + i);
    }

    @Override
    public void exception(LocalException e) {
        e.printStackTrace();
    }
}
