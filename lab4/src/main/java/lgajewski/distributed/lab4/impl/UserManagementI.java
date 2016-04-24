package lgajewski.distributed.lab4.impl;


import Ice.Current;
import Ice.Identity;
import Ice.ObjectAdapter;
import demo.UserPrx;
import demo.UserPrxHelper;
import demo._UserManagementDisp;

public class UserManagementI extends _UserManagementDisp {
    private static final long serialVersionUID = 4575983044111544624L;
    long nextId = 1001;
    ObjectAdapter adapter;


    public UserManagementI(ObjectAdapter oa) {
        adapter = oa;
    }

    @Override
    public UserPrx createUser(Current __current) {
        UserI u = new UserI(nextId); //new servant...
        Identity identity = new Identity(new Long(nextId).toString(), "users"); //and its identity...
        nextId++;
        return UserPrxHelper.uncheckedCast(adapter.add(u, identity));
    }

    @Override
    public UserPrx[] findUsers(String template, Current __current) {
        //TODO
        return null;
    }

}
