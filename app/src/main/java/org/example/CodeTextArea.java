package app.src.main.java.org.example;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.*;
import javax.swing.undo.UndoManager;
import java.awt.event.*;

public class CodeTextArea extends JTextArea {

    private static final int TAB_SIZE = 4;
    private UndoManager undoManager;

    public CodeTextArea() {
        super();
        setBackground(new Color(0x01031a));
        setForeground(Color.WHITE);
        setFont(new Font("Menlo", Font.PLAIN, 16));
        setCaretColor(Color.WHITE);
        
        setTabSize(TAB_SIZE);

        // Initialize the UndoManager
        undoManager = new UndoManager();
        getDocument().addUndoableEditListener(undoManager);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                handleAutoPairing(e);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    SwingUtilities.invokeLater(() -> handleEnterKey());
                }
            }
        });

        // Bind Ctrl+Z to undo
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), "Undo");
        getActionMap().put("Undo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (undoManager.canUndo()) {
                    undoManager.undo();
                }
            }
        });

        // Bind Ctrl+Y to redo
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK), "Redo");
        getActionMap().put("Redo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (undoManager.canRedo()) {
                    undoManager.redo();
                }
            }
        });
    }

    @Override
    public int getTabSize() {
        return TAB_SIZE;
    }

    public void setTextContent(String text) {
        setText(text);
    }

    public String getTextContent() {
        return getText();
    }

    private void handleAutoPairing(KeyEvent e) {
        char typedChar = e.getKeyChar();
        
        switch (typedChar) {
            case '(':
                insertPair(')');
                break;
            case '[':
                insertPair(']');
                break;
            case '{':
                insertPair('}');
                break;
        }
    }

    private void insertPair(char closingChar) {
        int caretPosition = getCaretPosition();
        Document doc = getDocument();
        
        try {
            doc.insertString(caretPosition, Character.toString(closingChar), null);
            setCaretPosition(caretPosition);
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }

    private void handleEnterKey() {
        Document doc = getDocument();
    
        if (isBetweenPairs(getCaretPosition())) {
            try {
                int indentLevel = getStartOfLineAbove(getCaretPosition());
                ++indentLevel;
                StringBuilder indentation = new StringBuilder();
                for (int i = 0; i < TAB_SIZE * indentLevel; i++) {
                    indentation.append(' ');
                }
                indentation.append('\n');
                --indentLevel;
                for (int i = 0; i < TAB_SIZE * indentLevel; i++) {
                    indentation.append(' ');
                }
                doc.insertString(getCaretPosition(), indentation.toString(), null);
                setCaretPosition(getCaretPosition() - (indentLevel * TAB_SIZE) - 1);
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        } else {
            try {
                int indentLevel = getStartOfLineAbove(getCaretPosition());
                StringBuilder indentation = new StringBuilder();
                for (int i = 0; i < TAB_SIZE * indentLevel; i++) {
                    indentation.append(' ');
                }    
                doc.insertString(getCaretPosition(), indentation.toString(), null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private boolean isBetweenPairs(int caretPosition) {
        Document doc = getDocument();
        String textBefore = "";
        
        try {
            if (caretPosition >= 1 && caretPosition < doc.getLength()) {
                textBefore = doc.getText(caretPosition, 1);
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        // something to fix later
        return textBefore.equals(")") || textBefore.equals("]") || textBefore.equals("}");
    }

    private int getStartOfLineAbove(int caretPosition) {
        try {
            int lineIndex = this.getLineOfOffset(caretPosition);
            if (--lineIndex >= 0) {
                String currentLine = getTextAtLine(lineIndex);
                int indentLevel = 0;
                for (int i = 0; i < currentLine.length(); ++i) {
                    if (currentLine.charAt(i) == '\t') indentLevel += TAB_SIZE;
                    else if (currentLine.charAt(i) == ' ') indentLevel += 1;
                    else break;
                }
                return indentLevel / 4;
            }
        } catch (Exception e) {};    
        return 0;
    }

    private String getTextAtLine(int lineNumber) {
        Element root = getDocument().getDefaultRootElement();
        if (lineNumber < 0 || lineNumber >= root.getElementCount()) {
            return ""; // Return empty string if line number is out of bounds
        }
    
        Element lineElement = root.getElement(lineNumber);
        int startOffset = lineElement.getStartOffset();
        int endOffset = lineElement.getEndOffset();
    
        try {
            return getDocument().getText(startOffset, endOffset - startOffset);
        } catch (BadLocationException e) {
            e.printStackTrace();
            return ""; // Handle exception by returning empty string
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Code Editor");
        CodeTextArea codeTextArea = new CodeTextArea();
        frame.add(new JScrollPane(codeTextArea));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }
}
