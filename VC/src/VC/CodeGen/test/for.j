.class public test\for
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
.var 1 is vc$ Ltest\for; from L0 to L1
	new test\for
	dup
	invokenonvirtual test\for/<init>()V
	astore_1
.var 2 is b Z from L0 to L1
	iconst_1
	istore_2
L2:
	iload_2
	iconst_1
	if_icmpeq L5
	iconst_0
	goto L6
L5:
	iconst_1
L6:
	ifeq L4
	iconst_1
	invokestatic VC/lang/System/putIntLn(I)V
L3:
	goto L2
L4:
	return
L1:
	return
	
	; set limits used by this method
.limit locals 3
.limit stack 2
.end method
