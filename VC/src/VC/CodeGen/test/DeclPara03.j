.class public test\DeclPara03
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
.method f(IZF)F
L0:
.var 0 is this Ltest\DeclPara03; from L0 to L1
.var 1 is i I from L0 to L1
.var 2 is b Z from L0 to L1
.var 3 is fpara F from L0 to L1
	iconst_1
	i2f
	freturn
L1:
	nop
	
	; set limits used by this method
.limit locals 4
.limit stack 1
.end method
.method public static main([Ljava/lang/String;)V
L0:
.var 0 is argv [Ljava/lang/String; from L0 to L1
.var 1 is vc$ Ltest\DeclPara03; from L0 to L1
	new test\DeclPara03
	dup
	invokenonvirtual test\DeclPara03/<init>()V
	astore_1
	return
L1:
	return
	
	; set limits used by this method
.limit locals 2
.limit stack 2
.end method
