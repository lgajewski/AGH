package pl.gajewski.others;

/**
 * Created by Lukasz on 16.04.14.
 */

import org.apache.commons.io.*;
import org.apache.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import pl.gajewski.*;

public class myURL {

    static Logger log = Logger.getLogger(myURL.class.getName());

    public static String read(String url) {

        String result = "";
        InputStream in = null;
        try {
            in = new URL( url ).openStream();
            result += IOUtils.toString(in);
        } catch(IOException e) {
            log.debug(e.getMessage());
        } finally {
            IOUtils.closeQuietly(in);
        }

        return result;

    }

    public static void getData() {

        try {
            String urlSource = myURL.read(Main.url);
            FileWriter target = new FileWriter(Main.html);
            target.write(urlSource);
            target.close();

            urlSource = myURL.read(Main.url + "style.css");
            target = new FileWriter(Main.style);
            target.write(urlSource);
            target.close();
            log.info(Language.Lang.MSG_CREATE_URL.getMsg());

        } catch (IOException e) {
            System.out.println("ERROR, MORE INFO IN LOGS");
            log.debug(e.getMessage());
        }

    }

}
