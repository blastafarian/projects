.class public test\stmtcontinue03
.super java/lang/Object
	
	
	; standard class static initializer 
.method static <clinit>()V
	
	
	; set limits used by this method
.limit locals 0
.limit stack 0
	return
.end method
	
	; standard constructor initializer 
.method public <init>()V
.limit stack 1
.limit locals 1
	aload_0
	invokespecial java/lang/Object/<init>()V
	return
.end method
.method public static main([Ljava/lang/String;)V
L0:
.var 0 is argv [Ljava/lang/String; from L0 to L1
.var 1 is vc$ Ltest\stmtcontinue03; from L0 to L1
	new test\stmtcontinue03
	dup
	invokenonvirtual test\stmtcontinue03/<init>()V
	astore_1
L2:
	iconst_1
	ifeq L3
L4:
.var 2 is i I from L4 to L5
	bipush 9
	istore_2
L6:
	iload_2
	bipush 13
	if_icmplt L9
	iconst_0
	goto L10
L9:
	iconst_1
L10:
	ifeq L8
L11:
	goto L7
L12:
L7:
	iload_2
	iconst_3
	iadd
	dup
	istore_2
	pop
	goto L6
L8:
L5:
	goto L2
L3:
L1:
	return
	
	; set limits used by this method
.limit locals 3
.limit stack 2
.end method
