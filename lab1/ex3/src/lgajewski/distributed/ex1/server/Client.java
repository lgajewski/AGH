package lgajewski.distributed.ex1.server;

import javax.swing.JOptionPane;

public class Client
{
  // create the GUI and show it. Invoke me only on the EDT.
  private static void createAndShowGUI(String username) {
    new ClientForm(username).setVisible(true);
  }

  public static void main(String[] args) {
    // get the username from command line args, else ask for it, else "guest".
    final String username = getUsername(args);
    //create and show the GUI on the EDT
    javax.swing.SwingUtilities.invokeLater(
      new Runnable() {
        public void run() {
          createAndShowGUI(username);
        }
      }
    );
  }
  
  private static String getUsername(String[] args) {
    if (args.length>0) return args[0];
    String username = JOptionPane.showInputDialog(
      null, "Username ?", "Enter username", JOptionPane.QUESTION_MESSAGE
    );
    if ( username!=null && username.trim().length()>0 ) return username;
    return "guest";
  }

}