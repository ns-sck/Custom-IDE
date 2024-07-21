package app.src.main.java.org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class MainFrame extends JFrame implements DirectoryPanel.FileSelectionListener {

    private FileHandler fileHandler;
    private ConfigManager configManager;

    private JPanel bottomPanel;
    private DirectoryPanel directoryPanel;
    private CodeTextPane CodeTextPane;
    private JPanel centerPanel;

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

    public void openFile(File file) {
        centerPanel.removeAll();

        CodeTextPane = new CodeTextPane();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            CodeTextPane.setText(content.toString());
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error opening file: " + file.getName(), "File Open Error", JOptionPane.ERROR_MESSAGE);
        }
        CodeTextPane.setFile(file);

        int fontSize = Integer.parseInt(configManager.getProperty("font.size", "16"));
        String font = configManager.getProperty("font", "Menlo");
        CodeTextPane.setFont(new Font(font, Font.PLAIN, fontSize));

        JScrollPane scrollPane = new JScrollPane(CodeTextPane);
        scrollPane.setPreferredSize(new Dimension(1000, 600));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        centerPanel.add(scrollPane, BorderLayout.CENTER);
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

    public void saveFile() {
        fileHandler.saveToFile();
    }

    public void toggleFocus() {
        Component focusedComponent = getFocusOwner();
        if (focusedComponent != null) {
            if (centerPanel.isAncestorOf(focusedComponent)) {
                focusBottomPanel();
            } else if (bottomPanel.isAncestorOf(focusedComponent)) {
                focusCodeTextPaneOrDirectoryPanel();
            } else {
                focusCodeTextPaneOrDirectoryPanel();
            }
        } else {
            focusCodeTextPaneOrDirectoryPanel();
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
    
    public void focusCodeTextPaneOrDirectoryPanel() {
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
        return CodeTextPane;
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
