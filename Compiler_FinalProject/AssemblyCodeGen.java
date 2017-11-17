package Compiler_FinalProject;


/**
 * Created by Parshwa on 3/6/17.
 * Implementation if MIPS code for equivalent ALGOL-W instructions
 */
public class AssemblyCodeGen {

    public static AssemblyCodeGen INSTANCE = new AssemblyCodeGen();

    private StringBuffer text = new StringBuffer();
    private StringBuffer data = new StringBuffer();

    private static int print_int = 1;
    private static int print_string = 4;
    private static int read_int = 5;
    private static int read_string = 8;

    public static AssemblyCodeGen getInstance(){
        return INSTANCE;
    }

    private AssemblyCodeGen(){
        proLog();
    }

    /**
     * ProLog code
     */
    private void proLog() {
        text.append(".text");
        text.append("\n");
//        text.append("globl main");
        text.append("main:");
        text.append("\n");
//        text.append("move $fp $sp");
        text.append("la $a0 ProgBegin");
        text.append("\n");
        text.append("li $v0 4");
        text.append("\n");
        text.append("syscall");
        text.append("\n");
        text.append("\n");

        data.append("newline : .asciiz \"\\n\"");
        data.append("\n");

    }

    /**
     * PostLog code
     */
    private void postLog() {
        text.append("\n");
        text.append("la $a0 ProgEnd");
        text.append("\n");
        text.append("li $v0 4");
        text.append("\n");
        text.append("syscall");
        text.append("\n");
        text.append("li $v0 10");
        text.append("\n");
        text.append("syscall");
        text.append("\n");
        text.append(".data");
        text.append("\n");
        text.append("ProgBegin: .asciiz  \"Program Begin\\n\"");
        text.append("\n");
        text.append("ProgEnd: .asciiz  \"\\nProgram End\\n\"");
        text.append("\n");
    }

    /**
     * MIPs code for assignment statement
     */
    public void assignmentStatement(int curroff, int sp) {
        System.out.println("assignmentStatement");
        text.append("lw $t0 "+curroff+"($sp)");
        text.append("\n");
        text.append("sw $t0 "+sp+"($sp)");
        text.append("\n");
    }

    /**
     * MIPS code for write String data in WRITE and WRITELN token
     */
    public void writeStringData(String param,int number) {
        System.out.println("writeStringNewLineData");
        text.append("la $a0 "+param+number);
        text.append("\n");
        text.append("li $v0 4");
        text.append("\n");
        text.append("syscall");
        text.append("\n");

        data.append(param+number+": .asciiz \"\\n\""+"\n");
        text.append("\n");
    }

    /**
     * MIPS code for write String data in WRITELN token for new line
     */
    public void writeStringNewLineData(String param){
        System.out.println("writeStringNewLineData");
        text.append("la $a0 "+param);
        text.append("\n");
        text.append("li $v0 4");
        text.append("\n");
        text.append("syscall");
        text.append("\n");
    }

    /**
     * MIPS code for write INTEGER data in WRITE and WRITELN token
     */
    public void writeStringInData(String param,int number, String str){
        System.out.println("writeStringInData");
        text.append("la $a0 "+param+number);
        text.append("\n");
        text.append("li $v0 4");
        text.append("\n");
        text.append("syscall");
        text.append("\n");

        data.append(param+number+": .asciiz "+str+"\n");
        text.append("\n");
    }

    /**
     * MIPS code for write INTEGER data for identifier in WRITE and WRITELN token
     */
    public void writeStringInDataForIdentifier(String param,String number, String str){
        System.out.println("writeStringInData");
        text.append("la $a0 "+param+number);
        text.append("\n");
        text.append("li $v0 4");
        text.append("\n");
        text.append("syscall");
        text.append("\n");
        text.append("\n");
        data.append(param+number+": .asciiz "+str+"\n");
        text.append("\n");
    }

    /**
     * MIPS code for READ token to read INTEGER or LOGICAL data user input
     */
    public void readIntData(int sp){
        System.out.println("readIntData");
        text.append("li $v0 "+read_int);
        text.append("\n");
        text.append("syscall");
        text.append("\n");
        text.append("sw $v0 "+sp+"($sp)");
        text.append("\n");
    }

    /**
     * MIPS code for READ token to read STRING data user input
     */
    public void readStringData(int sp){
        System.out.println("readStringData");
        text.append("li $v0 "+read_string);
        text.append("\n");
        text.append("syscall");
        text.append("\n");
        text.append("sw $v0 "+sp+"($sp)");
    }

    /**
     * MIPS code for initialize variable with datatype
     */
    public void InitializeIntData(int sp,int value){
        System.out.println("InitializeIntData");
        text.append("li $t0 "+value);
        text.append("\n");
        text.append("sw $t0 "+sp+"($sp)");
        text.append("\n");
    }

