.class public test\stmtbreak01
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
.var 1 is vc$ Ltest\stmtbreak01; from L0 to L1
	new test\stmtbreak01
	dup
	invokenonvirtual test\stmtbreak01/<init>()V
	astore_1
L2:
	iconst_1
	ifeq L3
L4:
	bipush 7
	pop
	iconst_1
	ifeq L6
L8:
	goto L3
L9:
	goto L7
L6:
L7:
	bipush 8
	pop
L5:
	goto L2
L3:
	bipush 9
	pop
L1:
	return
	
	; set limits used by this method
.limit locals 2
.limit stack 2
.end method
