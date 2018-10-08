package pl.gajewski;

import org.apache.log4j.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Lukasz on 14.04.14.
 */

public class Language {

    static Logger log = Logger.getLogger(Language.class.getName());

    public enum Lang {

        MSG_EQUAL(""),
        MSG_SAME(""),
        MSG_LANG(""),
        MSG_COMPARE(""),
        MSG_MUTABILITY(""),
        MSG_CREATEOBJ(""),
        MSG_CHANGEOBJ(""),
        MSG_EMPLOYEE_EQUAL(""),
        MSG_BOSS_EQUAL(""),
        MSG_STRING_EQUAL(""),
        MSG_EMAIL(""),
        MSG_START_EMAIL(""),
        MSG_ZIP(""),
        MSG_MYURL(""),
        MSG_CREATE_URL(""),
        MSG_CREATE_ZIP(""),
        MSG_END("");

        private String msg;

        private Lang(String msg) {
            this.msg = msg;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

    }

    public String loadProperties() throws IOException {
        Properties prop = new Properties();

        String filename = Main.config_Path;
        InputStream input = Language.class.getClassLoader().getResourceAsStream(filename);
        if (input == null) {
            throw new IllegalArgumentException("Sorry, unable to find " + filename);
        }

        //load a properties file from class path, inside static method
        prop.load(input);

        //get the property value
        String langInProperties = prop.getProperty("language");

        //closing InputStream
        input.close();

        return langInProperties;

    }

    public void loadLanguage(String lang) throws IOException {

        Properties prop = new Properties();
        String filename;

        if(lang.equals("PL")) {
            filename = Main.langPL_Path;
        } else if(lang.equals("EN")) {
            filename = Main.langEN_Path;
        } else {
            throw new IllegalArgumentException("Language " + lang + " is not supported.");
        }

        InputStream input = Language.class.getClassLoader().getResourceAsStream(filename);
        if(input == null) {
            log.debug("Sorry, unable to load language from: " + filename);
            throw new IllegalArgumentException("Sorry, unable to load language from: " + filename);
        }

        prop.load(input);

        for (Lang msg : Lang.values()) {
            String value = prop.getProperty(msg.toString());
            msg.setMsg(value);
        }

        input.close();

    }

}
