package pl.gajewski.others;

/**
 * Created by Lukasz on 17.04.14.
 */

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.log4j.Logger;
import pl.gajewski.Language;
import pl.gajewski.Main;

import java.io.*;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;


public class ZIP {

    static Logger log = Logger.getLogger(ZIP.class.getName());

    public static void make() {

        try {

            OutputStream zip_output = new FileOutputStream(new File(Main.zip));
            ArchiveOutputStream logical_zip = new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.ZIP, zip_output);
            logical_zip.putArchiveEntry(new ZipArchiveEntry(Main.html));
            IOUtils.copy(new FileInputStream(new File(Main.html)), logical_zip);
            logical_zip.closeArchiveEntry();
            logical_zip.putArchiveEntry(new ZipArchiveEntry(Main.style));
            IOUtils.copy(new FileInputStream(new File(Main.style)), logical_zip);
            logical_zip.closeArchiveEntry();
            logical_zip.finish();
            zip_output.close();

            log.info(Language.Lang.MSG_CREATE_ZIP.getMsg() + Main.zip);

        } catch (FileNotFoundException e) {
            System.out.println("ERROR, MORE INFO IN LOGS");
            log.debug(e.getMessage());
        } catch (ArchiveException e) {
            System.out.println("ERROR, MORE INFO IN LOGS");
            log.debug(e.getMessage());
        } catch (IOException e) {
            log.debug(e.getMessage());
        }

    }
}
