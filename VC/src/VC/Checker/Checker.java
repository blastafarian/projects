/**
 * Checker.java   
 *
 * Checks whether the source program, represented by its AST, 
 * satisfies the language's scope rules and type rules.
 * Also decorates the AST as follows:
 * (1) Each applied occurrence of an identifier is linked to
 * the corresponding declaration of that identifier.
 * (2) Each expression and variable is decorated by its type.
 *
 * Brendon Kwan z3100204
 * Oct 2006
 **/

package VC.Checker;

import VC.ASTs.*;
import VC.Scanner.SourcePosition;
import VC.ErrorReporter;
import VC.StdEnvironment;

public final class Checker implements Visitor {

    private String errMesg[] = {
        "*0: main function is missing",                            
        "*1: return type of main is not int",                    

        // defined occurrences of identifiers
        // for global, local and parameters
        "*2: identifier redeclared",                             
        "*3: identifier declared void",                         
        "*4: identifier declared void[]",                      

        // applied occurrences of identifiers
        "*5: identifier undeclared",                          

        // assignments
        "*6: incompatible type for =",                       
        "*7: invalid lvalue in assignment",                 

        // types for expressions 
        "*8: incompatible type for return",                
        "*9: incompatible type for this binary operator", 
        "*10: incompatible type for this unary operator",

        // scalars
        "*11: attempt to use an array/fuction as a scalar", 

        // arrays
        "*12: attempt to use a scalar/function as an array",
        "*13: wrong type for element in array initialiser",
        "*14: invalid initialiser: array initialiser for scalar",   
        "*15: invalid initialiser: scalar initialiser for array",  
        "*16: excess elements in array initialiser",              
        "*17: array subscript is not an integer",                
        "*18: array size missing",                              

        // functions
        "*19: attempt to reference a scalar/array as a function",

        // conditional expressions in if, for and while
        "*20: if conditional is not boolean",                    
        "*21: for conditional is not boolean",                  
        "*22: while conditional is not boolean",               

        // break and continue
        "*23: break must be in a while/for",                  
        "*24: continue must be in a while/for",              

        // parameters 
        "*25: too many actual parameters",                  
        "*26: too few actual parameters",                  
        "*27: wrong type for actual parameter",           

        // reserved for errors that I may have missed (J. Xue)
        "*28: misc 1",
        "*29: misc 2",

        // the following two checks are optional 
        "*30: statement(s) not reached",     
        "*31: missing return statement",    
    };


    private SymbolTable idTable;
    private static SourcePosition dummyPos = new SourcePosition();
    private ErrorReporter reporter;

    // ------------------------------
    // My member variables 
    // ------------------------------

    // parsedMain - true iff the main function has been parsed
    private boolean parsedMain = false; 

    // loopDepth - how many loops are we currently in? We count both 'while' and 'for' loops. 
    private int loopDepth = 0; 

    // ------------------------------
    // Constructor 
    // ------------------------------

    public Checker (ErrorReporter reporter) {

        this.reporter = reporter;
        this.idTable = new SymbolTable ();
        establishStdEnvironment();

    }

    public void check(AST ast) {
        ast.visit(this, null);
    }


    // ------------------------------
    // 1. Programs
    // ------------------------------

    public Object visitProgram(Program ast, Object o) {
        ast.FL.visit(this, null);

        // Check error 0: main function is missing
        if (! parsedMain) {
            reporter.reportError(errMesg[0], "", dummyPos);
        }

        return null;
    }

    // ------------------------------
    // 2. Lists for denoting the null reference
    // ------------------------------

    public Object visitEmptyDeclList(EmptyDeclList ast, Object o) {
        return null;
    }

    public Object visitEmptyStmtList(EmptyStmtList ast, Object o) {
        return null;
    }

    // Second arg is either null or a TypeAndLength object
    
    public Object visitEmptyExprList(EmptyExprList ast, Object o) {

        if (o != null) {
            TypeAndLength typeAndLength = (TypeAndLength) o;

            // Check for error 16: excess elements in array initialiser
            if (typeAndLength.exceedsArraySize()) {
                reporter.reportError(errMesg[16], "", ast.position);
            }
        }

        return null;
    }

    public Object visitEmptyParaList(EmptyParaList ast, Object o) {
        return null;
    }

    // Second argument is a List containing the formal parameters
    // that remain to be checked.
    public Object visitEmptyArgList(EmptyArgList ast, Object o) {

        // Check for error 26: too few actual parameters
        //
        // Error 26 occurs 
        // iff 
        // the list of actual parameters is empty 
        //    (which is true if we are at this function)
        // but the list of formal parameters is not empty
        //
        List formal = (List) o;
        if (! formal.isEmptyParaList()) {  
            reporter.reportError(errMesg[26], "", ast.position);
        }

        return null;
    }

    // ------------------------------
    // 3. Declarations
    // ------------------------------

    // Always returns null. Does not use the given object.

