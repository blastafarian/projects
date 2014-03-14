/*
 * Parser.java            
 *
 * This class has a public method, parseProgram(), 
 * that generates the abstract syntax tree of a VC program
 * 
 * The methods in this class follow the 
 * VC grammar that I have described below.
 *
 * --- Brendon Kwan (9 September 2006)

    program -> decl-list
    
    decl-list -> decl *
    decl -> type identifier (func-decl-tail | var-decl-tail) decl-list * 

    func-decl-tail -> para-list compound-stmt

    var-decl-tail -> var-first ( "," identifier var-first ) * ";"
    var-first       -> ( "[" INTLITERAL? "]" ) ? ( "=" initialiser ) ?
    initialiser     -> expr 
                    |  "{" expr ( "," expr )* "}"
    
    // primitive types
    type -> void | boolean | int | float

    // identifiers 
    identifier    -> ID
    
    // statements
    compound-stmt -> "{" local-var-decl-list stmt-list "}"
    local-var-decl-list -> ( type identifier var-decl-tail ) *
    stmt-list -> stmt *
    stmt -> compound-stmt
         |  if-stmt
         |  for-stmt
         |  while-stmt
         |  break-stmt
         |  continue-stmt
         |  return-stmt
         |  expr-stmt
    if-stmt -> "if" "(" expr ")" stmt ( "else" stmt )?
    for-stmt -> "for" "(" expr? ";" expr? ";" expr? ")" stmt
    while-stmt -> "while" "(" expr ")" stmt
    break-stmt -> "break" ";" 
    continue-stmt -> "continue" ";"
    return-stmt -> "return" expr? ";"
    expr-stmt -> expr? ";"

    // expressions 

    expr -> assignment-expr

    assignment-expr -> cond-or-expr ( "=" cond-or-expr )* 

    cond-or-expr -> cond-and-expr ("||" cond-and-expr)*

    cond-and-expr -> equality-expr ("&&" equality-expr)*

    equality-expr -> rel-expr ("==" rel-expr | 
                               "!=" rel-expr)*

    rel-expr -> additive-expr ("<" additive-expr  | 
                               "<=" additive-expr | 
                               ">" additive-expr  | 
                               ">=" additive-expr)*

    additive-expr -> multiplicative-expr ("+" multiplicative-expr | 
                                          "-" multiplicative-expr)*

    multiplicative-expr -> unary-expr ("*" unary-expr | 
                                       "/" unary-expr)*

    unary-expr -> "+" unary-expr | 
               |  "-" unary-expr
               |  "!" unary-expr
               |  primary-expr 

    primary-expr -> identifier (arg-list | "[" expr "]")?
                 |  "(" expr ")"
                 |  INTLITERAL
                 |  FLOATLITERAL   
                 |  BOOLEANLITERAL
                 |  STRINGLITERAL
    
    // parameters 

    para-list      -> "(" ( para-decl more-para-decl ) ? ")"
    para-decl      -> type identifier ( "[" array-size "]" ) ? 
    array-size     -> INTLITERAL ?
    more-para-decl -> ( "," para-decl more-para-decl ) *

    arg-list      -> "(" ( arg more-arg-list ) ? ")"
    arg           -> expr
    more-arg-list -> ( "," arg more-arg-list ) ?
 */

package VC.Parser;

import VC.Scanner.Scanner;
import VC.Scanner.SourcePosition;
import VC.Scanner.Token;
import VC.ErrorReporter;
import VC.ASTs.*;

import java.util.LinkedList;

public class Parser {

    private Scanner scanner;
    private ErrorReporter errorReporter;
    private Token currentToken;
    private SourcePosition previousTokenPosition;
    private SourcePosition dummyPos = new SourcePosition();

    public Parser (Scanner lexer, ErrorReporter reporter) {
        scanner = lexer;
        errorReporter = reporter;

        previousTokenPosition = new SourcePosition();

        currentToken = scanner.getToken();
    }

    // match checks to see f the current token matches tokenExpected.
    // If so, fetches the next token.
    // If not, reports a syntactic error.

