package app.src.main.java.org.example;

import javax.swing.*;
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
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Error opening file: " + file.getName(), "File Open Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to save current text to file
    public void saveToFile() {
        CodeTextPane currentTextPane = mainFrame.getCurrentCodeTextPane();
        if (currentTextPane == null) {
            JOptionPane.showMessageDialog(mainFrame, "No file is currently open.", "Save Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        File currentFile = currentTextPane.getFile();
        if (currentFile == null) {
            saveAs();
        } else {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile))) {
                writer.write(currentTextPane.getText());
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(mainFrame, "Error saving file: " + currentFile.getName(), "File Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Method to save text to a new file
    public void saveAs() {
        CodeTextPane currentTextPane = mainFrame.getCurrentCodeTextPane();
        if (currentTextPane == null) {
            JOptionPane.showMessageDialog(mainFrame, "No file is currently open.", "Save Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        int userChoice = fileChooser.showSaveDialog(mainFrame);
        if (userChoice == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile))) {
                writer.write(currentTextPane.getText());
                currentTextPane.setFile(selectedFile); // Associate the file with the current tab
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(mainFrame, "Error saving file: " + selectedFile.getName(), "File Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
