/***
 *** Recogniser.java            
 ***/

/* I have created a grammar that describes the same language as the 
 * VC grammar but has no left recursion and common prefixes.
 *
 * I hope that my grammar is LL(1) :-)
 *
 * --- Brendon Kwan (23 Aug 2006)
 
    program -> declaration*

    declaration -> type identifier (func-decl-tail | var-decl-tail)

    // function declarations

    func-decl-tail -> para-list compound-stmt

    // variable declarations

    var-decl -> type identifier var-decl-tail
    var-decl-tail  -> var-first var-rest  ";"
    var-first -> declarator-tail  init-declarator-tail 
    var-rest -> ( "," init-declarator ) *

    init-declarator -> declarator init-declarator-tail
    init-declarator-tail -> ( "=" initialiser ) ?     

    declarator -> identifier declarator-tail
    declarator-tail -> ( "[" INTLITERAL? "]" ) ?

    initialiser -> expr 
                |  "{" expr ( "," expr )* "}"

    // primitive types
    type -> void | boolean | int | float

    // identifiers
    identifier -> ID

    // statements
    compound-stmt -> "{" var-decl* stmt* "}"
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
                 |  BOOLLITERAL
                 |  STRINGLITERAL

    // parameters 

    para-list -> "(" proper-para-list? ")"
    proper-para-list -> para-decl ( "," para-decl )*
    para-decl -> type declarator

    arg-list -> "(" proper-arg-list? ")"
    proper-arg-list -> expr ( "," expr )* 

*/

package VC.Recogniser;

import VC.Scanner.Scanner;
import VC.Scanner.SourcePosition;
import VC.Scanner.Token;
import VC.ErrorReporter;

public class Recogniser {

    private Scanner scanner;
    private ErrorReporter errorReporter;
    private Token currentToken;

    public Recogniser (Scanner lexer, ErrorReporter reporter) {
        scanner = lexer;
        errorReporter = reporter;

        currentToken = scanner.getToken();
    }

    // match checks to see if the current token matches tokenExpected.
    // If so, fetches the next token.
    // If not, reports a syntactic error.

    void match(int tokenExpected) throws SyntaxError {
        if (currentToken.kind == tokenExpected) {
            currentToken = scanner.getToken();
        } else {
            syntacticError("\"%\" expected here", Token.spell(tokenExpected));
        }
    }

    // accepts the current token and fetches the next
   
    void accept() {
        currentToken = scanner.getToken();
    }

    void syntacticError(String messageTemplate, String tokenQuoted) throws SyntaxError {
        SourcePosition pos = currentToken.position;
        errorReporter.reportError(messageTemplate, tokenQuoted, pos);
        throw(new SyntaxError());
    }


    // ========================== PROGRAMS ========================

    // program -> declaration*
   
    public void parseProgram() {

        try {
            while (currentToken.kind != Token.EOF) {
                parseDeclaration();
            }
        }
        catch (SyntaxError s) {  }
    }

    // ========================== DECLARATIONS ========================

    // declaration -> type identifier (func-decl-tail | var-decl-tail)
   
    void parseDeclaration() throws SyntaxError {
        
        parseType();
        parseIdent();

        if (currentToken.kind == Token.LPAREN) {
            parseFuncDeclTail();
        } else {
            parseVarDeclTail();
        }
    }

    // func-decl-tail -> para-list compound-stmt
   
    void parseFuncDeclTail() throws SyntaxError {

        parseParaList();
        parseCompoundStmt();
    }

    // var-decl -> type identifier var-decl-tail
   
    void parseVarDecl() throws SyntaxError {

        parseType();
        parseIdent();
        parseVarDeclTail();
    }
    
    // var-decl-tail  -> var-first var-rest  ";"
   
    void parseVarDeclTail() throws SyntaxError {

        parseVarFirst();
        parseVarRest();
        match(Token.SEMICOLON);
    }

    // var-first -> declarator-tail  init-declarator-tail 
   
    void parseVarFirst() throws SyntaxError {
        
        parseDeclaratorTail();
        parseInitDeclaratorTail();
    }

    // var-rest -> ( "," init-declarator ) *
   
