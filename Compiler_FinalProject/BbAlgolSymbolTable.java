package Compiler_FinalProject;

import java.util.*;
import java.util.HashMap;
import java.util.Stack;


/**
 * Created by Parshwa on 2/19/17  for cs 4110.
 * Course - Compiler Design - B-Algol Symbol Table Program.
 * Language is Java and Program is to implement Algol Compiler Program.
 */

/**
 * This is a program to create symbol table in which Stack is maintain for symbol table.
 * And for every new scope HashMap push onto Stack as Stack object.
 * And when scope end pop HashMap object from top of Stack.
 * In HashMap, symbols store as key and IdentifierAttribute as its value in HashMap.
 * IdentifierAttribute is object which store Identifier type, its offset and its scope number.
 */

public class BbAlgolSymbolTable {
    ArrayList<HashMap> scopeWiseIdentifiers;

    //I choose stack because I want to perform operation on the last item inserted. And stack do operation on the top of stack.
    Stack<HashMap> st;

    //I use HashMap because it is easy to perform find routine. To find specific key from scope is easy by using HashMap.
    HashMap<String, Compiler_FinalProject.IdentifierAttribute> hashMapObj;

    //Global variable, scopeNumber is used to maintain scope or block number.
    static int scopeNumber = -1;

    public BbAlgolSymbolTable() {
        scopeWiseIdentifiers = new ArrayList<>();
        st = new Stack<>();
    }


    /**
     * Push new HashMap and increment scopeNumber by one every time new scope begin.
     */
    void openScope() {
        hashMapObj = new HashMap<>();
        st.push(hashMapObj);
        scopeNumber++;
    }


    /**
     * Stqck pop top of HashMap when scope end and store it in LinkedList.
     */
    void exitScope() {
        HashMap<String, Compiler_FinalProject.IdentifierAttribute> hm = st.pop();
        scopeWiseIdentifiers.add(hm);
    }


    /**
     * I used Linked List to store HashMap pop by stack at the end od scope.
     * Reason is just show(print) the output that program is working corrently or not and for testing program.
     */

    void showScopeList() {
        System.out.print("\n\nAll Identifier Scopewice List with its attribute values.");
        for(int i=0; i<scopeWiseIdentifiers.size(); i++) {
            HashMap<String, Compiler_FinalProject.IdentifierAttribute> hm = scopeWiseIdentifiers.get(i);
            HashMap<String, Compiler_FinalProject.IdentifierAttribute> hashMapObj = scopeWiseIdentifiers.get(i);
            if (!hm.isEmpty()) {
                int blockNo;
                Map.Entry<String, Compiler_FinalProject.IdentifierAttribute> entry = hashMapObj.entrySet().iterator().next();
                Compiler_FinalProject.IdentifierAttribute idenAttriObj = entry.getValue();
                blockNo = idenAttriObj.getScopeNo();

                Set set = hm.entrySet();
                Iterator itr1 = set.iterator();
                System.out.println("\n-------------------------------------------------------------------------");
                System.out.println("Scope:" + blockNo);
                System.out.println("-------------------------------------------------------------------------");
                while (itr1.hasNext()) {
                    Map.Entry h = (Map.Entry) itr1.next();
                    System.out.print("\n" + h.getKey() + "\t");
                    Compiler_FinalProject.IdentifierAttribute identiAttriObj = (Compiler_FinalProject.IdentifierAttribute) h.getValue();
                    System.out.println(identiAttriObj.getType() + "\t" + identiAttriObj.getOffSet() + "\t" + identiAttriObj.getScopeNo());
                }
                System.out.println("=========================================================================\n");
            }
        }
    }



    /**
     * Check whether symbol find into top of stack's object HashMap.
     * Return true if symbol find into HashMap means into current scope, otherwise false.
     * @param identifier Pass symbol as String argument
     * @return boolean value
     */
    boolean find(String identifier) {
        boolean b = false;
        if(!st.empty()) {
            HashMap<String, Compiler_FinalProject.IdentifierAttribute> hm = st.peek();
            if(hm.containsKey(identifier)) {
                b = true;
            }
        }
        return b;
    }


    /**
     * Take top of HashMap from stack.
     * Insert symbol into HashMap as HashMap key and symbol type, offset and scope number combined into one object IdentifierAttribute as HashMap value.
     * @param identifierTok pass it as string type
     * @param tokType Pass it as character type
     * @param tokOffSet Pass it as int type
     */
    void insert(String identifierTok, char tokType, int tokOffSet) {
        int blockVal;
        if(!st.empty()) {
            HashMap<String, Compiler_FinalProject.IdentifierAttribute> hm = st.peek();

            if(!hm.isEmpty()) {
                Map.Entry<String, Compiler_FinalProject.IdentifierAttribute> entry = hm.entrySet().iterator().next();
                Compiler_FinalProject.IdentifierAttribute idenAttriObj = entry.getValue();
                blockVal = idenAttriObj.getScopeNo();
            } else {
                blockVal = scopeNumber;
            }

            Compiler_FinalProject.IdentifierAttribute idenAttributeObj = new Compiler_FinalProject.IdentifierAttribute(tokType, tokOffSet, blockVal);
            hm.put(identifierTok, idenAttributeObj);
        }
    }


    /**
     * Find symbol into HashMap means find symbol from most recent scope.
     * If symbol is not find in any scope, it return null.
     * @param identifierTok Pass symbol as String argument
     * @return IdentifierAttribute object
     */
    Compiler_FinalProject.IdentifierAttribute findAll(String identifierTok) {
        Compiler_FinalProject.IdentifierAttribute iaobj = null;
        Iterator itr = st.iterator();
        while (itr.hasNext()) {
            HashMap<String, Compiler_FinalProject.IdentifierAttribute> h = (HashMap) itr.next();
            if(h.containsKey(identifierTok)) {
                iaobj = h.get(identifierTok);
                break;
            }
        }
        return iaobj;
    }
}


/**
 * Declare class for used as value object in HashMap.
 * Which store symbol type, offset and scope number combined into as object IdentifierAttribute as HashMap value.
 * And for all of them we made its getter method.
 */
class IdentifierAttribute {
    final char  type;
    final int offSet;
    final int scopeNo;

    IdentifierAttribute(char type, int offSet,  int scopeNo) {
        this.type = type;
        this.offSet = offSet;
        this.scopeNo = scopeNo;
    }

    public char getType() {
        return type;
    }

    public int getScopeNo() {
        return scopeNo;
    }

    public int getOffSet() {
        return offSet;
    }
}