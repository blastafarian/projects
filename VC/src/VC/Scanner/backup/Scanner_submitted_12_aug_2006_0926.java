/**
 *	Scanner.java                        
 *
 *  Brendon Kwan, August 2006
 *  COMP3131 Compilers, Assignment 1
 **/

package VC.Scanner;

import VC.ErrorReporter;

public final class Scanner { 

    private SourceFile sourceFile;
    private boolean debug;

    private ErrorReporter errorReporter;
    private StringBuffer currentSpelling;
    private char currentChar;
    private SourcePosition sourcePos;

    // ---------------------------------------------------------
    // My member variables
    
    private int currentLine;
    private int currentCol;
    private int currentKind;
    private final int TAB_WIDTH = 8;
    
    // =========================================================

    public Scanner(SourceFile source, ErrorReporter reporter) {
        sourceFile = source;
        errorReporter = reporter;
        currentChar = sourceFile.getNextChar();
        debug = false;

        // you may initialise your counters for line and column numbers here
        currentLine = 1;
        currentCol = 1;
        currentKind = Token.ERROR;
    }

    public void enableDebugging() {
        debug = true;
    }

    // accept gets the next character from the source program.
    //
    // accept causes the following side effects
    // 1. the next character is got from the source program
    // 2. currentCol is updated ... (i'm lazy to describe currentCol)
    // 3. currentLine is updated ... (i'm lazy to describe currentCol)
    // 4. currentChar stores the next character
    // 5. currentSpelling has currentChar appended to it

    private void accept() {

        //    System.out.println("pre accept() : #" + currentChar + "# + " + currentLine + "(" + currentCol + ")");
        currentChar = sourceFile.getNextChar();

        // you may save the lexeme of the current token incrementally here

        // you may also increment your line and column counters here
        updateLineAndCol();
        //   System.out.println("post accept() : line = " + currentLine + " col = " + currentCol);
    }

    private void updateLineAndCol() {

        if (atLineTerminator()) {
            currentLine++;
            currentCol = 0;
        } else if (currentChar == '\t') {
            currentCol += TAB_WIDTH - (currentCol % TAB_WIDTH);
        } else { 
            currentCol++;
        }
    }

    private boolean atLineTerminator() { 

        boolean isCR = currentChar == '\r';
        boolean isLF = currentChar == '\n';

        if (isCR) {
            boolean nextIsLF = inspectChar(1) == '\n';
            return ! nextIsLF;
        } else {
            return isLF;
        } 
    }

    // inspectChar returns the n-th character after currentChar
    // in the input stream. 
    //
    // If there are fewer than nthChar characters between currentChar 
    // and the end of file marker, SourceFile.eof is returned.
    // 
    // Both currentChar and the current position in the input stream
    // are *not* changed. Therefore, a subsequent call to accept()
    // will always return the next char after currentChar.

    private char inspectChar(int nthChar) {
        return sourceFile.inspectChar(nthChar);
    }

    // nextToken assumes that currentChar is positioned at the start of a token.
    //
    // If the assumption about currentChar is true nextToken does the following things 
    // 1. the next token is read
    // 2. the kind of the token (as defined in Token.java) is returned
    // 3. currentChar is advanced to the character after the end of the token
    // 4. currentCol is the column number of the character after the end of the token 
    // 5. currentLine is advanced to the line number of the token (The spec says that a token can only span one line)
    // 6. currentSpelling holds the string representing the token
    // 7. sourcePos holds the position of the token in the source file
    //
    // If the assumption about currentChar is false the behaviour of nextToken is undefined....

    private int nextToken() {

        // Let's read the next token by calling readToken, which produces the following
        // five side effects  
        // 1. currentCol is the column number of the character one past the end of the token
        // 2. currentLine is the line number of the token
        // 3. currentChar is the character one past the end of the token 
        //    (we still need to call accept() before we return from this function)
        // 4. currentSpelling holds the string representing the token
        // 5. currentKind holds the kind of the token
        readToken();

        return currentKind;
    }

