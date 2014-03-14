.class public test\ExprAssign06
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
.var 1 is vc$ Ltest\ExprAssign06; from L0 to L1
	new test\ExprAssign06
	dup
	invokenonvirtual test\ExprAssign06/<init>()V
	astore_1
.var 2 is a [I from L0 to L1
	iconst_1
	newarray int
	dup
	iconst_0
	bipush 11
	iastore
	astore_2
.var 3 is b [I from L0 to L1
	iconst_1
	newarray int
	dup
	iconst_0
	bipush 22
	iastore
	astore_3
	aload_2
	iconst_0
	aload_3
	iconst_0
	iaload
	iastore
	return
L1:
	return
	
	; set limits used by this method
.limit locals 4
.limit stack 4
.end method
