
import java.io.PrintStream;

import javax.swing.DefaultListModel;
import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.netbeans.lib.cvsclient.event.CVSAdapter;
import org.netbeans.lib.cvsclient.event.MessageEvent;

/**
 * Inner class extend CVSAdapter <br>
 * <p>used to get and send message of the CVS server</p>
 *
 */
public class MyMessage extends CVSAdapter {

    /**
     * Stores a tagged line
     */
    private final StringBuffer taggedLine = new StringBuffer();
    /**
     * Called when the server wants to send a message to be displayed to the
     * user. The message is only for information purposes and clients can choose
     * to ignore these messages if they wish.
     *
     * @param e the event
     */
    private JTextArea consoleOut;
    private JScrollPane pane;

    public MyMessage(JTextArea _consoleOut, JScrollPane _pane) {
        this.consoleOut = _consoleOut;
        this.pane = _pane;
    }

    public void printMessage(String _message) {
        this.consoleOut.append(_message + "\n");
        //this.consoleOut.setText(tmpTxt+_message+"\n");

    }

    public void messageSent(MessageEvent e) {
        String line = e.getMessage();
        PrintStream stream = e.isError() ? System.err : System.out;

        if (e.isTagged()) {
            String message = MessageEvent.parseTaggedMessage(taggedLine,
                    "Daniel Six");
            // if we get back a non-null line, we have something
            // to output. Otherwise, there is more to come and we
            // should do nothing yet.
            if (message != null) {
                if (this.consoleOut != null) {
                    this.consoleOut.append(message + "\n");
                    System.out.println(message);
                    JScrollBar jb = this.pane.getVerticalScrollBar();
                    jb.setValue(jb.getMaximum());
                }

            }
        } else {
            if (this.consoleOut != null) {
                //String tmpTxt = this.consoleOut.getText();
                //this.consoleOut.setText(tmpTxt+line+"\n");

                this.consoleOut.append(line + "\n");
                System.out.println(line);
                JScrollBar jb = this.pane.getVerticalScrollBar();
                jb.setValue(jb.getMaximum());
            }
        }
    }
}