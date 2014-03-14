.class public test\ExprAssign11
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
.var 1 is vc$ Ltest\ExprAssign11; from L0 to L1
	new test\ExprAssign11
	dup
	invokenonvirtual test\ExprAssign11/<init>()V
	astore_1
.var 2 is a [I from L0 to L1
.var 3 is i I from L0 to L1
	iconst_1
	istore_3
.var 4 is j I from L0 to L1
	iconst_2
	istore 4
	aload_2
	iload_3
	iconst_1
	iadd
	iload 4
	bipush 10
	iadd
	iastore
	return
L1:
	return
	
	; set limits used by this method
.limit locals 5
.limit stack 4
.end method