    // readToken reads the next token from the source file and 
    // produces the following five side effects  
    // 1. currentCol is the column number of the last character of the token 
    // 2. currentLine is the line number of the last character of the token
    // 3. currentChar is advanced to the last character of the token 
    //    (we still need to call accept() before we return from this function)
    // 4. currentSpelling holds the string representing the token
    // 5. currentKind holds the kind of the token

    private void readToken() {

        if (currentChar == SourceFile.eof) {
            //            System.out.println("readToken detected eof");
            currentSpelling = new StringBuffer("$");
            currentKind = Token.EOF;

        } else { 

            boolean recognisedToken = 
                didReadIdentifierOrKeyword() ||
                didReadOperator() || 
                didReadSeparator() ||
                didReadLiteral();

            if (! recognisedToken) {
                currentSpelling.append(currentChar);
                accept(); // Although we've detected a lexical analysis error we must move to the next character in the source file in order to process the whole source file
                currentKind = Token.ERROR;
            }
        }
    }

    private boolean isLetter(char c) {

        boolean isLower = c >= 'a' && c <= 'z';
        boolean isUpper = c >= 'A' && c <= 'Z';
        boolean isUnderscore = c == '_';

        return isLower || isUpper || isUnderscore; 
    }

    private boolean isDigit(char c) {

        return c >= '0' && c <= '9';
    }

    // didReadIdentifierOrKeyword detects if we are currently 
    // at a keyword or identifier. If we are, true is returned
    // and it reads the keyword or identifier. Else, false is
    // returned
    //
    //   Token.BOOLEAN     boolean
    //   Token.BREAK       break	
    //   Token.CONTINUE    continue
    //   Token.ELSE	       else
    //   Token.FLOAT 	   float
    //   Token.FOR         for
    //   Token.IF		   if
    //   Token.INT         int
    //   Token.RETURN	   return
    //   Token.VOID	       void
    //   Token.WHILE	   while

    private boolean didReadIdentifierOrKeyword() {

        // get the longest sequence of letters and digits that
        // starts with a letter
        if (isLetter(currentChar)) {
            getPossibleIdentifier();

            String s = currentSpelling.toString();

            if (s.equals("boolean")) {
                currentKind = Token.BOOLEAN;

            } else if (s.equals("break")) {
                currentKind = Token.BREAK;

            } else if (s.equals("continue")) {
                currentKind = Token.CONTINUE;

            } else if (s.equals("else")) {
                currentKind = Token.ELSE;

            } else if (s.equals("float")) {
                currentKind = Token.FLOAT;

            } else if (s.equals("for")) {
                currentKind = Token.FOR;

            } else if (s.equals("if")) {
                currentKind = Token.IF;

            } else if (s.equals("int")) {
                currentKind = Token.INT;

            } else if (s.equals("return")) {
                currentKind = Token.RETURN;

            } else if (s.equals("void")) {
                currentKind = Token.VOID;

            } else if (s.equals("while")) {
                currentKind = Token.WHILE;

            } else if (s.equals("true")) {
                currentKind = Token.BOOLEANLITERAL; // HACK, FIXME

            } else if (s.equals("false")) {
                currentKind = Token.BOOLEANLITERAL; // HACK, FIXME

            } else {
                // If we get here it means we have read an identifier
                currentKind = Token.ID;
            }

            return true; // We read an identifier or keyword
        } else { 
            return false; // Did not read an identifier or keyword
        }

    }

    private void getPossibleIdentifier() {

        while (isLetter(currentChar) || isDigit(currentChar)) {

            currentSpelling.append(currentChar);
            accept();
        }
    }

    // didReadOperator detects if the current token is an operator,
    // and if it is, reads the token and returns true. Else returns false.
    // 
    // An operator is either
    //   Token.PLUS	     +
    //   Token.MINUS	 -
    //   Token.MULT	     *
    //   Token.DIV		 /
    //   Token.NOT		 !
    //   Token.NOTEQ	 !=
    //   Token.EQ		 = 
    //   Token.EQEQ	     == 
    //   Token.LT		 <
    //   Token.LTEQ	     <=
    //   Token.GT		 >
    //   Token.GTEQ	     >=
    //   Token.ANDAND    && 
    //   Token.OROR      ||