    /**
     * MIPS code for READ token to read INTEGER data user input
     */
    public void printStringFromSP(String lable){
        System.out.println("printStringFromSP");
        text.append("li $v0 4");
        text.append("\n");
        text.append("la $a0 "+lable);
        text.append("\n");
    }

    /**
     * MIPS code for print INTEGER data in WRITE and WRITELN token
     */
    public void printIntFromSP(int number) {
        System.out.println("printIntFromSP");
        text.append("lw $t1 "+number+"($sp)");
        text.append("\n");
        text.append("move $a0 $t1");
        text.append("\n");
        text.append("li $v0 "+print_int);
        text.append("\n");
        text.append("syscall");
        text.append("\n");
    }

    /**
     * MIPS code for Additional Operator +, -, OR
     */
    public void addFromSP(int resultsp,int sp1,int sp2, String operator){
        text.append("lw $t1 "+sp1+"($sp)");
        text.append("\n");
        text.append("lw $t2 "+sp2+"($sp)");
        text.append("\n");
        text.append(operator+" $t0, $t1, $t2");
        text.append("\n");
        text.append("sw $t0  "+resultsp+"($sp)");
        text.append("\n");
    }

    /**
     * MIPS code for Multiplication Operator AND
     */
    public void andFromSP(int resultsp,int sp1,int sp2){
        text.append("lw $t1 "+sp1+"($sp)");
        text.append("\n");
        text.append("lw $t2 "+sp2+"($sp)");
        text.append("\n");
        text.append("and $t0, $t1, $t2");
        text.append("\n");
        text.append("sw $t0  "+resultsp+"($sp)");
        text.append("\n");
    }

    /**
     * MIPS code for Multiplication Operator *
     */
    public void MultiplicationFromSP(int resultsp,int sp1,int sp2){
        text.append("lw $t1 "+sp1+"($sp)");
        text.append("\n");
        text.append("lw $t2 "+sp2+"($sp)");
        text.append("\n");
        text.append("mult $t1, $t2");
        text.append("\n");
        text.append("mflo $t3");
        text.append("\n");
        text.append("sw $t3  "+resultsp+"($sp)");
        text.append("\n");
    }

    /**
     * MIPS code for Multiplication Operator /, DIV
     */
    public void divWithQuotientFromSP(int resultsp,int sp1,int sp2){
        text.append("lw $t1 "+sp1+"($sp)");
        text.append("\n");
        text.append("lw $t2 "+sp2+"($sp)");
        text.append("\n");
        text.append("div $t1, $t2");
        text.append("\n");
        text.append("mflo $t3");
        text.append("\n");
        text.append("sw $t3  "+resultsp+"($sp)");
        text.append("\n");
    }

    /**
     * MIPS code for Multiplication Operator REM - remainder
     */
    public void divWithReminderFromSP(int resultsp,int sp1,int sp2){
        text.append("lw $t1 "+sp1+"($sp)");
        text.append("\n");
        text.append("lw $t2 "+sp2+"($sp)");
        text.append("\n");
        text.append("div $t1, $t2");
        text.append("\n");
        text.append("mfhi $t3");
        text.append("\n");
        text.append("sw $t3  "+resultsp+"($sp)");
        text.append("\n");
    }

    /**
     * MIPS code for WhileloopBegin
     */
    public void whileLoop(int loopNo){
        text.append("loop"+loopNo+":");
        text.append("\n");
    }

    /**
     * MIPS code for WhileloopEnd
     */
    public void whileLoopEndLoop(int loopNo, int exitLoopNo){
        text.append("j loop"+loopNo);
        text.append("\n");
        text.append("ConditionLableW"+exitLoopNo+":");
        text.append("\n");
    }

    /**
     * MIPS code to generate If Condition Lable
     */
    public void operatorCondition(String operator, String conditionName){
        text.append(operator+" $t1, $t2 ,"+conditionName);
        text.append("\n");
    }

    /**
     * MIPS code to generate load two value into $t1 and $t2
     */
    public void readConditionDataFromSp(int sp1, int sp2){
        text.append("lw $t1 "+sp1+"($sp)");
        text.append("\n");
        text.append("lw $t2 "+sp2+"($sp)");
        text.append("\n");
    }

    /**
     * MIPS code to generate goto Condition Lable for While Loop
     */
    public void gotoCondition(String conditionName){
        text.append("\n");
        text.append(conditionName+":");
        text.append("\n");
    }

    /**
     * MIPS code return as string
     */
    public String returnCodeGen(){
        StringBuffer temp = new StringBuffer();
        postLog();
        temp.append(text);
        temp.append(data);
        return temp.toString();
    }
}