    void match(int tokenExpected) throws SyntaxError {
        if (currentToken.kind == tokenExpected) {
            previousTokenPosition = currentToken.position;
            currentToken = scanner.getToken();
        } else {
            syntacticError("\"%\" expected here", Token.spell(tokenExpected));
        }
    }

    void accept() {
        previousTokenPosition = currentToken.position;
        currentToken = scanner.getToken();
    }

    void syntacticError(String messageTemplate, String tokenQuoted) throws SyntaxError {
        SourcePosition pos = currentToken.position;
        errorReporter.reportError(messageTemplate, tokenQuoted, pos);
        throw(new SyntaxError());
    }

    // start records the position of the start of a phrase.
    // This is defined to be the position of the first
    // character of the first token of the phrase.

    void start(SourcePosition position) {
        position.lineStart = currentToken.position.lineStart;
        position.charStart = currentToken.position.charStart;
    }

    // finish records the position of the end of a phrase.
    // This is defined to be the position of the last
    // character of the last token of the phrase.

    void finish(SourcePosition position) {
        position.lineFinish = previousTokenPosition.lineFinish;
        position.charFinish = previousTokenPosition.charFinish;
    }

    void copyStart(SourcePosition from, SourcePosition to) {
        to.lineStart = from.lineStart;
        to.charStart = from.charStart;
    }

    // ========================== PROGRAMS ========================

    // program -> decl-list

    public Program parseProgram() {

        SourcePosition pos = new SourcePosition();
        start(pos);

        try {

            List dlAST = parseDeclList();

            if (currentToken.kind != Token.EOF) {
                syntacticError("\"%\" unknown type", currentToken.spelling);
            }

            finish(pos);
            return new Program(dlAST, pos); 

        } catch (SyntaxError s) { 

            s.printStackTrace();
            return null; 
        }
    }

    // ========================== DECLARATIONS ========================

    // decl-list -> decl *
    // decl -> type identifier (func-decl-tail | var-decl-tail) decl-list * 

    List parseDeclList() throws SyntaxError {

        SourcePosition pos = new SourcePosition();
        start(pos);

        if (isType(currentToken.kind)) {

            // The program has at least one declaration
            Type t = parseType();
            Ident i = parseIdent();

            if (currentToken.kind == Token.LPAREN) {

                // First we have a function declaration
                // By calling the following function we parse it and all subsequent declarations
                return parseFuncDeclList(t, i, pos);

            } else {

                // First we have a global variable declaration 
                // By calling the following function we parse it and all subsequent declarations
                return parseGlobalVarDeclList(t, i, pos); 
            }

            
        } else {

            // The program has no declarations
            finish(pos);
            return new EmptyDeclList(pos); 
        }
    }
    
    // This parses the first of the two non-nullable productions of decl-list:
    // 
    // decl-list -> type identifier func-decl-tail decl-list *

    List parseFuncDeclList(Type t, Ident i, SourcePosition pos) throws SyntaxError { 

        Decl d = parseFuncDeclTail(t, i, pos);
        List l = parseDeclList();

        finish(pos);
        return new DeclList(d, l, pos);
    }

    // This parses the second of the two non-nullable productions of decl-list:
    //
    // decl-list -> type identifier var-decl-tail decl-list *
    // var-decl-tail -> var-first ( "," identifier var-first ) * ";"

    List parseGlobalVarDeclList(Type t, Ident i, SourcePosition pos) throws SyntaxError {

        LinkedList<Decl> vars = new LinkedList<Decl>();
       
        // 'vars' is a linked list of the Decl objects representing variables
        // The elements in 'vars' are ordered in order of their appearance in the source code
        // The first element is the Decl object of the first variable
        // The last element is the Decl object of the last variable
        
        // Parse the first variable declaration 

        vars.addLast(parseVarFirst(t, i, pos, true)); // true means 'global variable'
        
        // Parse the rest of the variable declarations 

        while (currentToken.kind == Token.COMMA) {
            
            match(Token.COMMA);
            i = parseIdent(); 
            vars.addLast(parseVarFirst(t, i, pos, true)); // true means 'global variable'
        }
        
        match(Token.SEMICOLON);

        // Parse the rest of the declarations        

        List l = parseDeclList();

        // Combine the variable declarations with the rest of the declarations 
        // Then return the result

        while (! vars.isEmpty()) { 
            Decl d = vars.removeLast();
            l = new DeclList(d, l, pos);
        }

        return l; 
    }