    private boolean didReadOperator() {

        if (currentChar == '<' && inspectChar(1) == '=') {

            currentSpelling.append(currentChar);
            accept();
            currentSpelling.append(currentChar);
            accept();
            currentKind = Token.LTEQ;

        } else if (currentChar == '<') {

            currentSpelling.append(currentChar);
            accept();
            currentKind = Token.LT;

        } else if (currentChar == '>' && inspectChar(1) == '=') {

            currentSpelling.append(currentChar);
            accept();
            currentSpelling.append(currentChar);
            accept();
            currentKind = Token.GTEQ;

        } else if (currentChar == '>') {

            currentSpelling.append(currentChar);
            accept();
            currentKind = Token.GT;

        } else if (currentChar == '=' && inspectChar(1) == '=') {

            currentSpelling.append(currentChar);
            accept();
            currentSpelling.append(currentChar);
            accept();
            currentKind = Token.EQEQ;

        } else if (currentChar == '=') {

            currentSpelling.append(currentChar);
            accept();
            currentKind = Token.EQ;

        } else if (currentChar == '!' && inspectChar(1) == '=') {

            currentSpelling.append(currentChar);
            accept();
            currentSpelling.append(currentChar);
            accept();
            currentKind = Token.NOTEQ;

        } else if (currentChar == '!') {

            currentSpelling.append(currentChar);
            accept();
            currentKind = Token.NOT;

        } else if (currentChar == '&' && inspectChar(1) == '&') {

            currentSpelling.append(currentChar);
            accept();
            currentSpelling.append(currentChar);
            accept();
            currentKind = Token.ANDAND;

        } else if (currentChar == '|' && inspectChar(1) == '|') {

            currentSpelling.append(currentChar);
            accept();
            currentSpelling.append(currentChar);
            accept();
            currentKind = Token.OROR;

        } else if (currentChar == '+') {

            currentSpelling.append(currentChar);
            accept();
            currentKind = Token.PLUS;

        } else if (currentChar == '-') {

            currentSpelling.append(currentChar);
            accept();
            currentKind = Token.MINUS;

        } else if (currentChar == '*') {
            currentSpelling.append(currentChar);
            accept();
            currentKind = Token.MULT;

        } else if (currentChar == '/') {

            currentSpelling.append(currentChar);
            accept();
            currentKind = Token.DIV;

        } else {

            return false;
        }

        return true;
    }

    // didReadSeparator detects if the current token is a separator, and
    // if it is, reads the token and returns true. Else returns false.
    // 
    // A separator is either
    // Token.LCURLY    {  
    // Token.RCURLY    }
    // Token.LPAREN    (
    // Token.RPAREN    )
    // Token.LBRACKET  [
    // Token.RBRACKET  ]
    // Token.SEMICOLON ;
    // Token.COMMA     ,

    private boolean didReadSeparator() {

        if (currentChar == '{') {
            currentKind = Token.LCURLY;
            currentSpelling.append(currentChar);

        } else if (currentChar == '}') {
            currentKind = Token.RCURLY;
            currentSpelling.append(currentChar);

        } else if (currentChar == '(') {
            currentKind = Token.LPAREN;
            currentSpelling.append(currentChar);

        } else if (currentChar == ')') {
            currentKind = Token.RPAREN;
            currentSpelling.append(currentChar);

        } else if (currentChar == '[') {
            currentKind = Token.LBRACKET;
            currentSpelling.append(currentChar);

        } else if (currentChar == ']') {
            currentKind = Token.RBRACKET;
            currentSpelling.append(currentChar);

        } else if (currentChar == ';') {
            currentKind = Token.SEMICOLON;
            currentSpelling.append(currentChar);

        } else if (currentChar == ',') {
            currentKind = Token.COMMA;
            currentSpelling.append(currentChar);

        } else {
            return false;
        }

        accept();
        return true;
    }

