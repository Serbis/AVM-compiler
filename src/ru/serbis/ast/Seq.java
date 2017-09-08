package ru.serbis.ast;

/**
 * Секвенция - представляет собой основную структуру ast. Определить можно что
 * такое секвенция можно так - это один атомарный оператор. В секвенции левая
 * сторона описывает оператор, а правая является ссылкой на следующую
 * секвенцию. Например разберем такой участок кода:
 *      1. int a = 1;
 *      2. int b = 2;
 * Этот код можно представить как две секвенции, где первая в левой части будет
 * содержать оператор int a = 1, а в правой будет ссылать на вторую секвенцию,
 * котоаря будет в своей левой части содержать оператор int b = 2. В правой же
 * части будет находится пустой объект Stmt, как указатель но то, что это
 * последняя секвенция в дереве.
 */
public class Seq extends Stmt {
    public Stmt stmt1;
    public Stmt stmt2;

    public Seq(Stmt s1, Stmt s2) {
        stmt1 = s1;
        stmt2 = s2;
    }

    public void gen(int b, int a) {
        if (stmt1 == Stmt.Null)
            stmt2.gen(b, a);
        else if (stmt2 == Stmt.Null)
            stmt1.gen(b, a);
        else {
            int label = newlabel();
            stmt1.gen(b, label);
            emitlabel(label);
            stmt2.gen(label, a);
        }
    }

}