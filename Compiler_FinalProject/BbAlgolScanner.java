package Compiler_FinalProject;

import java.util.ArrayList;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Program is to implement Compiler for Algol Programming Language.
 * This program find token per line and return lexeme with its token no.
 * This program is implemented using DFA. DFA is implement by using Switch case.
 * Here, Comment and String is single line.
 */
/**
 * Created by Parshwa on 1/29/17 and Course - Compiler Design.
 * Language is Java and Program is to implement Algol Program.
 */
public class BbAlgolScanner {
    public BbAlgolScanner() {
        //Give Text File input to FileHandler
        FileHandler filehandlerobj = new FileHandler("/home/dipen/Documents/B.txt");
        Compiler_FinalProject.TokenClass tokenclassobj = new Compiler_FinalProject.TokenClass();
        Stack token;
        int linenumber = 0;
        /**
         * Find token on each line until end_of_token '.'
         */
        do {
            linenumber++;
            token = tokenclassobj.findToken(filehandlerobj.getNextLine(), linenumber);
        } while (token.peek() != "18     .");
    }


    static List<Compiler_FinalProject.TokenSignature> tokens = new ArrayList<>();
    static int index = 0;


    Compiler_FinalProject.TokenSignature getToken() {
        Compiler_FinalProject.TokenSignature ts = null;
        if (index <= (tokens.size()) - 1 && tokens.get(index) != null) {
            ts = tokens.get(index++);
        }
        return ts;
    }
}


/**
 * This class implement DFA for Algol compiler
 */
class TokenClass {
    /**
     * Routine to find keyword and reserved words
     *
     * @param str Pass identifier as argument to check whether it is reserved word or not.
     * @return keyword with its tokennumber and tokenclass as String if identifier is not reserved word it return null.
     */


    String KeywordClass(String str) {
        List<Compiler_FinalProject.Rule> rules = new ArrayList<>();
        String str1 = null;

        rules.add(new Compiler_FinalProject.Rule(13, "WRITELN", "WRITELN"));
        rules.add(new Compiler_FinalProject.Rule(13, "READ", "READ"));
        rules.add(new Compiler_FinalProject.Rule(13, "WRITE", "WRITE"));
        rules.add(new Compiler_FinalProject.Rule(2, "FALSE", "FALSE"));
        rules.add(new Compiler_FinalProject.Rule(2, "TRUE", "TRUE"));
        rules.add(new Compiler_FinalProject.Rule(3, "INTEGER", "INTEGER"));
        rules.add(new Compiler_FinalProject.Rule(3, "STRING", "STRING"));
        rules.add(new Compiler_FinalProject.Rule(3, "LOGICAL", "LOGICAL"));
        rules.add(new Compiler_FinalProject.Rule(4, "OR", "OR"));
        rules.add(new Compiler_FinalProject.Rule(5, "DIV", "DIV"));
        rules.add(new Compiler_FinalProject.Rule(5, "REM", "REM"));
        rules.add(new Compiler_FinalProject.Rule(5, "AND", "AND"));
        rules.add(new Compiler_FinalProject.Rule(7, "BEGIN", "BEGIN"));
        rules.add(new Compiler_FinalProject.Rule(8, "END", "END"));
        rules.add(new Compiler_FinalProject.Rule(9, "IF", "IF"));
        rules.add(new Compiler_FinalProject.Rule(10, "THEN", "THEN"));
        rules.add(new Compiler_FinalProject.Rule(11, "WHILE", "WHILE"));
        rules.add(new Compiler_FinalProject.Rule(12, "DO", "DO"));

        for (int i = 0; i < rules.size(); i++) {
            Matcher m = rules.get(i).getPattern().matcher(str);
            if (m.find()) {
                str1 = Integer.toString(rules.get(i).getTokennumber());
                break;
            } else {
                str1 = null;
            }
        }
        return str1;
    }