    public Object visitDeclList(DeclList ast, Object o) {
        ast.D.visit(this, null);
        ast.DL.visit(this, null);
        return null;
    }

    public Object visitFuncDecl(FuncDecl ast, Object o) {

        // Your code goes here
        ast.T.visit(this, ast);
        ast.I.visit(this, ast);

        // HINT
        // Pass ast as the 2nd argument (as done below) so that the
        // formal parameters of the function an be extracted from ast when the
        // function body is later visited
        declareIdent(ast.I, ast); 

        idTable.openScope();

        ast.PL.visit(this, ast);
        ast.S.visit(this, ast);

        // My code ...
        if (isMainFunction(ast)) {
            parsedMain = true;
        } 

        // Check for error 1: return type of main is not int
        if (ast.I.spelling.equals("main") && ! ast.T.isIntType()) {
            reporter.reportError(errMesg[1], "", ast.I.position);
        }

        idTable.closeScope();
        return null;
    }

    public Object visitGlobalVarDecl(GlobalVarDecl ast, Object o) {
        ast.T.visit(this, ast);
        ast.I.visit(this, ast);

        declareIdent(ast.I, ast);

        // Pass the AST's type to the next visitor method
        ast.E.visit(this, ast.T); 
        
        if (ast.T.isArrayType()) {
            checkError4((ArrayType) ast.T, ast.I, ast.E);
            checkError15((ArrayType) ast.T, ast.I, ast.E);
            checkError18((ArrayType) ast.T, ast.I, ast.E);
        } else {
            checkError3(ast.T, ast.I, ast.E);
        }
        
        return null;
    }

    public Object visitLocalVarDecl(LocalVarDecl ast, Object o) {
        ast.T.visit(this, ast);
        ast.I.visit(this, ast);

        declareIdent(ast.I, ast);

        // Pass the AST's type to the next visitor method
        ast.E.visit(this, ast.T);
        
        if (ast.T.isArrayType()) {
            checkError4((ArrayType) ast.T, ast.I, ast.E);
            checkError15((ArrayType) ast.T, ast.I, ast.E);
            checkError18((ArrayType) ast.T, ast.I, ast.E);
        } else {
            checkError3(ast.T, ast.I, ast.E);
        }

        return null;
    }

    // ----------------- Helpers for 3. Declarations ------------------
    
    // declareIdent(ident, decl)
    //
    // Adds this identifier and declaration to the symbol table
    // iff
    // This identifer has not been declared at the current scope level

    private void declareIdent(Ident ident, Decl decl) {
        IdEntry entry = idTable.retrieveOneLevel(ident.spelling);

        // Check for error 2: identifier redeclared
        if (entry != null) {
            reporter.reportError(errMesg[2] + ": %", ident.spelling, ident.position);
            return;
        } 

        idTable.insert(ident.spelling, decl);
    }

    // Returns true 
    // iff 
    // This is the declaration of the main function 
    //     int main() {
    //         ...    
    //     }
    // iff 
    // - its identifier is 'main', 
    // - its return type is 'int',
    // - and its parameter list is empty    

    private boolean isMainFunction(FuncDecl func) {
        return func.I.spelling.equals("main") && 
            func.T.isIntType() &&
            func.PL.isEmptyParaList();
    }

    // Check for error 3: identifier declared void
    private void checkError3(Type type, Ident ident, Expr expr) {
        if (type.isVoidType()) {
            reporter.reportError(errMesg[3] + ": %", ident.spelling, ident.position);
        }
    }
    
    // Check for error 4: identifier declared void[]
    private void checkError4(ArrayType arrayType, Ident ident, Expr expr) {
        if (arrayType.T.isVoidType()) {
            reporter.reportError(errMesg[4] + ": %", ident.spelling, ident.position);
        }
    }

    // Check for error 15: invalid initialiser: scalar initialiser for array
    // Error 15 occurs
    // iff
    // The initialiser is neither an InitExpr nor an EmptyExpr 
    private void checkError15(ArrayType arrayType, Ident ident, Expr initialiser) {
        if (! (initialiser instanceof InitExpr) &&
            ! (initialiser instanceof EmptyExpr)) {
            reporter.reportError(errMesg[15], "", initialiser.position);
        }
    }
    
    // Check for error 18: array size missing
    //
    // Error 18 occurs 
    // iff 
    // The length of the array is not specified in the [] 
    // and 
    // The array does not have an initialiser list
    //
    // Note that we don't have to check if array has an 
    // initialiser list that is empty. 
    //    This is because the VC context-free grammar assures us that 
    // in a grammatically correct VC program,
    // every initialiser list has at least one element.

    private void checkError18(ArrayType arrayType, Ident ident, Expr initialiser) {
        if (arrayType.E.isEmptyExpr()
            && 
            ! (initialiser instanceof InitExpr) ) {

            reporter.reportError(errMesg[18] + ": %", ident.spelling, ident.position);
        }
            
    }

    // ------------------------------
    // 4. Statements
    // ------------------------------