    private boolean didReadLiteral() {

        boolean recognisedLiteral = didReadIntOrFloatLiteral() ||
            //        didReadBoolLiteral() || // this method is not required because didReadIdentifierOrKeyword() recognises boolean literals
            didReadStringLiteral();

        if (recognisedLiteral) {
            return true;
        } else {
            currentKind = Token.ERROR;
            return false;
        }
    }

    private boolean didReadIntOrFloatLiteral() {

        if (didReadInt()) {

            if (didReadFraction()) {

                if (didReadExponent()) {
                    // we're looking at a (digit+ fraction exponent), a float literal    
                    currentKind = Token.FLOATLITERAL;
                    return true;
                } else {
                    // we're looking at a (digit+ fraction), a float literal    
                    currentKind = Token.FLOATLITERAL;
                    return true;
                }

            } else if (currentChar == '.') {
                currentSpelling.append(currentChar);
                accept();

                if (didReadExponent()) {
                    // we're looking at a (digit+ . exponent), a float literal
                    currentKind = Token.FLOATLITERAL;
                    return true;
                } else {
                    // we're looking at a (digit+ .), a float literal
                    currentKind = Token.FLOATLITERAL;
                    return true;
                }

            } else if (didReadExponent()) {

                // we're looking at a (digit+ exponent), a float literal
                currentKind = Token.FLOATLITERAL;
                return true;

            } else {

                // we're looking at a (digit+), an integer literal
                currentKind = Token.INTLITERAL;
                return true;
            }

        } else if (didReadFraction()) {

            if (didReadExponent()) {

                // we're looking at a (fraction exponent), a float literal
                currentKind = Token.FLOATLITERAL;
                return true;

            } else {

                // we're looking at a (fraction), a float literal
                currentKind = Token.FLOATLITERAL;
                return true;
            }

        } else {

            // we looking at neither a int literal nor float literal
            // so we return false
            return false;
        }
    }

    private boolean didReadInt() {


        if (isDigit(currentChar)) {

            currentSpelling.append(currentChar);
            accept(); 

            while (isDigit(currentChar)) {
                currentSpelling.append(currentChar);
                accept();
            }

            return true; // we just read an integer literal

        } else {

            return false; // we did not read an integer literal
        }
    }

    private boolean didReadFraction() {

        if (currentChar == '.' && isDigit(inspectChar(1))) {

            currentSpelling.append(currentChar); // add the decimal point to the string
            accept(); // go to the digit after the decimal point

            return didReadInt();  // try to read the integer literal

        } else {
            return false; // did not read a fraction
        }
    }

    private boolean didReadExponent() {

        if (currentChar == 'E' || currentChar == 'e') {

            if (inspectChar(1) == '+' || inspectChar(1) == '-') {
                
                if (isDigit(inspectChar(2))) {

                    //We have detected (E|e) (+|-) (digit), which is an exponent
                    currentSpelling.append(currentChar); // add the E or e to the string
                    accept(); // read the E or e
                    currentSpelling.append(currentChar); // add the + or - to the string
                    accept(); // read the + or -
                    return didReadInt();
                    
                } else {

                    //We have detected (E|e) (+|-) (non-digit), which is not an exponent
                    return false;
                }

            } else { 

                // we have detected (E|e) (not + and not -)
                if (isDigit(inspectChar(1))) {

                    // we have detected (E|e) (not + and not -) (digit), which is an exponent
                    currentSpelling.append(currentChar); // add the E or e to the string
                    accept(); // read the E or e
                    return didReadInt();
                    
                } else {

                    //We have detected (E|e) (not +, not - and not digit), which is not an exponent
                    return false;
                }

            }


        } else { 

            // we are not looking at an exponent 
            return false; // did not read an exponent
        }
    }

