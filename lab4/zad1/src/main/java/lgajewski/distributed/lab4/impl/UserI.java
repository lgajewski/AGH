package lgajewski.distributed.lab4.impl;


import Ice.Current;
import demo.Name;
import demo._UserDisp;

import java.util.Date;

public class UserI extends _UserDisp {
    private long id;
    private Name name;

    private long timestamp;

    public UserI(long id) {
        this(id, null);
    }

    public UserI(long id, Name name) {
        this.id = id;
        this.name = name;
        update();
    }

    @Override
    public long getTimestamp(Current __current) {
        return timestamp;
    }

    @Override
    public long getId(Current __current) {
        update();
        return id;
    }

    @Override
    public Name getName(Current __current) {
        update();
        return name;
    }

    @Override
    public void changeName(Name name1, Current __current) {
        update();
        name = name1;
    }

    private void update() {
        this.timestamp = new Date().getTime();
    }

}