    public Object visitStmtList(StmtList ast, Object o) {
        ast.S.visit(this, o);

        // Check for error 30: statement(s) not reached
        if ((ast.S instanceof ReturnStmt ||
             ast.S instanceof BreakStmt || 
             ast.S instanceof ContinueStmt) && 
            ast.SL instanceof StmtList) {
            reporter.reportError(errMesg[30], "", ast.SL.position);
        }

        ast.SL.visit(this, o);
        return null;
    }

    public Object visitIfStmt(IfStmt ast, Object o) {
        ast.E.visit(this, o);

        // Check for error 20: if conditional is not boolean 
        if (! ast.E.type.equals(StdEnvironment.booleanType)) {
            reporter.reportError(errMesg[20] + ". Instead it is %.", ast.E.type.toString() , ast.E.position);
        }
        // Check for error 22: while conditional is not boolean
         
        ast.S1.visit(this, o);
        ast.S2.visit(this, o);
        return null;
    }

    public Object visitWhileStmt(WhileStmt ast, Object o) {
        ast.E.visit(this, o);

        // Check for error 22: while conditional is not boolean
        if (! ast.E.type.equals(StdEnvironment.booleanType)) {
            reporter.reportError(errMesg[22] + ". Instead it is %.", ast.E.type.toString() , ast.E.position);
        }

        loopDepth++;
        ast.S.visit(this, o);
        loopDepth--;

        return null;
    }

    public Object visitForStmt(ForStmt ast, Object o) {
        ast.E1.visit(this, o);
        ast.E2.visit(this, o);

        // Check for error 21: for conditional is not boolean
        // 
        // A for statement is different from an if or a while statement
        // because its conditional is optional. Thus, error 21 occurs
        // iff
        // the for conditional is not empty and is not boolean
        //
        if (! ast.E2.isEmptyExpr() && 
            ! ast.E2.type.equals(StdEnvironment.booleanType)) {
          
            reporter.reportError(errMesg[21] + ". Instead it is %.", ast.E2.type.toString() , ast.E2.position);
        }

        ast.E3.visit(this, o);
        loopDepth++;
        ast.S.visit(this, o);
        loopDepth--;
        return null;
    }

    public Object visitBreakStmt(BreakStmt ast, Object o) {
        // Check for error 23: break must be in a while/for
        if (loopDepth < 1) {
            reporter.reportError(errMesg[23], "", ast.position); 
        }
        
        return null;
    }

    public Object visitContinueStmt(ContinueStmt ast, Object o) {
        // Check for error 24: continue must be in a while/for
        if (loopDepth < 1) {
            reporter.reportError(errMesg[24], "", ast.position); 
        }

        return null;
    }

    // Second arg is the FuncDecl object of the function
    // in which this return statement resides.
    public Object visitReturnStmt(ReturnStmt ast, Object o) {
        ast.E.visit(this, o);

        FuncDecl func = (FuncDecl) o;
        
        // Check for error 8: incompatible type for return
        if (! func.T.assignable(ast.E.type)) {
            reporter.reportError(errMesg[8] + ": expected " + func.T + " got " + ast.E.type, "", ast.position);
            
        // Type-coerce the return expression if necessary    
        } else if (func.T.isFloatType() && ast.E.type.isIntType()) {
            Operator i2f = new Operator("i2f", dummyPos);
            ast.E = new UnaryExpr(i2f, ast.E, dummyPos);
            ast.E.type = StdEnvironment.floatType;
        }
        
        return null;
    }

    public Object visitCompoundStmt(CompoundStmt ast, Object o) {
        boolean needNewScope = ast.parent instanceof FuncDecl;
        if (needNewScope) {
            idTable.openScope();
        }

        // Your code goes here
        ast.DL.visit(this, o);
        ast.SL.visit(this, o);

        if (needNewScope) {
            idTable.closeScope();
        }
        return null;
    }

    public Object visitExprStmt(ExprStmt ast, Object o) {
        ast.E.visit(this, o);
        return null;
    }

    public Object visitEmptyCompStmt(EmptyCompStmt ast, Object o) {
        return null;
    }

    public Object visitEmptyStmt(EmptyStmt ast, Object o) {
        return null;
    }

    // ------------------------------
    // 5. Expressions
    // ------------------------------

    // Returns the Type denoting the type of the expression. 
    //
    // None of these methods (except visitInitExpr(ast, o))
    // uses the given object o.
    //
    // But visitInitExpr(ast, o) expects the given object o 
    // to be the ArrayType of the array being initialised.


    public Object visitIntExpr(IntExpr ast, Object o) {
        ast.IL.visit(this, o);
        ast.type = StdEnvironment.intType;
        return ast.type;
    }

    public Object visitFloatExpr(FloatExpr ast, Object o) {
        ast.FL.visit(this, o);
        ast.type = StdEnvironment.floatType;
        return ast.type;
    }

    public Object visitBooleanExpr(BooleanExpr ast, Object o) {
        ast.BL.visit(this, o);
        ast.type = StdEnvironment.booleanType;
        return ast.type;
    }