    /**
     * Switch case Statement to implement DFA.
     *
     * @param input      one line at a time from input file as input.
     * @param lineNumber
     * @return Stack which store tokens of each line.
     */
    public Stack findToken(String input, int lineNumber) {
        CharacterReader cr = new CharacterReader(input);
        Stack<String> Token = new Stack<>();
        String token;
        char nextChar = cr.getNextChar();
        while (nextChar != '\0') {
            /**
             * Logic to find identifier
             */
            if (Character.isLetter(nextChar)) {
                String str1 = Character.toString(nextChar);
                nextChar = cr.getNextChar();
                while ((Character.isLetterOrDigit(nextChar)) || (nextChar == '_')) {
                    str1 = str1 + nextChar;
                    nextChar = cr.getNextChar();
                }
                /**
                 * Logic for COMMENT. Comment is single line and if ';' is not found it show error message.
                 */
                if (str1.equals("COMMENT")) {
                    nextChar = cr.getNextChar();
                    while (true) {
                        if (nextChar != ';' && nextChar != '\0') {
                            nextChar = cr.getNextChar();
                        } else if (nextChar != ';' && nextChar == '\0') {
                            Token.push("\nError at Line number: " + lineNumber + " \nCOMMENT is not followed by ;\n");
                            nextChar = cr.getNextChar();
                            break;
                        } else if (nextChar == ';') {
                            nextChar = cr.getNextChar();
                            break;
                        }
                    }
                } else {
                    token = KeywordClass(str1);
                    if (token == null) {
                        token = str1;
                        Token.push("1     " + token);
                        Compiler_FinalProject.BbAlgolScanner.tokens.add(new Compiler_FinalProject.TokenSignature(1, token, lineNumber));
                    } else {
                        Token.push(token);
                        Compiler_FinalProject.BbAlgolScanner.tokens.add(new Compiler_FinalProject.TokenSignature(Integer.parseInt(token), str1, lineNumber));
                    }
                }
            } else if (nextChar == '0') {
                Token.push("2     " + nextChar);
                Compiler_FinalProject.BbAlgolScanner.tokens.add(new Compiler_FinalProject.TokenSignature(2, Character.toString(nextChar), lineNumber));
                nextChar = cr.getNextChar();
            }
            /**
             * Logic to find Integer Literal or number sequence which start by non-zero digit.
             */
            else if (nextChar >= '1' && nextChar <= '9') {
                String str2 = Character.toString(nextChar);
                nextChar = cr.getNextChar();
                while (Character.isDigit(nextChar)) {
                    str2 = str2 + nextChar;
                    nextChar = cr.getNextChar();
                }
                Token.push("2     " + str2);
                Compiler_FinalProject.BbAlgolScanner.tokens.add(new Compiler_FinalProject.TokenSignature(2, str2, lineNumber));
            } else {
                switch (nextChar) {
                    case '(':
                        Token.push("14     (");
                        Compiler_FinalProject.BbAlgolScanner.tokens.add(new Compiler_FinalProject.TokenSignature(14, "(", lineNumber));
                        nextChar = cr.getNextChar();
                        break;

                    case ')':
                        Token.push("15     )");
                        Compiler_FinalProject.BbAlgolScanner.tokens.add(new Compiler_FinalProject.TokenSignature(15, ")", lineNumber));
                        nextChar = cr.getNextChar();
                        break;

                    case ';':
                        Token.push("16     ;");
                        Compiler_FinalProject.BbAlgolScanner.tokens.add(new Compiler_FinalProject.TokenSignature(16, ";", lineNumber));
                        nextChar = cr.getNextChar();
                        break;

                    case '.':
                        Token.push("18     .");
                        Compiler_FinalProject.BbAlgolScanner.tokens.add(new Compiler_FinalProject.TokenSignature(18, ".", lineNumber));
                        nextChar = cr.getNextChar();
                        break;

                    case '+':
                    case '-':
                        String s = "4     " + Character.toString(nextChar);
                        Token.push(s);
                        Compiler_FinalProject.BbAlgolScanner.tokens.add(new Compiler_FinalProject.TokenSignature(4, Character.toString(nextChar), lineNumber));
                        nextChar = cr.getNextChar();
                        break;

                    case '*':
                    case '/':
                        String s1 = "5     " + Character.toString(nextChar);
                        Token.push(s1);
                        Compiler_FinalProject.BbAlgolScanner.tokens.add(new Compiler_FinalProject.TokenSignature(5, Character.toString(nextChar), lineNumber));
                        nextChar = cr.getNextChar();
                        break;

                    case '<':
                    case '>':
                    case '=':
                        String s2 = "6     " + Character.toString(nextChar);
                        Token.push(s2);
                        Compiler_FinalProject.BbAlgolScanner.tokens.add(new Compiler_FinalProject.TokenSignature(6, Character.toString(nextChar), lineNumber));
                        nextChar = cr.getNextChar();
                        break;

                    case '!':
                        nextChar = cr.getNextChar();
                        if (nextChar == '=') {
                            Token.push("6     !=");
                            Compiler_FinalProject.BbAlgolScanner.tokens.add(new Compiler_FinalProject.TokenSignature(6, "!=", lineNumber));
                            nextChar = cr.getNextChar();
                        } else {
                            Token.push("17     !");
                            Compiler_FinalProject.BbAlgolScanner.tokens.add(new Compiler_FinalProject.TokenSignature(17, "!", lineNumber));
                            nextChar = cr.getNextChar();
                        }
                        break;

                    case ':':
                        nextChar = cr.getNextChar();
                        if (nextChar == '=') {
                            Token.push("19     :=");
                            Compiler_FinalProject.BbAlgolScanner.tokens.add(new Compiler_FinalProject.TokenSignature(19, ":=", lineNumber));
                            nextChar = cr.getNextChar();
                        } else {
                            Token.push("\nError at Line number: " + lineNumber + " \n':' is not followed by =\n");
                        }
                        break;

                    /**
                     * Case to find String Literal. String is single line and if " is not find at end od line error msg print.
                     * If quote is inside String literal it must precede by another quote.
                     */
                    case '"':
                        nextChar = cr.getNextChar();
                        int charlen = 0;
                        String str = "\"";
                        while (true) {
                            if (nextChar != '\"' && nextChar != '\0') {
                                charlen++;
                                if (charlen > 256) {
                                    Token.push("String length is more than 256 character.");
                                    nextChar = '\0';
                                    break;
                                } else {
                                    str = str + nextChar;
                                }
                                nextChar = cr.getNextChar();
                            } else if (nextChar != '\"' && nextChar == '\0') {
                                Token.push("\nError at Line number: " + lineNumber + " \nString is not end by \"\n");
                                nextChar = cr.getNextChar();
                                break;
                            }
                            /**
                             * Logic for how to handle " inside string
                             */
                            else if (nextChar == '\"') {
                                nextChar = cr.getNextChar();
                                if (nextChar == '\"') {
                                    str = str + "\"";
                                    nextChar = cr.getNextChar();
                                    continue;
                                }
                                str = str + "\"";
                                Token.push("2     " + str);
                                Compiler_FinalProject.BbAlgolScanner.tokens.add(new Compiler_FinalProject.TokenSignature(2, str, lineNumber));
                                break;
                            }
                        }
                        break;

                    default:
                        nextChar = cr.getNextChar();
                        break;
                }
            }
        }
        return Token;
    }
}


class Rule
{
    final int tokennumber;
    final String tokenclass;
    final Pattern pattern;

    Rule(int tokennumber, String tokenclass, String regex)
    {
        this.tokennumber = tokennumber;
        this.tokenclass = tokenclass;
        pattern = Pattern.compile(regex);
    }

    public int getTokennumber() {
        return tokennumber;
    }
    public String gettokenclass() { return tokenclass; }
    public Pattern getPattern() {
        return pattern;
    }
}


class TokenSignature {
    final int tokenNumber;
    final String tokenLexeme;
    final int tokenLineNo;

    TokenSignature(int tokennumber, String lexeme, int lineNo) {
        this.tokenNumber = tokennumber;
        this.tokenLexeme = lexeme;
        this.tokenLineNo = lineNo;
    }

    public int getTokenNumber() {
        return tokenNumber;
    }

    public String gettokenLexeme() {
        return tokenLexeme;
    }

    public int getTokenLineNo() {
        return tokenLineNo;
    }
}
