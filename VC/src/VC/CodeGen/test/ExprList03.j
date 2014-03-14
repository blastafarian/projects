.class public test\ExprList03
.super java/lang/Object
	
.field static returnofthemack [I
	
	; standard class static initializer 
.method static <clinit>()V
	
	iconst_3
	newarray int
	dup
	iconst_0
	iconst_4
	iastore
	dup
	iconst_1
	iconst_5
	iastore
	dup
	iconst_2
	bipush 6
	iastore
	putstatic test\ExprList03/returnofthemack [I
	
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
.method d([I)F
L0:
.var 0 is this Ltest\ExprList03; from L0 to L1
.var 1 is para [I from L0 to L1
	getstatic test\ExprList03/returnofthemack [I
	iconst_3
	iconst_1
	ineg
	iadd
	aload_1
	bipush 7
	iaload
	iastore
	fconst_0
	freturn
L1:
	nop
	
	; set limits used by this method
.limit locals 2
.limit stack 4
.end method
.method public static main([Ljava/lang/String;)V
L0:
.var 0 is argv [Ljava/lang/String; from L0 to L1
.var 1 is vc$ Ltest\ExprList03; from L0 to L1
	new test\ExprList03
	dup
	invokenonvirtual test\ExprList03/<init>()V
	astore_1
.var 2 is thisiscompilers [I from L0 to L1
	iconst_1
	newarray int
	dup
	iconst_0
	iconst_1
	iastore
	astore_2
	return
L1:
	return
	
	; set limits used by this method
.limit locals 3
.limit stack 4
.end method
