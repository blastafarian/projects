.class public test\ExprList02
.super java/lang/Object
	
.field static bglobal [Z
	
	; standard class static initializer 
.method static <clinit>()V
	
	iconst_5
	newarray boolean
	dup
	iconst_0
	iconst_1
	bastore
	dup
	iconst_1
	iconst_0
	ifeq L4
	iconst_0
	ifeq L4
	iconst_1
	goto L5
L4:
	iconst_0
L5:
	ifeq L2
	iconst_0
	ifeq L2
	iconst_1
	goto L3
L2:
	iconst_0
L3:
	ifeq L0
	iconst_1
	goto L1
L0:
	iconst_1
	iconst_1
	ixor
L1:
	bastore
	dup
	iconst_2
	iconst_1
	bastore
	dup
	iconst_3
	iconst_0
	bastore
	dup
	iconst_4
	iconst_1
	bastore
	putstatic test\ExprList02/bglobal [Z
	
	; set limits used by this method
.limit locals 0
.limit stack 6
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
.method minime([Z)V
L0:
.var 0 is this Ltest\ExprList02; from L0 to L1
.var 1 is bpara [Z from L0 to L1
	aload_1
	iconst_1
	aload_1
	iconst_1
	baload
	iconst_1
	ixor
	bastore
L1:
	
	; return may not be present in a VC function returning void
	; The following return inserted by the VC compiler
	return
	
	; set limits used by this method
.limit locals 2
.limit stack 4
.end method
.method public static main([Ljava/lang/String;)V
L0:
.var 0 is argv [Ljava/lang/String; from L0 to L1
.var 1 is vc$ Ltest\ExprList02; from L0 to L1
	new test\ExprList02
	dup
	invokenonvirtual test\ExprList02/<init>()V
	astore_1
.var 2 is blocal [Z from L0 to L1
	iconst_5
	newarray boolean
	dup
	iconst_0
	iconst_1
	bastore
	dup
	iconst_1
	iconst_0
	bastore
	dup
	iconst_2
	iconst_1
	bastore
	dup
	iconst_3
	iconst_0
	bastore
	dup
	iconst_4
	iconst_1
	bastore
	astore_2
	return
L1:
	return
	
	; set limits used by this method
.limit locals 3
.limit stack 4
.end method
