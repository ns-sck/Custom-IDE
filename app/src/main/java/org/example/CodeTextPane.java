package app.src.main.java.org.example;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.undo.UndoManager;
import java.io.File;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class CodeTextPane extends JTextPane {

    private static final int TAB_SIZE = 8;
    private UndoManager undoManager;
    private File file; // Variable to store the associated file

    public CodeTextPane() {
        super();
        setBackground(new Color(0x01118a));
        setForeground(Color.WHITE);
        setFont(new Font("Menlo", Font.PLAIN, 16));
        setCaretColor(Color.WHITE);
        setMargin(new Insets(0, 8, 0, 0));
        undoManager = new UndoManager();
        getDocument().addUndoableEditListener(undoManager);

        // Set up the default style
        StyledDocument doc = getStyledDocument();
        Style style = doc.addStyle("defaultStyle", null);
        StyleConstants.setFontFamily(style, "Menlo");
        StyleConstants.setFontSize(style, 16);
        StyleConstants.setForeground(style, Color.WHITE);
        StyleConstants.setBackground(style, new Color(0x01118a));
        doc.setCharacterAttributes(0, doc.getLength(), style, false);

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
        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                SyntaxHighlighter.highlightAll(CodeTextPane.this);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                SyntaxHighlighter.highlightAll(CodeTextPane.this);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // For styled documents, this method is not used
            }
        });
    }

    public void setCustomTabSize(int tabSize) {
        // Obtain the StyledDocument
        StyledDocument doc = getStyledDocument();
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        // Create or get the existing style
        Style style = doc.getStyle(StyleContext.DEFAULT_STYLE);
        if (style == null) {
            style = doc.addStyle(StyleContext.DEFAULT_STYLE, null);
        }

        // Create a new TabSet with a TabStop for the desired tab size
        TabStop[] tabStops = new TabStop[] { new TabStop(tabSize) };
        TabSet tabSet = new TabSet(tabStops);
        
        // Apply the TabSet to the style
        StyleConstants.setTabSet(style, tabSet);
        
        // Apply the style to the entire document
        doc.setParagraphAttributes(0, doc.getLength(), style, false);
    }

    public void setTextContent(String text) {
        setText(text);
    }

    public String getTextContent() {
        return getText();
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return this.file;
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
            case '\'':
                insertPair('\'');
                break;
            case '"':
                insertPair('"');
                break;
        }
    }

    private void insertPair(char closingChar) {
        int caretPosition = getCaretPosition();
        StyledDocument doc = getStyledDocument();

        try {
            doc.insertString(caretPosition, Character.toString(closingChar), null);
            setCaretPosition(caretPosition);
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }

    private void handleEnterKey() {
        StyledDocument doc = getStyledDocument();

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
        if (caretPosition < 2) return false;
        StyledDocument doc = getStyledDocument();
        String textBefore = "";
        caretPosition -= 2;
        try {
            if (caretPosition < doc.getLength()) {
                textBefore = doc.getText(caretPosition, 1);
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        return textBefore.equals("(") || textBefore.equals("[") || textBefore.equals("{");
    }

    private int getStartOfLineAbove(int caretPosition) {
        try {
            int lineIndex = getStyledDocument().getDefaultRootElement().getElementIndex(caretPosition);
            if (--lineIndex >= 0) {
                String currentLine = getTextAtLine(lineIndex);
                int indentLevel = 0;
                for (int i = 0; i < currentLine.length(); ++i) {
                    if (currentLine.charAt(i) == '\t') indentLevel += TAB_SIZE;
                    else if (currentLine.charAt(i) == ' ') indentLevel += 1;
                    else break;
                }
                return indentLevel / TAB_SIZE;
            }
        } catch (Exception e) {};    
        return 0;
    }

    private String getTextAtLine(int lineNumber) {
        Element root = getStyledDocument().getDefaultRootElement();
        if (lineNumber < 0 || lineNumber >= root.getElementCount()) {
            return ""; // Return empty string if line number is out of bounds
        }

        Element lineElement = root.getElement(lineNumber);
        int startOffset = lineElement.getStartOffset();
        int endOffset = lineElement.getEndOffset();

        try {
            return getStyledDocument().getText(startOffset, endOffset - startOffset);
        } catch (BadLocationException e) {
            e.printStackTrace();
            return ""; // Handle exception by returning empty string
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Code Editor");
        CodeTextPane codeTextPane = new CodeTextPane();
        frame.add(new JScrollPane(codeTextPane));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }
}
