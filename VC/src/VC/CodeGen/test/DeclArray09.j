.class public test\DeclArray09
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
.method ff([Z)V
L0:
.var 0 is this Ltest\DeclArray09; from L0 to L1
.var 1 is b [Z from L0 to L1
L1:
	
	; return may not be present in a VC function returning void
	; The following return inserted by the VC compiler
	return
	
	; set limits used by this method
.limit locals 2
.limit stack 0
.end method
.method public static main([Ljava/lang/String;)V
L0:
.var 0 is argv [Ljava/lang/String; from L0 to L1
.var 1 is vc$ Ltest\DeclArray09; from L0 to L1
	new test\DeclArray09
	dup
	invokenonvirtual test\DeclArray09/<init>()V
	astore_1
.var 2 is haha [Z from L0 to L1
	iconst_1
	newarray boolean
	dup
	iconst_0
	iconst_1
	bastore
	astore_2
	return
L1:
	return
	
	; set limits used by this method
.limit locals 3
.limit stack 4
.end method
