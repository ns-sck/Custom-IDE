package app.src.main.java.org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BottomPanel extends JPanel {
    private static JPanel panel;

    public static JPanel createBottomPanel(MainFrame mainFrame) {
        panel = new JPanel(new FlowLayout());
        panel.setBackground(Color.BLACK);

        JButton directoryButton = createButton("Directories", mainFrame);
        JButton terminalButton = createButton("Terminal", mainFrame);
        JButton templatesButton = createButton("Templates", mainFrame);

        panel.add(directoryButton);
        panel.add(terminalButton);
        panel.add(templatesButton);

        setupKeyboardNavigation(directoryButton, terminalButton, templatesButton);

        return panel;
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
                button.addActionListener(e -> {
                    // Handle templates button action
                });
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
