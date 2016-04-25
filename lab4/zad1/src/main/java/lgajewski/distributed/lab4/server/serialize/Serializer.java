package lgajewski.distributed.lab4.server.serialize;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Serializer {

    private static final Logger log = Logger.getGlobal();

    private File db;

    public Serializer() {
        this.db = new File("db");
    }

    public void serialize(String key, Serializable serializable) {
        try {
            FileOutputStream fos = new FileOutputStream(getObjectFile(key));

            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(serializable);
            oos.close();

            log.info("serialize object - " + key);

        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }

    }

    public Object deserialize(String key) {
        Object object = null;
        try {
            FileInputStream fileIn = new FileInputStream(getObjectFile(key));
            ObjectInputStream in = new ObjectInputStream(fileIn);
            object = in.readObject();
            in.close();
            fileIn.close();
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage());
        }

        log.info("deserialize object - " + key);

        return object;
    }

    private File getObjectFile(String key) {
        return new File(db, key);
    }

}
