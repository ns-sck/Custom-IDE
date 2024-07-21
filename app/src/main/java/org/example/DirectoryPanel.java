package app.src.main.java.org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class DirectoryPanel extends JPanel {

    private JList<File> fileList;
    private DefaultListModel<File> listModel;
    private File currentDirectory;
    private FileSelectionListener fileSelectionListener;

    public DirectoryPanel(FileSelectionListener fileSelectionListener) {
        this.fileSelectionListener = fileSelectionListener;
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        fileList = new JList<>(listModel);
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fileList.setFocusable(true);

        // Set colors for JList
        fileList.setBackground(Color.BLACK);
        fileList.setForeground(Color.WHITE);

        fileList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component cell = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                File file = (File) value;
                String displayName = file.getName(); // Display only the name
                setText(displayName);

                // Set background and foreground colors
                cell.setBackground(isSelected ? Color.DARK_GRAY : Color.BLACK);
                cell.setForeground(isSelected ? Color.WHITE : Color.LIGHT_GRAY);

                return cell;
            }
        });

        JScrollPane scrollPane = new JScrollPane(fileList);
        scrollPane.getViewport().setBackground(Color.BLACK);
        scrollPane.setBackground(Color.BLACK);
        add(scrollPane, BorderLayout.CENTER);

        // Set initial directory
        currentDirectory = new File(System.getProperty("user.home"));
        loadDirectory(currentDirectory);

        // Mouse listener for double-click actions
        fileList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Double-click
                    openSelectedFile();
                }
            }
        });

        // Key listener for Enter key actions
        fileList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    openSelectedFile();
                }
            }
        });

        // Focus the list on component showing
        fileList.setSelectedIndex(0); // Default selection to the first item
    }

    private void openSelectedFile() {
        File selectedFile = fileList.getSelectedValue();
        if (selectedFile != null) {
            if (selectedFile.isDirectory()) {
                if (selectedFile.getName().equals("..")) {
                    navigateUp();
                } else {
                    navigateTo(selectedFile);
                }
            } else {
                // Notify the listener to open the file in CodeTextArea
                if (fileSelectionListener != null) {
                    fileSelectionListener.onFileSelected(selectedFile);
                }
            }
        }
    }

    private void loadDirectory(File directory) {
        currentDirectory = directory;
        listModel.clear();

        // Add ".." to navigate up if not in root directory
        if (currentDirectory.getParentFile() != null) {
            listModel.addElement(new File(".."));
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                listModel.addElement(file);
            }
        }
    }

    private void navigateTo(File directory) {
        if (directory.isDirectory()) {
            loadDirectory(directory);
        }
    }

    private void navigateUp() {
        File parentDirectory = currentDirectory.getParentFile();
        if (parentDirectory != null) {
            loadDirectory(parentDirectory);
        }
    }

    public JList<File> getFileList() {
        return fileList;
    }

    // Interface for file selection listener
    public interface FileSelectionListener {
        void onFileSelected(File file);
    }
}
