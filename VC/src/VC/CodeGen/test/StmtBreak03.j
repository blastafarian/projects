.class public test\stmtbreak03
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
.var 1 is vc$ Ltest\stmtbreak03; from L0 to L1
	new test\stmtbreak03
	dup
	invokenonvirtual test\stmtbreak03/<init>()V
	astore_1
.var 2 is i I from L0 to L1
	iconst_0
	dup
	istore_2
	pop
L2:
	iload_2
	iconst_3
	if_icmplt L5
	iconst_0
	goto L6
L5:
	iconst_1
L6:
	ifeq L4
L7:
L9:
	iconst_1
	ifeq L10
L11:
	goto L10
L12:
	goto L9
L10:
	goto L4
L8:
L3:
	iload_2
	iconst_1
	iadd
	dup
	istore_2
	pop
	goto L2
L4:
	return
L1:
	return
	
	; set limits used by this method
.limit locals 3
.limit stack 2
.end method