    private boolean didReadStringLiteral() {

        if (currentChar == '"') {

            int startCol = currentCol;
            int startLine = currentLine;

            accept(); // accept the opening double quote (")  

            currentKind = Token.STRINGLITERAL;

            boolean inString = true;
            while (inString) {

                // have we detected the end of the string?
                if (currentChar == '"') {
                    inString = false;

                    // have we detected an unterminated string?
                } else if (currentChar == '\n' || currentChar == '\r' || currentChar == sourceFile.eof) { 
                    errorReporter.reportError(currentSpelling + ": unterminated string", "Token.STRINGLITERAL", new SourcePosition(startLine, startCol, startCol)); 
                    inString = false;

                    // have we detected an escape character?
                } else if (currentChar == '\\') { 

                    accept();  // accept the '\' but don't include it in the string's spelling 

                    if (isLegalEscapeCharacter(currentChar)) {

                        currentSpelling.append(toEscapeCharacter(currentChar)); // add the escape character to the string
                        accept();  // accept the character after the '\'

                    } else {
                        errorReporter.reportError("\\" + currentChar + ": illegal escape character", "Token.STRINGLITERAL", new SourcePosition(startLine, startCol, currentCol - 1)); 
                        currentSpelling.append('\\');
                        currentSpelling.append(currentChar); // add the character after the '\' 
                        accept();  // accept the character after the '\'

                    }

                    // otherwise we have detected a good character. Let's store and accept it.    
                } else {
                    currentSpelling.append(currentChar);
                    accept();
                }
            }
            if (currentChar == '\n' || currentChar == '\r' || currentChar == sourceFile.eof) { 
                sourcePos.charFinish = startCol + (currentSpelling.length() - 1) + 1; // the +1 is to include the opening double quote "
                sourcePos.lineFinish = startLine;
            } else {
                sourcePos.charFinish = currentCol;
                sourcePos.lineFinish = currentLine;
            }
            accept(); // Let's accept the character that terminates the string (it should be ", but if the string is unterminated, could be \n, \r or eof) 
            return true; // yes, we read a string from the source file
        } else {
            return false; // no, we did not read a string from the source file
        }
    }

    private boolean isLegalEscapeCharacter(char c) {

        return c == 'b' || 
            c == 'f' || 
            c == 'n' || 
            c == 'r' || 
            c == 't' ||
            c == '\'' ||
            c == '"' ||
            c == '\\'; 
    }

    private char toEscapeCharacter(char c) {
        if (c == 'b') {
            return '\b'; // backspace
        } else if (c == 'f') {
            return '\f'; // formfeed
        } else if (c == 'n') {
            return '\n'; // newline
        } else if (c == 'r') {
            return '\r'; // carriage return
        } else if (c == 't') {
            return '\t'; // tab
        } else if (c == '\'') {
            return c; // single quote
        } else if (c == '\"') {
            return c; // double quote
        } else if (c == '\\') {
            return c; // backslash
        } else {
            return SourceFile.eof; // should never get here....
        }
    }
    // --------------------------------------------------------------------
    //                      skipSpaceAndComments and its helpers
    // --------------------------------------------------------------------


    // skipSpaceAndComments gets characters until it reaches something that
    // is neither whitespace nor a comment.

    void skipSpaceAndComments() {

        while ((currentChar != SourceFile.eof) && 
                (skipOneSpace() || skipOneComment())) {
            //            System.out.println("Hello, curr char is " + currentChar);
        }
    }

    // skipOneSpace detects if currentChar is a whitespace character.
    //
    // If it is, currentChar is skipped and true is returned.
    // Else false is returned.
    //
    // A whitespace character is either 
    // 1. a space           ' '
    // 2. a tab             '\t'
    // 3. a formfeed        '\f'
    // 4. a carriage return '\r'
    // 5. a newline         '\n'

    private boolean skipOneSpace() {

        if (currentChar == ' ' || 
                currentChar == '\t' || 
                currentChar == '\f' || 
                currentChar == '\r' || 
                currentChar == '\n') {

            accept();
            return true;
        } else {
            return false;
        }
    }

    // skipOneComment detects if currentChar is the beginning of a comment.
    //
    // If it is, the entire comment is skipped and true is returned.
    // Else false is returned.
    //
    // There are two types of comments:
    // 1. A traditional comment. All the text from /* to the first */ is ignored. 
    // 2. A end-of-line comment. All text from // to the first line terminator is ignored.  

    private boolean skipOneComment() {

        // Did we detect a traditional comment?

        if (skipOneTraditionalComment()) {
            return true;
        } else if (skipOneEndOfLineComment()) {
            return true;
        } else {
            return false;
        }
    }