    // func-decl-tail -> para-list compound-stmt
    
    Decl parseFuncDeclTail(Type t, Ident i, SourcePosition pos) throws SyntaxError {

        List p = parseParaList();
        Stmt s = parseCompoundStmt();

        finish(pos);
        return new FuncDecl(t, i, p, s, pos);
    }

    // var-first -> ( "[" INTLITERAL? "]" ) ? ( "=" initialiser ) ?
    
    Decl parseVarFirst(Type t, Ident i, SourcePosition pos, boolean isGlobal) throws SyntaxError {

        // Upgrade the type of this declaration to an array if possible
        
        if (currentToken.kind == Token.LBRACKET) {

            match(Token.LBRACKET);
            Expr e = parseArraySize();
            match(Token.RBRACKET);

            t = new ArrayType(t, e, pos);
        } 

        // Parse the initialiser if it exists

        Expr initExpr = new EmptyExpr(pos);

        if (currentToken.kind == Token.EQ) {

            match(Token.EQ);
            initExpr = parseInitialiser(); 
        }

        // Create a variable declaration ...

        finish(pos);

        if (isGlobal) {
            return new GlobalVarDecl(t, i, initExpr, pos);
        } else {
            return new LocalVarDecl(t, i, initExpr, pos);
        }
        
    }

    // initialiser -> expr 
    //             |  "{" expr ( "," expr )* "}"

    Expr parseInitialiser() throws SyntaxError {

        SourcePosition pos = new SourcePosition();
        start(pos);
        
        if (currentToken.kind != Token.LCURLY) {

            return parseExpr();

        } else {
            
            match(Token.LCURLY);

            LinkedList<Expr> exprs = new LinkedList<Expr>();
            exprs.addLast(parseExpr());
            
            while (currentToken.kind == Token.COMMA) {

                match(Token.COMMA);
                exprs.addLast(parseExpr());
            }

            match(Token.RCURLY);

            List l = new EmptyExprList(pos);
            while (! exprs.isEmpty()) {
                l = new ExprList(exprs.removeLast(), l, pos);     
            }
            
            finish(pos);
            return new InitExpr(l, pos);

        }
    }

    // ============================ TYPES ============================

    // type -> void | boolean | int | float
 
    Type parseType() throws SyntaxError {

        SourcePosition pos = new SourcePosition();
        start(pos);

        switch (currentToken.kind) {

            case Token.VOID:
                accept();
                finish(pos);
                return new VoidType(pos);
                
            case Token.BOOLEAN:
                accept();
                finish(pos);
                return new BooleanType(pos);

            case Token.INT:
                accept();
                finish(pos);
                return new IntType(pos);

            case Token.FLOAT:
                accept();
                finish(pos);
                return new FloatType(pos);

            default:
                syntacticError("type expected here", currentToken.spelling);
                return null; // syntacticError() calls System.exit(), so execution should not reach this line
        }
    }

    boolean isType(int tokenKind) {
        
        switch (tokenKind) {
            case Token.VOID:
            case Token.BOOLEAN:
            case Token.INT:
            case Token.FLOAT:
                return true;
            default:
                return false;
        }
    }

    // ======================= STATEMENTS ==============================

    // compound-stmt -> "{" local-var-decl-list stmt-list "}"
   
    Stmt parseCompoundStmt() throws SyntaxError {

        SourcePosition pos = new SourcePosition();
        start(pos);

        match(Token.LCURLY);
        List dlAST = parseLocalVarDeclList(); 
        List slAST = parseStmtList();
        match(Token.RCURLY);

        finish(pos);
        return new CompoundStmt(dlAST, slAST, pos);
    }

