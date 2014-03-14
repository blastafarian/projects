.class public test\ExprArg01
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
.method givemefloat(F)I
L0:
.var 0 is this Ltest\ExprArg01; from L0 to L1
.var 1 is f F from L0 to L1
L1:
	nop
	
	; set limits used by this method
.limit locals 2
.limit stack 0
.end method
.method public static main([Ljava/lang/String;)V
L0:
.var 0 is argv [Ljava/lang/String; from L0 to L1
.var 1 is vc$ Ltest\ExprArg01; from L0 to L1
	new test\ExprArg01
	dup
	invokenonvirtual test\ExprArg01/<init>()V
	astore_1
	aload_1
	bipush 9
	i2f
	invokevirtual test\ExprArg01/givemefloat(F)I
	pop
	return
L1:
	return
	
	; set limits used by this method
.limit locals 2
.limit stack 2
.end method
