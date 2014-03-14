/*
 * IfStmt.java    
 */

package VC.ASTs;

import VC.Scanner.SourcePosition;

public class UnlessStmt extends Stmt {

  public Expr E;
  public Stmt S;

  public UnlessStmt(Expr eAST, Stmt sAST, SourcePosition position) {
    super (position);
    E = eAST;
    S = sAST;
    E.parent = S.parent = this;
  }

  public Object visit(Visitor v, Object o) {
    return v.visitUnlessStmt(this, o);
  }

}
