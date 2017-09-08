package ru.serbis.lexer;

public class Token {
    public Tag tag;
    public String lexeme;
    public int line;

    public Token(Tag tag, String lexeme, int line) {
        this.tag = tag;
        this.lexeme = lexeme;
        this.line = line;
    }
    public String toString() {
        return  tag + " : " + lexeme;
    }
}