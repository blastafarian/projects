.class public test/while
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
.var 1 is vc$ Ltest/while; from L0 to L1
	new test/while
	dup
	invokenonvirtual test/while/<init>()V
	astore_1
L2:
	iconst_1
	ifeq L3
L4:
L5:
	goto L2
L3:
L1:
	return
	
	; set limits used by this method
.limit locals 2
.limit stack 2
.end method
