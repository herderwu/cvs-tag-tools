
import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridBagLayout;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import javax.swing.Box;

import org.netbeans.lib.cvsclient.command.CommandAbortedException;
import org.netbeans.lib.cvsclient.connection.AuthenticationException;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JToggleButton;
import javax.swing.JCheckBox;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JEditorPane;
import javax.swing.JList;

public class main {

    private JFrame frmTagTool;
    private JTextField tagName;
    private JTextField releaseFileName;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    main window = new main();
                    window.frmTagTool.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void writeReg(String _tagName, String _module, boolean _checked) {
        Preferences pre = Preferences.systemRoot().node("/tagtool");
        pre.put("tagName", _tagName);
        pre.put("module", _module);
        pre.putBoolean("checked", _checked);

    }

    public String getPreTagName() {
        Preferences pre = Preferences.systemRoot().node("/tagtool");
        return pre.get("tagName", "");
    }

    public String getModule() {
        Preferences pre = Preferences.systemRoot().node("/tagtool");
        return pre.get("module", "");
    }

    public boolean getChecked() {
        Preferences pre = Preferences.systemRoot().node("/tagtool");
        return pre.getBoolean("checked", false);
    }

    /**
     * Create the application.
     *
     * @throws IOException
     */
    public main() throws IOException {
        initialize();
    }

    public void showDialog(String _message) {
        JOptionPane.showMessageDialog(null, _message);
    }