    // local-var-decl-list -> ( type identifier var-decl-tail ) *
    // var-decl-tail -> var-first ( "," identifier var-first ) * ";"

    List parseLocalVarDeclList() throws SyntaxError {

        SourcePosition pos = new SourcePosition();
        start(pos);

        if (isType(currentToken.kind)) {
            
            // There is at least one declaration 

            // Parse the first declaration, which is of the form <type> <identifier> <var-decl-tail>
            
            LinkedList<Decl> vars = new LinkedList<Decl>();

            Type t = parseType();
            Ident i = parseIdent();
            Decl d = parseVarFirst(t, i, pos, false); // false means 'local variable'

            vars.addLast(d); 

            while (currentToken.kind == Token.COMMA) {

                match(Token.COMMA);
                i = parseIdent(); 
                d = parseVarFirst(t, i, pos, false); // false means 'local variable'

                vars.addLast(d);
            }

            match(Token.SEMICOLON);
            
            // Parse the remaining declarations. To do so, use recursion.
            
            List l = parseLocalVarDeclList(); 

            // Combine the result of parsing the first declaration
            // with the result of parsing the remaining declarations.
            
            while (! vars.isEmpty()) {
                l = new DeclList(vars.removeLast() , l, pos);
            }

            return l;

        } else {

            // There are no declarations
            
            finish(pos);
            return new EmptyDeclList(pos);
        }
    }
    
    // stmt-list -> stmt *

    List parseStmtList() throws SyntaxError {

        SourcePosition pos = new SourcePosition();
        start(pos);

        if (currentToken.kind != Token.RCURLY) {
             
            Stmt sAST = parseStmt();
            List slAST = parseStmtList(); // recursion ...

            finish(pos);
            return new StmtList(sAST, slAST, pos); 

        } else {

            finish(pos);
            return new EmptyStmtList(pos);
        }
    }
    
    // stmt -> compound-stmt
    //      |  if-stmt
    //      |  for-stmt
    //      |  while-stmt
    //      |  break-stmt
    //      |  continue-stmt
    //      |  return-stmt
    //      |  expr-stmt
        
    Stmt parseStmt() throws SyntaxError {

        switch (currentToken.kind) {

            case Token.LCURLY:
                return parseCompoundStmt();

            case Token.IF:
                return parseIfStmt();

            case Token.FOR:
                return parseForStmt();

            case Token.WHILE:
                return parseWhileStmt();

            case Token.BREAK:
                return parseBreakStmt();

            case Token.CONTINUE:
                return parseContinueStmt();

            case Token.RETURN:
                return parseReturnStmt();

            default:
                return parseExprStmt();

        }
    }

    // if-stmt -> "if" "(" expr ")" stmt ( "else" stmt )?
  
    Stmt parseIfStmt() throws SyntaxError {

        SourcePosition pos = new SourcePosition();
        start(pos);

        match(Token.IF);
        match(Token.LPAREN);
        Expr eAST = parseExpr();
        match(Token.RPAREN);
        Stmt s1AST = parseStmt();

        if (currentToken.kind == Token.ELSE) {

            accept();
            Stmt s2AST = parseStmt();

            finish(pos);
            return new IfStmt(eAST, s1AST, s2AST, pos);

        } else {

            finish(pos);
            return new IfStmt(eAST, s1AST, pos);
        }
    }

    // for-stmt -> "for" "(" expr? ";" expr? ";" expr? ")" stmt
   
    Stmt parseForStmt() throws SyntaxError {

        SourcePosition pos = new SourcePosition();
        start(pos);

        Expr e1 = new EmptyExpr(dummyPos);
        Expr e2 = new EmptyExpr(dummyPos);
        Expr e3 = new EmptyExpr(dummyPos);

        match(Token.FOR);
        match(Token.LPAREN);

        if (currentToken.kind != Token.SEMICOLON) {
            e1 = parseExpr();
        }
        
        match(Token.SEMICOLON);

        if (currentToken.kind != Token.SEMICOLON) {
            e2 = parseExpr();
        }

        match(Token.SEMICOLON);

        if (currentToken.kind != Token.RPAREN) {
            e3 = parseExpr();
        }

        match(Token.RPAREN);
        Stmt s = parseStmt();
        
        finish(pos);
        return new ForStmt(e1, e2, e3, s, pos);
    }
    
