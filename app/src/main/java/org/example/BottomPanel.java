package app.src.main.java.org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.*;



public class BottomPanel extends JPanel {
    private static JPanel panel;
    private static String utilityDirectory;
    private static File inputFile;
    private static File outputFile;
    private static Color bg;

    public static JPanel createBottomPanel(MainFrame mainFrame) {
        Properties config = loadConfig();
        utilityDirectory = config.getProperty("utility.directory", "C:");
        inputFile = new File(config.getProperty("input.file", "C:"));
        outputFile = new File(config.getProperty("output.file", "C:"));
        bg = Color.decode(config.getProperty("bottom.panel.color", "C:"));

        panel = new JPanel(new FlowLayout());
        panel.setBackground(bg);
        JButton directoryButton = createButton("Directories", mainFrame);
        JButton terminalButton = createButton("Terminal", mainFrame);
        JButton templatesButton = createButton("Templates", mainFrame);
        JButton inputButton = createButton("Input", mainFrame);
        JButton outputButton = createButton("Output", mainFrame);

        panel.add(directoryButton);
        panel.add(inputButton);
        panel.add(outputButton);
        panel.add(terminalButton);
        panel.add(templatesButton);

        setupKeyboardNavigation(directoryButton, inputButton, outputButton, terminalButton, templatesButton);

        return panel;
    }

    private static Properties loadConfig() {
        Properties config = new Properties();
        try (FileInputStream input = new FileInputStream("config.properties")) {
            config.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return config;
    }

    private static JButton createButton(String text, MainFrame mainFrame) {
        JButton button = new JButton(text);
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);

        switch (text) {
            case "Directories":
                button.addActionListener(e -> mainFrame.openDirectoryPanel());
                break;
            case "Terminal":
                button.addActionListener(e -> mainFrame.openTerminal());
                break;
            case "Templates":
                button.addActionListener(e -> mainFrame.openUtilityDirectory(utilityDirectory));
                break;
            case "Input":
                button.addActionListener(e -> mainFrame.openFile(inputFile));
            break;
            case "Output":
                button.addActionListener(e -> mainFrame.openFile(outputFile));
            break;
        }

        button.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                button.setBackground(Color.WHITE);
                button.setForeground(Color.BLACK);
                button.setOpaque(true);
            }

            public void focusLost(FocusEvent e) {
                button.setBackground(Color.BLACK);
                button.setForeground(Color.WHITE);
                button.setOpaque(false);
            }
        });

        return button;
    }

    private static void setupKeyboardNavigation(JButton... buttons) {
        for (int i = 0; i < buttons.length; i++) {
            JButton button = buttons[i];
            int nextIndex = (i + 1) % buttons.length;
            int prevIndex = (i - 1 + buttons.length) % buttons.length;

            button.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_RIGHT:
                            buttons[nextIndex].requestFocus();
                            break;
                        case KeyEvent.VK_LEFT:
                            buttons[prevIndex].requestFocus();
                            break;
                        case KeyEvent.VK_ENTER:
                            button.doClick();
                            break;
                    }
                }
            });
        }
    }
}
