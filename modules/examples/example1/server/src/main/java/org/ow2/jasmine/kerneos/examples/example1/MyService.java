package org.ow2.jasmine.kerneos.examples.example1;

import java.io.Serializable;

public class MyService implements Serializable {
    MyObject obj;

    MyService() {
        obj = new MyObject();
        obj.field1 = "TEST";
        obj.field2 = new MySubObject();
        obj.field2.field1 = "SUBTEST";
        obj.field2.field2 = 0;

    }

    public MyObject getObject() {

        obj.field2.field2++;
        return obj;
    }
}
