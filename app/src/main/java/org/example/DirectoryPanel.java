package app.src.main.java.org.example;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class DirectoryPanel {

    private static JPanel panel;

    public static JPanel createDirectoryPanel(MainFrame mainFrame) {
        panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.BLACK);
        panel.setPreferredSize(new Dimension(200, 600)); // Set preferred size

        // Initialize the JTree to display the directory structure
        File rootDirectory = new File(System.getProperty("user.dir"));
        DefaultMutableTreeNode rootNode = createNodes(rootDirectory);


        // Create components for file operations
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        buttonPanel.setBackground(Color.BLACK);

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

        // Add buttons to the button panel
        buttonPanel.add(openButton);
        buttonPanel.add(saveButton);

        // Add button panel to the bottom of the left panel
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private static DefaultMutableTreeNode createNodes(File directory) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(directory.getName());
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    node.add(createNodes(file));
                } else {
                    node.add(new DefaultMutableTreeNode(file.getName()));
                }
            }
        }

        return node;
    }
}
