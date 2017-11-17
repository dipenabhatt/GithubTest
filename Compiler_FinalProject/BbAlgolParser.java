package Compiler_FinalProject;

/**
 * Created by Parshwa on 2/9/17.
 * Course - Compiler Design - B-Algol Parser Program.
 * Language is Java and Program is to implement Algol Program.
 */

import java.util.Stack;

/**
 * Program is to implement Compiler for Algol Programming Language.
 * This program implement Algol BNF and generate Parse tree.
 * This program use Scanner Program's getToken() routine which give one token at a time with its token number, lexeme and line number.
 */

public class BbAlgolParser {
    //Assemblycode class object
    static AssemblyCodeGen assemblyCodeGen = AssemblyCodeGen.getInstance();

    //Stack to maintain MIPS stack frame
    static Stack<ExpressionRecord> recordExpression = new Stack();

    //To create If condition lable
    static  String successString = null;

    //To maintain string literal lable number to generate lable
    static int lableNo = 0;

    static String lable = null;
    static int currOff = 4;

    //Current rule name is stored
    static String rule = null;
    static String rule1 = null;

    //To maintain if condition to generate lable integer variable lableNoForIfCondition is used to distinguish different lable
    static int lableNoForIfCondition = 0;

    //To maintain while condition to generate lable integer variable lableNoForWhileCondition is used to distinguish different lable
    static int lableNoForWhileCondition = 0;
    //To maintain while loop to generate lable integer variable lableNoForWhileloop is used to distinguish different lable
    static int lableNoForWhileloop = 0;

    /**
     * Used to store token no return by getToken() routine.
     */
    static int token;
    /**
     * obj is BbalgolScanner Object used to access its getToken() routine.
     */
    static Compiler_FinalProject.BbAlgolScanner obj = new Compiler_FinalProject.BbAlgolScanner();
    /**
     * ts is TokenSignature Object which store token number, lexeme and line number for particular token.
     */
    static Compiler_FinalProject.TokenSignature ts = obj.getToken();

    //SymbolTable Code
    static Compiler_FinalProject.BbAlgolSymbolTable symbolTableObj = new BbAlgolSymbolTable();
    static int offSetVal = 0;

    public static void main(String args[]) {
        /**
         * Way to obtain token no from getToken() routine of BbalgolScanner class.
         */
        token = ts.getTokenNumber();
        program();
        if(token == 18) {
            System.out.println("\nSuccess");
        } else {
            System.out.println("\nError");
        }
    }

    /**
     * 1.program -> blockst ‘.’
     */
    static void program() {
        blockSt();
        match(18);      //18 - . token
        //Generate all MIPS instruction as string
        System.out.println(assemblyCodeGen.returnCodeGen());
    }

    /**
     * 2.blockst -> BEGINTOK stats ENDTOK
     */
    static void blockSt() {
        if(!match(7)) {     //7 - BEGIN token
            errorRoutine("\nError in 'blockst' routine. \nProgram Doesn't start with BEGIN Token.\n");
        }
        match(7);       //7 - BEGIN token

        /**
         * Every time BEGIN Keyword appear, new scope begin.
         */
        symbolTableObj.openScope();

        ts = obj.getToken();
        token = ts.getTokenNumber();
        stats();
        if(match(8)) {      //8 - END token
            ts = obj.getToken();
            token = ts.getTokenNumber();
            /**
             * Every time END Keyword appear, scope at the top of stack is pop.
             */
            symbolTableObj.exitScope();
        } else {
            errorRoutine("\nError in 'blockst' routine. \nMissing END Token.\n");
        }
    }

    /**
     * 3.stats -> statmt ';'  stats  |  <empty>
     */
    static void stats() {
        //3 - DATATYPE token   //9 - IF token  //1 - identifier token   //7 - BEGIN token   //11 - WHILE token
        //13 - IO statement token (READ, WRITE, WRITELN)
        if(match(3) || match(9) || match(1) || match(7) || match(11) || match(13)) {
            statmt();
            if(!match(16)) {        //16 - ; token
                errorRoutine("\nError in 'stats' routine. \nMissing ';' at the end of statement.\n");
            }
            match(16);      //16 - ; token
            ts = obj.getToken();
            token = ts.getTokenNumber();
            stats();
        } else {
        }
    }