    /**
     * Initialize the contents of the frame.
     *
     * @throws IOException
     */
    private void initialize() throws IOException {
        Properties prop = new Properties();
        DES des = new DES("tag");
        FileInputStream fis = new FileInputStream("./config.properties");
        prop.load(fis);
        final String CSVString = des.getCVSString(prop.getProperty("CVSSTRING"));
        String modules = prop.getProperty("MODULES");
        final String srcFolder = prop.getProperty("SOURCEFOLDER");
        final String localRoot = prop.getProperty("LOCALROOT");
        final String releaseFileFolder = prop.getProperty("RELEASE_FILE_FOLDER");
        final String releaseFilePrefix = prop.getProperty("RELEASE_FILE_PREFIX");
        File detailFolder = new File(localRoot);
        if (!detailFolder.exists()) {
            detailFolder.mkdirs();
        }

        CVSClient cvsclient = null;

        try {
            //System.out.println("csv start:");
            cvsclient = new CVSClient(CSVString);
            cvsclient.setLocalPath(localRoot);
            cvsclient.openConnection();
            cvsclient.closeConnection();
        } catch (Exception e) {
            //System.out.println("csv error:");
            JOptionPane.showMessageDialog(null, "[Error] " + e.getMessage());
        }

        frmTagTool = new JFrame();
        frmTagTool.setTitle("Tag Tool");
        frmTagTool.setBounds(100, 100, 1122, 666);
        frmTagTool.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{9, 110, 154, 178, 580, 4, 0};
        gridBagLayout.rowHeights = new int[]{9, 31, 29, 33, 0, 494, 4, 0};
        gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
        frmTagTool.getContentPane().setLayout(gridBagLayout);

        JLabel lblTagname = new JLabel("Tag Name:");
        lblTagname.setFont(new Font("Arial Black", Font.PLAIN, 16));
        GridBagConstraints gbc_lblTagname = new GridBagConstraints();
        gbc_lblTagname.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblTagname.insets = new Insets(0, 0, 5, 5);
        gbc_lblTagname.gridx = 1;
        gbc_lblTagname.gridy = 1;
        frmTagTool.getContentPane().add(lblTagname, gbc_lblTagname);

        tagName = new JTextField();
        tagName.setFont(new Font("Arial Black", Font.PLAIN, 16));
        GridBagConstraints gbc_tagName = new GridBagConstraints();
        gbc_tagName.insets = new Insets(0, 0, 5, 5);
        gbc_tagName.anchor = GridBagConstraints.SOUTH;
        gbc_tagName.fill = GridBagConstraints.HORIZONTAL;
        gbc_tagName.gridwidth = 2;
        gbc_tagName.gridx = 2;
        gbc_tagName.gridy = 1;
        frmTagTool.getContentPane().add(tagName, gbc_tagName);
        tagName.setColumns(10);
        if (this.getPreTagName() != null) {
            this.tagName.setText(this.getPreTagName());
        }

//                //EncString
//                JTextField useName = new JTextField();
//		useName.setFont(new Font("Arial Black", Font.PLAIN, 16));
//		GridBagConstraints gbc_useName = new GridBagConstraints();
//		gbc_useName.insets = new Insets(0, 0, 5, 5);
//		gbc_useName.anchor = GridBagConstraints.SOUTH;
//		gbc_useName.fill = GridBagConstraints.HORIZONTAL;
//		gbc_useName.gridwidth = 20;
//		gbc_useName.gridx = 20;
//		gbc_useName.gridy = 10;
//		frmTagTool.getContentPane().add(useName, gbc_useName);
//		useName.setColumns(100);
//                useName.setText("abc");
//                //EncString end

        final JScrollPane scrollPane = new JScrollPane();
        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridheight = 5;
        gbc_scrollPane.gridx = 4;
        gbc_scrollPane.gridy = 1;
        frmTagTool.getContentPane().add(scrollPane, gbc_scrollPane);

        final JTextArea Console = new JTextArea();
        scrollPane.setViewportView(Console);

        JLabel lblModule = new JLabel("Module:");
        lblModule.setFont(new Font("Arial Black", Font.PLAIN, 16));
        GridBagConstraints gbc_lblModule = new GridBagConstraints();
        gbc_lblModule.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblModule.insets = new Insets(0, 0, 5, 5);
        gbc_lblModule.gridx = 1;
        gbc_lblModule.gridy = 2;
        frmTagTool.getContentPane().add(lblModule, gbc_lblModule);

        final JComboBox module = new JComboBox();
        module.setEditable(true);
        module.setFont(new Font("Arial Black", Font.PLAIN, 16));
        GridBagConstraints gbc_module = new GridBagConstraints();
        gbc_module.anchor = GridBagConstraints.NORTH;
        gbc_module.fill = GridBagConstraints.HORIZONTAL;
        gbc_module.insets = new Insets(0, 0, 5, 5);
        gbc_module.gridwidth = 2;
        gbc_module.gridx = 2;
        gbc_module.gridy = 2;

        frmTagTool.getContentPane().add(module, gbc_module);

        if (modules != null && !modules.trim().equals("")) {
            String[] modulesItem = modules.split("\\|");
            for (String itemStr : modulesItem) {
                module.addItem(itemStr);
            }
        }
        if (this.getModule() != null) {
            boolean find = false;
            for (int i = 0; i < module.getItemCount(); i++) {
                String itemText = module.getItemAt(i).toString().trim();
                String regText = this.getModule();
                if (itemText.trim().equals(regText)) {

                    module.setSelectedIndex(i);
                    find = true;
                    break;
                }
            }
            if (!find) {
                module.addItem(this.getModule());
                module.setSelectedItem(this.getModule());
            }

        }
        final JCheckBox autoCheckFile = new JCheckBox("Auto check");
        autoCheckFile.setFont(new Font("Arial Black", Font.PLAIN, 16));
        GridBagConstraints gbc_autoCheckFile = new GridBagConstraints();
        gbc_autoCheckFile.insets = new Insets(0, 0, 5, 5);
        gbc_autoCheckFile.gridx = 1;
        gbc_autoCheckFile.gridy = 3;
        frmTagTool.getContentPane().add(autoCheckFile, gbc_autoCheckFile);
        autoCheckFile.setSelected(this.getChecked());

        releaseFileName = new JTextField();
        releaseFileName.setFont(new Font("Arial Black", Font.PLAIN, 16));
        releaseFileName.setColumns(10);
        GridBagConstraints gbc_releaseFileName = new GridBagConstraints();
        gbc_releaseFileName.gridwidth = 2;
        gbc_releaseFileName.insets = new Insets(0, 0, 5, 5);
        gbc_releaseFileName.fill = GridBagConstraints.HORIZONTAL;
        gbc_releaseFileName.gridx = 2;
        gbc_releaseFileName.gridy = 3;
        frmTagTool.getContentPane().add(releaseFileName, gbc_releaseFileName);
        releaseFileName.setEditable(false);

        JLabel lblFileList = new JLabel("File list:");
        lblFileList.setFont(new Font("Arial Black", Font.PLAIN, 16));
        GridBagConstraints gbc_lblFileList = new GridBagConstraints();
        gbc_lblFileList.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblFileList.insets = new Insets(0, 0, 5, 5);
        gbc_lblFileList.gridx = 1;
        gbc_lblFileList.gridy = 4;
        frmTagTool.getContentPane().add(lblFileList, gbc_lblFileList);

        final JButton btnNewButton = new JButton("TAG");

        GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
        gbc_btnNewButton.fill = GridBagConstraints.BOTH;
        gbc_btnNewButton.insets = new Insets(0, 0, 5, 5);
        gbc_btnNewButton.gridx = 3;
        gbc_btnNewButton.gridy = 4;
        frmTagTool.getContentPane().add(btnNewButton, gbc_btnNewButton);

        btnNewButton.setFont(new Font("Arial Black", Font.BOLD, 16));
        JScrollPane scrollPane_1 = new JScrollPane();
        GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
        gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
        gbc_scrollPane_1.insets = new Insets(0, 0, 5, 5);
        gbc_scrollPane_1.gridwidth = 3;
        gbc_scrollPane_1.gridx = 1;
        gbc_scrollPane_1.gridy = 5;
        frmTagTool.getContentPane().add(scrollPane_1, gbc_scrollPane_1);

        final JTextArea fileList = new JTextArea();
        scrollPane_1.setViewportView(fileList);
        fileList.setFont(new Font("Consolas", Font.PLAIN, 15));
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                String[] files = fileList.getText().split("\n");
                ArrayList<String> processedFilePath = new ArrayList<String>();
                System.out.println(tagName.getText());

                System.out.println(module.getSelectedItem().toString());
                WorkThread wt = new WorkThread(CSVString, module.getSelectedItem().toString().trim(), srcFolder, localRoot, tagName.getText().trim(), files,
                        Console, scrollPane, releaseFileFolder, autoCheckFile.isSelected(), releaseFileName.getText(), btnNewButton);
                Thread th = new Thread(wt);
                writeReg(tagName.getText(), module.getSelectedItem().toString().trim(), autoCheckFile.isSelected());
                th.start();

            }
        });

        if (this.getChecked()) {
            releaseFileName.setEditable(true);
            releaseFileName.setText(this.getPreTagName() + releaseFilePrefix);
            fileList.setEnabled(false);
            fileList.setBackground(new Color(230, 230, 230));
        }
        DocumentListener dl = new DocumentListener() {
            private void updateFileName() {
                if (autoCheckFile.isSelected()) {
                    releaseFileName.setText(tagName.getText() + releaseFilePrefix);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent arg0) {
                this.updateFileName();

            }

            @Override
            public void insertUpdate(DocumentEvent arg0) {
                this.updateFileName();

            }

            @Override
            public void removeUpdate(DocumentEvent arg0) {
                this.updateFileName();

            }
        };
        tagName.getDocument().addDocumentListener(dl);
        ItemListener itemListener = new ItemListener() {
            public void itemStateChanged(ItemEvent e) {

                Object obj = e.getItem();
                if (obj.equals(autoCheckFile)) {
                    if (autoCheckFile.isSelected()) {
                        releaseFileName.setEditable(true);
                        releaseFileName.setText(tagName.getText() + releaseFilePrefix);
                        fileList.setEnabled(false);
                        fileList.setBackground(new Color(230, 230, 230));
                    } else {
                        releaseFileName.setText("");
                        releaseFileName.setEditable(false);
                        fileList.setEnabled(true);
                        fileList.setBackground(Color.WHITE);
                    }
                }

            }
        };
        autoCheckFile.addItemListener(itemListener);
    }
}
