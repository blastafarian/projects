.class public test\ExprAssign08
.super java/lang/Object
	
.field static a I
.field static b I
.field static c I
.field static d I
.field static e I
	
	; standard class static initializer 
.method static <clinit>()V
	
	iconst_0
	putstatic test\ExprAssign08/a I
	iconst_0
	putstatic test\ExprAssign08/b I
	iconst_0
	putstatic test\ExprAssign08/c I
	iconst_0
	putstatic test\ExprAssign08/d I
	iconst_0
	putstatic test\ExprAssign08/e I
	
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
.var 1 is vc$ Ltest\ExprAssign08; from L0 to L1
	new test\ExprAssign08
	dup
	invokenonvirtual test\ExprAssign08/<init>()V
	astore_1
	bipush 99
	dup
	putstatic test\ExprAssign08/e I
	dup
	putstatic test\ExprAssign08/d I
	dup
	putstatic test\ExprAssign08/c I
	dup
	putstatic test\ExprAssign08/b I
	putstatic test\ExprAssign08/a I
L1:
	return
	
	; set limits used by this method
.limit locals 2
.limit stack 2
.end method
