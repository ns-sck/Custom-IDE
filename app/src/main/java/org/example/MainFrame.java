package app.src.main.java.org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class MainFrame extends JFrame {

    private JTabbedPane tabbedPane; // Use JTabbedPane to manage multiple tabs
    private FileHandler fileHandler; // Instance of FileHandler to handle file operations
    private ConfigManager configManager; // Instance of ConfigManager

    public MainFrame() {
        super("Your IDE Name");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600); // Set your preferred initial size
        setLocationRelativeTo(null); // Center the frame on screen
        setBackground(Color.BLACK);

        // Initialize FileHandler
        fileHandler = new FileHandler(this);

        // Initialize ConfigManager
        configManager = new ConfigManager();

        // Create components
        JMenuBar menuBar = MenuBar.createMenuBar(this); // Pass 'this' to provide MainFrame instance
        JPanel leftPanel = LeftPanel.createLeftPanel(this); // Pass 'this' to provide MainFrame instance
        tabbedPane = new JTabbedPane(); // Initialize JTabbedPane

        // Add components to content pane
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(menuBar, BorderLayout.NORTH);
        getContentPane().add(leftPanel, BorderLayout.WEST);
        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        // Add keyboard shortcut (Ctrl+T) for opening terminal
        KeyStroke ctrlT = KeyStroke.getKeyStroke(KeyEvent.VK_T, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        InputMap inputMap = tabbedPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(ctrlT, "openTerminal");
        ActionMap actionMap = tabbedPane.getActionMap();
        actionMap.put("openTerminal", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                openTerminal();
            }
        });

        setVisible(true);
    }

    private void openTerminal() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            String command = "";

            if (os.contains("win")) {
                // For Windows
                command = "cmd.exe /c start cmd.exe /K cd \"%cd%\"";
            } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
                // For Unix-like systems (Linux, macOS)
                command = "x-terminal-emulator -e bash -c 'cd \"$PWD\" && exec bash'";
            } else {
                // Unsupported OS
                JOptionPane.showMessageDialog(this, "Unsupported OS for opening terminal.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Runtime.getRuntime().exec(command);
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to open terminal.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to add a new tab with a file
    public void addTab(String title, File file) {
        CodeTextArea newTextArea = new CodeTextArea();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            newTextArea.setText(content.toString());
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error opening file: " + file.getName(), "File Open Error", JOptionPane.ERROR_MESSAGE);
        }
        newTextArea.setFile(file);

        int fontSize = Integer.parseInt(configManager.getProperty("font.size", "16")); // Default to 16 if not set
        String font = configManager.getProperty("font", "Menlo");
        newTextArea.setFont(new Font(font, Font.PLAIN, fontSize));

        JScrollPane scrollPane = new JScrollPane(newTextArea);
        scrollPane.setPreferredSize(new Dimension(600, 400)); // Example preferred size
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        tabbedPane.addTab(title, scrollPane);
        tabbedPane.setSelectedComponent(scrollPane);
    }

    // Getter method for the currently selected CodeTextArea
    public CodeTextArea getCurrentCodeTextArea() {
        JScrollPane selectedScrollPane = (JScrollPane) tabbedPane.getSelectedComponent();
        if (selectedScrollPane != null) {
            return (CodeTextArea) selectedScrollPane.getViewport().getView();
        }
        return null;
    }

    // Method to open a file
    public void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        int userChoice = fileChooser.showOpenDialog(this);
        if (userChoice == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            fileHandler.openFile(selectedFile); // Delegate file opening to FileHandler
            LeftPanel.updateTree(selectedFile.getParentFile()); // Update the directory tree
        }
    }

    // Method to save file
    public void saveFile() {
        fileHandler.saveToFile(); // Delegate file saving to FileHandler
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
    
    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }
}