    public Object visitStringExpr(StringExpr ast, Object o) {
        ast.SL.visit(this, o);
        ast.type = StdEnvironment.stringType;
        return ast.type;
    }

    public Object visitUnaryExpr(UnaryExpr ast, Object o) {
        ast.O.visit(this, o);
        ast.E.visit(this, o);

        // Check for error 10: incompatible type for this unary operator
        //
        // There are three unary operators: +, -, !
        // + operates only on int or float 
        // - operates only on int or float
        // ! operates only on boolean 
        String op = ast.O.spelling;
        if (op.equals("+") || op.equals("-")) {
            if (! ast.E.type.isIntType() && 
                ! ast.E.type.isFloatType()) {
                reporter.reportError(errMesg[10], "", ast.O.position); 
            }
        } else if (op.equals("!")) {
            if (! ast.E.type.isBooleanType()) {
                reporter.reportError(errMesg[10], "", ast.O.position);
            }
        }

        // Replace the overloaded unary expression with its non-overloaded equivalent
        if (ast.O.spelling.equals("!")) {
            ast.O.spelling = "i" + ast.O.spelling;
        } else if (ast.O.spelling.equals("+") || ast.O.spelling.equals("-")) {
            if (ast.E.type.isIntType()) {
                ast.O.spelling = "i" + ast.O.spelling;
            } else {
                ast.O.spelling = "f" + ast.O.spelling;
            }
        } 
        
        // The type of a unary expression is 
        // equal to the type of its subexpression
        ast.type = ast.E.type;

        return ast.type; 
    }

    public Object visitBinaryExpr(BinaryExpr ast, Object o) {
        ast.E1.visit(this, o);
        ast.E2.visit(this, o);
        ast.O.visit(this, o);

        // There are 12 binary operators, 
        // and I have partitioned them into four subsets:
        //
        // Arithmetic: +, -, *, /             
        // Inequality: >, <=, >, >=  
        // Equality:   ==, != 
        // Logic:      &&, ||
        //

        // Check for error 9: incompatible type for this binary operator
        if (! isValidBinaryExpr(ast.E1.type, ast.E2.type, ast.O)) {
            reporter.reportError(errMesg[9], "", ast.position);
            ast.type = StdEnvironment.errorType;
            return ast.type;

        // Otherwise the binary operator has valid operands    
        } else {

            // Set the type of the binary expression
            if (isArithmeticOp(ast.O)) {
                if (ast.E1.type.isFloatType() || ast.E2.type.isFloatType()) {
                    ast.type = StdEnvironment.floatType; 
                } else {
                    ast.type = StdEnvironment.intType;
                }
            } else if (isEqualityOp(ast.O) || isInequalityOp(ast.O) || isLogicOp(ast.O)) {
                ast.type = StdEnvironment.booleanType;
            } else {
                // Should never get here as each operator
                // is either an arithmetic, comparison or logic operator
                ast.type = StdEnvironment.errorType;
            }

            // Type-coerce the operands if necessary
            if (ast.E1.type.isIntType() && ast.E2.type.isFloatType()) {
                Operator i2f = new Operator("i2f", dummyPos);
                ast.E1 = new UnaryExpr(i2f, ast.E1, dummyPos);
                ast.E1.type = StdEnvironment.floatType;
            } else if (ast.E1.type.isFloatType() && ast.E2.type.isIntType()) {
                Operator i2f = new Operator("i2f", dummyPos);
                ast.E2 = new UnaryExpr(i2f, ast.E2, dummyPos);
                ast.E2.type = StdEnvironment.floatType;
            }

            // Replace each overloaded operator with its non-overloaded equivalent
            if (isLogicOp(ast.O))  {
                ast.O.spelling = "i" + ast.O.spelling;
            } else if (isArithmeticOp(ast.O) || isEqualityOp(ast.O) || isInequalityOp(ast.O)) {
                if (ast.E1.type.isFloatType() || ast.E2.type.isFloatType()) {
                    ast.O.spelling = "f" + ast.O.spelling;
                } else {
                    ast.O.spelling = "i" + ast.O.spelling;
                }
            }

            return ast.type;
        }
    }

    // Returns true iff the types of the operands
    // of the binary expression are correct
    private boolean isValidBinaryExpr(Type left, Type right, Operator op) {

        boolean leftIsNumber = left.isIntType() || left.isFloatType();
        boolean rightIsNumber = right.isIntType() || right.isFloatType();
        boolean bothBoolean = left.isBooleanType() && right.isBooleanType();
        if (isArithmeticOp(op) || isInequalityOp(op)) {
            return leftIsNumber && rightIsNumber;
        } else if (isEqualityOp(op)) {
            return bothBoolean || (leftIsNumber && rightIsNumber);
        } else { // Must be a logic operator || or &&
            return bothBoolean;
        }
    }
    
    private boolean isArithmeticOp(Operator op) {
        return op.spelling.equals("+") || 
            op.spelling.equals("-") ||
            op.spelling.equals("*") ||
            op.spelling.equals("/");
    }

