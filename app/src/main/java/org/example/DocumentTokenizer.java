package app.src.main.java.org.example;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.StyledDocument;

public class DocumentTokenizer {

    private CodeTextPane codeTextPane;

    public DocumentTokenizer(CodeTextPane codeTextPane) {
        this.codeTextPane = codeTextPane;
    }

    // Method to get the first word of the current line
    public String getFirstWordOfLine(int lineIndex) throws BadLocationException {
        StyledDocument doc = codeTextPane.getStyledDocument();
        Element root = doc.getDefaultRootElement();
        if (lineIndex < 0 || lineIndex >= root.getElementCount()) {
            throw new IllegalArgumentException("Invalid line index");
        }

        Element lineElement = root.getElement(lineIndex);
        int startOffset = lineElement.getStartOffset();
        int endOffset = lineElement.getEndOffset();

        String lineText = doc.getText(startOffset, endOffset - startOffset).trim();
        String[] words = lineText.split("\\s+");

        return words.length > 0 ? words[0] : "";
    }

    // Method to get the previous word before the caret position
    public String getPreviousWord() throws BadLocationException {
        int caretPos = codeTextPane.getCaretPosition();
        if (caretPos == 0) {
            return "";
        }

        StyledDocument doc = codeTextPane.getStyledDocument();
        String text = doc.getText(0, caretPos);
        String[] words = text.split("\\s+");

        return words.length > 1 ? words[words.length - 2] : "";
    }

    // Method to get the word at the caret position
    public String getWordAtCaret() throws BadLocationException {
        int caretPos = codeTextPane.getCaretPosition();
        StyledDocument doc = codeTextPane.getStyledDocument();
        Element root = doc.getDefaultRootElement();
        int line = root.getElementIndex(caretPos);
        Element lineElement = root.getElement(line);
        int startOffset = lineElement.getStartOffset();
        int endOffset = lineElement.getEndOffset();

        String lineText = doc.getText(startOffset, endOffset - startOffset);
        int wordStart = startOffset;
        int wordEnd = endOffset;

        for (int i = startOffset; i < endOffset; i++) {
            if (Character.isWhitespace(lineText.charAt(i - startOffset))) {
                if (i >= caretPos) {
                    wordEnd = i;
                    break;
                } else {
                    wordStart = i + 1;
                }
            }
        }

        return lineText.substring(wordStart - startOffset, wordEnd - startOffset).trim();
    }
}
