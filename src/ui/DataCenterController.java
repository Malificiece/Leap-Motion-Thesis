/*
 * Copyright (c) 2015, Garrett Benoit. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

package ui;

import static javax.media.opengl.GL.GL_COLOR_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_BUFFER_BIT;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import javax.media.opengl.GL2;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileSystemView;

import utilities.MyUtilities;
import enums.FileExt;
import enums.FileName;
import enums.FilePath;
import enums.Keyboard;
import enums.KeyboardType;
import experiment.data.formatter.DataFormatter;
import experiment.playback.PlaybackManager;

public class DataCenterController extends GraphicsController {
    private final ReentrantLock DATA_LOCK = new ReentrantLock();
    private JSplitPane splitPane;
    private Component playbackComponent;
    // File chooser to chose home data folder -- default is recorded_data
    private JTextField selectedDataFolderTextField;
    private JButton browseDataFolderButton;
    private JButton consolidateDataButton;
    private File dataFolder;
    // File list viewer
    private JList<File> currentDirectoryList;
    private DefaultListModel<File> currentDirectoryListModel;
    private JList<File> fileViewerList;
    private DefaultListModel<File> fileViewerListModel;
    private JButton goBackFolderButton;
    private JButton homeFolderButton;
    private JButton calculateDataButton;
    private JButton specialDataButton;
    // File data previewer
    private JTextArea fileDetailsTextArea;
    // File playback
    private JLabel currentTime;
    private JLabel maximumTime;
    private JProgressBar playbackProgressBar;
    private JPanel canvasPanel;
    private JTextField selectedFileTextField;
    private JButton playPauseButton;
    ImageIcon playIcon;
    ImageIcon pauseIcon;
    private JButton stopButton;
    private boolean isPlayButton = true;
    // Managers
    private boolean isFormatting = false;
    private PlaybackManager playbackManager;

    @SuppressWarnings("unchecked")
    public DataCenterController () {
        // main
        frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        // File chooser to chose home data folder
        selectedDataFolderTextField = new JTextField(FilePath.RECORDED_DATA.getPath());
        browseDataFolderButton = new JButton("Browse...");
        consolidateDataButton = new JButton("Consolidate"); // Pools all calculations into a single file containing all subjects
        // File list viewer
        currentDirectoryListModel = new DefaultListModel<File>();
        currentDirectoryList = new JList<File>(currentDirectoryListModel);
        currentDirectoryList.setCellRenderer(new FileRenderer(true, true));
        fileViewerListModel = new DefaultListModel<File>();
        fileViewerList = new JList<File>(fileViewerListModel);
        fileViewerList.setCellRenderer(new FileRenderer(true, false));
        goBackFolderButton = new JButton(MyUtilities.SWING_UTILITIES.resizeImageIcon(new ImageIcon(FilePath.ASSETS.getPath()
                + FileName.UP_FOLDER.getName() + FileExt.PNG.getExt()), 16, 16));
        homeFolderButton = new JButton(MyUtilities.SWING_UTILITIES.resizeImageIcon(new ImageIcon(FilePath.ASSETS.getPath()
                + FileName.HOME.getName() + FileExt.PNG.getExt()), 16, 16));
        calculateDataButton = new JButton("Calculate"); // Calculates all data for this subject for every input
        specialDataButton = new JButton("Special");
        // File data previewer
        fileDetailsTextArea = new JTextArea();
        // File playback
        currentTime = new JLabel();
        maximumTime = new JLabel();
        playbackProgressBar = new JProgressBar();
        playbackProgressBar.setMinimum(0);
        canvasPanel = new JPanel();
        canvas = new GLCanvas(CAPABILITIES);
        canvasPanel.add(canvas);
        selectedFileTextField = new JTextField();
        playIcon = MyUtilities.SWING_UTILITIES.resizeImageIcon(new ImageIcon(FilePath.ASSETS.getPath()
                + FileName.PLAY.getName() + FileExt.PNG.getExt()), 16, 16);
        pauseIcon = MyUtilities.SWING_UTILITIES.resizeImageIcon(new ImageIcon(FilePath.ASSETS.getPath()
                + FileName.PAUSE.getName() + FileExt.PNG.getExt()), 16, 16);
        playPauseButton = new JButton(playIcon);
        stopButton = new JButton(MyUtilities.SWING_UTILITIES.resizeImageIcon(new ImageIcon(FilePath.ASSETS.getPath()
                + FileName.STOP.getName() + FileExt.PNG.getExt()), 16, 16));
        
        // set home folder
        dataFolder = new File(FilePath.RECORDED_DATA.getPath());
        selectedDataFolderTextField.setText(dataFolder.toString());
        currentDirectoryListModel.clear();
        currentDirectoryListModel.addElement(dataFolder);
        for(File file: currentDirectoryListModel.get(0).listFiles()) {
            fileViewerListModel.addElement(file);
        }
        
        JButton buttons[] = {browseDataFolderButton, consolidateDataButton, goBackFolderButton,
                homeFolderButton, calculateDataButton, playPauseButton, stopButton, specialDataButton};
        @SuppressWarnings("rawtypes")
        JList lists[] = {currentDirectoryList, fileViewerList};
        JTextField textFields[] = {selectedDataFolderTextField, selectedFileTextField};
        JLabel labels[] = {currentTime, maximumTime};
        
        // Window builder builds window using important fields here. It adds unimportant fields that we use for aesthetics only.
        WindowBuilder.buildDataCenterWindow(frame, canvasPanel, fileDetailsTextArea, textFields, lists, buttons, splitPane, labels, playbackProgressBar);
        playbackComponent = splitPane.getBottomComponent();
        splitPane.setBottomComponent(null);
        canvas.setFocusable(false);
        
        browseDataFolderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // needs to prompt the file chooser
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File("."));
                fileChooser.setDialogTitle("Choose Data Folder...");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setAcceptAllFileFilterUsed(false);
                if(fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    dataFolder = fileChooser.getSelectedFile();
                    selectedDataFolderTextField.setText(dataFolder.toString());
                    currentDirectoryListModel.clear();
                    currentDirectoryListModel.addElement(dataFolder);
                    fileViewerList.clearSelection();
                    fileViewerListModel.clear();
                    for(File file: currentDirectoryListModel.get(0).listFiles()) {
                        fileViewerListModel.addElement(file);
                    }
                } else {
                    // canceled, do nothing
                }
                fileChooser = null;
            }
        });
        
        consolidateDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DATA_LOCK.lock();
                try {
                    if(!isFormatting()) {
                        // needs to safeguard from exiting or doing anything
                        // disable UI
                        // Use the Data Formatter to take all of the data from subjects
                        // and to stuff it into one master .m file.
                        disableUI();
                        isFormatting = true;
                        Thread thread = new Thread(new DataFormatter(DataFormatter.FormatProcessType.CONSOLIDATE, dataFolder,
                                new JButton [] {browseDataFolderButton, consolidateDataButton, calculateDataButton, specialDataButton}));
                        thread.start();
                    }
                } finally {
                    DATA_LOCK.unlock();
                }
            }
        });
        
        goBackFolderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!currentDirectoryListModel.get(0).equals(dataFolder)) {
                    File parentDirectory = currentDirectoryListModel.get(0).getParentFile();
                    currentDirectoryListModel.clear();
                    currentDirectoryListModel.addElement(parentDirectory);
                    fileViewerList.clearSelection();
                    fileViewerListModel.clear();
                    for(File file: currentDirectoryListModel.get(0).listFiles()) {
                        fileViewerListModel.addElement(file);
                    }
                }
            }
        });
        
        homeFolderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentDirectoryListModel.clear();
                currentDirectoryListModel.addElement(dataFolder);
                fileViewerList.clearSelection();
                fileViewerListModel.clear();
                for(File file: currentDirectoryListModel.get(0).listFiles()) {
                    fileViewerListModel.addElement(file);
                }
            }
        });
        
        calculateDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DATA_LOCK.lock();
                try {
                    if(!isFormatting()) {
                        // needs to safeguard from exiting or doing anything
                        // disable UI
                        // Use the Data Formatter to calculate the special data
                        // for an individual subject
                        disableUI();
                        isFormatting = true;
                        Thread thread = new Thread(new DataFormatter(DataFormatter.FormatProcessType.CALCULATE, dataFolder,
                                new JButton [] {browseDataFolderButton, consolidateDataButton, calculateDataButton, specialDataButton}));
                        thread.start();
                    }
                } finally {
                    DATA_LOCK.unlock();
                }
            }
        });
        
        specialDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                DATA_LOCK.lock();
                try {
                    if(!isFormatting()) {
                        // needs to safeguard from exiting or doing anything
                        // disable UI
                        // Use the Data Formatter to calculate the special data
                        // for an individual subject
                        disableUI();
                        isFormatting = true;
                        Thread thread = new Thread(new DataFormatter(DataFormatter.FormatProcessType.SPECIAL, dataFolder,
                                new JButton [] {browseDataFolderButton, consolidateDataButton, calculateDataButton, specialDataButton}));
                        thread.start();
                    }
                } finally {
                    DATA_LOCK.unlock();
                }
            }
        });
        
        playPauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DATA_LOCK.lock();
                try {
                    if(isPlayButton) { // if showing a play button then it is paused or stopped.
                        // play the playback
                        playPauseButton.setIcon(pauseIcon);
                        playbackManager.setPause(false);
                    } else { // if showing a pause button then it is playing
                        // pause the playback
                        playPauseButton.setIcon(playIcon);
                        playbackManager.setPause(true);
                    }
                    isPlayButton = !isPlayButton;
                } finally {
                    DATA_LOCK.unlock();
                }
            }
        });
        
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DATA_LOCK.lock();
                try {
                    if(playbackManager != null) {
                        playbackManager.setPause(true);   
                        playbackManager.playFromBeginning();
                        isPlayButton = true;
                        playbackProgressBar.setValue(0);
                        currentTime.setText(String.format("%d:%02d", 0, 0));
                    }
                    playPauseButton.setIcon(playIcon);
                } finally {
                    DATA_LOCK.unlock();
                }
            }
        });
        
        fileViewerList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                DATA_LOCK.lock();
                try {
                    if(fileViewerList.getSelectedIndex() == -1) {
                        selectedFileTextField.setText("");
                        fileDetailsTextArea.setText("");
                        // Remove previous playback tools if they exist.
                        if(keyboard != null) {
                            if(playbackManager != null) keyboard.finishPlayback(playbackManager);
                            playbackManager = null;
                            disablePlaybackUI();
                            keyboard = null;
                        }
                    }
                } finally {
                    DATA_LOCK.unlock();
                }
            }
        });
        
        fileViewerList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                DATA_LOCK.lock();
                try {
                    JList<File> list = (JList<File>) me.getSource();
                    File file = fileViewerListModel.get(list.locationToIndex(me.getPoint()));
                    if(me.getClickCount() == 1) {
                        if(!selectedFileTextField.getText().equalsIgnoreCase(file.getName())
                                && !selectedFileTextField.getText().equalsIgnoreCase(file.toString())) {
                            // Populate the file previewer with file contents.
                            try {
                                if(file.isDirectory()) {
                                    selectedFileTextField.setText(file.toString());
                                    fileDetailsTextArea.setText("Directory contents:\n");
                                    for(File f: file.listFiles()) {
                                        fileDetailsTextArea.append(f.getName() + "\n");
                                    }
                                } else {
                                    selectedFileTextField.setText(file.getName());
                                    ArrayList<String> fileContents = MyUtilities.FILE_IO_UTILITIES.readListFromFile(file);
                                    // Populate the text area with the file contents.
                                    fileDetailsTextArea.setText("");
                                    for(String line: fileContents) {
                                        fileDetailsTextArea.append(line + "\n");
                                    }
                                }
                            } catch (IOException e) {
                                System.out.println("An error occured while trying to load the file.");
                                e.printStackTrace();
                                // Populate the text area with an error message.
                                fileDetailsTextArea.setText("An error occured while trying to load this file.");
                            }
                            // Populate the canvas and enable playback tools if a .playback file.
                            if(file.toString().contains(FileExt.PLAYBACK.getExt())) {
                                // Transition existing playback tools to the new .playback file.
                                // Else create and enable new playback tools.
                                KeyboardType keyboardType = null;
                                for(KeyboardType kt: KeyboardType.values()) {
                                    if(file.toString().contains(kt.getFileName())) {
                                        keyboardType = kt;
                                        break;
                                    }
                                }
                                if(keyboard != null) {
                                    // Delete old playback manager
                                    if(playbackManager != null) keyboard.finishPlayback(playbackManager);
                                    playbackManager = null;
                                    // Swap keyboards
                                    if(keyboard.getType() != keyboardType) {
                                        // Remove all settings, attributes, and renderables from the previous keyboard.
                                        removeKeyboardFromUI();
                                        
                                        // Add all settings, attributes, and renderables from the new keyboard.
                                        keyboard = Keyboard.getByType(keyboardType).getKeyboard();
                                        addKeyboardToUI();
                                    }
                                    playPauseButton.setIcon(playIcon);
                                    isPlayButton = true;
                                } else {
                                    // Add all settings, attributes, and renderables from the new keyboard.
                                    keyboard = Keyboard.getByType(keyboardType).getKeyboard();
                                    enablePlaybackUI();
                                    addKeyboardToUI();
                                }
                                // Update the play time and initial progress bar.
                                playbackManager = new PlaybackManager(false, fileViewerList.getSelectedValue());
                                keyboard.beginPlayback(playbackManager);
                                playbackManager.setPause(true);
                                playbackProgressBar.setMaximum(playbackManager.getFinishTime());
                                playbackProgressBar.setValue(0);
                                int minutes = playbackManager.getFinishTime() / 60;
                                int seconds = playbackManager.getFinishTime() % 60;
                                currentTime.setText(String.format("%d:%02d", 0, 0));
                                maximumTime.setText(String.format("%d:%02d", minutes, seconds));
                            } else {
                                // Remove previous playback tools if they exist.
                                if(keyboard != null) {
                                    if(playbackManager != null) keyboard.finishPlayback(playbackManager);
                                    playbackManager = null;
                                    disablePlaybackUI();
                                    keyboard = null;
                                }
                            }
                        }
                    } else if(me.getClickCount() > 1 && file.isDirectory()) {
                        currentDirectoryListModel.clear();
                        currentDirectoryListModel.addElement(file);
                        fileViewerList.clearSelection();
                        fileViewerListModel.clear();
                        for(File f: currentDirectoryListModel.get(0).listFiles()) {
                            fileViewerListModel.addElement(f);
                        }
                    }
                } finally {
                    DATA_LOCK.unlock();
                }
            }
        });
        
        // by default, an AWT Frame doesn't do anything when you click
        // the close button; this bit of code will terminate the program when
        // the window is asked to close
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                DATA_LOCK.lock();
                try {
                    if(!isFormatting()) {
                        if(keyboard != null) {
                            if(playbackManager != null) keyboard.finishPlayback(playbackManager);
                            playbackManager = null;
                            disablePlaybackUI();
                            // reset home folder
                        }
                        disable();
                    }
                } finally {
                    DATA_LOCK.unlock();
                }
            }
        });
        
        frame.pack();
    }
    
    @Override
    public void enable() {
        frame.setVisible(true);
        frame.requestFocusInWindow();
        canvas.addGLEventListener(this);
        isEnabled = true;
    }

    @Override
    protected void disable() {
        removeKeyboardFromUI();
        frame.setVisible(false);
        canvas.disposeGLEventListener(this, true);
        isEnabled = false;
        frame.dispose();
    }
    
    private void disableUI() {
        consolidateDataButton.setEnabled(false);
        calculateDataButton.setEnabled(false);
        browseDataFolderButton.setEnabled(false);
    }
    
    private boolean isFormatting() {
        if(isFormatting) {
            if(browseDataFolderButton.isEnabled()) {
                isFormatting = false;
            }
        }
        return isFormatting;
    }
    
    private void enablePlaybackUI() {
        splitPane.setBottomComponent(playbackComponent);
        playPauseButton.setEnabled(true);
        stopButton.setEnabled(true);
        frame.pack();
    }
    
    private void disablePlaybackUI() {
        splitPane.setBottomComponent(null);
        playPauseButton.setEnabled(false);
        stopButton.setEnabled(false);
        playPauseButton.setIcon(playIcon);
        isPlayButton = true;
        if(isEnabled) {
            frame.revalidate();
            frame.repaint();
            frame.pack();
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Dimension windowSize = frame.getSize();
            frame.setLocation((int)(screenSize.getWidth()/2 - windowSize.getWidth()/2),
                              (int)(screenSize.getHeight()/2 - windowSize.getHeight()/2));
        }
    }
    
    public void update() {
        DATA_LOCK.lock();
        try {
            // order for each one needs to be corrected to probably be in the same order so that we can compare same words too.
            if(keyboard != null) {
                keyboard.update();
                if(playbackManager != null && playbackProgressBar.getValue() != playbackManager.getElapsedTime()
                        && playbackManager.getElapsedTime() >= 0 && playbackManager.getElapsedTime() <= playbackManager.getFinishTime()) {
                    playbackProgressBar.setValue(playbackManager.getElapsedTime());
                    int minutes = playbackManager.getElapsedTime() / 60;
                    int seconds = playbackManager.getElapsedTime() % 60;
                    currentTime.setText(String.format("%d:%02d", minutes, seconds));
                }
            }
        } finally {
            DATA_LOCK.unlock();
        }
    }

    @Override
    protected void render(GL2 gl) {
        // Only need to render when we're playing back. Otherwise no point.
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        if(keyboard != null) {
            keyboard.render(gl);
        }
    }
    
    private void removeKeyboardFromUI() {
        if(keyboard != null) {
            keyboard.removeFromUI(canvasPanel, canvas);
        }
    }
    
    private void addKeyboardToUI() {
        if(keyboard != null) {
            keyboard.addToUI(canvasPanel, canvas);
            canvas.setPreferredSize(new Dimension(keyboard.getImageWidth(), keyboard.getImageHeight()));
            canvas.setSize(keyboard.getImageWidth(), keyboard.getImageHeight());
            frame.revalidate();
            frame.repaint();
            frame.pack();
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Dimension windowSize = frame.getSize();
            frame.setLocation((int)(screenSize.getWidth()/2 - windowSize.getWidth()/2),
                              (int)(screenSize.getHeight()/2 - windowSize.getHeight()/2));
        }
    }
    
    @SuppressWarnings("serial")
    class FileRenderer extends DefaultListCellRenderer {
        private boolean pad;
        private boolean noSelect;
        private Border padBorder = new EmptyBorder(3,3,3,3);

        FileRenderer(boolean pad, boolean noSelect) {
            this.pad = pad;
            this.noSelect = noSelect;
        }

        @Override
        public Component getListCellRendererComponent(
            JList<?> list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {

            Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            JLabel label = (JLabel) component;
            File file = (File) value;
            label.setText(file.getName());
            label.setIcon(FileSystemView.getFileSystemView().getSystemIcon(file));
            if (noSelect) label.setBackground(Color.WHITE);
            if (pad) label.setBorder(padBorder);

            return label;
        }
    }
}