    private boolean isInequalityOp(Operator op) {
        return op.spelling.equals(">") ||
            op.spelling.equals(">=") ||
            op.spelling.equals("<") ||
            op.spelling.equals("<=");
    }

    private boolean isEqualityOp(Operator op) {
        return op.spelling.equals("==") || 
            op.spelling.equals("!=");
    }

    private boolean isLogicOp(Operator op) {
        return op.spelling.equals("||") || 
            op.spelling.equals("&&");
    }
    
    // visitInitExpr(ast, o) expects o to be the Type of the 
    // identifier being initialised.

    public Object visitInitExpr(InitExpr ast, Object o) {

        Type type = (Type) o;

        // Check for error 14: invalid initialiser: array initialiser for scalar
        // Error 14 occurs 
        // iff
        // The type is not an array 
        if (! type.isArrayType()) {
            reporter.reportError(errMesg[14], "", ast.position);
            ast.type = StdEnvironment.errorType;
            return ast.type;
            
        // Otherwise all is good; we have an array initialiser for an array    
        } else {

            ArrayType arrayType = (ArrayType) o;

            // Prepare a TypeAndLength object and give it as the second 
            // argument to the ExprList visitor
            //
            // The ExprList visitor will use it 
            // to check whether the initialiser list is valid. 

            TypeAndLength typeAndLength = new TypeAndLength(arrayType);
            ast.IL.visit(this, typeAndLength);

            // Store the size of the array in the array type
            String sizeStr = new Integer(typeAndLength.getArraySize()).toString();
            IntLiteral sizeIL = new IntLiteral(sizeStr, dummyPos);
            arrayType.E = new IntExpr(sizeIL, dummyPos);
            
            // The type of an array initialiser is the array type 
            ast.type = arrayType;

            return ast.type;
        }
    }

    // TypeAndLength
    //
    // Just a private data structure that I use when 
    // checking the type and number of elements in an array initialiser

    private class TypeAndLength {
        private ArrayType arrayType;
        private int numInit;
        
        TypeAndLength(ArrayType a) {
            this.arrayType = a;
            this.numInit = 0;
        }
        
        public boolean exceedsArraySize() {
            // arrayType.E is guaranteed to either be empty
            // or be an IntExpr
            if (arrayType.E.isEmptyExpr()) {
                return false;
            } else {
                IntExpr ie = (IntExpr) arrayType.E;
                int arraySize = Integer.parseInt(ie.IL.spelling);
                return numInit > arraySize;
            }
        }

        public boolean isCorrectType(Type candidate) {
            return arrayType.T.assignable(candidate);
        }

        public void oneMoreInit() {
            numInit++;
        }

        public Type getBaseType() {
            return arrayType.T;
        }

        public int getArraySize() {
            return numInit;
        }
    }

    // Second arg is either null or a TypeAndLength object

    public Object visitExprList(ExprList ast, Object o) {
        
        if (o != null) {

            TypeAndLength typeAndLength = (TypeAndLength) o;
            typeAndLength.oneMoreInit();

            ast.E.visit(this, null);

            // Check for error 13: wrong type for element in array initialiser
            if (! typeAndLength.isCorrectType(ast.E.type)) {
                reporter.reportError(errMesg[13] + ": expected " + typeAndLength.getBaseType() + ", got %.", ast.E.type.toString(), ast.E.position);
                ast.EL.visit(this, typeAndLength);

                return StdEnvironment.errorType;
            } else {
                    System.out.println("array's type is " + typeAndLength.arrayType.T.isFloatType() +" my type is "+  ast.E.type.isIntType());
                // An integer value inside a float array should be typecast to float
                if (typeAndLength.arrayType.T.isFloatType() && ast.E.type.isIntType()) {
                    Operator i2f = new Operator("i2f", dummyPos);
                    ast.E = new UnaryExpr(i2f, ast.E, dummyPos);
                    ast.E.type = StdEnvironment.floatType;
                }

                ast.EL.visit(this, typeAndLength);
                return ast.E.type;
            }

        } else {
            ast.E.visit(this, o);
            ast.EL.visit(this, o);
            return ast.E.type; 
        }
    }

    public Object visitArrayExpr(ArrayExpr ast, Object o) {

        // ast.V.visit(this, useScalar) will call 
        // visitSimpleVar(ast.V, useScalar),
        // where useScalar is true iff we wish to use the SimpleVar
        // as a scalar value (but we do not; instead we wish to use it as 
        // an array value)
        boolean useScalar = false;
        ast.V.visit(this, useScalar);
        ast.E.visit(this, o);

        // Check for error 12: attempt to use a scalar/function as an array
        if (! ast.V.type.isArrayType()) {
            reporter.reportError(errMesg[12], "", ast.V.position);
            ast.type = StdEnvironment.errorType;
            return ast.type;

        // Check for error 17: array subscript is not an integer
        } else if (! ast.E.type.isIntType()) {
            reporter.reportError(errMesg[17] + ". Instead it is %.", ast.E.type.toString(), ast.E.position); 
            ast.type = StdEnvironment.errorType;
            return ast.type;
            
        // Else there are no problems. The type of an arrayExpr 
        // is then equal to the type of the elements of the array
        } else {

            ast.type = ((ArrayType) ast.V.type).T;
            return ast.type;
        }
    }

