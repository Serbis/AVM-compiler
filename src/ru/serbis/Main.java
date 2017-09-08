package ru.serbis;

import ru.serbis.lexer.Lexer;
import ru.serbis.lexer.Token;
import ru.serbis.parcers.MethodParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {


    public static void main(String[] args) throws IOException {
        List<Token> tokens = new ArrayList<>();
        File file = new File("/home/serbis/tmp/fft.avmc");
        FileInputStream fis = new FileInputStream(file);
        Lexer lexer = new Lexer(fis);
        tokens = lexer.getList();
        MethodParser mp = new MethodParser();
        mp.parce(tokens);
    }
}
