.class public test\DeclGlobal04
.super java/lang/Object
	
.field static f F
	
	; standard class static initializer 
.method static <clinit>()V
	
	iconst_0
	putstatic test\DeclGlobal04/f F
	
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
.method public static main([Ljava/lang/String;)V
L0:
.var 0 is argv [Ljava/lang/String; from L0 to L1
.var 1 is vc$ Ltest\DeclGlobal04; from L0 to L1
	new test\DeclGlobal04
	dup
	invokenonvirtual test\DeclGlobal04/<init>()V
	astore_1
	return
L1:
	return
	
	; set limits used by this method
.limit locals 2
.limit stack 2
.end method
