package app.src.main.java.org.example;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class LeftPanel {

    private static JTree directoryTree;
    private static DefaultTreeModel treeModel;
    private static JPanel panel;

    public static JPanel createLeftPanel(MainFrame mainFrame) {
        panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.BLACK);
        panel.setPreferredSize(new Dimension(200, 600)); // Set preferred size

        // Initialize the JTree to display the directory structure
        File rootDirectory = new File(System.getProperty("user.dir"));
        DefaultMutableTreeNode rootNode = createNodes(rootDirectory);
        treeModel = new DefaultTreeModel(rootNode);
        directoryTree = new JTree(treeModel);

        // Customize the appearance of the JTree
        directoryTree.setBackground(Color.BLACK);
        directoryTree.setForeground(Color.WHITE);
        directoryTree.setCellRenderer(new DefaultTreeCellRenderer() {
            {
                setTextSelectionColor(Color.WHITE);
                setTextNonSelectionColor(Color.WHITE);
                setBackgroundSelectionColor(Color.GRAY);
                setBorderSelectionColor(null);
                setBackgroundNonSelectionColor(Color.BLACK);
            }
        });

        // Add a tree selection listener to open files on selection
        directoryTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) directoryTree.getLastSelectedPathComponent();
            if (selectedNode == null) return;
            File selectedFile = new File(rootDirectory, getFilePath(selectedNode));
            if (selectedFile.isFile()) {
            }
        });

        // Add the JTree to a JScrollPane
        JScrollPane treeScrollPane = new JScrollPane(directoryTree);
        treeScrollPane.setBackground(Color.BLACK);
        treeScrollPane.setForeground(Color.WHITE);

        // Add the JScrollPane to the panel
        panel.add(treeScrollPane, BorderLayout.CENTER);

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

    public static void updateTree(File rootDirectory) {
        DefaultMutableTreeNode rootNode = createNodes(rootDirectory);
        treeModel.setRoot(rootNode);
        treeModel.reload();
        expandAllNodes(directoryTree, 0, directoryTree.getRowCount());
    }

    private static void expandAllNodes(JTree tree, int startingIndex, int rowCount) {
        for (int i = startingIndex; i < rowCount; ++i) {
            tree.expandRow(i);
        }

        if (tree.getRowCount() != rowCount) {
            expandAllNodes(tree, rowCount, tree.getRowCount());
        }
    }

    private static String getFilePath(DefaultMutableTreeNode node) {
        StringBuilder filePath = new StringBuilder();
        TreeNode[] nodes = node.getPath();

        for (int i = 1; i < nodes.length; i++) { // Start from 1 to skip the root node name
            filePath.append(File.separator).append(nodes[i].toString());
        }

        return filePath.toString();
    }
}