    /**
     * 4.decl -> BASICTYPETOK IDTOK
     */
    static void decl(){
        match(3);       //3 - BASIC TYPE token

        /**
         *Symbol Table Code
         * Take type token into tokType
         */
        String tokType = ts.gettokenLexeme();
        char tokTypeChar = '\0';
        if(tokType.equals("INTEGER")) {
            tokTypeChar = 'I';
        }else if(tokType.equals("STRING")) {
            tokTypeChar = 'S';
        }else if(tokType.equals("LOGICAL")) {
            tokTypeChar = 'L';
        }

        ts = obj.getToken();
        token = ts.getTokenNumber();
        if(!match(1)) {     //1 - identifier token
            errorRoutine("\nError in 'decl' routine. \nTYPE Token doesn't followed by IDENTIFIER.\n");
        }
        match(1);       //1 - identifier token

        /**
         *Symbol Table Code
         *If tokIdentifier is not find in current scope then insert it, otherwise print error message.
         */
        String tokIdentifier = ts.gettokenLexeme();
        if(!symbolTableObj.find(tokIdentifier)) {
            symbolTableObj.insert(tokIdentifier, tokTypeChar, offSetVal);
            offSetVal = offSetVal-4;
        } else {
            System.out.println("\nError: Variable is already declared in the scope.");
        }

        ts = obj.getToken();
        token = ts.getTokenNumber();
    }

    /**
     * 5.statmt -> decl | ifstat | assstat |  blockst | loopst | iostat | <empty>
     */
    static void statmt(){
        if(match(3)) {      //3 - DATATYPE token
            decl();
        } else if(match(9)) {       //9 - IF token
            ifstat();
        } else if(match(1)) {       //1 - identifier token
            assstat();
        } else if(match(7)) {       //7 - BEGIN token
            blockSt();
        } else if(match(11)) {      //11 - WHILE token
            loopst();
        } else if(match(13)) {      //13 - IO statement token (READ, WRITE, WRITELN)
            iostat();
        } else {
        }
    }

    /**
     * 6.assstat -> idref   ASTOK  expression
     */
    static void assstat(){
        rule = "assstat";
        idref();
        int sp = recordExpression.peek().getLocation();
        match(19);      //19 - := token

        ts = obj.getToken();
        token = ts.getTokenNumber();

        expression();
        int curoff = recordExpression.peek().getLocation();
        //Assembly code for assignment statement
        assemblyCodeGen.assignmentStatement(curoff, sp);
    }

    /**
     * 7.ifstat -> IFTOK  expression THENTOK statmt
     */
    static void ifstat(){
        rule1 = "ifstat";
        match(9);        //9 - IF token
        ts = obj.getToken();
        token = ts.getTokenNumber();
        expression();
        if(!match(10)) {    //10 - THEN token
            errorRoutine("\nError in 'ifstat' routine. \nIF is not followed by THEN Token.\n");
        }
        match(10);       //10 - THEN token
        ts = obj.getToken();
        token = ts.getTokenNumber();
        successString = null;

        statmt();
        //Code for if statement condition lable
        assemblyCodeGen.gotoCondition("ConditionLable"+lableNoForIfCondition);
        lableNoForIfCondition++;
    }

    /**
     * 8.loopst -> WHILETOK expression DOTOK stats
     */
    static void loopst(){
        rule1 = "loopst";
        match(11);       //11 - WHILE token
        // assembly code for WhileloopBegin
        assemblyCodeGen.whileLoop(lableNoForWhileloop);
        ts = obj.getToken();
        token = ts.getTokenNumber();
        expression();
        if(!match(12)) {     //12 - DO token
            errorRoutine("\nError in 'loopst' routine. \nWHILE is not followed by DO Token.\n");
        }
        match(12);      //12 - DO token
        ts = obj.getToken();
        token = ts.getTokenNumber();
        stats();
        //assembly code for WhileloopEnd
        assemblyCodeGen.whileLoopEndLoop(lableNoForWhileloop, lableNoForWhileCondition);
        lableNoForWhileCondition++;
        lableNoForWhileloop++;


    }