    // The second argument of this method is either 
    // a Boolean object or something else.
    // If it is a Boolean object, then it is true iff 
    // the formal parameter is not an array
    public Object visitVarExpr(VarExpr ast, Object o) {

        boolean useScalar;
        if (o instanceof Boolean) {
            useScalar = (Boolean) o;
        } else {
            useScalar = true;
        }
        
        ast.type = (Type) ast.V.visit(this, useScalar);
        return ast.type;
    }

    public Object visitCallExpr(CallExpr ast, Object o) {
        
        // Search for the function being called
        Decl decl = idTable.retrieve(ast.I.spelling);

        // Check for error 5: identifier undeclared
        if (decl == null) {
            reporter.reportError(errMesg[5] + ": %", ast.I.spelling, ast.I.position);
            ast.type = StdEnvironment.errorType;
            return ast.type;

        // Check for error 19: attempt to reference a scalar/array as a function
        } else if (! decl.isFuncDecl()) {
            reporter.reportError(errMesg[19] + ": %", ast.I.spelling, ast.I.position);
            ast.type = StdEnvironment.errorType;
            return ast.type;

        // Else the call expression is valid 
        } else {
            FuncDecl func = (FuncDecl) decl;

            ast.I.visit(this, o);

            // We want visitArgList(x, y) to check the actual parameters 
            // against the formal parameters 
            //
            // To make this possible, we will pass the formal parameters 
            // as the second argument to visit(x, y). 
            //
            // If the parse tree has been constructed correctly, visit(x, y) 
            // will call visitArgList(x, y), and visitArgList(x, y) will 
            // do the checking for us.
            ast.AL.visit(this, func.PL);

            // The type of the call expression 
            // is the return type of the function being called
            ast.type = func.T;
            return ast.type;
        }

    }

    public Object visitAssignExpr(AssignExpr ast, Object o) {
        ast.E1.visit(this, o);
        ast.E2.visit(this, o);

        // Check for error 6: incompatible type for =
        //
        // Error 6 occurs 
        // iff 
        // ast.E2.type is not assignable to ast.E1.type
        // or 
        // (   
        //      ast.E1.type is arrayType 
        //      but ast.E2.type is not assignable to ast.E1's base type
        // )
        if (! ast.E1.type.assignable(ast.E2.type) ||
            (
                ast.E1.type.isArrayType() &&
                ! ast.E1.type.assignable(ast.E2.type) 
            )
            ) {
            reporter.reportError(errMesg[6], "", ast.position);
        }
        
        // Check for error 7: invalid lvalue in assignment
        //
        // Error 7 occurs iff ast.E1 has an "error type" or 
        // is neither an instance of VarExpr 
        // nor an instance of ArrayExpr

        if (ast.E1.type.isErrorType() ||
            (
             ! (ast.E1 instanceof VarExpr) &&
             ! (ast.E1 instanceof ArrayExpr)
            )
            ) { 
            reporter.reportError(errMesg[7], "", ast.position);
        }

        // Type-coerce the right hand side from an int to a float if necessary
        if (ast.E1.type.isFloatType() && ast.E2.type.isIntType()) {
            Operator i2f = new Operator("i2f", dummyPos);
            ast.E2 = new UnaryExpr(i2f, ast.E2, dummyPos);
            ast.E2.type = StdEnvironment.floatType;
        }

        ast.type = ast.E1.type;
        return ast.E1.type;
    }

    public Object visitEmptyExpr(EmptyExpr ast, Object o) {
        ast.type = StdEnvironment.voidType;
        return ast.type;
    }


    // ------------------------------
    // 6. Literals, Identifiers and Operators
    // ------------------------------

    public Object visitIntLiteral(IntLiteral IL, Object o) {
        return StdEnvironment.intType;
    }

    public Object visitFloatLiteral(FloatLiteral IL, Object o) {
        return StdEnvironment.floatType;
    }

    public Object visitBooleanLiteral(BooleanLiteral SL, Object o) {
        return StdEnvironment.booleanType;
    }

    public Object visitStringLiteral(StringLiteral IL, Object o) {
        return StdEnvironment.stringType;
    }

    public Object visitIdent(Ident I, Object o) {
        Decl binding = idTable.retrieve(I.spelling);
        if (binding != null)
            I.decl = binding;
        return binding;
    }

    public Object visitOperator(Operator O, Object o) {
        return null;
    }

    // ------------------------------
    // 7. Parameters
    // ------------------------------

    // Always returns null. Does not use the given object.

