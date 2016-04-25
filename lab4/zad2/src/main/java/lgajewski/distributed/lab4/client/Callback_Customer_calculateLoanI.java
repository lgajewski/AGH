package lgajewski.distributed.lab4.client;

import Ice.LocalException;
import Ice.UserException;
import demo.Callback_Customer_calculateLoan;

public class Callback_Customer_calculateLoanI extends Callback_Customer_calculateLoan {

    @Override
    public void exception(UserException e) {
        e.printStackTrace();
    }

    @Override
    public void response(int i) {
        System.out.println("Calculated loan: " + i);
    }

    @Override
    public void exception(LocalException e) {
        e.printStackTrace();
    }
}
