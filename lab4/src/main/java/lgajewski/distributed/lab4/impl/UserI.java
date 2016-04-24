package lgajewski.distributed.lab4.impl;


import Ice.Current;
import demo.Name;
import demo._UserDisp;

public class UserI extends _UserDisp {
    private long id;
    private Name name;

    public UserI(long id) {
        this.id = id;
    }

    @Override
    public long getId(Current __current) {
        return id;
    }

    @Override
    public Name getName(Current __current) {
        return name;
    }

    @Override
    public void changeName(Name name1, Current __current) {
        name = name1;
    }

}
