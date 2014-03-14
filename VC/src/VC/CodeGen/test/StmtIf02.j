.class public test\StmtIf02
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
.var 1 is vc$ Ltest\StmtIf02; from L0 to L1
	new test\StmtIf02
	dup
	invokenonvirtual test\StmtIf02/<init>()V
	astore_1
	iconst_5
	iconst_2
	if_icmpgt L4
	iconst_0
	goto L5
L4:
	iconst_1
L5:
	ifeq L2
L6:
L7:
	goto L3
L2:
L8:
L9:
L3:
	return
L1:
	return
	
	; set limits used by this method
.limit locals 2
.limit stack 0
.end method
