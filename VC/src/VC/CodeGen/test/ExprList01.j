.class public test\ExprList01
.super java/lang/Object
	
.field static f [F
	
	; standard class static initializer 
.method static <clinit>()V
	
	iconst_2
	newarray float
	putstatic test\ExprList01/f [F
	
	; set limits used by this method
.limit locals 0
.limit stack 1
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
.method myself([F)I
L0:
.var 0 is this Ltest\ExprList01; from L0 to L1
.var 1 is f [F from L0 to L1
	aload_1
	iconst_2
	iconst_1
	i2f
	aload_1
	bipush 20
	faload
	fadd
	fastore
L1:
	nop
	
	; set limits used by this method
.limit locals 2
.limit stack 5
.end method
.method public static main([Ljava/lang/String;)V
L0:
.var 0 is argv [Ljava/lang/String; from L0 to L1
.var 1 is vc$ Ltest\ExprList01; from L0 to L1
	new test\ExprList01
	dup
	invokenonvirtual test\ExprList01/<init>()V
	astore_1
.var 2 is f [F from L0 to L1
	iconst_5
	newarray float
	dup
	iconst_0
	bipush 7
	i2f
	fastore
	dup
	iconst_1
	bipush 7
	i2f
	fastore
	dup
	iconst_2
	fconst_1
	fastore
	dup
	iconst_3
	fconst_0
	fneg
	fastore
	dup
	iconst_4
	ldc 5.4
	ldc 9.6
	fadd
	fastore
	astore_2
	return
L1:
	return
	
	; set limits used by this method
.limit locals 3
.limit stack 5
.end method
