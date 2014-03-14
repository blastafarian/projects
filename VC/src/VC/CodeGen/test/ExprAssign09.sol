Compiled from "ExprAssign09.java"
class ExprAssign09 extends java.lang.Object{
static int[] a;

ExprAssign09();
  Code:
   0:	aload_0
   1:	invokespecial	#1; //Method java/lang/Object."<init>":()V
   4:	return

public static void main(java.lang.String[]);
  Code:
   0:	getstatic	#2; //Field a:[I
   3:	iconst_0
   4:	bipush	99
   6:	iastore
   7:	return

static {};
  Code:
   0:	iconst_2
   1:	newarray int
   3:	dup
   4:	iconst_0
   5:	iconst_5
   6:	iastore
   7:	dup
   8:	iconst_1
   9:	bipush	7
   11:	iastore
   12:	putstatic	#2; //Field a:[I
   15:	return

}

