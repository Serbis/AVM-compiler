package ru.serbis.ast;

import ru.serbis.symbols.Symbol;

/**
 * Оператор присвания. В левой части оператора находится символ, а в правой
 * выражение.
 */
public class Set extends Stmt {
    public Symbol symbol;
    public Expr expr;

    public Set(Symbol s, Expr x) {
        symbol = s;
        expr = x;
        //if (check(id.type, expr.type) == null)
        //    error("type error");
    }

    /*public Type check(Type p1, Type p2) {
        if (Type.nuberic(p1) && Type.nuberic(p2))
            return p2;
        else if (p1 == Type.Bool && p2 == Type.Bool)
            return p2;
        else
            return null;
    }*/

    //public void gen(int b, int a) {
    //    emit(id.toString() + " = " + expr.gen().toString());
    //}
}