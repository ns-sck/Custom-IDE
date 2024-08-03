package app.src.main.java.org.example;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class SyntaxHighlighter {

    private static final Map<String, Color> KEYWORDS = new HashMap<>();
    private static Color keywordColor;
    private static Color symbolColor;
    private static Color numberColor;

    static {
        Properties config = loadConfig();
        keywordColor = Color.decode(config.getProperty("keyword.color", "0x00fffb"));
        symbolColor = Color.decode(config.getProperty("symbol.color", "0x00fffb"));
        numberColor = Color.decode(config.getProperty("number.color", "0x00fffb"));
        addCppKeywords();
        addPythonKeywords();
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
    private static void addCppKeywords() {

        String[] cppKeywords = {
            "int", "string", "for", "while", "include", "define", "long long", "return",
            "priority_queue", "queue", "stack", "map", "unordered_map", "set", "unordered_set",
            "multiset", "unordered_multiset", "multimap", "unordered_multimap", "insert", "push_back",
            "pop_back", "front", "back", "using", "namespace", "template", "typename"
        };
        for (String keyword : cppKeywords) {
            KEYWORDS.put(keyword, new Color(0x00ff37));
        }
    }

    private static void addPythonKeywords() {
        String[] pythonKeywords = {
        };
        for (String keyword : pythonKeywords) {
            KEYWORDS.put(keyword, Color.BLUE);
        }
    }
    public static void highlightAll(CodeTextPane textPane) {
        SwingUtilities.invokeLater(() -> {
            try {

                Properties config = loadConfig();
                keywordColor = Color.decode(config.getProperty("keyword.color", "0x00fffb"));
                symbolColor = Color.decode(config.getProperty("symbol.color", "0x00fffb"));
                numberColor = Color.decode(config.getProperty("number.color", "0x00fffb"));
                
                textPane.trackingEnabled = false;
                String text = textPane.getText();
                StyledDocument doc = textPane.getStyledDocument();

                doc.setCharacterAttributes(0, text.length(), new SimpleAttributeSet(), true);

                MutableAttributeSet keywordAttributes = new SimpleAttributeSet();
                StyleConstants.setForeground(keywordAttributes, keywordColor); 

                MutableAttributeSet symbolAttributes = new SimpleAttributeSet();
                StyleConstants.setForeground(symbolAttributes, symbolColor); 

                MutableAttributeSet numberAttributes = new SimpleAttributeSet();
                StyleConstants.setForeground(numberAttributes, numberColor); 
                
                for (Map.Entry<String, Color> entry : KEYWORDS.entrySet()) {
                    String keyword = entry.getKey();
                    Pattern pattern = Pattern.compile("\\b" + Pattern.quote(keyword) + "\\b");
                    Matcher matcher = pattern.matcher(text);
                    while (matcher.find()) {
                        int start = matcher.start();
                        int end = matcher.end();
                        if (start >= 0 && end <= doc.getLength() && end > start) {
                            doc.setCharacterAttributes(start, end - start, keywordAttributes, false);
                        }
                    }
                }

                String[] symbols = {"{", "}", "[", "]", "(", ")", "#", "<", ">", "'", "\"", "=", "|", "&", "^"};{}
                for (String symbol : symbols) {
                    Pattern pattern = Pattern.compile(Pattern.quote(symbol));
                    Matcher matcher = pattern.matcher(text);

                    while (matcher.find()) {
                        int start = matcher.start();
                        int end = matcher.end();
                        if (start >= 0 && end <= doc.getLength() && end > start) {
                            doc.setCharacterAttributes(start, end - start, symbolAttributes, false);
                        }
                    }
                }

                String[] numbers = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
                for (String number : numbers) {
                    Pattern pattern = Pattern.compile(Pattern.quote(number));
                    Matcher matcher = pattern.matcher(text);

                    while (matcher.find()) {
                        int start = matcher.start();
                        int end = matcher.end();
                        if (start >= 0 && end <= doc.getLength() && end > start) {
                            doc.setCharacterAttributes(start, end - start, numberAttributes, false);
                        }
                    }
                }

                highlightComments(textPane);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                textPane.trackingEnabled = true;
            }
        });
    }

    public static void highlightComments(CodeTextPane textPane) {
        DocumentTokenizer dt = new DocumentTokenizer(textPane);
        StyledDocument doc = textPane.getStyledDocument();
        Element root = doc.getDefaultRootElement();
    
        StyleContext styleContext = StyleContext.getDefaultStyleContext();
        AttributeSet commentStyle = styleContext.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.GREEN);
    
        int lineCount = root.getElementCount();
        for (int i = 0; i < lineCount; ++i) {
            try {
                String first = dt.getFirstWordOfLine(i);
                if (first.length() >= 2 && first.charAt(0) == '/' && first.charAt(1) == '/') {
                    Element lineElement = root.getElement(i);
                    int startOffset = lineElement.getStartOffset();
                    int endOffset = lineElement.getEndOffset();
                    doc.setCharacterAttributes(startOffset, endOffset - startOffset, commentStyle, false);
                }
            } catch (Exception e) {
                System.out.println("Line start exception: " + e.getMessage());
            }
        }
    }
    
    
    public static String getWordAtCaret(JTextPane textPane) {
        int caretPosition = textPane.getCaretPosition();
        Document doc = textPane.getDocument();

        try {
            String text = doc.getText(0, caretPosition);

            int start = Math.max(0, caretPosition - 1);
            while (start > 0) {
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
