.class public test\ExprAssign05
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
.var 1 is vc$ Ltest\ExprAssign05; from L0 to L1
	new test\ExprAssign05
	dup
	invokenonvirtual test\ExprAssign05/<init>()V
	astore_1
.var 2 is b Z from L0 to L1
	iconst_1
	istore_2
	iconst_0
	istore_2
	return
L1:
	return
	
	; set limits used by this method
.limit locals 3
.limit stack 2
.end method
