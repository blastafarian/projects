.class public test\ExprCall02
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
.method f([I)I
L0:
.var 0 is this Ltest\ExprCall02; from L0 to L1
.var 1 is a [I from L0 to L1
	aload_1
	iconst_0
	bipush 99
	iastore
L1:
	nop
	
	; set limits used by this method
.limit locals 2
.limit stack 3
.end method
.method public static main([Ljava/lang/String;)V
L0:
.var 0 is argv [Ljava/lang/String; from L0 to L1
.var 1 is vc$ Ltest\ExprCall02; from L0 to L1
	new test\ExprCall02
	dup
	invokenonvirtual test\ExprCall02/<init>()V
	astore_1
.var 2 is myarray [I from L0 to L1
	bipush 10
	newarray int
	dup
	iconst_0
	bipush 7
	iastore
	dup
	iconst_1
	bipush 6
	iastore
	dup
	iconst_2
	iconst_5
	iastore
	dup
	iconst_3
	iconst_4
	iastore
	dup
	iconst_4
	iconst_3
	iastore
	dup
	iconst_5
	iconst_2
	iastore
	dup
	bipush 6
	iconst_1
	iastore
	dup
	bipush 7
	iconst_0
	iastore
	dup
	bipush 8
	bipush 9
	iastore
	dup
	bipush 9
	bipush 8
	iastore
	astore_2
	aload_1
	aload_2
	invokevirtual test\ExprCall02/f([I)I
	pop
L1:
	return
	
	; set limits used by this method
.limit locals 3
.limit stack 4
.end method