    private boolean skipOneTraditionalComment() { 

        // If we detect the character sequence /* then we are at
        // the beginning of a traditional comment and we should skip
        // the entire comment

        if (currentChar == '/' && inspectChar(1) == '*') {

            // remember the line and column of the start of the comment, 
            // so that we can print it out if the comment is unterminated
            int startLine = currentLine;
            int startCol = currentCol;
            //            System.out.println("Start of traditional comment. CurrentChar is " + currentChar);

            accept(); // skip the /
            accept(); // skip the *

            boolean inComment = true;
            while (inComment) {

                if (currentChar == '*' && inspectChar(1) == '/') {

                    // we've reached the end of the traditional comment
                    accept(); // skip the *
                    accept(); // skip the /
                    inComment = false; 

                } else if (currentChar == SourceFile.eof) {

                    // we've reached the end-of-file before reaching the
                    // end of the traditional comment. This means the
                    // comment is unterminated. So let's report an error
                    // and get out of here
                    errorReporter.reportError(": unterminated comment", "", new SourcePosition(startLine, startCol, startCol)); 
                    inComment = false;

                } else {

                    // we are still inside the traditional comment so let's
                    // skip this character and continue
                    accept();
                }
            }

            return true; // we were at a traditional comment and we skipped it 

        } else {

            return false; // we were not at the beginning of a traditional comment
        }

    }

    private boolean skipOneEndOfLineComment() { 

        // if we are at the character sequence // then we are at 
        // the start of an end-of-line comment

        if (currentChar == '/' && inspectChar(1) == '/') {

            // remember where the comment started, just in case we discover the comment
            // is unterminated and we need to tell the user where the comment started 
            int startLine = currentLine;
            int startCol = currentCol;

            //            System.out.println("Start of End of Line comment. CurrentChar is " + currentChar);

            boolean inComment = true;
            accept(); // skip the first /
            accept(); // skip the second /

            while (inComment) {

                // if we are at a line terminator then
                // we have reached the end of the end-of-line comment

                if (currentChar == '\r' || currentChar == '\n') {

                    accept(); // skip the line terminator 
                    inComment = false;

                } else if (currentChar == SourceFile.eof) {

                    // we've reached the end-of-file before reaching the
                    // end of the traditional comment. This means the
                    // comment is unterminated. So let's report an error
                    // and get out of here
                    errorReporter.reportError(": unterminated comment", "", new SourcePosition(startLine, startCol, startCol));
                    inComment = false;

                } else {

                    // we're still in the end-of-line comment
                    // let's skip this character and continue... 
                    accept();
                }
            }

            return true;
        } else {
            return false;
        }
    }

    // --------------------------------------------------------------------

    public Token getToken() {
        Token tok;
        int kind;

        // skip white space and comments

        skipSpaceAndComments();

        currentSpelling = new StringBuffer("");

        // You must record the position of the current token somehow

        sourcePos = new SourcePosition();
        sourcePos.lineStart = currentLine;
        sourcePos.charStart = currentCol;

        kind = nextToken();

        sourcePos.lineFinish = sourcePos.lineStart; 
        if (currentKind != Token.STRINGLITERAL) {
            sourcePos.charFinish = sourcePos.charStart + lengthOfToken(currentKind, currentSpelling) - 1; 
        }

        tok = new Token(kind, currentSpelling.toString(), sourcePos);

        // * do not remove these three lines
        if (debug)
            System.out.println(tok);
        return tok;
    }


    private int lengthOfToken(int kind, StringBuffer spelling) {

        if (kind == Token.STRINGLITERAL) {

            int length = 0;

            if (spelling.length() >= 1) {

                if (spelling.charAt(0) == '"') {
                    length++;
                    spelling.deleteCharAt(0);
                } 

                char lastChar = spelling.charAt(spelling.length() - 1);
                if (lastChar == '"') {
                    length++;
                    spelling.deleteCharAt(spelling.length() - 1);
                }
            }

            return length + spelling.length();
        } else {
            return spelling.length();
        }

    }

}
