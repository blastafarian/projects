.class public test\StmtIf01
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
.var 1 is vc$ Ltest\StmtIf01; from L0 to L1
	new test\StmtIf01
	dup
	invokenonvirtual test\StmtIf01/<init>()V
	astore_1
.var 2 is b I from L0 to L1
	iconst_5
	istore_2
.var 3 is a I from L0 to L1
	iconst_2
	istore_3
	iload_3
	iload_2
	if_icmpgt L4
	iconst_0
	goto L5
L4:
	iconst_1
L5:
	ifeq L2
L6:
	ldc "greater"
	invokestatic VC/lang/System/putStringLn(Ljava/lang/String;)V
L7:
	goto L3
L2:
	iload_3
	iload_2
	if_icmpeq L10
	iconst_0
	goto L11
L10:
	iconst_1
L11:
	ifeq L8
L12:
	ldc "equal"
	invokestatic VC/lang/System/putStringLn(Ljava/lang/String;)V
L13:
	goto L9
L8:
L14:
	ldc "less"
	invokestatic VC/lang/System/putStringLn(Ljava/lang/String;)V
L15:
L9:
L3:
	return
L1:
	return
	
	; set limits used by this method
.limit locals 4
.limit stack 0
.end method
