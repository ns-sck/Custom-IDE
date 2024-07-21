package app.src.main.java.org.example;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SyntaxHighlighter {

    private static final Map<String, Color> KEYWORDS = new HashMap<>();

    static {
        addCppKeywords();
        addPythonKeywords();
    }

    private static void addCppKeywords() {
        String[] cppKeywords = {
            "int", "string", "for", "while", "include", "define", "long long", "return", "<<>>"
        };
        for (String keyword : cppKeywords) {
            KEYWORDS.put(keyword, new Color(0x17a6b0));
        }
    }

    private static void addPythonKeywords() {
        String[] pythonKeywords = {
            // Python keywords can be added here if needed
        };
        for (String keyword : pythonKeywords) {
            KEYWORDS.put(keyword, Color.BLUE);
        }
    }

    public static void highlightAll(JTextPane textPane) {
        SwingUtilities.invokeLater(() -> {
            try {
                StyledDocument doc = textPane.getStyledDocument();
                String text = textPane.getText();

                // Clear existing styles
                doc.setCharacterAttributes(0, text.length(), doc.getStyle(StyleContext.DEFAULT_STYLE), true);

                // Apply new styles
                for (Map.Entry<String, Color> entry : KEYWORDS.entrySet()) {
                    String keyword = entry.getKey();
                    Color color = entry.getValue();
                    Pattern pattern = Pattern.compile("\\b" + Pattern.quote(keyword) + "\\b");
                    Matcher matcher = pattern.matcher(text);

                    // Create a unique style for each keyword
                    Style style = doc.addStyle("KeywordStyle_" + keyword, null);
                    StyleConstants.setForeground(style, color);

                    // Apply styles to matching keywords
                    while (matcher.find()) {
                        doc.setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(), style, false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static String getWordAtCaret(JTextPane textPane) {
        int caretPosition = textPane.getCaretPosition();
        Document doc = textPane.getDocument();

        try {
            String text = doc.getText(0, caretPosition);

            int start = Math.max(0, caretPosition - 1);
            while (start > 0 && !Character.isWhitespace(text.charAt(start - 1))) {
                start--;
            }

            int end = caretPosition;
            while (end < text.length() && !Character.isWhitespace(text.charAt(end))) {
                end++;
            }

            return text.substring(start, end).trim();
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        return null;
    }
}
