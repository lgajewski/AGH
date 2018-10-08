package pl.gajewski;

/**
 * Created by Lukasz on 08.04.14.
 *
 */

import org.apache.log4j.*;
import pl.gajewski.others.*;
import pl.gajewski.mutable.Mutability;

import java.io.IOException;
import java.nio.file.Paths;

public class Main {

    public static String config_Path = Paths.get("config", "config.properties").toString();
    public static String langPL_Path = Paths.get("config", "PL.properties").toString();
    public static String langEN_Path = Paths.get("config", "EN.properties").toString();
    public static String url = "http://student.agh.edu.pl/~gajewski/";
    public static String html = Paths.get("src", "index.html").toString();
    public static String style = Paths.get("src", "style.css").toString();
    public static String zip = Paths.get("html.zip").toString();

    static Logger log = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {

        log.info("START");
        Language lang = new Language();

        try {

            String userLanguage = lang.loadProperties();
            lang.loadLanguage(userLanguage);
            log.info(Language.Lang.MSG_LANG.getMsg() + userLanguage);

            //MUTABILITY HERE
            System.out.println(Language.Lang.MSG_MUTABILITY.getMsg() + "\n");
            Mutability.classInfo();         // class mutability / immutability
            Mutability.objectInfo();        // object immutability

            //SENDING AN EMAIL
            System.out.println(Language.Lang.MSG_START_EMAIL.getMsg() + "\n");
            MailManagement.sendMail();

            //GETTING SOURCE CODE FROM URL, WRITING TO HTML FILE
            System.out.println(Language.Lang.MSG_MYURL.getMsg() + "\n");
            myURL.getData();

            //PACKING DATA TO ZIP FILE
            System.out.println(Language.Lang.MSG_ZIP.getMsg() + "\n");
            ZIP.make();

            System.out.println(Language.Lang.MSG_END.getMsg());
        } catch(IllegalArgumentException e) {
            System.out.println("ERROR, MORE INFO IN LOGS");
            log.debug(e.getMessage());
        } catch (IOException e) {
            System.out.println("ERROR, MORE INFO IN LOGS");
            log.debug(e.getMessage());
        } finally {
            log.info("END\r\n\r\n\r\n");
        }

    }


}
