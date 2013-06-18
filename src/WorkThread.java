
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class WorkThread implements Runnable {

    private String CVString = "";
    private String module = "";
    private String srcFolder = "";
    private String localRoot = "";
    private String tagName;
    private String[] fileList = null;
    private JTextArea consoleOut = null;
    private main parent = null;
    private MyMessage listener = null;
    private JScrollPane pane = null;
    private String releaseFileFolder = null;
    private boolean useCVSReleaseFile = false;
    private String CVSReleaseFileName = null;
    private JButton clickButton = null;

    public WorkThread(String _CVSString, String _module, String _srcFolder,
            String _localRoot, String _tagName, String[] _fileList,
            JTextArea _consoleOut, JScrollPane _pane,
            String _releaseFileFolder, boolean _useCVSReleaseFile,
            String _CVSReleaseFileName, JButton _button) {
        this.CVString = _CVSString;
        this.module = _module;
        this.srcFolder = _srcFolder;

        this.localRoot = _localRoot;
        this.tagName = _tagName;
        this.fileList = _fileList;
        this.consoleOut = _consoleOut;
        this.pane = _pane;
        this.releaseFileFolder = _releaseFileFolder;
        this.listener = new MyMessage(this.consoleOut, this.pane);
        this.useCVSReleaseFile = _useCVSReleaseFile;
        this.CVSReleaseFileName = _CVSReleaseFileName;
        this.clickButton = _button;
//		System.out.println(this.useCVSReleaseFile);
//		System.out.println(this.CVSReleaseFileName);
//		System.out.println(this.releaseFileFolder);
        // this.parent = _parent;

    }

    @Override
    public void run() {
        this.clickButton.setEnabled(false);
        try {
            if (localRoot == null) {
                localRoot = "c:\\CVSROOT";
            } else if (localRoot.trim().equals("")) {
                localRoot = "c:\\CVSROOT";
            }
            File detailFolder = new File(localRoot);
            if (!detailFolder.exists()) {
                detailFolder.mkdirs();
            }

            CVSClient cvsclient = new CVSClient(CVString);
            cvsclient.setLocalPath(localRoot);
            cvsclient.openConnection();
            cvsclient.excute(cvsclient.checkout(this.module), this.listener);
            cvsclient.closeConnection();
            this.listener.printMessage("");
            this.listener.printMessage("[Check out over!]");
            this.listener.printMessage("");
            Thread.sleep(500);
            JScrollBar jb = this.pane.getVerticalScrollBar();
            jb.setValue(jb.getMaximum());
            String releaseFilePath = "";
            ArrayList<File> updateFiles = new ArrayList<File>();
            if (this.useCVSReleaseFile) {
                this.listener
                        .printMessage("==============================================================");

                this.listener.printMessage("");
                releaseFilePath = this.localRoot + "\\" + this.module + "\\"
                        + this.releaseFileFolder + "\\"
                        + this.CVSReleaseFileName;
                this.listener.printMessage("[ Finding release note: "
                        + this.localRoot + "\\" + this.module + "\\"
                        + this.releaseFileFolder + "\\"
                        + this.CVSReleaseFileName + " ]");
                this.listener.printMessage("");
                File releaseFile = new File(releaseFilePath);

                if (releaseFile.exists()) {

                    BufferedReader br = new BufferedReader(new FileReader(
                            releaseFilePath));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        if (!line.startsWith("#") && !line.trim().equals("")) {
                            while (line.startsWith("/")) {
                                line = line.substring(1);
                            }
                            String tagFile = this.localRoot + "\\"
                                    + this.module + "\\" + this.srcFolder
                                    + "\\" + line.replace('/', '\\');
                            File tagToFile = new File(tagFile);

                            if (!tagToFile.exists()) {
                                this.listener.printMessage("[WARMING] "
                                        + tagToFile.getAbsolutePath()
                                        + " does not exists!!");
                            } else {
                                updateFiles.add(tagToFile);
                            }
                        }
                    }
                } else {
                    this.listener
                            .printMessage("[ERROR] Can't find release notes file: "
                            + releaseFilePath);
                }

            }// get tag file from release notes file
            else// get tagFile from text area
            {
                for (String line : fileList) {
                    if (!line.startsWith("#") && !line.trim().equals("")) {
                        while (line.startsWith("/")) {
                            line = line.substring(1);
                        }
                        String tagFile = this.localRoot + "\\" + this.module
                                + "\\" + this.srcFolder + "\\"
                                + line.replace('/', '\\');
                        File tagToFile = new File(tagFile);

                        if (!tagToFile.exists()) {
                            this.listener.printMessage("[WARMING] "
                                    + tagToFile.getAbsolutePath()
                                    + " does not exists!!");
                        } else {
                            updateFiles.add(tagToFile);
                        }
                    }
                }
            }
            Thread.sleep(500);
            jb.setValue(jb.getMaximum());
            if (updateFiles.size() == 0) {
                this.listener.printMessage("NO file to add tag!");
            } else {
                this.listener.printMessage("=======================File list==============================");
                for (File singleFile : updateFiles) {
                    this.listener.printMessage("To tag: " + singleFile.getAbsolutePath());
                }

                this.listener.printMessage("==============================================================");
                this.listener.printMessage("Start add tag to " + updateFiles.size() + " files.");
                this.listener.printMessage("==============================================================");
                CVSClient cvsclient1 = new CVSClient(this.CVString);
                cvsclient1.setLocalPath(this.localRoot);
                cvsclient1.openConnection();
                File[] x = new File[updateFiles.size()];
                for (int i = 0; i < updateFiles.size(); i++) {
                    x[i] = new File(updateFiles.get(i).getAbsolutePath());
                }
                if (updateFiles.size() > 0) {
                    cvsclient1.excute(cvsclient1.setTag(tagName, x), this.listener);

                }
                this.listener.printMessage("==============================================================");
                this.listener.printMessage("Finish tagging");
                this.listener.printMessage("==============================================================");
            }
            Thread.sleep(500);
            jb.setValue(jb.getMaximum());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "[Error]" + e.getMessage());
            e.printStackTrace();
        } finally {
            this.clickButton.setEnabled(true);
        }

    }
}
