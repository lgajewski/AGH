package pl.edu.agh.iosr.raft.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    static int nodeId;
    static int nodeAmount;

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            LOG.info("java -jar <jar_path> <node_id> <amount_of_nodes>");
            return;
        }

        nodeId = Integer.parseInt(args[0]);
        nodeAmount = Integer.parseInt(args[1]);
        SpringApplication.run(Application.class, args);
    }

}