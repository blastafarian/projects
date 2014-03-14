/***
 *** Recogniser.java            
 ***/

/* At this stage, this parser accepts a subset of VC defined by
 * the following grammar. 
 *
 * You need to modify the supplied parsing methods (if necessary) and 
 * add the missing ones to obtain a parser for the VC language.
 *
 * --- Jingling Xue (14/8/2006)
 
    program -> declaration*

    declaration -> type identifier (func-decl-tail | var-decl-tail)

    // function declarations

    func-decl-tail -> para-list compound-stmt

    // variable declarations

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

    assignment-expr -> ( cond-or-expr "=" )* cond-or-expr

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

    unary-expr -> primary-expr ("+" primary-expr | 
                                "-" primary-expr | 
                                "!" primary-expr)*

    primary-expr -> identifier (arg-list | "[" expr "]")?
                 |  "(" expr ")"
                 |  INTLITERAL
                 |  FLOATLITERAL   
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

    // match checks to see f the current token matches tokenExpected.
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

        switch (currentToken.kind) {
            case Token.LPAREN:
                parseFuncDeclTail();
                break;
            case Token.SEMICOLON:
            case Token.COMMA:
            case Token.EQ:
            case Token.LBRACKET:
                parseVarDeclTail();
                break;
            default:
                syntacticError("expected variable declaration or function declaration", "");
        }
    }

    void parseFuncDeclTail() throws SyntaxError {

        match(Token.VOID);
        parseIdent();
        match(Token.LPAREN);
        match(Token.RPAREN);
        parseCompoundStmt();
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
            match(Token.EQ);
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
            match(Token.LBRACKET);
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

            if (currentToken.kind == Token.COMMA) {
                while (currentToken.kind == Token.COMMA) {
                    match(Token.COMMA);
                    parseExpr();
                }
            }

            match(Token.RCURLY);
        } else {
            parseExpr();
        }
    }

    // ======================= STATEMENTS ==============================


    void parseCompoundStmt() throws SyntaxError {

        match(Token.LCURLY);
        parseStmtList();
        match(Token.RCURLY);
    }

    // Here, a new nontermial has been introduced to define { stmt } *
    void parseStmtList() throws SyntaxError {

        while (currentToken.kind != Token.RCURLY) 
            parseStmt();
    }

    void parseStmt() throws SyntaxError {

        switch (currentToken.kind) {

            case Token.CONTINUE:
                parseContinueStmt();
                break;

            default:
                parseExprStmt();
                break;

        }
    }

    void parseContinueStmt() throws SyntaxError {

        match(Token.CONTINUE);
        match(Token.SEMICOLON);

    }

    void parseExprStmt() throws SyntaxError {

        if (currentToken.kind == Token.ID
                || currentToken.kind == Token.INTLITERAL
                || currentToken.kind == Token.MINUS
                || currentToken.kind == Token.LPAREN) {
            parseExpr();
            match(Token.SEMICOLON);
        } else {
            match(Token.SEMICOLON);
        }
    }


    // ===================== PRIMITIVE TYPES ====================

    // type -> void | boolean | int | float
    void parseType() throws SyntaxError {

        switch (currentToken.kind) {
            case Token.VOID:
            case Token.BOOLEAN:
            case Token.INT:
            case Token.FLOAT:
                accept();
                break;
            default:
                syntacticError("type expected here", currentToken.spelling);
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

    // ======================= OPERATORS ======================

    // Call acceptOperator rather than accept(). 
    // In Assignment 3, an Operator Node will be constructed in here.

    void acceptOperator() throws SyntaxError {

        currentToken = scanner.getToken();
    }


    // ======================= EXPRESSIONS ======================

    void parseExpr() throws SyntaxError {
        parseAssignExpr();
    }


    void parseAssignExpr() throws SyntaxError {

        parseAdditiveExpr();

    }

    void parseAdditiveExpr() throws SyntaxError {

        parseMultiplicativeExpr();
        while (currentToken.kind == Token.PLUS) {
            acceptOperator();
            parseMultiplicativeExpr();
        }
    }

    void parseMultiplicativeExpr() throws SyntaxError {

        parseUnaryExpr();
        while (currentToken.kind == Token.MULT) {
            acceptOperator();
            parseUnaryExpr();
        }
    }

    void parseUnaryExpr() throws SyntaxError {

        switch (currentToken.kind) {
            case Token.MINUS:
                {
                    acceptOperator();
                    parseUnaryExpr();
                }
                break;

            default:
                parsePrimaryExpr();
                break;

        }
    }

    void parsePrimaryExpr() throws SyntaxError {

        switch (currentToken.kind) {

            case Token.ID:
                parseIdent();
                break;

            case Token.LPAREN:
                {
                    accept();
                    parseExpr();
                    match(Token.RPAREN);
                }
                break;

            case Token.INTLITERAL:
                parseIntLiteral();
                break;

            default:
                syntacticError("illegal parimary expression", currentToken.spelling);

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

}
