// **********************************************************************
//
// Copyright (c) 2003-2011 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

package lgajewski.distributed.lab4.impl;

import Ice.Current;
import demo.AMD_Calc_add2;
import demo.RequestCanceledException;
import demo._CalcDisp;
import lgajewski.distributed.lab4.server.WorkQueue;

public class CalcI extends _CalcDisp {

    private static final long serialVersionUID = -2448962912780867770L;
    private WorkQueue _workQueue;

    public CalcI() {
        _workQueue = null;
    }


    public CalcI(WorkQueue workQueue) {
        _workQueue = workQueue;
    }


    @Override
    public float add1(float a, float b, Current __current) {
        /*try
        {
            Thread.sleep(2000);
        }
        catch(java.lang.InterruptedException ex)
        {
        }*/

        System.out.println("ADD: a = " + a + ", b = " + b + ", result = " + (a + b));

        return a + b;
    }

    @Override
    public float subtract(float a, float b, Current __current) {
        System.out.println("ADD: a = " + a + ", b = " + b + ", result = " + (a - b));

        return a - b;
    }

    @Override
    public void add2_async(AMD_Calc_add2 __cb, float a, float b, Current __current) throws RequestCanceledException {
        if (a < 10 && b < 10) //zadanie jest proste
        {
            System.out.println("ADD (immediate): a = " + a + ", b = " + b + ", result = " + (a + b));
            __cb.ice_response(a + b);
        } else //zadanie jest skomplikowane
        {
            _workQueue.addTask(__cb, 5000, a, b);
        }
    }


}
