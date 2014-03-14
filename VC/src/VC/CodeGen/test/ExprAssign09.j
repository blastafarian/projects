.class public test\ExprAssign09
.super java/lang/Object
	
.field static a [I
	
	; standard class static initializer 
.method static <clinit>()V
	
	bipush 7
	newarray int
	dup
	iconst_0
	iconst_5
	iastore
	dup
	iconst_1
	bipush 7
	iastore
	dup
	iconst_2
	bipush 11
	iastore
	dup
	iconst_3
	bipush 13
	iastore
	dup
	iconst_4
	bipush 17
	iastore
	dup
	iconst_5
	bipush 19
	iastore
	dup
	bipush 6
	bipush 23
	iastore
	putstatic test\ExprAssign09/a [I
	
	; set limits used by this method
.limit locals 0
.limit stack 4
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
.var 1 is vc$ Ltest\ExprAssign09; from L0 to L1
	new test\ExprAssign09
	dup
	invokenonvirtual test\ExprAssign09/<init>()V
	astore_1
	getstatic test\ExprAssign09/a [I
	iconst_0
	bipush 99
	iastore
	return
L1:
	return
	
	; set limits used by this method
.limit locals 2
.limit stack 3
.end method
