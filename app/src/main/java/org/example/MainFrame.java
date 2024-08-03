package app.src.main.java.org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MainFrame extends JFrame implements DirectoryPanel.FileSelectionListener {

    private FileHandler fileHandler;
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

    private Properties config;

    public MainFrame() {
        super("DEI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 900);
        setLocationRelativeTo(null);
        setBackground(Color.BLACK);

        fileHandler = new FileHandler(this);

        bottomPanel = BottomPanel.createBottomPanel(this);
        centerPanel = new JPanel(new BorderLayout());

        JMenuBar menuBar = MenuBar.createMenuBar(this);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(menuBar, BorderLayout.NORTH);
        getContentPane().add(centerPanel, BorderLayout.CENTER);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        loadConfigProperties();

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

        KeyStroke ctrlW = KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        InputMap inputMap2 = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap2.put(ctrlW, "closeCodeTextPane");
        ActionMap actionMap2 = getRootPane().getActionMap();
        actionMap2.put("closeCodeTextPane", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeCodeTextPane();
            }
        });

        KeyStroke ctrlS = KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        inputMap.put(ctrlS, "saveFile");
        actionMap.put("saveFile", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveFile();
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

        KeyStroke ctrlR = KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        InputMap inputMap3 = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap3.put(ctrlR, "runCode");
        ActionMap actionMap3 = getRootPane().getActionMap();
        actionMap3.put("runCode", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runCurrentCode();
            }
        });

        setVisible(true);
    }

    private void loadConfigProperties() {
        config = new Properties();
        try (InputStream input = new FileInputStream("config.properties")) {
            config.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load configuration properties.", "Error", JOptionPane.ERROR_MESSAGE);
        }
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
            codeTextPane.makeFocusable(this);
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
    
            applyConfigurations(codeTextPane);
    
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
                splitPane.setOneTouchExpandable(false);
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
                rightSplitPane.setOneTouchExpandable(false);
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
            currentCodeTextPane = codeTextPane;
            revalidate();
            repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to open file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void applyConfigurations(CodeTextPane codeTextPane) {
        String fontName = config.getProperty("font", "Menlo");
        int fontSize = Integer.parseInt(config.getProperty("font.size", "16"));
        Color backgroundColor = Color.decode(config.getProperty("textpane.background.color", "#000847"));

        codeTextPane.setFont(new Font(fontName, Font.PLAIN, fontSize));
        codeTextPane.setBackground(backgroundColor);
        codeTextPane.setForeground(Color.decode(config.getProperty("default.text.color", "#ffffff")));
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
        if (directoryPanel != null && centerPanel.isAncestorOf(directoryPanel)) {
            if (directoryPanel.getFileList().isFocusable()) {
                directoryPanel.getFileList().requestFocus();
                return;
            }
        }
    
        int currentFocusIndex = openCodeTextPanes.indexOf(currentCodeTextPane);
    
        if (currentFocusIndex == -1 || openCodeTextPanes.isEmpty()) {
            return;
        }
    
        int nextFocusIndex = (currentFocusIndex + 1) % openCodeTextPanes.size();
        currentCodeTextPane = openCodeTextPanes.get(nextFocusIndex);
        setCurrentCodeTextPane(currentCodeTextPane);
        currentCodeTextPane.requestFocus();
    }

    public CodeTextPane getCurrentCodeTextPane() {
        return currentCodeTextPane;
    }

    public List<CodeTextPane> getOpenCodeTextPanes() {
        return openCodeTextPanes;
    }

    public void closeCodeTextPane() {
        if (currentCodeTextPane == null) {
            return;
        }
        openCodeTextPanes.remove(currentCodeTextPane);
        centerPanel.removeAll();
        if (openCodeTextPanes.size() == 1) {
            scrollPane1 = new JScrollPane(openCodeTextPanes.get(0));
            scrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
            scrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
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
            splitPane.setOneTouchExpandable(false);
            splitPane.setContinuousLayout(true);
            centerPanel.add(splitPane, BorderLayout.CENTER);
        } else return;
        setCurrentCodeTextPane(openCodeTextPanes.get(0));
        revalidate();
        repaint();
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

    public void setCurrentCodeTextPane(CodeTextPane c) {
        currentCodeTextPane = c;
    }

    private void runCurrentCode() {
        if (currentCodeTextPane == null || currentCodeTextPane.getFile() == null) {
            JOptionPane.showMessageDialog(this, "No file is open to compile and run.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        File file = currentCodeTextPane.getFile();
        if (!file.getName().endsWith(".cpp")) {
            JOptionPane.showMessageDialog(this, "The open file is not a C++ source file.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        String fileNameWithoutExtension = file.getName().replaceFirst("[.][^.]+$", "");
        String inputFilePath = config.getProperty("input.file");
        String outputFilePath = config.getProperty("output.file");
        
        // Adjust paths for the current operating system
        String command;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            command = String.format("cmd.exe /c g++ \"%s\" -o \"%s.exe\" && .\\%s.exe < \"%s\" > \"%s\"", 
                                    file.getAbsolutePath(), 
                                    fileNameWithoutExtension, 
                                    fileNameWithoutExtension, 
                                    inputFilePath, 
                                    outputFilePath);
        } else {
            command = String.format("g++ \"%s\" -o \"%s\" && ./%s < \"%s\" > \"%s\"", 
                                    file.getAbsolutePath(), 
                                    fileNameWithoutExtension, 
                                    fileNameWithoutExtension, 
                                    inputFilePath, 
                                    outputFilePath);
        }
    
        try {
            System.out.println("dbg");
            Process process = Runtime.getRuntime().exec(command);
            new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line); // Optionally print the output to console
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }).start();
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to compile or run the code.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