    public Object visitParaList(ParaList ast, Object o) {
        ast.P.visit(this, null);
        ast.PL.visit(this, null);
        return null;
    }

    public Object visitParaDecl(ParaDecl ast, Object o) {
        ast.T.visit(this, ast);
        ast.I.visit(this, ast);

        declareIdent(ast.I, ast);

        // Check for error 3: identifier declared void
        // and for error 4: identifier declared void[]
        if (ast.T.isVoidType()) {
            reporter.reportError(errMesg[3] + ": %", ast.I.spelling, ast.I.position);
        } else if (ast.T.isArrayType()) {
            if (((ArrayType) ast.T).T.isVoidType())
                reporter.reportError(errMesg[4] + ": %", ast.I.spelling, ast.I.position);
        }
        return null;
    }

    // ------------------------------
    // 8. Arguments
    // ------------------------------

    // Your visitor methods for arguments go here

    // visitArgList(ast, o)
    //
    // Its second argument is a List of formal parameters 
    //
    // We check for two restrictions:
    // 1. The number of actual parameters is equal to
    //    the number of formal parameters.
    // 2. The type of each actual parameter is 
    //    assignable (not necessarily equal) 
    //    to the corresponding formal parameter. 
    //
    // If these two restrictions are not met then 
    // at least one of the errors 25, 26 or 27 will be reported
    //
    public Object visitArgList(ArgList ast, Object o) {
        List list = (List) o;    

        // Check for error 25: too many actual parameters
        if (list.isEmptyParaList()) {
            reporter.reportError(errMesg[25], "", ast.position);

        // If error 25 doesn't occur then we can continue 
        // checking the rest of the args
        } else {
            ParaList paralist = (ParaList) list;
            ast.A.visit(this, paralist.P);
            ast.AL.visit(this, paralist.PL);
        }

        return null;
    }

    // visitArg(ast, o)
    //
    // Its second argument is a ParaDecl representing the
    // formal parameter corresponding to this actual parameter.
    //
    // The type of this actual parameter be assignable to the 
    // formal parameter.
    //
    // If not then type-coerce the argument and check again.
    //
    // If type-coercion is not possible or the check fails then  
    // report an error
    //
    public Object visitArg(Arg ast, Object o) {

        // Get the declaration of the formal parameter from the second argument
        ParaDecl formal = (ParaDecl) o;

        // Give ast.E.visit() a second argument: a Boolean object,
        // called useScalar, that is true iff the formal parameter
        // is not an array
        boolean useScalar = ! formal.T.isArrayType();
        ast.E.visit(this, new Boolean(useScalar));
        ast.type = ast.E.type;

        // Check for error 27: wrong type for actual parameter
        if (! validParameterTypes(ast.E.type, formal.T)) { 
            reporter.reportError(errMesg[27] + ": expected " + formal.T + " got " + ast.E.type, "", ast.position);
        }

        // Type-coerce the actual parameter if necessary
        if (formal.T.isFloatType() && ast.E.type.isIntType()) {
            Operator i2f = new Operator("i2f", dummyPos);
            ast.E = new UnaryExpr(i2f, ast.E, dummyPos);
            ast.E.type = StdEnvironment.floatType;
        }
        
        return null;
    }

    private boolean validParameterTypes(Type actual, Type formal) {
        if (formal.isArrayType() && actual.isArrayType()) {
            Type formalBase = ((ArrayType) formal).T;
            Type actualBase = ((ArrayType) actual).T;

            // The VC specification tells us that no coercions can be done 
            // between two array types.
            // Therefore we require that if the formal and actual parameters
            // are arrays then their base types must be equal, not just assignable.
            return formalBase.equals(actualBase);
        } else if (! formal.isArrayType() && ! actual.isArrayType()) {
            return formal.assignable(actual);
        } else {
            return false; // Parameter types are not valid in this case
                          // because one parameter is an array but the other is not
        }
    }

    // ------------------------------
    // 9. Types 
    // ------------------------------

    // Returns the type predefined in the standard environment. 

    public Object visitVoidType(VoidType ast, Object o) {
        return StdEnvironment.voidType;
    }

    public Object visitBooleanType(BooleanType ast, Object o) {
        return StdEnvironment.booleanType;
    }

    public Object visitIntType(IntType ast, Object o) {
        return StdEnvironment.intType;
    }

    public Object visitFloatType(FloatType ast, Object o) {
        return StdEnvironment.floatType;
    }

    public Object visitStringType(StringType ast, Object o) {
        return StdEnvironment.stringType;
    }

    public Object visitArrayType(ArrayType ast, Object o) { 
        ast.T.visit(this, o);
        ast.E.visit(this, o);
        return new ArrayType(ast.T, ast.E, ast.position); 
    }

    public Object visitErrorType(ErrorType ast, Object o) {
        return StdEnvironment.errorType;
    }

    // ------------------------------
    // 10. Variables
    // ------------------------------

    // Returns the type of the variable

