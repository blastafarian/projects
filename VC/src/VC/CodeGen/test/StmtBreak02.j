.class public test\stmtbreak02
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
.var 1 is vc$ Ltest\stmtbreak02; from L0 to L1
	new test\stmtbreak02
	dup
	invokenonvirtual test\stmtbreak02/<init>()V
	astore_1
.var 2 is i I from L0 to L1
	iconst_0
	dup
	istore_2
	pop
L2:
	iload_2
	bipush 9
	if_icmplt L5
	iconst_0
	goto L6
L5:
	iconst_1
L6:
	ifeq L4
L7:
	bipush 16
	pop
	iload_2
	bipush 6
	if_icmpgt L11
	iconst_0
	goto L12
L11:
	iconst_1
L12:
	ifeq L9
L13:
	goto L4
L14:
	goto L10
L9:
L10:
	bipush 17
	pop
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
	bipush 18
	pop
L1:
	return
	
	; set limits used by this method
.limit locals 3
.limit stack 2
.end method
