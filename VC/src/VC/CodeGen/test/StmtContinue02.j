.class public test\stmtcontinue02
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
.var 1 is vc$ Ltest\stmtcontinue02; from L0 to L1
	new test\stmtcontinue02
	dup
	invokenonvirtual test\stmtcontinue02/<init>()V
	astore_1
.var 2 is i I from L0 to L1
	iconst_1
	dup
	istore_2
	pop
L2:
	iload_2
	bipush 8
	if_icmplt L5
	iconst_0
	goto L6
L5:
	iconst_1
L6:
	ifeq L4
L7:
	iconst_5
	pop
	iconst_1
	ifeq L9
L11:
	goto L3
L12:
	goto L10
L9:
L13:
	iconst_3
	pop
L14:
L10:
L8:
L3:
	iload_2
	iconst_2
	imul
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