    void parseVarRest() throws SyntaxError {

        while (currentToken.kind == Token.COMMA) {
            accept();
            parseInitDeclarator();
        }
    }

    // init-declarator -> declarator init-declarator-tail
   
    void parseInitDeclarator() throws SyntaxError {

        parseDeclarator();
        parseInitDeclaratorTail();
    }

    // init-declarator-tail -> ( "=" initialiser ) ?     
   
    void parseInitDeclaratorTail() throws SyntaxError {

        if (currentToken.kind == Token.EQ) {
            accept();
            parseInitialiser();
        }
    }

    // declarator -> identifier declarator-tail
   
    void parseDeclarator() throws SyntaxError {

        parseIdent();
        parseDeclaratorTail();
    }
    
    // declarator-tail -> ( "[" INTLITERAL? "]" ) ?
    
    void parseDeclaratorTail() throws SyntaxError {

        if (currentToken.kind == Token.LBRACKET) {
            accept();
            if (currentToken.kind == Token.INTLITERAL) {
                parseIntLiteral();
            }
            match(Token.RBRACKET);
        }
    }

    // initialiser -> expr 
    //             |  "{" expr ( "," expr )* "}"
    
    void parseInitialiser() throws SyntaxError {

        if (currentToken.kind == Token.LCURLY) {

            match(Token.LCURLY);
            parseExpr();

            while (currentToken.kind == Token.COMMA) {
                accept(); 
                parseExpr();
            }

            match(Token.RCURLY);

        } else {
            parseExpr();
        }
    }


    // ===================== PRIMITIVE TYPES ====================

    // type -> void | boolean | int | float
 
