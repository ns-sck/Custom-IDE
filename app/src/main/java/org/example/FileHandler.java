package app.src.main.java.org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class FileHandler {

    private MainFrame mainFrame; // Reference to the MainFrame instance

    public FileHandler(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    // Method to open a file
    public void openFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            mainFrame.getCodeTextArea().setText(content.toString()); // Set text area content
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Error opening file: " + file.getName(), "File Open Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to save current text to file
    public void saveToFile() {
        File currentFile = mainFrame.getCurrentFile();
        if (currentFile == null) {
            saveAs();
        } else {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile))) {
                writer.write(mainFrame.getCodeTextArea().getText());
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(mainFrame, "Error saving file: " + currentFile.getName(), "File Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Method to save text to a new file
    public void saveAs() {
        JFileChooser fileChooser = new JFileChooser();
        int userChoice = fileChooser.showSaveDialog(mainFrame);
        if (userChoice == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile))) {
                writer.write(mainFrame.getCodeTextArea().getText());
                mainFrame.setCurrentFile(selectedFile);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(mainFrame, "Error saving file: " + selectedFile.getName(), "File Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
