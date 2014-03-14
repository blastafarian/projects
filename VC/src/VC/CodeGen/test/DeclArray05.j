.class public test\DeclArray05
.super java/lang/Object
	
.field static pi [I
	
	; standard class static initializer 
.method static <clinit>()V
	
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
	putstatic test\DeclArray05/pi [I
	
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
.var 1 is vc$ Ltest\DeclArray05; from L0 to L1
	new test\DeclArray05
	dup
	invokenonvirtual test\DeclArray05/<init>()V
	astore_1
	return
L1:
	return
	
	; set limits used by this method
.limit locals 2
.limit stack 2
.end method