    void parseType() throws SyntaxError {

        if (isType(currentToken.kind)) {
                accept();
        } else {
                syntacticError("type expected here", currentToken.spelling);
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

    // ======================= IDENTIFIERS ======================

    // Call parseIdent rather than match(Token.ID). 
    // In Assignment 3, an Identifier node will be constructed in here.


    void parseIdent() throws SyntaxError {

        if (currentToken.kind == Token.ID) {
            currentToken = scanner.getToken();
        } else 
            syntacticError("identifier expected here", "");
    }


    // ======================= STATEMENTS ==============================


    // compound-stmt -> "{" var-decl* stmt* "}"
   
    void parseCompoundStmt() throws SyntaxError {

        match(Token.LCURLY);
        while (isType(currentToken.kind)) {
            parseVarDecl(); 
        }
        while (currentToken.kind != Token.RCURLY) {
            parseStmt();
        }
        match(Token.RCURLY);
    }

    //stmt -> compound-stmt
    //     |  if-stmt
    //     |  for-stmt
    //     |  while-stmt
    //     |  break-stmt
    //     |  continue-stmt
    //     |  return-stmt
    //     |  expr-stmt
   
    void parseStmt() throws SyntaxError {

        switch (currentToken.kind) {

            case Token.LCURLY:
                parseCompoundStmt();
                break;

            case Token.IF:
                parseIfStmt();
                break;

            case Token.FOR:
                parseForStmt();
                break;

            case Token.WHILE:
                parseWhileStmt();
                break;

            case Token.BREAK:
                parseBreakStmt();
                break;

            case Token.CONTINUE:
                parseContinueStmt();
                break;

            case Token.RETURN:
                parseReturnStmt();
                break;

            default:
                parseExprStmt();
                break;

        }
    }

    // if-stmt -> "if" "(" expr ")" stmt ( "else" stmt )?
  
    void parseIfStmt() throws SyntaxError {

        match(Token.IF);

        match(Token.LPAREN);
        parseExpr();
        match(Token.RPAREN);

        parseStmt();

        if (currentToken.kind == Token.ELSE) {
            accept();
            parseStmt();
        }
    }

    // for-stmt -> "for" "(" expr? ";" expr? ";" expr? ")" stmt
   
    void parseForStmt() throws SyntaxError {

        match(Token.FOR);
        match(Token.LPAREN);

        if (currentToken.kind != Token.SEMICOLON) {
            parseExpr();
        }
        match(Token.SEMICOLON);

        if (currentToken.kind != Token.SEMICOLON) {
            parseExpr();
        }
        match(Token.SEMICOLON);

        if (currentToken.kind != Token.RPAREN) {
            parseExpr();
        }
        match(Token.RPAREN);

        parseStmt();
    }
    
    // while-stmt -> "while" "(" expr ")" stmt
   
    void parseWhileStmt() throws SyntaxError {

        match(Token.WHILE);

        match(Token.LPAREN);
        parseExpr();
        match(Token.RPAREN);

        parseStmt();
    }

    // break-stmt -> "break" ";" 
    
    void parseBreakStmt() throws SyntaxError {

        match(Token.BREAK);
        match(Token.SEMICOLON);
    }

    // continue-stmt -> "continue" ";"
   
    void parseContinueStmt() throws SyntaxError {

        match(Token.CONTINUE);
        match(Token.SEMICOLON);
    }

    // return-stmt -> "return" expr? ";"
    
    void parseReturnStmt() throws SyntaxError {

        match(Token.RETURN);
        if (currentToken.kind != Token.SEMICOLON) {
            parseExpr();
        }
        match(Token.SEMICOLON);
    }

    // expr-stmt -> expr? ";"
    
    void parseExprStmt() throws SyntaxError {

        if (currentToken.kind != Token.SEMICOLON) {
            parseExpr();
        } 
        match(Token.SEMICOLON);
    }

    // ======================= OPERATORS ======================

    // Call acceptOperator rather than accept(). 
    // In Assignment 3, an Operator Node will be constructed in here.

    void acceptOperator() throws SyntaxError {

        currentToken = scanner.getToken();
    }


    // ======================= EXPRESSIONS ======================

    // expr -> assignment-expr
    
    void parseExpr() throws SyntaxError {

        parseAssignmentExpr();
    }

    // assignment-expr -> cond-or-expr ( "=" cond-or-expr )* 
   
    void parseAssignmentExpr() throws SyntaxError {

        parseCondOrExpr();
        while (currentToken.kind == Token.EQ) {
            acceptOperator();
            parseCondOrExpr();
        }
    }

    // cond-or-expr -> cond-and-expr ("||" cond-and-expr)*

    void parseCondOrExpr() throws SyntaxError {

        parseCondAndExpr();
        while (currentToken.kind == Token.OROR) {
            acceptOperator();
            parseCondAndExpr();
        }
    }

    // cond-and-expr -> equality-expr ("&&" equality-expr)*

    void parseCondAndExpr() throws SyntaxError {

        parseEqualityExpr();
        while (currentToken.kind == Token.ANDAND) {
            acceptOperator();
            parseEqualityExpr();
        }
    }

    // equality-expr -> rel-expr ("==" rel-expr | 
    //                            "!=" rel-expr)*

    void parseEqualityExpr() throws SyntaxError {

        parseRelExpr();
        while (currentToken.kind == Token.EQEQ ||
               currentToken.kind == Token.NOTEQ) {
            acceptOperator();
            parseRelExpr();
        }
    }

    // rel-expr -> additive-expr ("<" additive-expr  | 
    //                            "<=" additive-expr | 
    //                            ">" additive-expr  | 
    //                            ">=" additive-expr)*

    void parseRelExpr() throws SyntaxError {

        parseAdditiveExpr();
        while (currentToken.kind == Token.LT ||
               currentToken.kind == Token.LTEQ ||
               currentToken.kind == Token.GT ||
               currentToken.kind == Token.GTEQ) {
            acceptOperator();
            parseAdditiveExpr();
        }
    }

    // additive-expr -> multiplicative-expr ("+" multiplicative-expr | 
    //                                       "-" multiplicative-expr)*

    void parseAdditiveExpr() throws SyntaxError {

        parseMultiplicativeExpr();
        while (currentToken.kind == Token.PLUS ||
               currentToken.kind == Token.MINUS) {
            acceptOperator();
            parseMultiplicativeExpr();
        }
    }

    // multiplicative-expr -> unary-expr ("*" unary-expr | 
    //                                    "/" unary-expr)*

    void parseMultiplicativeExpr() throws SyntaxError {

        parseUnaryExpr();
        while (currentToken.kind == Token.MULT ||
               currentToken.kind == Token.DIV) {
            acceptOperator();
            parseUnaryExpr();
        }
    }

    // unary-expr -> "+" unary-expr | 
    //            |  "-" unary-expr
    //            |  "!" unary-expr
    //            |  primary-expr 

    void parseUnaryExpr() throws SyntaxError {

        if (currentToken.kind == Token.PLUS ||
            currentToken.kind == Token.MINUS ||
            currentToken.kind == Token.NOT) {
            acceptOperator();
            parseUnaryExpr();
        } else {
            parsePrimaryExpr();
        }
    }

    // primary-expr -> identifier (arg-list | "[" expr "]")?
    //              |  "(" expr ")"
    //              |  INTLITERAL
    //              |  FLOATLITERAL   
    //              |  BOOLLITERAL
    //              |  STRINGLITERAL

    void parsePrimaryExpr() throws SyntaxError {

        switch (currentToken.kind) {

            case Token.ID:
                parseIdent();
                if (currentToken.kind == Token.LPAREN) {
                    parseArgList();
                } else if (currentToken.kind == Token.LBRACKET) {
                    accept();
                    parseExpr();
                    match(Token.RBRACKET);
                }
                break;

            case Token.LPAREN:
                accept();
                parseExpr();
                match(Token.RPAREN);
                break;

            case Token.INTLITERAL:
                parseIntLiteral();
                break;

            case Token.FLOATLITERAL:
                parseFloatLiteral();
                break;

            case Token.BOOLEANLITERAL:
                parseBooleanLiteral();
                break;

            case Token.STRINGLITERAL:
                parseStringLiteral();
                break;

            default:
                syntacticError("illegal primary expression", currentToken.spelling);

        }
    }

    // ========================= PARAMETERS =======================

    // para-list -> "(" proper-para-list? ")" 

    void parseParaList() throws SyntaxError {

        match(Token.LPAREN);
        if (currentToken.kind != Token.RPAREN) {
            parseProperParaList();
        }
        match(Token.RPAREN);
    }

    // proper-para-list -> para-decl ( "," para-decl )*
    
    void parseProperParaList() throws SyntaxError {

        parseParaDecl();
        while (currentToken.kind == Token.COMMA) {
            accept();
            parseParaDecl();
        }
    }

    // para-decl -> type declarator
    
    void parseParaDecl() throws SyntaxError {

        parseType();
        parseDeclarator();
    }

    // ========================= ARGUMENTS =======================

    // arg-list -> "(" proper-arg-list? ")"
    
    void parseArgList() throws SyntaxError {

        match(Token.LPAREN);
        if (currentToken.kind != Token.RPAREN) {
            parseProperArgList();
        }
        match(Token.RPAREN);
    }

    // proper-arg-list -> expr ( "," expr )* 
    
    void parseProperArgList() throws SyntaxError {

        parseExpr();
        while (currentToken.kind == Token.COMMA) {
            accept();
            parseExpr();
        }
    }

    // ========================== LITERALS ========================

    // Call these methods rather than accept().  In Assignment 3, 
    // literal AST nodes will be constructed inside these methods. 

    void parseIntLiteral() throws SyntaxError {

        if (currentToken.kind == Token.INTLITERAL) {
            currentToken = scanner.getToken();
        } else 
            syntacticError("integer literal expected here", "");
    }

    void parseFloatLiteral() throws SyntaxError {

        if (currentToken.kind == Token.FLOATLITERAL) {
            currentToken = scanner.getToken();
        } else 
            syntacticError("float literal expected here", "");
    }

    void parseBooleanLiteral() throws SyntaxError {

        if (currentToken.kind == Token.BOOLEANLITERAL) {
            currentToken = scanner.getToken();
        } else 
            syntacticError("boolean literal expected here", "");
    }

    void parseStringLiteral() throws SyntaxError {

        if (currentToken.kind == Token.STRINGLITERAL) {
            currentToken = scanner.getToken();
        } else 
            syntacticError("string literal expected here", "");
    }

}
