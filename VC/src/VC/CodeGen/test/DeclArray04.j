.class public test\DeclArray04
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
.var 1 is vc$ Ltest\DeclArray04; from L0 to L1
	new test\DeclArray04
	dup
	invokenonvirtual test\DeclArray04/<init>()V
	astore_1
.var 2 is pi [I from L0 to L1
	bipush 8
	newarray int
	dup
	iconst_0
	iconst_3
	iastore
	dup
	iconst_1
	iconst_1
	iastore
	dup
	iconst_2
	iconst_4
	iastore
	dup
	iconst_3
	iconst_1
	iastore
	dup
	iconst_4
	iconst_5
	iastore
	dup
	iconst_5
	bipush 9
	iastore
	dup
	bipush 6
	bipush 6
	iastore
	dup
	bipush 7
	iconst_2
	iastore
	astore_2
	return
L1:
	return
	
	; set limits used by this method
.limit locals 3
.limit stack 4
.end method