    /**
     * 9.iostat -> READTOK ( idref ) | WRITETOK (expression)
     */
    static void iostat(){
        if(match(13) && ts.gettokenLexeme().equals("READ")) {       //13 - IO statement token (READ)
            ts = obj.getToken();
            token = ts.getTokenNumber();
            if(!match(14)) {    //14 - ( token
                errorRoutine("\nError in 'iostat' routine of READTOK. \n'(' Token is missing.\n");
            }
            match(14);      //14 - ( token
            ts = obj.getToken();
            token = ts.getTokenNumber();
            ExpressionRecord ex = new ExpressionRecord();
            idref();

            //assembly code
            Compiler_FinalProject.IdentifierAttribute identifierAttributeObj = symbolTableObj.findAll(ts.gettokenLexeme());
            if(identifierAttributeObj.getType() == 'I' || identifierAttributeObj.getType() == 'L') {
                assemblyCodeGen.readIntData(recordExpression.peek().getLocation());
            } else if(identifierAttributeObj.getType() == 'S') {
                assemblyCodeGen.readStringData(recordExpression.peek().getLocation());
            }

            if(!match(15)) {    //15 - ) token
                errorRoutine("\nError in 'iostat' routine of READTOK.\n')' Token is missing.\n");
            }

            match(15);  //15 - ) token

            ts = obj.getToken();
            token = ts.getTokenNumber();
        } else if(match(13)) {      //13 - IO statement token (WRITE, WRITELN)
            String str = ts.gettokenLexeme();
            ts = obj.getToken();
            token = ts.getTokenNumber();
            if(!match(14)) {    //14 - ( token
                errorRoutine("\nError in 'factor' routine of WRITETOK. \n'(' Token is missing.\n");
            }
            expression();

            //assembly code for print integer variable value
            if(rule.equals("idref")) {
                ExpressionRecord expressionRecord = recordExpression.peek();
                if (expressionRecord.getType1() == 'S') {
                    assemblyCodeGen.printStringFromSP("lable" + lable);
                } else if (expressionRecord.getType1() == 'I') {
                    assemblyCodeGen.printIntFromSP(recordExpression.peek().getLocation());
                }
            }

            //assenbly code for WRITELN to generate new line at end of statement
            if(str.equals("WRITELN")) {
                assemblyCodeGen.writeStringNewLineData("newline");
            }
        }
    }

    /**
     * 10.expression -> term expprime
     */
    static void expression(){
        term();
        expprime();
    }

    /**
     * 11.expprime -> ADDOPTOK  term expprime | <empty>
     */
    static void expprime(){
        ExpressionRecord ler = new ExpressionRecord();
        //Left hand side expression in parse tree
        ler.setLocation(recordExpression.peek().getLocation());
        ler.setType(recordExpression.peek().getType1());
        String operator = null;
        if(match(4)) {  //4 - ADDITION OPERATOR token
            //Assembly code for additional oparation
            if(ts.gettokenLexeme().equals("+")) {
                operator = "add";
            } else if(ts.gettokenLexeme().equals("-")) {
                operator = "sub";
            }else {
                operator = "or";
            }
            ts = obj.getToken();
            token = ts.getTokenNumber();
            term();

            //right hand side expression in parse tree
            ExpressionRecord rer = new ExpressionRecord();
            rer.setLocation(recordExpression.peek().getLocation());
            rer.setType(recordExpression.peek().getType1());

            ExpressionRecord ex = new ExpressionRecord();

            if(ler.getType1() == 'I' && rer.getType1() == 'I') {
                assemblyCodeGen.addFromSP(currOff, ler.getLocation(), rer.getLocation(),operator);

                ex.setType('I');
                ex.setLocation(currOff);
                recordExpression.push(ex);
                currOff += 4;
            }
            expprime();

        } else {
        }
    }

    /**
     * 12.term -> relfactor termprime
     */
    static void term(){
        relfactor();
        termprime();
    }

    /**
     * 13.termprime -> MULOPTOK  relfactor termprime | <empty>
     */
    static void termprime(){
        //Left hand side expression in parse tree
        ExpressionRecord ler = new ExpressionRecord();
        ler.setLocation(recordExpression.peek().getLocation());
        ler.setType(recordExpression.peek().getType1());
        String operator = null;
        if(match(5)) {  //5 - MULTIPLICATION OPERATOR token
            //assembly code to generate Multiplication operation
                if(ts.gettokenLexeme().equals("*")) {
                    operator = "*";
                } else if(ts.gettokenLexeme().equals("/") || ts.gettokenLexeme().equals("DIV")) {
                    operator = "/";
                }else if(ts.gettokenLexeme().equals("REM")) {
                    operator = "REM";
                }else {
                    operator = "AND";
                }
                ts = obj.getToken();
                token = ts.getTokenNumber();
                term();

                //right hand side expression in parse tree
                ExpressionRecord rer = new ExpressionRecord();
                rer.setLocation(recordExpression.peek().getLocation());
                rer.setType(recordExpression.peek().getType1());

                ExpressionRecord ex = new ExpressionRecord();

                if(ler.getType1() == 'I' && rer.getType1() == 'I') {
                    if(operator.equals("*")) {
                        assemblyCodeGen.MultiplicationFromSP(currOff, ler.getLocation(), rer.getLocation());
                    } else if(operator.equals("/")) {
                        assemblyCodeGen.divWithQuotientFromSP(currOff, ler.getLocation(), rer.getLocation());
                    } else if(operator.equals("REM")) {
                        assemblyCodeGen.divWithReminderFromSP(currOff, rer.getLocation(), ler.getLocation());
                    } else {
                        assemblyCodeGen.andFromSP(currOff, ler.getLocation(), rer.getLocation());
                    }

                    ex.setType('I');
                    ex.setLocation(currOff);
                    recordExpression.push(ex);
                    currOff += 4;
                }

            ts = obj.getToken();
            token = ts.getTokenNumber();
            relfactor();
            termprime();
        } else {
        }
    }