    // visitSimpleVar(ast, o)
    //
    // Second argument is a boolean flag, useScalar,
    // that is true iff this SimpleVar is being used as a scalar. 
    public Object visitSimpleVar(SimpleVar ast, Object o) {

        boolean useScalar = (Boolean) o;
        ast.I.visit(this, o);

        // Check for error 5: identifier undeclared
        Decl decl = idTable.retrieve(ast.I.spelling);
        if (decl == null) {
            reporter.reportError(errMesg[5] + ": %", ast.I.spelling, ast.I.position);
            ast.type = StdEnvironment.errorType;
            return ast.type;

        // Check for error 11: attempt to use an array/fuction as a scalar
        } else if (useScalar && (decl.T.isArrayType() || decl.isFuncDecl())) {
            reporter.reportError(errMesg[11] + ": %", ast.I.spelling, ast.I.position);
            ast.type = StdEnvironment.errorType;
            return ast.type;
        
        // Else there's no error. The type of the variable 
        // is equal to the type of its declaration
        } else {
            ast.type = decl.T;
            return ast.type;
        }
    }

    // ------------------------------
    // Helper functions
    // ------------------------------


    // auxiliary methods

    // Creates a small AST to represent the "declaration" of each built-in
    // function, and enters it in the symbol table.

    private FuncDecl declareStdFunc (Type resultType, String id, List pl) {

        FuncDecl binding;

        binding = new FuncDecl(resultType, new Ident(id, dummyPos), pl, 
                new EmptyStmt(dummyPos), dummyPos);
        idTable.insert (id, binding);
        return binding;
    }

    // Creates small ASTs to represent "declarations" of all 
    // built-in functions.
    // Inserts these "declarations" into the symbol table.

    private final static Ident dummyI = new Ident("x", dummyPos);

    private void establishStdEnvironment () {

        // Define four primitive types
        // errorType is assigned to ill-typed expressions

        StdEnvironment.booleanType = new BooleanType(dummyPos);
        StdEnvironment.intType = new IntType(dummyPos);
        StdEnvironment.floatType = new FloatType(dummyPos);
        StdEnvironment.stringType = new StringType(dummyPos);
        StdEnvironment.voidType = new VoidType(dummyPos);
        StdEnvironment.errorType = new ErrorType(dummyPos);

        // enter the declarations for built-in functions into the table

        StdEnvironment.getIntDecl = declareStdFunc( StdEnvironment.intType,
                "getInt", new EmptyParaList(dummyPos)); 
        StdEnvironment.putIntDecl = declareStdFunc( StdEnvironment.voidType,
                "putInt", new ParaList(
                    new ParaDecl(StdEnvironment.intType, dummyI, dummyPos),
                    new EmptyParaList(dummyPos), dummyPos)); 
        StdEnvironment.putIntLnDecl = declareStdFunc( StdEnvironment.voidType,
                "putIntLn", new ParaList(
                    new ParaDecl(StdEnvironment.intType, dummyI, dummyPos),
                    new EmptyParaList(dummyPos), dummyPos)); 
        StdEnvironment.getFloatDecl = declareStdFunc( StdEnvironment.floatType,
                "getFloat", new EmptyParaList(dummyPos)); 
        StdEnvironment.putFloatDecl = declareStdFunc( StdEnvironment.voidType,
                "putFloat", new ParaList(
                    new ParaDecl(StdEnvironment.floatType, dummyI, dummyPos),
                    new EmptyParaList(dummyPos), dummyPos)); 
        StdEnvironment.putFloatLnDecl = declareStdFunc( StdEnvironment.voidType,
                "putFloatLn", new ParaList(
                    new ParaDecl(StdEnvironment.floatType, dummyI, dummyPos),
                    new EmptyParaList(dummyPos), dummyPos)); 
        StdEnvironment.putBoolDecl = declareStdFunc( StdEnvironment.voidType,
                "putBool", new ParaList(
                    new ParaDecl(StdEnvironment.booleanType, dummyI, dummyPos),
                    new EmptyParaList(dummyPos), dummyPos)); 
        StdEnvironment.putBoolLnDecl = declareStdFunc( StdEnvironment.voidType,
                "putBoolLn", new ParaList(
                    new ParaDecl(StdEnvironment.booleanType, dummyI, dummyPos),
                    new EmptyParaList(dummyPos), dummyPos)); 

        StdEnvironment.putStringLnDecl = declareStdFunc( StdEnvironment.voidType,
                "putStringLn", new ParaList(
                    new ParaDecl(StdEnvironment.stringType, dummyI, dummyPos),
                    new EmptyParaList(dummyPos), dummyPos)); 

        StdEnvironment.putStringDecl = declareStdFunc( StdEnvironment.voidType,
                "putString", new ParaList(
                    new ParaDecl(StdEnvironment.stringType, dummyI, dummyPos),
                    new EmptyParaList(dummyPos), dummyPos)); 

        StdEnvironment.putLnDecl = declareStdFunc( StdEnvironment.voidType,
                "putLn", new EmptyParaList(dummyPos));

    }

}
