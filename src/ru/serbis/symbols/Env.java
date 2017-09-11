package ru.serbis.symbols;

import ru.serbis.lexer.Token;

import java.util.Hashtable;

/**
 * Представление таблицы символов компилятора
 */
public class Env {
    private Hashtable<Token, Symbol> table;
    private Env prev;

    public Env(Env n) {
        table = new Hashtable<>();
        prev = n;
    }

    public void put(Token w, Symbol symbol) {
        table.put(w, symbol);
    }

    public Symbol get(Token w) {
        for (Env e = this; e != null; e = e.prev) {
            Symbol found = (e.table.get(w));
            if (found != null) return found;
        }
        return null;
    }

    /*public Id getByLexeme(String lexeme) {
        Enumeration enumeration = table.keys();
        Word key;
        while (enumeration.hasMoreElements()) {
            key = (Word) enumeration.nextElement();
            if (key.lexeme.equals(lexeme)) {
                return (Id) table.get(key);
            }
        }

        return null;
    }

    public void setByLexeme(String lexeme, int number) {
        Enumeration enumeration = table.keys();
        Word key;
        while (enumeration.hasMoreElements()) {
            key = (Word) enumeration.nextElement();
            if (key.lexeme.equals(lexeme)) {
                Id id = (Id) table.get(key);
                id.asmnum = number;
            }
        }
    }*/
}