package ru.serbis.parcers;

import ru.serbis.ast.Expr;
import ru.serbis.ast.Seq;
import ru.serbis.ast.Set;
import ru.serbis.ast.Stmt;
import ru.serbis.lexer.Token;
import ru.serbis.lexer.Tag;
import ru.serbis.symbols.Env;
import ru.serbis.symbols.Symbol;

import java.util.List;


public class MethodParser {
    private List<Token> tokens;
    private Token look;
    private int curTokPos = 0;
    /** */
    private Env top;

    public Stmt parce(List<Token> tokens) {
        this.tokens = tokens;

        //Создание верхней таблицы символ с родителем равным null
        top = new Env(top);

        move();

        //Код должен начинаться с блока, поэтому ожидая что далее
        //будет идти блок, производим его парсинг
        return block();
    }

    private Stmt block() {
        //Первый символ блок равен открывающей фигурной скобке
        match(Tag.LBRACE); // {

        //Сохраняетм текущую таблицу (предидущего блока, или верхушку, если
        //это первый блок в программе).
        Env savedEnv = top;

        //Создаем новую таблицу символов для блока, определяем ее как текущую,
        //а в качестве родителя устанавливаем вышестояющу - ту что ранее сохранили
        top = new Env(top);

        //flatenv.add(top);

        //decls();

        //Первым элеметом в блоке всегда ожидается некоторый оператор, поэтому
        //парсим оператор. Он будет являеться верхущкой дерева для данного блка.
        //По факту сюда возвращается секвенция, представляющее из себя дерево
        //операторов блока.
        Stmt s = stmts();
        //Окончанием блока является закрывающая фигурная скобка
        match(Tag.RBRACE);

        //Восстанавливаем состояние таблицы символов предидущего блока
        top = savedEnv;

        return s;
    }

    /*private void decls() {
        while (look.tag == Tag.TYPE) {
            //Type p = type();
            Token tok = look;
            move();
            match(Tag.ID);
            match(Tag.COMMA); // ;
            //Id id = new Id((Word) tok, p, used);
            Symbol symbol = new Symbol(tok.lexeme);
            top.put(tok, symbol);
            //used = used + p.width;
        }
    }*/

    private Stmt stmts() {
        if (look.tag == Tag.RBRACE) // }

            return Stmt.Null;
        else
            return new Seq(stmt(), stmts());

    }

    private Stmt stmt() {
        switch (look.tag) {
            //Ситуация пустого оператора, когда в строке находится точка с запятой
            case SEMI: // ;
                match(Tag.SEMI);
                return Stmt.Null;

            //Если рассматриваемый токен является типом, то это одна из трех ситуаций.
            //Первая ситуация - декларация переменной без присваивания. В данной ситуации
            //создается новый символ, затем происходит рекурсивный вызов, для парсинга
            //следующего оператора.
            //Вторая ситуация - декларация переменной с присваиванием. В данной ситуации
            //создается новый симов, затем происходит вызов парсера assing.
            //Третья ситуаия - вызов метода. Пока данная операция не описана.
            case TYPE:
                move();
                Token tok = look;
                match(Tag.ID);
                if (look.tag == Tag.SEMI) {
                    match(Tag.SEMI);
                    Symbol symbol = new Symbol(tok.lexeme);
                    top.put(tok, symbol);
                    return stmts();
                } else {
                    Symbol symbol = new Symbol(tok.lexeme);
                    top.put(tok, symbol);
                    back();
                    back();
                    return assign();
                }

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

        //В выражении присваивания первым идет идентификатор
        match(Tag.ID);
        Token t = look;
        move();

        //Найти в таблице символов блока заданный идентификатор. Если он не будет там найден
        //Значет это незадекларированный символ, вывести ошибеку
        Symbol symbol = top.get(t);
        if (symbol == null)
            error(t.toString() + " undeclared");

        //Далее может следовать два варанта развития событий.
        //Первый - после идентификатора идет оператор присваивания, в следствии
        //чего будет создан новый оператор в дереве Set
        //Второй - после идентитфикатора идет точка, в следствии чего это выражение
        //является обращением  к методу объекта. В данный момент данная логика не
        //проработана
        if (look.tag == Tag.ASSIGNBASE) {
            move();

            //Это варинт с присвание. В следсвии которого создается оператор Set, по правой
            //стороне котого вызывается парсер bool
            stmt = new Set(symbol, bool());
        }

        //Оканичивается выражение точкой с запятой
        match(Tag.SEMI); // ;

        return stmt;
    }

    private Expr bool()  {
        Expr x = join();
        while (look.tag == Tag.OR) {
            Token tok = look;
            move();
            x = new Or(tok, x, join());
        }
        return x;
    }

    private Expr join() {
        Expr x = equality();
        while (look.tag == Tag.AND) {
            Token tok = look;
            move();
            x = new And(tok, x, equality());
        }
        return x;
    }

    private Expr equality() {
        Expr x = rel();
        while (look.tag == Tag.EQ || look.tag == Tag.NE) {
            Token tok = look;
            move();
            x = new Rel(tok, x, rel());
        }
        return x;
    }

    private Expr rel() {
        Expr x = expr();
        switch (look.tag) {
            case '<':
            case Tag.LE:
            case Tag.GE:
            case '>':
                Token tok = look;
                move();
                return new Rel(tok, x, expr());

            default:
                return x;
        }
    }

    private Expr expr() {
        Expr x = term();
        while (look.tag == '+' || look.tag == '-') {
            Token tok = look;
            move();
            x = new Arith(tok, x, term());
        }
        return x;
    }

    private Expr term() {
        Expr x = unary();
        while (look.tag == '*' || look.tag == '/') {
            Token tok = look;
            move();
            x = new Arith(tok, x, unary());
        }
        return x;
    }

    private Expr unary() {
        if (look.tag == '-') {
            move();
            return new Unary(Word.minus, unary());
        } else if (look.tag == '!') {
            Token tok = look;
            move();
            return new Not(tok, unary());
        } else {
            return factor();
        }

    }

    private Expr factor() {
        Expr x = null;
        switch (look.tag) {
            case '(':
                move();
                x = bool();
                match(')');
                return x;

            case Tag.NUM:
                x = new Constant(look, Type.Int);
                move();
                return x;

            case Tag.REAL:
                x = new Constant(look, Type.Float);
                move();
                return x;

            case Tag.TRUE:
                x = Constant.True;
                move();
                return x;

            case Tag.FALSE:
                x = Constant.False;
                move();
                return x;

            case Tag.ID:
                String s = look.toString();
                Id id = top.get(look);
                if (id == null)
                    error(look.toString() + " undeclared");
                move();
                if (look.tag != '[')
                    return id;
                else
                    return offset(id);

            default:
                error("syntax error");
                return x;
        }
    }

    private void move() {
        look = tokens.get(curTokPos);
        curTokPos++;
    }

    private void back() {
        look = tokens.get(curTokPos - 1);
        curTokPos--;
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