    /**
     *14.relfactor -> factor factorprime
     */
    static void relfactor(){
        factor();
        factorprime();
    }


    /**
     * 15.factorprime -> RELOPTOK  factor |  <empty>
     */
    static void factorprime(){
        ExpressionRecord ler = new ExpressionRecord();
        //Left hand side expression in parse tree
        ler.setLocation(recordExpression.peek().getLocation());
        ler.setType(recordExpression.peek().getType1());
        if(match(6)) {  //6 - RELATIONAL OPERATOR token
            String operator = null;
            if(ts.gettokenLexeme().equals("<")) {
                operator = "<";
            } else if(ts.gettokenLexeme().equals(">")) {
                operator = ">";
            }else if(ts.gettokenLexeme().equals("=")) {
                operator = "=";
            }else if(ts.gettokenLexeme().equals("!=")) {
                operator = "!=";
            }

            ts = obj.getToken();
            token = ts.getTokenNumber();

            factor();
            //right hand side expression in parse tree
            ExpressionRecord rer = new ExpressionRecord();
            rer.setLocation(recordExpression.peek().getLocation());
            rer.setType(recordExpression.peek().getType1());

            //assembly code for relational operation both for if condition and while condition
            if(ler.getType1() == 'I' && rer.getType1() == 'I') {
                if(rule1.equals("loopst")) {
                    assemblyCodeGen.readConditionDataFromSp(rer.getLocation(), ler.getLocation());
                } else if(rule1.equals("ifstat")){
                    assemblyCodeGen.readConditionDataFromSp(ler.getLocation(), rer.getLocation());
                }

                if(operator.equals("<")) {
                    if(rule1.equals("loopst")) {
                        assemblyCodeGen.operatorCondition("blt", "ConditionLableW"+lableNoForWhileCondition);
                    } else if(rule1.equals("ifstat")){
                        assemblyCodeGen.operatorCondition("bgt", "ConditionLable" + lableNoForIfCondition);
                    }
                } else if(operator.equals(">")) {
                    if(rule1.equals("loopst")) {
                        assemblyCodeGen.operatorCondition("bgt", "ConditionLableW"+lableNoForWhileCondition);
                    }else if(rule1.equals("ifstat")){
                        assemblyCodeGen.operatorCondition("blt", "ConditionLable" + lableNoForIfCondition);
                    }
                } else if(operator.equals("=")) {
                    if(rule1.equals("loopst")) {
                        assemblyCodeGen.operatorCondition("beq", "ConditionLableW"+lableNoForWhileCondition);
                    } else if(rule1.equals("ifstat")){
                        assemblyCodeGen.operatorCondition("bne", "ConditionLable" + lableNoForIfCondition);
                    }
                }else if(operator.equals("!=")) {
                    if(rule1.equals("loopst")) {
                        assemblyCodeGen.operatorCondition("bne", "ConditionLableW"+lableNoForWhileCondition);
                    } else if(rule1.equals("ifstat")){
                        assemblyCodeGen.operatorCondition("beq", "ConditionLable" + lableNoForIfCondition);
                    }
                }
            }
        } else {
        }
    }

