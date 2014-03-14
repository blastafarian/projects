.class public test\break1
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
.var 1 is vc$ Ltest\break1; from L0 to L1
	new test\break1
	dup
	invokenonvirtual test\break1/<init>()V
	astore_1
L2:
	iconst_1
	ifeq L3
L4:
.var 2 is i I from L4 to L5
	invokestatic VC/lang/System.getInt()I
	istore_2
.var 3 is j I from L4 to L5
	invokestatic VC/lang/System.getInt()I
	istore_3
	iload_2
	iload_3
	if_icmpge L8
	iconst_0
	goto L9
L8:
	iconst_1
L9:
	ifeq L6
	iload_2
	invokestatic VC/lang/System/putIntLn(I)V
	goto L7
L6:
	iload_3
	invokestatic VC/lang/System/putIntLn(I)V
L7:
	goto L3
L5:
	goto L2
L3:
L1:
	return
	
	; set limits used by this method
.limit locals 4
.limit stack 0
.end method
