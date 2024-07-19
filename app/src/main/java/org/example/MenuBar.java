package app.src.main.java.org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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
        
        // Settings menu
        JMenu settingsMenu = new JMenu("Settings");
        settingsMenu.setForeground(Color.WHITE);

        
        // Font submenu
        JMenu fontMenu = new JMenu("Font");
        fontMenu.setForeground(Color.WHITE);
        
        JMenuItem changeFontMenuItem = new JMenuItem("Change Font");
        changeFontMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                changeFont(frame);
            }
        });
        
        JMenuItem changeFontSizeMenuItem = new JMenuItem("Change Font Size");
        changeFontSizeMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                changeFontSize(frame);
            }
        });
        
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
            Font currentFont = frame.getCodeTextArea().getFont();
            frame.getCodeTextArea().setFont(new Font(selectedFont, currentFont.getStyle(), currentFont.getSize()));
        }
    }
    
    private static void changeFontSize(MainFrame frame) {
        String input = JOptionPane.showInputDialog(frame, "Enter font size:");
        if (input != null && !input.isEmpty()) {
            try {
                int fontSize = Integer.parseInt(input);
                Font currentFont = frame.getCodeTextArea().getFont();
                frame.getCodeTextArea().setFont(currentFont.deriveFont((float) fontSize));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid font size entered.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
