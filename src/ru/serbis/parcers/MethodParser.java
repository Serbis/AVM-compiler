package ru.serbis.parcers;

import ru.serbis.ast.Seq;
import ru.serbis.ast.Stmt;
import ru.serbis.lexer.Token;
import ru.serbis.lexer.Tag;

import java.util.List;


public class MethodParser {
    private List<Token> tokens;
    private Token look;
    private int curTokPos = 0;

    public Stmt parce(List<Token> tokens) {
        this.tokens = tokens;
        move();
        return block();
    }

    private Stmt block() {
        match(Tag.LBRACE); // {
        //Env savedEnv = top;
        //top = new Env(top);
        //flatenv.add(top);
        //decls();
        Stmt s = stmts();
        match(Tag.RBRACE);
        //top = savedEnv;
        return s;
    }

    private Stmt stmts() {
        if (look.tag == Tag.RBRACE) // }
            return Stmt.Null;
        else
            return new Seq(stmt(), stmts());

    }

    private Stmt stmt() {
        switch (look.tag) {
            case SEMI: // ;
                move();
                return Stmt.Null;
            default:
                return assign();
        }
    }

    /**
     * Парсер выражения присваивания
     *
     * @return выражение
     */
    private Stmt assign() {
        Stmt stmt = null;
        Token t = look;
        match(Tag.ID);
        //Id id = top.get(t);
        //if (id == null)
        //    error(t.toString() + " undeclared");
        if (look.tag == Tag.ASSIGNBASE) {
            move();
            stmt = new Set(id, bool());
        } else if (look.tag == '(') {
            move();

        } else {
            Assess x = offset(id);
            match('=');
            stmt = new SetElem(x, bool());
        }
        match(';');

        return stmt;
    }

    private void move() {
        look = tokens.get(curTokPos);
        curTokPos++;
    }

    private void match(Tag tag) {
        if (look.tag == tag) {
            if (curTokPos < tokens.size())
                move();
        } else {
            error("syntax error");
        }
    }

    private void error(String s) {
        throw new Error("near line " + look.line + ": " + s);
    }
}
