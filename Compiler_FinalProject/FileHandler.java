package Compiler_FinalProject;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * This is File handler file in which file is take as input and separate it into line.
 * Then line is split into character array.
 * Two main function of this class:
 *          1. getNextLine() - which return next line
 *          2. getNextChar() - which return next character
 */

/**
 * Created by parshwa on 1/29/17. Course - Compiler Design
 * Language - Java
 * Handle file input using BufferReader.
 */
public class FileHandler {

    private String filePath;
    private String nextLine;
    private BufferedReader bufferedReader;

    public FileHandler(String filePath) {
        this.filePath = filePath;
        try {
            FileReader file = new FileReader(this.filePath);
            bufferedReader = new BufferedReader(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public String getNextLine() {
        try {
            String line;
            if ((line = bufferedReader.readLine()) != null) {
                this.nextLine = line;
            } else {
                this.nextLine = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this.nextLine;
    }
}

class CharacterReader {
    private char[] nextChar;
    private int index = 0;

    public CharacterReader(String line) {
        nextChar = line.toCharArray();
    }
    public char getNextChar() {
        char next = '\0';
        if(index <= (nextChar.length)-1 && nextChar[index] != '\0') {
            next = nextChar[index++];
        }
        return next;
    }
}