    // while-stmt -> "while" "(" expr ")" stmt
   
    Stmt parseWhileStmt() throws SyntaxError {

        SourcePosition pos = new SourcePosition();
        start(pos);

        match(Token.WHILE);
        match(Token.LPAREN);
        Expr e = parseExpr();
        match(Token.RPAREN);
        Stmt s = parseStmt();

        finish(pos);
        return new WhileStmt(e, s, pos);
    }

    // break-stmt -> "break" ";" 
    
    Stmt parseBreakStmt() throws SyntaxError {

        SourcePosition pos = new SourcePosition();
        start(pos);
        
        match(Token.BREAK);
        match(Token.SEMICOLON);

        finish(pos);
        return new BreakStmt(pos);
    }

    // continue-stmt -> "continue" ";"
   
    Stmt parseContinueStmt() throws SyntaxError {

        SourcePosition pos = new SourcePosition();
        start(pos);

        match(Token.CONTINUE);
        match(Token.SEMICOLON);

        finish(pos);
        return new ContinueStmt(pos);
    }

    // return-stmt -> "return" expr? ";"
    
    Stmt parseReturnStmt() throws SyntaxError {

        SourcePosition pos = new SourcePosition();
        start(pos);

        match(Token.RETURN);

        Expr e = new EmptyExpr(dummyPos);

        if (currentToken.kind != Token.SEMICOLON) {
            e = parseExpr();
        }

        match(Token.SEMICOLON);

        finish(pos);
        return new ReturnStmt(e, pos);
    }

    // expr-stmt -> expr? ";"

    Stmt parseExprStmt() throws SyntaxError {

        SourcePosition pos = new SourcePosition();
        start(pos);

        Expr eAST = new EmptyExpr(dummyPos);

        if (currentToken.kind != Token.SEMICOLON) {
            eAST = parseExpr();
        }

        match(Token.SEMICOLON);

        finish(pos);
        return new ExprStmt(eAST, pos);
    }


    // ======================= EXPRESSIONS ======================

    // expr -> assignment-expr
    
    Expr parseExpr() throws SyntaxError {

        return parseAssignmentExpr();
    }

    // assignment-expr -> cond-or-expr ( "=" cond-or-expr )* 
   
    Expr parseAssignmentExpr() throws SyntaxError {

        SourcePosition pos = new SourcePosition();
        start(pos);

        Expr condOrAST = parseCondOrExpr();

        if (currentToken.kind == Token.EQ) {

            accept();
            Expr assignAST = parseAssignmentExpr(); // recursive ...

            finish(pos);
            return new AssignExpr(condOrAST, assignAST, pos);

        } else {
            
            return condOrAST;
        }

    }

    // cond-or-expr -> cond-and-expr ("||" cond-and-expr)*

    Expr parseCondOrExpr() throws SyntaxError {


        SourcePosition startPos = new SourcePosition();
        start(startPos);

        Expr exprAST = parseCondAndExpr();
        
        while (currentToken.kind == Token.OROR) {
            
            Operator oAST = acceptOperator();
            Expr e2AST = parseCondAndExpr();

            SourcePosition pos = new SourcePosition();
            copyStart(startPos, pos);
            finish(pos);

            exprAST = new BinaryExpr(exprAST, oAST, e2AST, pos);
        }

        return exprAST;
    }

    // cond-and-expr -> equality-expr ("&&" equality-expr)*

    Expr parseCondAndExpr() throws SyntaxError {

        SourcePosition startPos = new SourcePosition();
        start(startPos);

        Expr exprAST = parseEqualityExpr();
        
        while (currentToken.kind == Token.ANDAND) {
            
            Operator oAST = acceptOperator();
            Expr e2AST = parseEqualityExpr();
            
            SourcePosition pos = new SourcePosition();
            copyStart(startPos, pos);
            finish(pos);

            exprAST = new BinaryExpr(exprAST, oAST, e2AST, pos);
        }

        return exprAST;
    }

