package app.src.main.java.org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class MainFrame extends JFrame {

    private CodeTextArea codeTextArea; // Use CodeTextArea instead of JTextArea
    private File currentFile; // Track the currently opened file
    private FileHandler fileHandler; // Instance of FileHandler to handle file operations

    public MainFrame() {
        super("Your IDE Name");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600); // Set your preferred initial size
        setLocationRelativeTo(null); // Center the frame on screen
        setBackground(Color.BLACK);

        // Initialize FileHandler
        fileHandler = new FileHandler(this);

        // Create components
        JMenuBar menuBar = MenuBar.createMenuBar(this); // Pass 'this' to provide MainFrame instance
        JPanel leftPanel = LeftPanel.createLeftPanel(this); // Pass 'this' to provide MainFrame instance
        codeTextArea = new CodeTextArea(); // Initialize CodeTextArea
        
        // Wrap codeTextArea in a JScrollPane
        JScrollPane scrollPane = new JScrollPane(codeTextArea);
        scrollPane.setPreferredSize(new Dimension(600, 400)); // Example preferred size
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // Apply custom ScrollBar UI
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
        verticalScrollBar.setUI(new CustomScrollBarUI(Color.BLUE, Color.LIGHT_GRAY)); // Customize thumb and track color
        horizontalScrollBar.setUI(new CustomScrollBarUI(Color.BLUE, Color.LIGHT_GRAY)); // Customize thumb and track color

        // Create a panel for the south button to ensure no gap
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBorder(null); // Remove border from panel
        southPanel.setBackground(Color.BLACK); // Match the background color
        
        // Add Open Terminal button
        JButton openTerminalButton = new JButton("Open Terminal");
        openTerminalButton.setMargin(new Insets(0, 0, 0, 0)); // Remove margins
        openTerminalButton.setBorder(null); // Remove border
        openTerminalButton.setBackground(Color.GRAY); // Set a background color if needed
        openTerminalButton.setForeground(Color.WHITE); // Set text color if needed
        southPanel.add(openTerminalButton, BorderLayout.CENTER);
        
        // Add components to content pane
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(menuBar, BorderLayout.NORTH);
        getContentPane().add(leftPanel, BorderLayout.WEST);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(southPanel, BorderLayout.SOUTH);
        
        // Add keyboard shortcut (Ctrl+T) for opening terminal
        KeyStroke ctrlT = KeyStroke.getKeyStroke(KeyEvent.VK_T, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        InputMap inputMap = codeTextArea.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(ctrlT, "openTerminal");
        ActionMap actionMap = codeTextArea.getActionMap();
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
    
    // Getter method for codeTextArea
    public CodeTextArea getCodeTextArea() {
        return codeTextArea;
    }

    // Getter method for currentFile
    public File getCurrentFile() {
        return currentFile;
    }

    // Setter method for currentFile
    public void setCurrentFile(File file) {
        this.currentFile = file;
    }

    // Getter method for fileHandler
    public FileHandler getFileHandler() {
        return fileHandler;
    }

    // Method to open a file
    public void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        int userChoice = fileChooser.showOpenDialog(this);
        if (userChoice == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            fileHandler.openFile(selectedFile); // Delegate file opening to FileHandler
        }
    }

    // Method to save file
    public void saveFile() {
        if (currentFile == null) {
            fileHandler.saveAs(); // If no current file, prompt for save location
        } else {
            fileHandler.saveToFile(); // Otherwise, save to current file
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}