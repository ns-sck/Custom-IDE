package app.src.main.java.org.example;

import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Properties;

public class MenuBar {

    private static Properties config = new Properties();

    static {
        try (InputStream input = new FileInputStream("config.properties")) {
            config.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static JMenuBar createMenuBar(MainFrame mainFrame) {
        JMenuBar menuBar = new JMenuBar();

        // Font Menu
        JMenu fontMenu = new JMenu("Font");
        JMenuItem changeFontItem = new JMenuItem("Change Font");
        JMenuItem changeFontSizeItem = new JMenuItem("Change Font Size");
        fontMenu.add(changeFontItem);
        fontMenu.add(changeFontSizeItem);

        changeFontItem.addActionListener(e -> changeFont(mainFrame));
        changeFontSizeItem.addActionListener(e -> changeFontSize(mainFrame));

        // Color Menu
        JMenu colorMenu = new JMenu("Color");
        JMenuItem changeBackgroundColorItem = new JMenuItem("Background");
        JMenuItem changeForegroundColorItem = new JMenuItem("Foreground");
        JMenuItem changeKeywordColorItem = new JMenuItem("Keyword");
        JMenuItem changeSymbolColorItem = new JMenuItem("Symbol");
        JMenuItem changeNumberColorItem = new JMenuItem("Number");
        colorMenu.add(changeBackgroundColorItem);
        colorMenu.add(changeForegroundColorItem);
        colorMenu.add(changeKeywordColorItem);
        colorMenu.add(changeSymbolColorItem);
        colorMenu.add(changeNumberColorItem);

        changeBackgroundColorItem.addActionListener(e -> changeBackgroundColor(mainFrame));
        changeForegroundColorItem.addActionListener(e -> changeForegroundColor(mainFrame));
        changeKeywordColorItem.addActionListener(e -> changeKeywordColor(mainFrame));
        changeSymbolColorItem.addActionListener(e -> changeSymbolColor(mainFrame));
        changeNumberColorItem.addActionListener(e -> changeNumberColor(mainFrame));

        // Paths Menu
        JMenu pathsMenu = new JMenu("Paths");
        JMenuItem changeInputPathItem = new JMenuItem("Change Input File Path");
        JMenuItem changeOutputPathItem = new JMenuItem("Change Output File Path");
        JMenuItem changeTemplatesPathItem = new JMenuItem("Change Templates Directory Path");
        pathsMenu.add(changeInputPathItem);
        pathsMenu.add(changeOutputPathItem);
        pathsMenu.add(changeTemplatesPathItem);

        changeInputPathItem.addActionListener(e -> changeInputPath(mainFrame));
        changeOutputPathItem.addActionListener(e -> changeOutputPath(mainFrame));
        changeTemplatesPathItem.addActionListener(e -> changeTemplatesPath(mainFrame));

        // Add menus to menu bar
        menuBar.add(fontMenu);
        menuBar.add(colorMenu);
        menuBar.add(pathsMenu);

        return menuBar;
    }

    private static void changeFont(MainFrame mainFrame) {
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        String currentFont = config.getProperty("font");
        String newFont = (String) JOptionPane.showInputDialog(mainFrame, "Choose Font", "Font Selection", JOptionPane.PLAIN_MESSAGE, null, fonts, currentFont);

        if (newFont != null && !newFont.isEmpty()) {
            config.setProperty("font", newFont);
            saveConfig();
            updateFont(mainFrame, newFont, Integer.parseInt(config.getProperty("font.size")));
        }
    }

    private static void changeFontSize(MainFrame mainFrame) {
        String currentSize = config.getProperty("font.size");
        String newSize = JOptionPane.showInputDialog(mainFrame, "Enter Font Size", currentSize);

        try {
            int fontSize = Integer.parseInt(newSize);
            config.setProperty("font.size", newSize);
            saveConfig();
            updateFont(mainFrame, config.getProperty("font"), fontSize);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(mainFrame, "Invalid font size.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void changeBackgroundColor(MainFrame mainFrame) {
        Color currentColor = Color.decode(config.getProperty("textpane.background.color"));
        Color newColor = JColorChooser.showDialog(mainFrame, "Choose Background Color", currentColor);

        if (newColor != null) {
            String colorHex = String.format("#%02x%02x%02x", newColor.getRed(), newColor.getGreen(), newColor.getBlue());
            config.setProperty("textpane.background.color", colorHex);
            saveConfig();
            updateBackgroundColor(mainFrame, newColor);
        }
    }

    private static void changeForegroundColor(MainFrame mainFrame) {
        Color currentColor = Color.decode(config.getProperty("textpane.foreground.color"));
        Color newColor = JColorChooser.showDialog(mainFrame, "Choose Foreground Color", currentColor);

        if (newColor != null) {
            String colorHex = String.format("#%02x%02x%02x", newColor.getRed(), newColor.getGreen(), newColor.getBlue());
            config.setProperty("textpane.foreground.color", colorHex);
            saveConfig();
            updateForegroundColor(mainFrame, newColor);
        }
    }

    private static void changeKeywordColor(MainFrame mainFrame) {
        Color currentColor = Color.decode(config.getProperty("keyword.color"));
        Color newColor = JColorChooser.showDialog(mainFrame, "Choose Keyword Color", currentColor);

        if (newColor != null) {
            String colorHex = String.format("#%02x%02x%02x", newColor.getRed(), newColor.getGreen(), newColor.getBlue());
            config.setProperty("keyword.color", colorHex);
            saveConfig();
            updateHighlighter(mainFrame, newColor);
        }
    }

    private static void changeSymbolColor(MainFrame mainFrame) {
        Color currentColor = Color.decode(config.getProperty("symbol.color"));
        Color newColor = JColorChooser.showDialog(mainFrame, "Choose Symbol Color", currentColor);

        if (newColor != null) {
            String colorHex = String.format("#%02x%02x%02x", newColor.getRed(), newColor.getGreen(), newColor.getBlue());
            config.setProperty("symbol.color", colorHex);
            saveConfig();
            updateHighlighter(mainFrame, newColor);
        }
    }

    private static void changeNumberColor(MainFrame mainFrame) {
        Color currentColor = Color.decode(config.getProperty("number.color"));
        Color newColor = JColorChooser.showDialog(mainFrame, "Choose Number Color", currentColor);

        if (newColor != null) {
            String colorHex = String.format("#%02x%02x%02x", newColor.getRed(), newColor.getGreen(), newColor.getBlue());
            config.setProperty("number.color", colorHex);
            saveConfig();
            updateHighlighter(mainFrame, newColor);
        }
    }

    private static void changeInputPath(MainFrame mainFrame) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(config.getProperty("input.file")));
        int result = fileChooser.showOpenDialog(mainFrame);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            config.setProperty("input.file", selectedFile.getAbsolutePath());
            saveConfig();
        }
    }

    private static void changeOutputPath(MainFrame mainFrame) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(config.getProperty("output.file")));
        int result = fileChooser.showOpenDialog(mainFrame);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            config.setProperty("output.file", selectedFile.getAbsolutePath());
            saveConfig();
        }
    }

    private static void changeTemplatesPath(MainFrame mainFrame) {
        JFileChooser dirChooser = new JFileChooser();
        dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        dirChooser.setCurrentDirectory(new File(config.getProperty("utility.directory")));
        int result = dirChooser.showOpenDialog(mainFrame);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedDir = dirChooser.getSelectedFile();
            config.setProperty("utility.directory", selectedDir.getAbsolutePath());
            saveConfig();
        }
    }

    private static void updateFont(MainFrame mainFrame, String fontName, int fontSize) {
        for (CodeTextPane codeTextPane : mainFrame.getOpenCodeTextPanes()) {
            codeTextPane.setFont(new Font(fontName, Font.PLAIN, fontSize));
        }
    }

    private static void updateBackgroundColor(MainFrame mainFrame, Color color) {
        for (CodeTextPane codeTextPane : mainFrame.getOpenCodeTextPanes()) {
            codeTextPane.setBackground(color);
        }
    }

    private static void updateForegroundColor(MainFrame mainFrame, Color color) {
        for (CodeTextPane codeTextPane : mainFrame.getOpenCodeTextPanes()) {
            codeTextPane.setForeground(color);
        }
    }

    private static void updateHighlighter(MainFrame mainFrame, Color color) {
        for (CodeTextPane codeTextPane : mainFrame.getOpenCodeTextPanes()) {
            SyntaxHighlighter.highlightAll(codeTextPane);
        }
    }

    private static void saveConfig() {
        try (OutputStream output = new FileOutputStream("config.properties")) {
            config.store(output, null);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }
}
