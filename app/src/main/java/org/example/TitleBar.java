package app.src.main.java.org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TitleBar {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setUndecorated(true); // Remove default title bar
            frame.setSize(800, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            // Custom title bar panel
            JPanel titleBar = new JPanel();
            titleBar.setBackground(Color.GRAY);
            titleBar.setLayout(new FlowLayout(FlowLayout.RIGHT));

            // Add close button
            JButton closeButton = new JButton("X");
            closeButton.addActionListener(e -> System.exit(0));
            titleBar.add(closeButton);

            // Add title bar to frame
            frame.add(titleBar, BorderLayout.NORTH);

            // Add content to the frame
            JTextArea textArea = new JTextArea("Hello, World!");
            frame.add(textArea, BorderLayout.CENTER);

            frame.setVisible(true);
        });
    }
}
