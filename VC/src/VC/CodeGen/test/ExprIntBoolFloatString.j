.class public test\ExprIntBoolFloatstring
.super java/lang/Object
	
.field static i I
.field static b Z
.field static f F
	
	; standard class static initializer 
.method static <clinit>()V
	
	bipush 9
	bipush 8
	iadd
	putstatic test\ExprIntBoolFloatstring/i I
	iconst_1
	ifeq L0
	iconst_1
	ifeq L0
	iconst_1
	goto L1
L0:
	iconst_0
L1:
	putstatic test\ExprIntBoolFloatstring/b Z
	ldc 5.3
	fneg
	ldc 9.0
	ldc 3.0
	fdiv
	fadd
	ldc 1.1
	fadd
	putstatic test\ExprIntBoolFloatstring/f F
	
	; set limits used by this method
.limit locals 0
.limit stack 3
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
.method hello()V
L0:
.var 0 is this Ltest\ExprIntBoolFloatstring; from L0 to L1
	ldc "i am a string"
	invokestatic VC/lang/System/putStringLn(Ljava/lang/String;)V
L1:
	
	; return may not be present in a VC function returning void
	; The following return inserted by the VC compiler
	return
	
	; set limits used by this method
.limit locals 1
.limit stack 1
.end method
.method public static main([Ljava/lang/String;)V
L0:
.var 0 is argv [Ljava/lang/String; from L0 to L1
.var 1 is vc$ Ltest\ExprIntBoolFloatstring; from L0 to L1
	new test\ExprIntBoolFloatstring
	dup
	invokenonvirtual test\ExprIntBoolFloatstring/<init>()V
	astore_1
	return
L1:
	return
	
	; set limits used by this method
.limit locals 2
.limit stack 2
.end method