    // equality-expr -> rel-expr ("==" rel-expr | 
    //                            "!=" rel-expr)*

    Expr parseEqualityExpr() throws SyntaxError {

        SourcePosition startPos = new SourcePosition();
        start(startPos);

        Expr exprAST = parseRelExpr();
        
        while (currentToken.kind == Token.EQEQ ||
               currentToken.kind == Token.NOTEQ) {
            
            Operator oAST = acceptOperator();
            Expr e2AST = parseRelExpr();
            
            SourcePosition pos = new SourcePosition();
            copyStart(startPos, pos);
            finish(pos);

            exprAST = new BinaryExpr(exprAST, oAST, e2AST, pos);
        }

        return exprAST;
    }
    
    // rel-expr -> additive-expr ("<" additive-expr  | 
    //                            "<=" additive-expr | 
    //                            ">" additive-expr  | 
    //                            ">=" additive-expr)*

    Expr parseRelExpr() throws SyntaxError {

        SourcePosition startPos = new SourcePosition();
        start(startPos);

        Expr exprAST = parseAdditiveExpr();
        
        while (currentToken.kind == Token.LT ||
               currentToken.kind == Token.LTEQ ||
               currentToken.kind == Token.GT ||
               currentToken.kind == Token.GTEQ) {

            Operator oAST = acceptOperator();
            Expr eAST = parseAdditiveExpr();

            SourcePosition pos = new SourcePosition();
            copyStart(startPos, pos);
            finish(pos);

            exprAST = new BinaryExpr(exprAST, oAST, eAST, pos);
            
        }

        return exprAST;
    }

    // additive-expr -> multiplicative-expr ("+" multiplicative-expr | 
    //                                       "-" multiplicative-expr)*

    Expr parseAdditiveExpr() throws SyntaxError {

        SourcePosition addStartPos = new SourcePosition();
        start(addStartPos);

        Expr exprAST = parseMultiplicativeExpr();

        while (currentToken.kind == Token.PLUS || 
               currentToken.kind == Token.MINUS) {

            Operator opAST = acceptOperator();
            Expr e2AST = parseMultiplicativeExpr();

            SourcePosition addPos = new SourcePosition();
            copyStart(addStartPos, addPos);
            finish(addPos);

            exprAST = new BinaryExpr(exprAST, opAST, e2AST, addPos);
        }

        return exprAST;
    }

    // multiplicative-expr -> unary-expr ("*" unary-expr | 
    //                                    "/" unary-expr)*

    Expr parseMultiplicativeExpr() throws SyntaxError {

        SourcePosition multStartPos = new SourcePosition();
        start(multStartPos);

        Expr eAST = parseUnaryExpr();

        while (currentToken.kind == Token.MULT ||
               currentToken.kind == Token.DIV) {

            Operator opAST = acceptOperator();
            Expr e2AST = parseUnaryExpr();

            SourcePosition multPos = new SourcePosition();
            copyStart(multStartPos, multPos);
            finish(multPos);
            
            eAST = new BinaryExpr(eAST, opAST, e2AST, multPos);
        }

        return eAST;
    }

    // unary-expr -> "+" unary-expr | 
    //            |  "-" unary-expr
    //            |  "!" unary-expr
    //            |  primary-expr 
    
    Expr parseUnaryExpr() throws SyntaxError {

        SourcePosition unaryPos = new SourcePosition();
        start(unaryPos);

        switch (currentToken.kind) {
            
            case Token.MINUS:
            case Token.PLUS:
            case Token.NOT:
            
                Operator oAST = acceptOperator();
                Expr eAST = parseUnaryExpr(); // recursive...

                finish(unaryPos);
                return new UnaryExpr(oAST, eAST, unaryPos);

            default:
                return parsePrimaryExpr();
        }
    }

    // primary-expr -> identifier (arg-list | "[" expr "]")?
    //             |  "(" expr ")"
    //             |  INTLITERAL
    //             |  FLOATLITERAL   
    //             |  BOOLLITERAL
    //             |  STRINGLITERAL

