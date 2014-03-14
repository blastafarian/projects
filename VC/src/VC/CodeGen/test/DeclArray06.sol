Compiled from "DeclArray06.java"
class DeclArray06 extends java.lang.Object{
DeclArray06();
  Code:
   0:	aload_0
   1:	invokespecial	#1; //Method java/lang/Object."<init>":()V
   4:	return

void func(int[]);
  Code:
   0:	return

public static void main(java.lang.String[]);
  Code:
   0:	new	#2; //class DeclArray06
   3:	dup
   4:	invokespecial	#3; //Method "<init>":()V
   7:	astore_1
   8:	iconst_1
   9:	newarray int
   11:	dup
   12:	iconst_0
   13:	bipush	9
   15:	iastore
   16:	astore_2
   17:	aload_1
   18:	aload_2
   19:	invokevirtual	#4; //Method func:([I)V
   22:	return

}

