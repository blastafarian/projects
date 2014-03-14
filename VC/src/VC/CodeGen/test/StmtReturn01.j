.class public test\stmtreturn01
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
.method i()I
L0:
.var 0 is this Ltest\stmtreturn01; from L0 to L1
	bipush 8
	ireturn
L1:
	nop
	
	; set limits used by this method
.limit locals 1
.limit stack 1
.end method
.method f()F
L0:
.var 0 is this Ltest\stmtreturn01; from L0 to L1
	ldc 4.3
	freturn
L1:
	nop
	
	; set limits used by this method
.limit locals 1
.limit stack 1
.end method
.method b()Z
L0:
.var 0 is this Ltest\stmtreturn01; from L0 to L1
	iconst_1
	ireturn
L1:
	nop
	
	; set limits used by this method
.limit locals 1
.limit stack 1
.end method
.method public static main([Ljava/lang/String;)V
L0:
.var 0 is argv [Ljava/lang/String; from L0 to L1
.var 1 is vc$ Ltest\stmtreturn01; from L0 to L1
	new test\stmtreturn01
	dup
	invokenonvirtual test\stmtreturn01/<init>()V
	astore_1
	aload_1
	invokevirtual test\stmtreturn01/b()Z
	ifeq L2
L4:
	aload_1
	invokevirtual test\stmtreturn01/f()F
	aload_1
	invokevirtual test\stmtreturn01/i()I
	i2f
	fadd
	pop
L5:
	goto L3
L2:
L3:
	return
L1:
	return
	
	; set limits used by this method
.limit locals 2
.limit stack 2
.end method
