package ru.serbis.lexer;


import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class Lexer {
    public static int line = 1;
    private char peek = ' ';
    private Reader reader;
    private boolean aval = true;
    private boolean notNext = false;

    public Lexer(FileInputStream fis) {
        Reader r = new InputStreamReader(fis);
        reader = new BufferedReader(r);

    }

    private void readch() throws IOException {
        int r;
        if ((r = reader.read()) != -1){
            peek = (char) r;
            aval= true;
        } else {
            aval = false;
        }
    }

    private boolean readch(char c) throws IOException{
        readch();
        if (peek != c) return false;
        peek = ' ';
        return true;
    }

    public List<Token> getList() throws IOException {
        List<Token> tokens = new ArrayList<>();
        while (true) {
            Token token = scan();
            if (token == null)
                return tokens;

            tokens.add(token);
        }
    }

    public Token scan() throws IOException {
        while (true) {
            if (!notNext)
                readch();
            else
                notNext = false;
            if (!aval)
                return null;
            if (peek == ' ' || peek == '\t') continue;
            else if (peek == '\n') line++;
            else break;
        }

        switch (peek) {
            case '{':
                return new Token(Tag.LBRACE, "{", line);
            case '}':
                return new Token(Tag.RBRACE, "}", line);
            case '(':
                return new Token(Tag.LBRACKET, "(", line);
            case ')':
                return new Token(Tag.RBRACKET, ")", line);
            case ',':
                return new Token(Tag.COMMA, ",", line);
            case ';':
                return new Token(Tag.SEMI, ";", line);
            case '+':
                return new Token(Tag.PLUS, "+", line);
            case '&':
                if (readch('&')) return new Token(Tag.LOGAND, "&&", line);
                else return new Token(Tag.BITAND, "&", line);
            case '|':
                if (readch('|')) return new Token(Tag.LOGOR, "||", line);
                else return new Token(Tag.BITOR, "|", line);
            case '=':
                if (readch('=')) return new Token(Tag.EQ, "==", line);
                else return new Token(Tag.ASSIGNBASE, "=", line);
            case '!':
                if (readch('=')) return new Token(Tag.NE, "!=", line);
                else return new Token(Tag.NOT, "!", line);
            case '<':
                if (readch('=')) return new Token(Tag.LE, "<=", line);
                else return new Token(Tag.LESS, "<", line);
            case '>':
                if (readch('=')) return new Token(Tag.GE, ">=", line);
                else return new Token(Tag.GROSS, ">", line);
        }
        if (Character.isDigit(peek)) {
            int v = 0;
            do {
                v = 10 * v + Character.digit(peek, 10);
                readch();
            } while (Character.isDigit(peek));
            notNext =true;
            if (peek != '.') return new Token(Tag.INT, String.valueOf(v), line);
            float x = v;
            float d = 10;
            for (;;) {
                readch();
                if (!Character.isDigit(peek)) break;
                x = x + Character.digit(peek, 10) / d;
                d = d * 10;
            }
            notNext = true;
             return new Token(Tag.FLOAT, String.valueOf(x), line);
        }

        if (Character.isLetter(peek)) {
            StringBuffer b = new StringBuffer();
            do {
                b.append(peek);
                readch();
            } while (Character.isLetterOrDigit(peek));
            String s = b.toString();
            peek = ' ';
            switch (s) {
                case "class":
                    return new Token(Tag.CLASS, s, line);
                case "if":
                    return new Token(Tag.IF, s, line);
                case "else":
                    return new Token(Tag.ELSE, s, line);
                case "while":
                    return new Token(Tag.WHILE, s, line);
                case "do":
                    return new Token(Tag.DO, s, line);
                case "break":
                    return new Token(Tag.BREAK, s, line);
                case "int":
                    return new Token(Tag.TYPE, s, line);
                case "float":
                    return new Token(Tag.TYPE, s, line);
                case "void":
                    return new Token(Tag.TYPE, s, line);
                default:
                    return new Token(Tag.ID, s, line);
            }

        }

        return null;
    }


}