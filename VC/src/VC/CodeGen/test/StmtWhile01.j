.class public test\StmtWhile01
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
.var 1 is vc$ Ltest\StmtWhile01; from L0 to L1
	new test\StmtWhile01
	dup
	invokenonvirtual test\StmtWhile01/<init>()V
	astore_1
.var 2 is dexxa I from L0 to L1
	iconst_5
	istore_2
L2:
	iload_2
	iconst_0
	if_icmpgt L4
	iconst_0
	goto L5
L4:
	iconst_1
L5:
	ifeq L3
L6:
	iload_2
	iconst_2
	isub
	istore_2
L7:
	goto L2
L3:
	return
L1:
	return
	
	; set limits used by this method
.limit locals 3
.limit stack 0
.end method
