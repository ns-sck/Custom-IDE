package app.src.main.java.org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class MenuBar {

    public static JMenuBar createMenuBar(MainFrame frame) {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(0x020e21));
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setForeground(Color.WHITE);
        JMenuItem newMenuItem = new JMenuItem("New");
        JMenuItem openMenuItem = new JMenuItem("Open");
        JMenuItem saveMenuItem = new JMenuItem("Save");
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        
        fileMenu.add(newMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);
        
        // FileHandler instance
        FileHandler fileHandler = new FileHandler(frame);
        
        // Action listeners for menu items
        newMenuItem.addActionListener(e -> {
            frame.addTab("Untitled", null);
        });
        
        openMenuItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int userChoice = fileChooser.showOpenDialog(frame);
            if (userChoice == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                frame.addTab(selectedFile.getName(), selectedFile);
            }
        });
        
        saveMenuItem.addActionListener(e -> {
            fileHandler.saveToFile();
        });
        
        exitMenuItem.addActionListener(e -> {
            System.exit(0);
        });
        
        // Settings menu
        JMenu settingsMenu = new JMenu("Settings");
        settingsMenu.setForeground(Color.WHITE);

        // Font submenu
        JMenu fontMenu = new JMenu("Font");
        fontMenu.setForeground(Color.WHITE);
        
        JMenuItem changeFontMenuItem = new JMenuItem("Change Font");
        changeFontMenuItem.addActionListener(e -> changeFont(frame));
        
        JMenuItem changeFontSizeMenuItem = new JMenuItem("Change Font Size");
        changeFontSizeMenuItem.addActionListener(e -> changeFontSize(frame));
        
        fontMenu.add(changeFontMenuItem);
        fontMenu.add(changeFontSizeMenuItem);
        
        settingsMenu.add(fontMenu);
        
        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(settingsMenu);
        
        return menuBar;
    }
    
    private static void changeFont(MainFrame frame) {
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        String selectedFont = (String) JOptionPane.showInputDialog(frame, "Choose Font:", "Font Selection", JOptionPane.PLAIN_MESSAGE, null, fonts, fonts[0]);
        if (selectedFont != null) {
            JScrollPane selectedScrollPane = (JScrollPane) frame.getTabbedPane().getSelectedComponent();
            if (selectedScrollPane != null) {
                CodeTextArea codeTextArea = (CodeTextArea) selectedScrollPane.getViewport().getView();
                Font currentFont = codeTextArea.getFont();
                codeTextArea.setFont(new Font(selectedFont, currentFont.getStyle(), currentFont.getSize()));
            }
        }
    }
    
    private static void changeFontSize(MainFrame frame) {
        String input = JOptionPane.showInputDialog(frame, "Enter font size:");
        if (input != null && !input.isEmpty()) {
            try {
                int fontSize = Integer.parseInt(input);
                JScrollPane selectedScrollPane = (JScrollPane) frame.getTabbedPane().getSelectedComponent();
                if (selectedScrollPane != null) {
                    CodeTextArea codeTextArea = (CodeTextArea) selectedScrollPane.getViewport().getView();
                    Font currentFont = codeTextArea.getFont();
                    codeTextArea.setFont(currentFont.deriveFont((float) fontSize));
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid font size entered.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
