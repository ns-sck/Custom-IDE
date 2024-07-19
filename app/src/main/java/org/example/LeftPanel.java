package app.src.main.java.org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class LeftPanel {
    
    public static JPanel createLeftPanel(MainFrame mainFrame) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.BLACK);
        panel.setPreferredSize(new Dimension(200, 600)); // Set preferred size
        
        // Create components for file operations
        JButton openButton = new JButton("Open File");
        openButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainFrame.openFile();
            }
        });
        
        JButton saveButton = new JButton("Save File");
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainFrame.saveFile(); // Delegate saving file to MainFrame
            }
        });
        
        // Add components to left panel
        panel.add(openButton);
        panel.add(saveButton);
        
        return panel;
    }
}
