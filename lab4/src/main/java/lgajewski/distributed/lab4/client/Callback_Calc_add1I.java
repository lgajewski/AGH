package lgajewski.distributed.lab4.client;

import Ice.LocalException;
import demo.Callback_Calc_add1;

public class Callback_Calc_add1I extends Callback_Calc_add1 {

    @Override
    public void response(float __ret) {
        System.out.println("RESULT (asyn.) = " + __ret);
    }

    @Override
    public void exception(LocalException __ex) {
        System.out.println("EXCEPTION: " + __ex);
    }
}