    /**
     * 16.factor -> NOTTOK  factor |  idref |  LITTOK  |  '('  expression  ')'
     */
    static void factor(){
        if(match(17)) {     //17 - ! token
            ts = obj.getToken();
            token = ts.getTokenNumber();
            factor();
        } else if(match(1)) {   //1 - identifier token
            idref();
        } else if(match(2)) {     // 2 - literal token
            char c = getType(ts.gettokenLexeme());
            String currentRule = rule;
            rule = "factor";
            //assembly code for integer literal
            if(c == 'I') {
                ExpressionRecord ex = new ExpressionRecord();
                ex.setType('I');
                ex.setLocation(currOff);
                recordExpression.push(ex);
                currOff += 4;
                assemblyCodeGen.InitializeIntData(recordExpression.peek().getLocation(), Integer.parseInt(ts.gettokenLexeme()));
            }
            //assembly code for String literal
            else if(c == 'S') {
                if(currentRule.equals("idref")) {
                    assemblyCodeGen.writeStringInDataForIdentifier("lable", lable, ts.gettokenLexeme());
                } else {
                    assemblyCodeGen.writeStringInData("lable", lableNo, ts.gettokenLexeme());
                    lableNo++;
                }
            }
            //assembly code for Logical literal
            else if(c == 'L') {
                ExpressionRecord ex = new ExpressionRecord();
                ex.setType('I');
                ex.setLocation(currOff);
                recordExpression.push(ex);
                currOff += 4;
                int value;
                if(ts.gettokenLexeme().equals("TRUE"))
                    value = 1;
                else
                    value = 0;

                assemblyCodeGen.InitializeIntData(recordExpression.peek().getLocation(), value);
            } else {
                System.out.println("Error");
            }

            ts = obj.getToken();
            token = ts.getTokenNumber();
        } else if(match(14)) {  //14 - ( token
            ts = obj.getToken();
            token = ts.getTokenNumber();
            expression();
            if (!match(15)) {   //15 - ) token
                errorRoutine("\nError in 'factor' routine of WRITETOK. \n')' Token is missing.\n");
            }
            match(15);  //15 - ) token
            ts = obj.getToken();
            token = ts.getTokenNumber();
        }
    }

    /**
     * 17. idref -> IDTOK
     */
    static void idref(){
        rule = "idref";

        if(!match(1)) {     //1 - identifier token
            errorRoutine("\nError in 'idref' routine. \nNot IDENTIFIER Token.\n");
        }
        match(1);

        /**
         * Print Identifier with all its attribute values on the most recent scope when it is used in the the program.
         * Also Print at what line number that identifier located in program.
         * If identifier is not in scope, it print error message.
         */

        String identifier = ts.gettokenLexeme();
        if(symbolTableObj.findAll(identifier) != null) {
            Compiler_FinalProject.IdentifierAttribute identifierAttributeObj = symbolTableObj.findAll(identifier);
            //assembly code for identifier and its type and location puch into stack frame
            ExpressionRecord ex = new ExpressionRecord();
            ex.type = identifierAttributeObj.getType();
            ex.location = identifierAttributeObj.getOffSet();
            if(ex.type == 'S') {
                lable = identifier;
            }
            recordExpression.push(ex);

        } else {
            System.out.println("\nError: Declared variable before used it.");
        }

        ts = obj.getToken();
        token = ts.getTokenNumber();
    }


    /**
     * Match tokenno with token return by getToken() routine.
     * @param tokenNo terminal tokenno used in aprticular routine.
     * @return boolean value true if token match, otherwise false.
     */
    static boolean match(int tokenNo) {
        boolean b = true;
        if(tokenNo != token){
            b = false;
        }
        return b;
    }


    /**
     * Call when Error occur in program.
     * It print error message and exit entire program.
     * @param str is error message string.
     */
    static void errorRoutine(String str) {
        System.out.println(str);
        System.exit(0);
    }

    /**
     *
     * @param str token lexeme as string
     * @return char type valuse which store type of variable
     */
    static char getType(String str) {
        char lexeme = '\0';
        char[] s = str.toCharArray();
        if(s[0] == 't' || s[0] == 'f') {
            lexeme = 'L';
        } else if (s[0] == '"') {
            lexeme = 'S';
        } else {
            lexeme = 'I';
        }
        return lexeme;
    }
}

/**
 * Used as datatype to store variable type and location into stack frame
 */
class ExpressionRecord {
    char type;
    int location;

    public char getType1() {
        return type;
    }

    public int getLocation() {
        return location;
    }

    public void setType(char type) {
        this.type = type;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExpressionRecord that = (ExpressionRecord) o;

        if (type != that.type) return false;
        return location == that.location;

    }

    @Override
    public int hashCode() {
        int result = (int) type;
        result = 31 * result + location;
        return result;
    }
}


