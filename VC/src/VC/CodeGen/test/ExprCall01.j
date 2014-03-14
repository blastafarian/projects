.class public test\ExprCall01
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
.method increment(I)I
L0:
.var 0 is this Ltest\ExprCall01; from L0 to L1
.var 1 is i I from L0 to L1
	iload_1
	iconst_1
	iadd
	ireturn
L1:
	nop
	
	; set limits used by this method
.limit locals 2
.limit stack 2
.end method
.method multiply(II)I
L0:
.var 0 is this Ltest\ExprCall01; from L0 to L1
.var 1 is i I from L0 to L1
.var 2 is j I from L0 to L1
	iload_1
	iload_2
	imul
	ireturn
L1:
	nop
	
	; set limits used by this method
.limit locals 3
.limit stack 2
.end method
.method subtract(II)I
L0:
.var 0 is this Ltest\ExprCall01; from L0 to L1
.var 1 is i I from L0 to L1
.var 2 is j I from L0 to L1
	iload_1
	iload_2
	isub
	ireturn
L1:
	nop
	
	; set limits used by this method
.limit locals 3
.limit stack 2
.end method
.method public static main([Ljava/lang/String;)V
L0:
.var 0 is argv [Ljava/lang/String; from L0 to L1
.var 1 is vc$ Ltest\ExprCall01; from L0 to L1
	new test\ExprCall01
	dup
	invokenonvirtual test\ExprCall01/<init>()V
	astore_1
	aload_1
	bipush 9
	aload_1
	iconst_3
	aload_1
	iconst_1
	invokevirtual test\ExprCall01/increment(I)I
	invokevirtual test\ExprCall01/multiply(II)I
	invokevirtual test\ExprCall01/subtract(II)I
	pop
	return
L1:
	return
	
	; set limits used by this method
.limit locals 2
.limit stack 6
.end method