    Expr parsePrimaryExpr() throws SyntaxError {

        Expr exprAST = null;

        SourcePosition pos = new SourcePosition();
        start(pos);

        switch (currentToken.kind) {

            case Token.ID:
                Ident idAST = parseIdent();

                if (currentToken.kind == Token.LPAREN) {

                    List aplAST = parseArgList();

                    finish(pos);
                    exprAST = new CallExpr(idAST, aplAST, pos);
                    break;

                } else if (currentToken.kind == Token.LBRACKET) {
                                    
                    accept();
                    Expr indexAST = parseExpr();
                    match(Token.RBRACKET);

                    finish(pos);
                    Var simVAST = new SimpleVar(idAST, pos);
                    exprAST = new ArrayExpr(simVAST, indexAST, pos);
                    break;

                } else { 

                    finish(pos);
                    Var simVAST = new SimpleVar(idAST, pos);
                    exprAST = new VarExpr(simVAST, pos);
                    break;
                }

            case Token.LPAREN:
                accept();
                exprAST = parseExpr();
                match(Token.RPAREN);
                finish(pos);
                break;

            case Token.INTLITERAL:
                IntLiteral ilAST = parseIntLiteral();
                finish(pos);
                exprAST = new IntExpr(ilAST, pos);
                break;

            case Token.FLOATLITERAL:
                FloatLiteral flAST = parseFloatLiteral();
                finish(pos);
                exprAST = new FloatExpr(flAST, pos);
                break;

            case Token.BOOLEANLITERAL:
                BooleanLiteral blAST = parseBooleanLiteral();
                finish(pos);
                exprAST = new BooleanExpr(blAST, pos);
                break;

            case Token.STRINGLITERAL:
                StringLiteral slAST = parseStringLiteral();
                finish(pos);
                exprAST = new StringExpr(slAST, pos);
                break;
                
            default:
                syntacticError("illegal primary expression", currentToken.spelling);

        }

        return exprAST;
    }

    Expr parseIntExpr() throws SyntaxError {

        SourcePosition pos = new SourcePosition();
        start(pos);
        
        IntLiteral ilAST = parseIntLiteral();

        finish(pos);
        return new IntExpr(ilAST, pos);
    }

    // ================================= PARAMETERS ================================
    
    // para-list -> "(" ( para-decl more-para-decl ) ? ")"

    List parseParaList() throws SyntaxError {

        SourcePosition pos = new SourcePosition();
        start(pos);

        match(Token.LPAREN);
        
        if (currentToken.kind != Token.RPAREN) {
            
            ParaDecl pAST = parseParaDecl();
            List plList = parseMoreParaDecl();
            match(Token.RPAREN);

            finish(pos);
            return new ParaList(pAST, plList, pos);
            
        } else {

            match(Token.RPAREN);

            finish(pos);
            return new EmptyParaList(pos);
        }
    }

    // para-decl -> type identifier ( "[" array-size "]" ) ? 

    ParaDecl parseParaDecl() throws SyntaxError {

        SourcePosition pos = new SourcePosition();
        start(pos);

        Type tAST = parseType();
        Ident idAST = parseIdent();
        
        if (currentToken.kind == Token.LBRACKET) {

            accept();
            Expr dAST = parseArraySize();
            match(Token.RBRACKET);

            finish(pos);
            Type arrayAST = new ArrayType(tAST, dAST, pos);
            return new ParaDecl(arrayAST, idAST, pos);
            
        } else {

            finish(pos);
            return new ParaDecl(tAST, idAST, pos);
        }

    }

    // array-size -> INTLITERAL ?
    
    Expr parseArraySize() throws SyntaxError {
        
        SourcePosition pos = new SourcePosition();
        start(pos);

        if (currentToken.kind == Token.INTLITERAL) {

            IntLiteral ilAST = parseIntLiteral();
            finish(pos);
            return new IntExpr(ilAST, pos);

        } else {

            finish(pos);
            return new EmptyExpr(pos);
        }

    }
    
    // more-para-decl -> ( "," para-decl more-para-decl ) *

