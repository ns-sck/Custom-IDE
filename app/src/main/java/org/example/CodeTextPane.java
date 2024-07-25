package app.src.main.java.org.example;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.undo.UndoManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
// import org.fife.ui.rsyntaxtextarea.*;
// import org.fife.ui.rsyntaxtextarea.*;
import javax.swing.event.UndoableEditListener;

public class CodeTextPane extends JTextPane {

    private static final int TAB_SIZE = 4;
    private UndoManager undoManager;
    public UndoableEditListener undoableEditListener;
    private File file;
    public boolean trackingEnabled = true;
    private static CodeTextPane focusedPane;

    public CodeTextPane() {
        super();
        
        Properties config = loadConfig();
        String fontName = config.getProperty("font", "Menlo");
        int fontSize = Integer.parseInt(config.getProperty("font.size", "16"));
        Color textColor = Color.decode(config.getProperty("default.text.color", "0xffffff"));
        Color backgroundColor = Color.decode(config.getProperty("textpane.background.color", "0x000847"));

        setBackground(backgroundColor);
        setForeground(textColor);
        setFont(new Font(fontName, Font.PLAIN, fontSize));
        setCaretColor(textColor);
        setMargin(new Insets(16, 16, 8, 8));
        setCustomTabSize(TAB_SIZE);
        undoManager = new UndoManager();

        StyledDocument doc = getStyledDocument();
        Style style = doc.addStyle("defaultStyle", null);
        StyleConstants.setFontFamily(style, fontName);
        StyleConstants.setFontSize(style, fontSize);
        StyleConstants.setForeground(style, textColor);
        StyleConstants.setBackground(style, backgroundColor);
        doc.setCharacterAttributes(0, doc.getLength(), style, false);
        
        doc.addUndoableEditListener(e -> {
            if (trackingEnabled) {
                undoManager.addEdit(e.getEdit());
            }
        });

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

        this.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                focusedPane = CodeTextPane.this;
            }
        });

        // getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), "Undo");
        // getActionMap().put("Undo", new AbstractAction() {
        //     @Override
        //     public void actionPerformed(ActionEvent e) {
        //         if (undoManager.canUndo()) {
        //             undoManager.undo();
        //         }
        //     }
        // });

        // getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK), "Redo");
        // getActionMap().put("Redo", new AbstractAction() {
        //     @Override
        //     public void actionPerformed(ActionEvent e) {
        //         if (undoManager.canRedo()) {
        //             undoManager.redo();
        //         }
        //     }
        // });

    }
    private Properties loadConfig() {
        Properties config = new Properties();
        try (FileInputStream input = new FileInputStream("config.properties")) {
            config.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return config;
    }
    public void setCustomTabSize(int tabSize) {
        FontMetrics fm = getFontMetrics(getFont());
        int charWidth = fm.charWidth('m'); // Width of a single character (m is usually one of the widest)
        int tabWidth = charWidth * tabSize;

        TabStop[] tabs = new TabStop[10];
        for (int i = 0; i < tabs.length; i++) {
            tabs[i] = new TabStop((i + 1) * tabWidth);
        }

        TabSet tabSet = new TabSet(tabs);
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.TabSet, tabSet);
        setParagraphAttributes(aset, false);
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
    
    public static CodeTextPane getFocusedPane() {
        return focusedPane;
    }
}