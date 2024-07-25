package app.src.main.java.org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame implements DirectoryPanel.FileSelectionListener {

    private FileHandler fileHandler;
    private ConfigManager configManager;
    private JPanel bottomPanel;
    private DirectoryPanel directoryPanel;
    private CodeTextPane currentCodeTextPane;
    private JPanel centerPanel;

    private List<CodeTextPane> openCodeTextPanes = new ArrayList<>();
    private JPanel firstPane;
    private JPanel secondPane;
    private JPanel thirdPane;
    private JScrollPane scrollPane1;
    private JScrollPane scrollPane2;
    private JScrollPane scrollPane3;
    private JSplitPane splitPane;
    private boolean isFullscreen = false;
    private Rectangle windowedBounds;

    public MainFrame() {
        super("DEI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 900);
        setLocationRelativeTo(null);
        setBackground(Color.BLACK);

        fileHandler = new FileHandler(this);
        configManager = new ConfigManager();

        bottomPanel = BottomPanel.createBottomPanel(this);
        centerPanel = new JPanel(new BorderLayout());

        JMenuBar menuBar = MenuBar.createMenuBar(this);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(menuBar, BorderLayout.NORTH);
        getContentPane().add(centerPanel, BorderLayout.CENTER);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        KeyStroke ctrlT = KeyStroke.getKeyStroke(KeyEvent.VK_T, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(ctrlT, "openTerminal");
        ActionMap actionMap = getRootPane().getActionMap();
        actionMap.put("openTerminal", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openTerminal();
            }
        });

        KeyStroke ctrlE = KeyStroke.getKeyStroke(KeyEvent.VK_E, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        inputMap.put(ctrlE, "toggleFocus");
        actionMap.put("toggleFocus", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleFocus();
            }
        });
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (splitPane != null) {
                    splitPane.setDividerLocation(centerPanel.getWidth() / 2);
                }
            }
        });
        KeyStroke f11 = KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0);
        inputMap.put(f11, "toggleFullscreen");
        actionMap.put("toggleFullscreen", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleFullscreen();
            }
        });

        setVisible(true);
    }

    @Override
    public void onFileSelected(File file) {
        openFile(file);
    }

    public void openDirectoryPanel() {
        centerPanel.removeAll();

        if (directoryPanel == null) {
            directoryPanel = new DirectoryPanel(this);
            directoryPanel.setPreferredSize(new Dimension(1000, 600));
        }

        centerPanel.add(directoryPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public void openUtilityDirectory(String s) {
        centerPanel.removeAll();

        if (directoryPanel == null) {
            directoryPanel = new DirectoryPanel(this);
            directoryPanel.setPreferredSize(new Dimension(1000, 600));
        }

        directoryPanel.loadDirectory(new File(s));
        centerPanel.add(directoryPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
    
    public void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        int userChoice = fileChooser.showOpenDialog(this);
        if (userChoice == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            openFile(selectedFile);
        }
    }

    public void openFile(File file) {
        try {
            CodeTextPane codeTextPane = new CodeTextPane();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                codeTextPane.setText(content.toString());
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error opening file: " + file.getName(), "File Open Error", JOptionPane.ERROR_MESSAGE);
            }
            codeTextPane.setFile(file);
            
            openCodeTextPanes.add(codeTextPane);
            
            if (openCodeTextPanes.size() == 1) {

                scrollPane1 = new JScrollPane(openCodeTextPanes.get(0));
                scrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
                scrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                centerPanel.removeAll();
                centerPanel.add(scrollPane1, BorderLayout.CENTER);

            } else if (openCodeTextPanes.size() == 2) {
                
                scrollPane1 = new JScrollPane(openCodeTextPanes.get(0));
                scrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
                scrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

                scrollPane2 = new JScrollPane(openCodeTextPanes.get(1));
                scrollPane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
                scrollPane2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

                firstPane = new JPanel(new BorderLayout());
                firstPane.add(scrollPane1, BorderLayout.CENTER);
    
                secondPane = new JPanel(new BorderLayout());
                secondPane.add(scrollPane2, BorderLayout.CENTER);
                
                splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, firstPane, secondPane);
                splitPane.setDividerLocation(centerPanel.getWidth() / 2);
                splitPane.setDividerSize(3);
                splitPane.setOneTouchExpandable(true);
                splitPane.setContinuousLayout(true);
                centerPanel.removeAll();
                centerPanel.add(splitPane, BorderLayout.CENTER);

            } else if (openCodeTextPanes.size() == 3) {

                scrollPane3 = new JScrollPane(openCodeTextPanes.get(2));
                scrollPane3.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
                scrollPane3.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

                thirdPane = new JPanel(new BorderLayout());
                thirdPane.add(scrollPane3, BorderLayout.CENTER);
    
                JSplitPane rightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, secondPane, thirdPane);
                rightSplitPane.setDividerSize(3);
                rightSplitPane.setOneTouchExpandable(true);
                rightSplitPane.setContinuousLayout(true);
                splitPane.setRightComponent(rightSplitPane);
                splitPane.setDividerLocation(centerPanel.getWidth() / 2);
                rightSplitPane.setDividerLocation(centerPanel.getWidth() / 4);
                
                centerPanel.removeAll();
                centerPanel.add(splitPane, BorderLayout.CENTER);
            } else {
                scrollPane3 = new JScrollPane(codeTextPane);
                scrollPane3.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
                scrollPane3.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

                openCodeTextPanes.set(2, codeTextPane);
                thirdPane.removeAll();
                thirdPane.add(scrollPane3, BorderLayout.CENTER);
                thirdPane.revalidate();
                thirdPane.repaint();
                centerPanel.removeAll();
                centerPanel.add(splitPane, BorderLayout.CENTER);
            }
    
            revalidate();
            repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to open file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void saveFile() {
        fileHandler.saveToFile();
    }

    private void toggleFullscreen() {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        if (isFullscreen) {
            gd.setFullScreenWindow(null);
            dispose();
            setUndecorated(false);
            setBounds(windowedBounds);
            setVisible(true);
            isFullscreen = false;
        } else {
            windowedBounds = getBounds();
            dispose();
            setUndecorated(true);
            setVisible(true);
            gd.setFullScreenWindow(this);
            isFullscreen = true;
        }
    }

    public void toggleFocus() {
        Component focusedComponent = getFocusOwner();
        if (focusedComponent != null) {
            if (centerPanel.isAncestorOf(focusedComponent)) {
                focusBottomPanel();
            } else if (bottomPanel.isAncestorOf(focusedComponent)) {
                changeFocus();
            } else {
                changeFocus();
            }
        } else {
            changeFocus();
        }
    }
    
    public void focusBottomPanel() {
        for (Component component : bottomPanel.getComponents()) {
            if (component.isFocusable() && !(component instanceof JScrollBar)) {
                component.requestFocus();
                break;
            }
        }
    }
    
    public void changeFocus() {
        for (Component component : centerPanel.getComponents()) {
            if (component instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) component;
                JViewport viewport = scrollPane.getViewport();
                Component view = viewport.getView();
                if (view instanceof CodeTextPane) {
                    view.requestFocus();
                    return;
                }
            } else if (component instanceof DirectoryPanel) {
                DirectoryPanel dirPanel = (DirectoryPanel) component;
                if (dirPanel.getFileList().isFocusable()) {
                    dirPanel.getFileList().requestFocus();
                    return;
                }
            }
        }
    }

    public CodeTextPane getCurrentCodeTextPane() {
        return currentCodeTextPane;
    }

    public void openTerminal() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            String command = "";
            File currentFile = getCurrentCodeTextPane() != null ? getCurrentCodeTextPane().getFile() : new File(System.getProperty("user.home"));
            String workingDirectory = currentFile.isDirectory() ? currentFile.getAbsolutePath() : currentFile.getParentFile().getAbsolutePath();
    
            if (os.contains("win")) {
                command = "cmd.exe /c start cmd.exe /K \"cd /d " + workingDirectory + "\"";
            } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
                command = "x-terminal-emulator -e bash -c 'cd \"" + workingDirectory + "\" && exec bash'";
            } else {
                JOptionPane.showMessageDialog(this, "Unsupported OS for opening terminal.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            Runtime.getRuntime().exec(command);
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to open terminal.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
