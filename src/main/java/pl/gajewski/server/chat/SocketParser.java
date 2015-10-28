package pl.gajewski.server.chat;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * @author Gajo
 *         06/05/2015
 */

public class SocketParser {

    private BufferedReader input;

    public SocketParser(BufferedReader input) {
        this.input = input;
    }

    public String getJSON() throws IOException {
        StringBuilder jsonBuilder = new StringBuilder();

        int brackets_no = 0;
        int in;

        while ((in = input.read()) != -1) {
            char c = (char) in;

            if (c == '{') {
                brackets_no++;
            }

            if (brackets_no > 0) {
                jsonBuilder.append(c);
            }

            if (c == '}') {
                brackets_no--;

                // check end of json
                if(brackets_no == 0) break;
            }
        }

        return jsonBuilder.toString();
    }

}