    List parseMoreParaDecl() throws SyntaxError {

        SourcePosition listPos = new SourcePosition();
        start(listPos);

        if (currentToken.kind == Token.COMMA) {

            accept();
            ParaDecl pAST = parseParaDecl();
            List plList = parseMoreParaDecl();

            finish(listPos);
            return new ParaList(pAST, plList, listPos);

        } else {

            finish(listPos);
            return new EmptyParaList(listPos);
        }

    }

    // arg-list -> "(" ( arg more-arg-list ) ? ")"

    List parseArgList() throws SyntaxError {

        SourcePosition pos = new SourcePosition();
        start(pos);

        match(Token.LPAREN);
        
        if (currentToken.kind != Token.RPAREN) {

            Arg aAST = parseArg();
            List alAST = parseMoreArgList();
            match(Token.RPAREN);

            finish(pos);
            return new ArgList(aAST, alAST, pos); 
            
        } else {
            
            match(Token.RPAREN);

            finish(pos);
            return new EmptyArgList(pos);
        }
    }

    // arg -> expr

    Arg parseArg() throws SyntaxError {

        SourcePosition pos = new SourcePosition();
        start(pos);

        Expr eAST = parseExpr(); 
        
        finish(pos);
        return new Arg(eAST, pos);
    }
    
    // more-arg-list -> ( "," arg more-arg-list ) ?

    List parseMoreArgList() throws SyntaxError {

        SourcePosition pos = new SourcePosition();
        start(pos);

        if (currentToken.kind == Token.COMMA) {

            accept();
            Arg aAST = parseArg();
            List alList = parseMoreArgList();

            finish(pos);
            return new ArgList(aAST, alList, pos);

        } else {

            finish(pos);
            return new EmptyArgList(pos);
        }
    }

    // ========================== ID, OPERATOR and LITERALS ========================

    Ident parseIdent() throws SyntaxError {

        Ident I = null; 

        if (currentToken.kind == Token.ID) {
            previousTokenPosition = currentToken.position;
            String spelling = currentToken.spelling;
            I = new Ident(spelling, previousTokenPosition);
            currentToken = scanner.getToken();
        } else 
            syntacticError("identifier expected here", "");
        return I;
    }

    // acceptOperator parses an operator, and constructs a leaf AST for it

    Operator acceptOperator() throws SyntaxError {
        Operator O = null;

        previousTokenPosition = currentToken.position;
        String spelling = currentToken.spelling;
        O = new Operator(spelling, previousTokenPosition);
        currentToken = scanner.getToken();
        return O;
    }


    IntLiteral parseIntLiteral() throws SyntaxError {
        IntLiteral IL = null;

        if (currentToken.kind == Token.INTLITERAL) {
            String spelling = currentToken.spelling;
            accept();
            IL = new IntLiteral(spelling, previousTokenPosition);
        } else 
            syntacticError("integer literal expected here", "");
        return IL;
    }

    FloatLiteral parseFloatLiteral() throws SyntaxError {
        FloatLiteral FL = null;

        if (currentToken.kind == Token.FLOATLITERAL) {
            String spelling = currentToken.spelling;
            accept();
            FL = new FloatLiteral(spelling, previousTokenPosition);
        } else 
            syntacticError("float literal expected here", "");
        return FL;
    }

    BooleanLiteral parseBooleanLiteral() throws SyntaxError {
        BooleanLiteral BL = null;

        if (currentToken.kind == Token.BOOLEANLITERAL) {
            String spelling = currentToken.spelling;
            accept();
            BL = new BooleanLiteral(spelling, previousTokenPosition);
        } else 
            syntacticError("boolean literal expected here", "");
        return BL;
    }

    StringLiteral parseStringLiteral() throws SyntaxError {
        StringLiteral SL = null;

        if (currentToken.kind == Token.STRINGLITERAL) {
            String spelling = currentToken.spelling;
            accept();
            SL = new StringLiteral(spelling, previousTokenPosition);
        } else 
            syntacticError("string literal expected here", "");
        return SL;
    }

}

