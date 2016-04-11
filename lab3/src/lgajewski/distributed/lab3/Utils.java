package lgajewski.distributed.lab3;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;
import java.util.Scanner;

public class Utils {

    private static final boolean RANDOM_POLICY = false;
    private static Task.RandomTask<Task> randomTask = new Task.RandomTask<>(Task.class);

    public static void handleShutdownApplication() {
        Scanner scanner = new Scanner(System.in);
        String line = "";

        while (!line.equals("exit")) {
            line = scanner.nextLine();
        }

        System.out.println("Terminating!");
    }

    public static Context initializeJndiContext(String providerUrl, String jndiContextClassName) throws NamingException {
        // JNDI Context
        System.out.println("JNDI context initialization.");
        Properties props = new Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY, jndiContextClassName);
        props.put(Context.PROVIDER_URL, providerUrl);
        return new InitialContext(props);
    }

    public static Task getNextTask(int i) {
        if (RANDOM_POLICY) {
            return randomTask.random();
        } else {
            return randomTask.index(i);
        }
    }

}
