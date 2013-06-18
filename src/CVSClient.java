import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.netbeans.lib.cvsclient.*;
import org.netbeans.lib.cvsclient.admin.StandardAdminHandler;
import org.netbeans.lib.cvsclient.command.Command;
import org.netbeans.lib.cvsclient.command.CommandAbortedException;
import org.netbeans.lib.cvsclient.command.CommandException;
import org.netbeans.lib.cvsclient.command.GlobalOptions;
import org.netbeans.lib.cvsclient.command.add.AddCommand;
import org.netbeans.lib.cvsclient.command.checkout.CheckoutCommand;
import org.netbeans.lib.cvsclient.command.commit.CommitCommand;
import org.netbeans.lib.cvsclient.command.diff.DiffCommand;
import org.netbeans.lib.cvsclient.command.history.HistoryCommand;
import org.netbeans.lib.cvsclient.command.tag.TagCommand;
import org.netbeans.lib.cvsclient.command.update.UpdateCommand;
import org.netbeans.lib.cvsclient.connection.AuthenticationException;
import org.netbeans.lib.cvsclient.connection.Connection;
import org.netbeans.lib.cvsclient.connection.ConnectionFactory;
import org.netbeans.lib.cvsclient.event.CVSAdapter;
import org.netbeans.lib.cvsclient.event.MessageEvent;

public class CVSClient {

    /**
     * Cvs clinet instance used to communicate with cvs server
     */
    private Client cvsclient;
    /**
     * Cvs connect string
     */
    private CVSRoot cvsroot;
    /**
     * Connection instance to keep connect with cvs server
     */
    private Connection connection;
    /**
     * Global options to store the requied parameter for cvs server
     */
    private GlobalOptions globalOptions;
    /**
     * The local path on ur local machine
     */
    private String localPath;

    /**
     * Default constructor allows to construct CVSRoot from Properties object.
     * The names are exactly the same as the attribute names in this class.
     */
    public CVSClient() {
    }

    /**
     * Breaks the string representation of CVSClient into it's components:
     *
     * The valid format (from the cederqvist) is:
     *
     * :method:[[user][:password]
     *
     * @]hostname[:[port]]/path/to/repository
     *
     * e.g. :pserver;username=anonymous;hostname=localhost:/path/to/repository
     */
    public CVSClient(String connectionString) {
        cvsroot = CVSRoot.parse(connectionString);
    }

    /**
     * Get the localPath
     *
     * @return localPath the local path to get project from the CVS server
	  *
     */
    public String getLocalPath() {
        return localPath;
    }

    /**
     * Set the localPath
     *
     *
     */
    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    /**
     * Parse the CVSROOT string into CVSRoot object.
     *
     * The valid format (from the cederqvist) is:
     *
     * :method:[[user][:password]
     *
     * @]hostname[:[port]]/path/to/repository
     *
     * e.g. :pserver;username=anonymous;hostname=localhost:/path/to/repository
     */
    public void createConnection(String connectionString) {
        cvsroot = CVSRoot.parse(connectionString);
    }

    /**
     * Open connection to the cvs server <br>
     *
     * @return connection to cvs server
     * @throws AuthenticationException
     * @throws CommandAbortedException
     */
    public Connection openConnection() throws AuthenticationException,
            CommandAbortedException {
        connection = ConnectionFactory.getConnection(cvsroot);
        connection.open();
        return connection;
    }

    /**
     * Close connection to the cvs server <br>
	  *
     */
    public void closeConnection() throws IOException {
        connection.close();
    }

    /**
     * <p>Excute cvs command</p>
     *
     * @param command to be excute by the cliet
     * @throws AuthenticationException
     * @throws CommandAbortedException
     * @throws IOException
     * @throws CommandException
     */
    public void excute(Command command, CVSAdapter _listener) throws AuthenticationException,
            CommandAbortedException, IOException, CommandException {
        cvsclient = new Client(connection, new StandardAdminHandler());
        cvsclient.setLocalPath(localPath);
        globalOptions = new GlobalOptions();
        globalOptions.setCVSRoot("C:/dev/Investopedia/shanghai");
        cvsclient.getEventManager().addCVSListener(_listener);
        //put the command to the console
        _listener.messageSent(new MessageEvent("***Command***" + command.getCVSCommand(), "***Command***" + command.getCVSCommand(), false));

        cvsclient.executeCommand(command, globalOptions);
    }

