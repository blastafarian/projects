.class public test\ExprAssign10
.super java/lang/Object
	
.field static a [I
	
	; standard class static initializer 
.method static <clinit>()V
	
	iconst_1
	newarray int
	dup
	iconst_0
	iconst_0
	iastore
	putstatic test\ExprAssign10/a [I
	
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
.var 1 is vc$ Ltest\ExprAssign10; from L0 to L1
	new test\ExprAssign10
	dup
	invokenonvirtual test\ExprAssign10/<init>()V
	astore_1
.var 2 is b [I from L0 to L1
	iconst_1
	newarray int
	dup
	iconst_0
	iconst_0
	iastore
	astore_2
	getstatic test\ExprAssign10/a [I
	aload_2
	iconst_0
	iaload
	iaload
	pop
	return
L1:
	return
	
	; set limits used by this method
.limit locals 3
.limit stack 4
.end method