    /**
     * <p>Called when need add files</p>
     *
     * @param files that indicate to be added
     * @return command of add files
     */
    public Command add(String[] files) {
        AddCommand command = new AddCommand();
        command.setBuilder(null);
        for (int i = 0; i < files.length; i++) {
            command.setFiles(new File[]{new File(files[i])});
        }
        return command;
    }

    public Command setTag(String _tagName, File[] _files) {
        TagCommand tagCommon = new TagCommand();
        tagCommon.setTag(_tagName);
        tagCommon.setFiles(_files);
        tagCommon.setOverrideExistingTag(true);
        return tagCommon;
    }

//	 public Command getTag(String _tagName, String _tagName)
//	 {
//		 TagCommand tagCommon = new TagCommand();
//		 _tagName tagCommon.getTag();
//		 
//		 return tagCommon;
//	 }
    
    /**
     * Called when need commit all files under the local path
     *
     * @return command command of commit files
     */
    public Command commit() {
        CommitCommand command = new CommitCommand();
        command.setBuilder(null);
        command.setForceCommit(true);
        command.setRecursive(true);
        return command;
    }

    /**
     * Called when need commit files
     *
     * @param files need to be commit
     * @return command command of commit files
	  *
     */
    public Command commit(String[] files) {
        CommitCommand command = new CommitCommand();
        for (int i = 0; i < files.length; i++) {
            command.setFiles(new File[]{new File(files[i])});
        }
        command.setBuilder(null);
        command.setForceCommit(true);
        command.setRecursive(true);
        return command;
    }

    /**
     * Called when need update the certain files
     *
     * @param files need to be update
     * @return command command of update files and directoris
	  *
     */
    public Command update(String[] files) {
        UpdateCommand command = new UpdateCommand();
        //fetch files from the array
        for (int i = 0; i < files.length; i++) {
            command.setFiles(new File[]{new File(files[i])});
        }
        command.setBuilder(null);
        command.setRecursive(true);
        command.setBuildDirectories(true);
        command.setPruneDirectories(true);
        return command;
    }

    /**
     * Called to show the history list since given date
     *
     * @param date Date of the history
     * @return command command show history list
	  *
     */
    public Command historysincedate(String date) {
        HistoryCommand command = new HistoryCommand();
        //Format is yyyymmdd e.g 20070205
        command.setSinceDate(date);
        return command;
    }

    /**
     * Called to show the history list since given version
     *
     * @param reversion reversion of the history
     * @return command command show history list 
	  *
     */
    public Command historysincerRevision(String reversion) {
        //Init command
        HistoryCommand command = new HistoryCommand();
        //set parameters
        command.setSinceRevision(reversion);
        return command;
    }

    /**
     * Called to show the different between two versions
     *
     * @param files the files to compare with
     * @param revision1 one revision
     * @param revision2 another revision
     * @return
	  *
     */
    public Command diffbyreveision(String[] files, String revision1, String revision2) {
        //Inite command
        DiffCommand command = new DiffCommand();
        //Set parameters
        for (int i = 0; i < files.length; i++) {
            command.setFiles(new File[]{new File(files[i])});
        }
        command.setRevision1(revision1);
        command.setRevision2(revision2);
        return command;
    }

    /**
     * Show difference between of the file that with different date
     *
     * @param files an array of files path
     * @param date1 one date
     * @param date2 another date
     * @return command command of show difference between files
	  *
     */
    public Command diffbydate(String[] files, String date1, String date2) {
        //Init command
        DiffCommand command = new DiffCommand();
        //Set parameters
        for (int i = 0; i < files.length; i++) {
            command.setFiles(new File[]{new File(files[i])});
        }
        //Format is yyyymmdd e.g 20070205
        command.setBeforeDate1(date1);
        command.setBeforeDate2(date2);
//	  command.
        return command;
    }

    /**
     * Check out the module
     *
     * @param modulename name of the module that to be checked out
     * @return command command of check out the module
	  *
     */
    public Command checkout(String modulename) {
        //Init new command
        CheckoutCommand command = new CheckoutCommand();
        //Set paramaters
        command.setModule(modulename);
        command.setRecursive(true);
        return command;
    }

    /**
     * Check out the module
     *
     * @param modulename name of the module that to be checked out
     * @return command command of check out the module
	  *
     */
    public Command checkouttoOutput(String modulename) {
        //Init new command
        CheckoutCommand command = new CheckoutCommand();
        //Set paramaters
        command.setModule(modulename);
        command.setPipeToOutput(true);
        command.setRecursive(true);
        return command;
    }
}
