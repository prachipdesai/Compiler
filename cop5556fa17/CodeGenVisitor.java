package cop5556fa17;

import java.util.ArrayList;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.TypeUtils.Type;
import cop5556fa17.AST.ASTNode;
import cop5556fa17.AST.ASTVisitor;
import cop5556fa17.AST.Declaration;
import cop5556fa17.AST.Declaration_Image;
import cop5556fa17.AST.Declaration_SourceSink;
import cop5556fa17.AST.Declaration_Variable;
import cop5556fa17.AST.Expression;
import cop5556fa17.AST.Expression_Binary;
import cop5556fa17.AST.Expression_BooleanLit;
import cop5556fa17.AST.Expression_Conditional;
import cop5556fa17.AST.Expression_FunctionAppWithExprArg;
import cop5556fa17.AST.Expression_FunctionAppWithIndexArg;
import cop5556fa17.AST.Expression_Ident;
import cop5556fa17.AST.Expression_IntLit;
import cop5556fa17.AST.Expression_PixelSelector;
import cop5556fa17.AST.Expression_PredefinedName;
import cop5556fa17.AST.Expression_Unary;
import cop5556fa17.AST.Index;
import cop5556fa17.AST.LHS;
import cop5556fa17.AST.Program;
import cop5556fa17.AST.Sink;
import cop5556fa17.AST.Sink_Ident;
import cop5556fa17.AST.Sink_SCREEN;
import cop5556fa17.AST.Source;
import cop5556fa17.AST.Source_CommandLineParam;
import cop5556fa17.AST.Source_Ident;
import cop5556fa17.AST.Source_StringLiteral;
import cop5556fa17.AST.Statement_In;
import cop5556fa17.AST.Statement_Out;
import cop5556fa17.AST.Statement_Assign;
//import cop5556fa17.image.ImageFrame;
//import cop5556fa17.image.ImageSupport;

public class CodeGenVisitor implements ASTVisitor, Opcodes {

	/**
	 * All methods and variable static.
	 */


	/**
	 * @param DEVEL
	 *            used as parameter to genPrint and genPrintTOS
	 * @param GRADE
	 *            used as parameter to genPrint and genPrintTOS
	 * @param sourceFileName
	 *            name of source file, may be null.
	 */
	public CodeGenVisitor(boolean DEVEL, boolean GRADE, String sourceFileName) {
		super();
		this.DEVEL = DEVEL;
		this.GRADE = GRADE;
		this.sourceFileName = sourceFileName;
	}

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;

	MethodVisitor mv; // visitor of method currently under construction
	FieldVisitor fv; 

	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;

	public static String getJVMType(Type t) {
		String ret = "";
		if(t == Type.INTEGER) 
			ret = "I";
		else if(t == Type.BOOLEAN) 
			ret = "Z";
		else if( t == Type.IMAGE )
			ret = ImageSupport.ImageDesc;
		else if( t == Type.URL)
			ret = ImageSupport.StringDesc;
		else if ( t == Type.FILE)
			ret = ImageSupport.StringDesc;
		else throw new RuntimeException("Wrong type encountered" + t);
		
		return ret;
	}

	Object getInitVal(Type t) {
		Object iv= null;
		
		if(t == Type.INTEGER) 
			iv = new Integer(0);
		else if(t == Type.BOOLEAN) 
			iv = new Boolean(true);
		else if (t == Type.IMAGE) 
			iv = ImageSupport.makeImage(256, 256);
		
		return iv;
	}

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		className = program.name;  
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;

		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object", null);
		cw.visitSource(sourceFileName, null);
		// create main method
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
		// initialize
		mv.visitCode();		
		//add label before first instruction
		Label mainStart = new Label();
		mv.visitLabel(mainStart);		
		// if GRADE, generates code to add string to log
		//CodeGenUtils.genLog(GRADE, mv, "entering main");

		// visit decs and statements to add field to class
		//  and instructions to main method, respectivley
		
		//*******************************************************************
		fv = cw.visitField(ACC_STATIC, "x", "I", null, null);
		fv.visitEnd();
		//fv = cw.visitField(access, name, desc, signature, value)
		fv = cw.visitField(ACC_STATIC, "y", "I", null, null);
		fv.visitEnd();
		fv = cw.visitField(ACC_STATIC, "X", "I", null, null);
		fv.visitEnd();
		fv = cw.visitField(ACC_STATIC, "Y", "I", null, null);
		fv.visitEnd();
		fv = cw.visitField(ACC_STATIC, "r", "I", null, null);		
		fv.visitEnd();		
		fv = cw.visitField(ACC_STATIC, "a", "I", null, null);		
		fv.visitEnd();		
		fv = cw.visitField(ACC_STATIC, "R", "I", null, null);		
		fv.visitEnd();		
		fv = cw.visitField(ACC_STATIC, "A", "I", null, null);		
		fv.visitEnd();		
		fv = cw.visitField(ACC_STATIC, "Z", "I", null, new Integer(16777215));		
		fv.visitEnd();		
		fv = cw.visitField(ACC_STATIC, "DEF_X", "I", null, new Integer(256));		
		fv.visitEnd();		
		fv = cw.visitField(ACC_STATIC, "DEF_Y", "I", null, new Integer(256));		
		fv.visitEnd();
		//********************************************************************
		
		ArrayList<ASTNode> decsAndStatements = program.decsAndStatements;
		for (ASTNode node : decsAndStatements) {
			node.visit(this, arg);
		}

		//generates code to add string to log
		//CodeGenUtils.genLog(GRADE, mv, "leaving main");

		//adds the required (by the JVM) return statement to main
		mv.visitInsn(RETURN);

		//adds label at end of code
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);

		//handles parameters and local variables of main. Right now, only args
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart, mainEnd, 0);

		//Sets max stack size and number of local vars.
		//Because we use ClassWriter.COMPUTE_FRAMES as a parameter in the constructor,
		//asm will calculate this itself and the parameters are ignored.
		//If you have trouble with failures in this routine, it may be useful
		//to temporarily set the parameter in the ClassWriter constructor to 0.
		//The generated classfile will not be correct, but you will at least be
		//able to see what is in it.
		mv.visitMaxs(0, 0);

		//terminate construction of main method
		mv.visitEnd();

		//terminate class construction
		cw.visitEnd();


		//generate classfile as byte array and return
		return cw.toByteArray();
	}

	@Override
	public Object visitDeclaration_Variable(Declaration_Variable declaration_Variable, Object arg) throws Exception {
		// TODO 
		String fieldType = getJVMType(declaration_Variable.type_declaration);
		String fieldName = declaration_Variable.getname();
		Object initValue = getInitVal(declaration_Variable.type_declaration);  
		Expression e = declaration_Variable.gete();

		fv = cw.visitField(ACC_STATIC, fieldName, fieldType, null, initValue);
		fv.visitEnd();

		if(e !=null) {
			e.visit(this, arg);
			//CodeGenUtils.genLogTOS(GRADE, mv, declaration_Variable.type_declaration);
			mv.visitFieldInsn(PUTSTATIC, className, fieldName, fieldType);
		} 

		return null;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpression_Binary(Expression_Binary expression_Binary, Object arg) throws Exception {
		// TODO 
		Label set_true = new Label();
		Label end_true = new Label();
		Expression e0 = expression_Binary.gete0();
		Expression e1 = expression_Binary.gete1();
		Kind op = expression_Binary.getop();

		e0.visit(this, arg);
		e1.visit(this, arg);

		switch(op) {
		case OP_OR:{
			mv.visitInsn(IOR);
			break;
		}
		case OP_AND:{
			mv.visitInsn(IAND);
			break;
		}
		case OP_PLUS:{
			mv.visitInsn(IADD);
			break;
		}
		case OP_MINUS:{
			mv.visitInsn(ISUB);
			break;
		}
		case OP_TIMES:{
			mv.visitInsn(IMUL);
			break;
		}
		case OP_DIV:{
			mv.visitInsn(IDIV);
			break;
		}
		case OP_MOD:{
			mv.visitInsn(IREM);
			break;
		}
		case OP_EQ:{
			mv.visitJumpInsn(IF_ICMPEQ, set_true);
			mv.visitLdcInsn(false);
			break;
		}
		case OP_NEQ:{
			mv.visitJumpInsn(IF_ICMPNE, set_true);
			mv.visitLdcInsn(false);
			break;
		}
		case OP_GT:{
			mv.visitJumpInsn(IF_ICMPGT, set_true);
			mv.visitLdcInsn(false);
			break;
		}
		case OP_GE:{
			mv.visitJumpInsn(IF_ICMPGE, set_true);
			mv.visitLdcInsn(false);
			break;
		}
		case OP_LT:{
			mv.visitJumpInsn(IF_ICMPLT, set_true);
			mv.visitLdcInsn(false);
			break;
		}
		case OP_LE:{
			mv.visitJumpInsn(IF_ICMPLE, set_true);
			mv.visitLdcInsn(false);
			break;
		}
		default:
		}
		mv.visitJumpInsn(GOTO, end_true);
		mv.visitLabel(set_true);
		mv.visitLdcInsn(true);
		mv.visitLabel(end_true);
		//throw new UnsupportedOperationException();
		//CodeGenUtils.genLogTOS(GRADE, mv, expression_Binary.type_expr);
		return null;
	}

	@Override
	public Object visitExpression_Unary(Expression_Unary expression_Unary, Object arg) throws Exception {
		// TODO 
		Label set_true = new Label();
		Label end_true = new Label();
		Expression e = expression_Unary.gete();
		String fieldType = getJVMType(expression_Unary.gete().type_expr);

		Kind op = expression_Unary.getop();
		e.visit(this, arg);
		switch(op) {

		case OP_PLUS:{
			break;
		}
		case OP_MINUS:{
			mv.visitInsn(INEG);
			break;
		}
		case OP_EXCL:{
			if(fieldType == "I") {
				mv.visitLdcInsn(INTEGER.MAX_VALUE);
				mv.visitInsn(IXOR);
			}
			else if(fieldType == "Z") {
				mv.visitJumpInsn(IFEQ, set_true);
				mv.visitLdcInsn(false);
			}
			break;
		}
		default:
		}

		mv.visitJumpInsn(GOTO, end_true);
		mv.visitLabel(set_true);
		mv.visitLdcInsn(true);
		mv.visitLabel(end_true);

		//throw new UnsupportedOperationException();
		//CodeGenUtils.genLogTOS(GRADE, mv, expression_Unary.type_expr);
		return null;
	}

	// generate code to leave the two values on the stack
	@Override
	public Object visitIndex(Index index, Object arg) throws Exception {
		// TODO HW6
		Expression e0 = index.gete0();
		e0.visit(this, arg);
		Expression e1 = index.gete1();
		e1.visit(this, arg);

		if(!(index.isCartesian())) {
			mv.visitInsn(DUP2);
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className  , "cart_x" , RuntimeFunctions.cart_xSig,false);
			mv.visitInsn(DUP_X2);
			mv.visitInsn(POP);
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className , "cart_y" , RuntimeFunctions.cart_ySig,false);

		}
		return null;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpression_PixelSelector(Expression_PixelSelector expression_PixelSelector, Object arg)
			throws Exception {
		// TODO HW6
		Index i = expression_PixelSelector.getindex();
		String fieldName = expression_PixelSelector.getname();
		String fieldType = ImageSupport.ImageDesc;

		mv.visitFieldInsn(GETSTATIC, className, fieldName, fieldType);
		i.visit(this, arg);
		mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className  , "getPixel" , ImageSupport.getPixelSig,false);

		return null;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpression_Conditional(Expression_Conditional expression_Conditional, Object arg)
			throws Exception {
		// TODO 

		Expression condition = expression_Conditional.getcondition();
		Expression trueExpression= expression_Conditional.gettrueExpression();
		Expression falseExpression = expression_Conditional.getfalseExpression();

		Label visitfalse = new Label();
		Label visittrue = new Label();

		condition.visit(this, arg);

		mv.visitJumpInsn(IFEQ, visitfalse);
		trueExpression.visit(this, arg);
		mv.visitJumpInsn(GOTO, visittrue);
		mv.visitLabel(visitfalse);
		falseExpression.visit(this, arg);
		mv.visitLabel(visittrue);

		//throw new UnsupportedOperationException();
		//CodeGenUtils.genLogTOS(GRADE, mv, expression_Conditional.trueExpression.type_expr);

		return null;
	}


	@Override
	public Object visitDeclaration_Image(Declaration_Image declaration_Image, Object arg) throws Exception {
		// TODO HW6
		String fieldName = declaration_Image.getname();
		String fieldType = getJVMType(declaration_Image.type_declaration);
		Object initValue = null;
		Source s = declaration_Image.getsource();
		Expression x = declaration_Image.getxSize();
		Expression y = declaration_Image.getySize();

		fv = cw.visitField(ACC_STATIC, fieldName, fieldType, null, initValue);
		fv.visitEnd();

		if(s != null) {
			s.visit(this, arg);
			if(x == null && y == null) {
				mv.visitInsn(ACONST_NULL);
				mv.visitInsn(ACONST_NULL);
			}
			else {
				x.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer" , "valueOf" , "(I)Ljava/lang/Integer;",false);
				y.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer" , "valueOf" , "(I)Ljava/lang/Integer;",false);
			}
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className , "readImage" , ImageSupport.readImageSig ,false);
		}
		else if (s == null) {
			if(x == null && y == null) {
				mv.visitFieldInsn(GETSTATIC, className, "DEF_X", "I");
				mv.visitFieldInsn(GETSTATIC, className, "DEF_Y", "I");
			}
			else {
				x.visit(this, arg);
				y.visit(this, arg);
			}
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className , "makeImage" ,  ImageSupport.makeImageSig,false);
		}

		mv.visitFieldInsn(PUTSTATIC, className, fieldName, fieldType); //image ref is stored..

		return null;//throw new UnsupportedOperationException();
	}


	@Override
	public Object visitSource_StringLiteral(Source_StringLiteral source_StringLiteral, Object arg) throws Exception {
		// TODO HW6
		String fieldName = source_StringLiteral.getfileOrUrl();
		String fieldType = ImageSupport.StringDesc;
		mv.visitLdcInsn(new String(fieldName));
		//mv.visitFieldInsn(GETSTATIC, className, fieldName, fieldType);
		return null;//throw new UnsupportedOperationException();
	}



	@Override
	public Object visitSource_CommandLineParam(Source_CommandLineParam source_CommandLineParam, Object arg)
			throws Exception {
		// TODO 
		Expression paramNum = source_CommandLineParam.getparamNum();

		mv.visitVarInsn(ALOAD, 0);
		paramNum.visit(this, arg);
		mv.visitInsn(AALOAD);
		//CodeGenUtils.genLogTOS(GRADE, mv, source_CommandLineParam.type_source);
		return null;

		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitSource_Ident(Source_Ident source_Ident, Object arg) throws Exception {
		// TODO HW6
		String fieldType = ImageSupport.StringDesc;
		String fieldName = source_Ident.getname();

		mv.visitFieldInsn(GETSTATIC, className, fieldName, fieldType);
		return null;//throw new UnsupportedOperationException();
	}


	@Override
	public Object visitDeclaration_SourceSink(Declaration_SourceSink declaration_SourceSink, Object arg)
			throws Exception {
		// TODO HW6
		String fieldName = declaration_SourceSink.getname();
		Source s = declaration_SourceSink.getsource();
		String fieldType = getJVMType(declaration_SourceSink.type_declaration);
		Object initValue = null;

		fv = cw.visitField(ACC_STATIC, fieldName, fieldType, null, initValue);
		fv.visitEnd();
		if(s!=null) {
			s.visit(this, arg);
			mv.visitFieldInsn(PUTSTATIC, className, fieldName, fieldType);
		}

		return null;//throw new UnsupportedOperationException();
	}



	@Override
	public Object visitExpression_IntLit(Expression_IntLit expression_IntLit, Object arg) throws Exception {
		// TODO 
//		String fieldName = null;
//		String fieldType = null;

		mv.visitLdcInsn(new Integer(expression_IntLit.getvalue()));
		//CodeGenUtils.genLogTOS(GRADE, mv, Type.INTEGER);
		return null;

		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpression_FunctionAppWithExprArg(
			Expression_FunctionAppWithExprArg expression_FunctionAppWithExprArg, Object arg) throws Exception {
		// TODO HW6

		Expression e = expression_FunctionAppWithExprArg.getarg();
		e.visit(this, arg);

		Kind function = expression_FunctionAppWithExprArg.getfunction();

		if(function == Kind.KW_abs)
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className , "abs" , RuntimeFunctions.absSig,false);

		else if(function == Kind.KW_log)
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className , "log" ,  RuntimeFunctions.logSig,false);

		return null;//throw new UnsupportedOperationException();

	}

	@Override
	public Object visitExpression_FunctionAppWithIndexArg(
			Expression_FunctionAppWithIndexArg expression_FunctionAppWithIndexArg, Object arg) throws Exception {
		// TODO HW6
		expression_FunctionAppWithIndexArg.getarg().gete0().visit(this, arg);
		expression_FunctionAppWithIndexArg.getarg().gete1().visit(this, arg);

		Kind function = expression_FunctionAppWithIndexArg.getfunction();

		if(function == Kind.KW_cart_x)
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className , "cart_x" ,  RuntimeFunctions.cart_xSig,false);

		else if(function == Kind.KW_cart_y)
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className , "cart_y" , RuntimeFunctions.cart_ySig,false);

		else if(function == Kind.KW_polar_a)
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className , "polar_a" ,RuntimeFunctions.polar_aSig,false);

		else if(function == Kind.KW_polar_r)
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className , "polar_r" , RuntimeFunctions.polar_rSig,false);


		return null;//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpression_PredefinedName(Expression_PredefinedName expression_PredefinedName, Object arg)
			throws Exception {
		// TODO HW6

		Kind k = expression_PredefinedName.getkind();

		if(k == Kind.KW_x)
			mv.visitFieldInsn(GETSTATIC, className, "x", "I");
		if(k == Kind.KW_y)
			mv.visitFieldInsn(GETSTATIC, className, "y", "I");

		if(k == Kind.KW_X)
			mv.visitFieldInsn(GETSTATIC, className, "X", "I");
		if(k == Kind.KW_Y)
			mv.visitFieldInsn(GETSTATIC, className, "Y", "I");

		if(k == Kind.KW_DEF_X)
			mv.visitFieldInsn(GETSTATIC, className, "DEF_X", "I");
		if(k == Kind.KW_DEF_Y)
			mv.visitFieldInsn(GETSTATIC, className, "DEF_Y", "I");
		
		if(k == Kind.KW_r)
			mv.visitFieldInsn(GETSTATIC, className, "r", "I");
		if(k == Kind.KW_a)
			mv.visitFieldInsn(GETSTATIC, className, "a", "I");
		if(k == Kind.KW_R)
			mv.visitFieldInsn(GETSTATIC, className, "R", "I");
		if(k == Kind.KW_A)
			mv.visitFieldInsn(GETSTATIC, className, "A", "I");
		
		if(k == Kind.KW_Z)
			mv.visitFieldInsn(GETSTATIC, className, "Z", "I");
		
		return null;//throw new UnsupportedOperationException();

	}

	/** For Integers and booleans, the only "sink"is the screen, so generate code to print to console.
	 * For Images, load the Image onto the stack and visit the Sink which will generate the code to handle the image.
	 */
	@Override
	public Object visitStatement_Out(Statement_Out statement_Out, Object arg) throws Exception {
		// TODO in HW5:  only INTEGER and BOOLEAN
		// TODO HW6 remaining cases 
		Sink sink = statement_Out.getsink();

		String fieldName = statement_Out.getname();
		String fieldType = getJVMType(statement_Out.getDec().type_declaration);
		Object initValue = getInitVal(statement_Out.getDec().type_declaration);

		if (fieldType == "I") {
			mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
			mv.visitFieldInsn(GETSTATIC, className, fieldName, fieldType);
			CodeGenUtils.genLogTOS(GRADE, mv, statement_Out.getDec().type_declaration);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
		}
		else if (fieldType == "Z") {
			mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
			mv.visitFieldInsn(GETSTATIC, className, fieldName, fieldType);
			CodeGenUtils.genLogTOS(GRADE, mv, statement_Out.getDec().type_declaration);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Z)V", false);
		}
		else if (fieldType == ImageSupport.ImageDesc) {
			mv.visitFieldInsn(GETSTATIC, className, fieldName, fieldType);
			CodeGenUtils.genLogTOS(GRADE, mv, statement_Out.getDec().type_declaration);
			sink.visit(this, arg);
		}
		return null;
		//throw new UnsupportedOperationException();
	}

	/**
	 * Visit source to load rhs, which will be a String, onto the stack
	 * 
	 *  In HW5, you only need to handle INTEGER and BOOLEAN
	 *  Use java.lang.Integer.parseInt or java.lang.Boolean.parseBoolean 
	 *  to convert String to actual type. 
	 *  
	 *  TODO HW6 remaining types
	 */
	@Override
	public Object visitStatement_In(Statement_In statement_In, Object arg) throws Exception {
		// TODO (see comment )
		Source s = statement_In.getsource();

		String fieldName = statement_In.getname();
		String fieldType = getJVMType(statement_In.getDec().type_declaration);
		Object initValue = null;
		s.visit(this, arg);
		if(fieldType == "I") {
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I",false);
		}
		else if(fieldType == "Z") {
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z",false);
		}
		else if(fieldType == ImageSupport.ImageDesc) {
			Declaration_Image dec_Img = (Declaration_Image)statement_In.getDec();
			Expression x = dec_Img.getxSize();
			Expression y = dec_Img.getySize();

			if(x == null && y == null) {
				mv.visitInsn(ACONST_NULL);
				mv.visitInsn(ACONST_NULL);
			}
			else {
				x.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer" , "valueOf" , "(I)Ljava/lang/Integer;",false);
				y.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer" , "valueOf" , "(I)Ljava/lang/Integer;",false);
			}

			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className , "readImage" ,ImageSupport.readImageSig,false);
		}
		//CodeGenUtils.genLogTOS(GRADE, mv, statement_In.getDec().type_declaration);
		mv.visitFieldInsn(PUTSTATIC, className, fieldName, fieldType);



		return null;
		//throw new UnsupportedOperationException();
	}


	/**
	 * In HW5, only handle INTEGER and BOOLEAN types.
	 */
	@Override
	public Object visitStatement_Assign(Statement_Assign statement_Assign, Object arg) throws Exception {
		//TODO  (see comment)
		String fieldType = getJVMType(statement_Assign.getlhs().type_lhs);
		Expression e = statement_Assign.gete();
		LHS lhs = statement_Assign.getlhs();
		String fieldName = statement_Assign.lhs.getname();

		if(fieldType == "I" || fieldType == "Z") {
			e.visit(this, arg);
			lhs.visit(this, arg);
		}
		else if(fieldType == ImageSupport.ImageDesc) {
			String JVMType = getJVMType(Type.INTEGER);
			
			mv.visitFieldInsn(GETSTATIC, className, fieldName, ImageSupport.ImageDesc);
			mv.visitInsn(DUP);
			// Values of X and Y are assigned.
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getX", ImageSupport.getXSig,false);
			mv.visitFieldInsn(PUTSTATIC, className, "X", JVMType);
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getY", ImageSupport.getYSig, false);
			mv.visitFieldInsn(PUTSTATIC, className, "Y", JVMType);

			
			mv.visitInsn(ICONST_0);
			mv.visitFieldInsn(PUTSTATIC, className, "x", JVMType);
			
			mv.visitInsn(ICONST_0);
			mv.visitFieldInsn(PUTSTATIC, className, "y", JVMType);
			Label l0 = new Label();

			mv.visitLabel(l0);
			if(!(statement_Assign.isCartesian())) {
				mv.visitFieldInsn(GETSTATIC, className, "x", JVMType);
				mv.visitFieldInsn(GETSTATIC, className, "y", JVMType);
				mv.visitInsn(DUP2);
				mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_r", RuntimeFunctions.polar_rSig,false);
				mv.visitFieldInsn(PUTSTATIC, className, "r", JVMType);
				mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_a", RuntimeFunctions.polar_aSig,false);
				mv.visitFieldInsn(PUTSTATIC, className, "a", JVMType);
			}

			e.visit(this, arg);
			mv.visitFieldInsn(GETSTATIC, className, fieldName, ImageSupport.ImageDesc);
			mv.visitFieldInsn(GETSTATIC, className, "x", JVMType);
			mv.visitFieldInsn(GETSTATIC, className, "y", JVMType);
			
			lhs.visit(this, arg);

			mv.visitFieldInsn(GETSTATIC, className, "y", JVMType);
			mv.visitInsn(ICONST_1);
			mv.visitInsn(IADD);
			mv.visitFieldInsn(PUTSTATIC, className, "y", JVMType);
			mv.visitFieldInsn(GETSTATIC, className, "y", JVMType);
			mv.visitFieldInsn(GETSTATIC, className, "Y", JVMType);
			mv.visitJumpInsn(IF_ICMPLT, l0);

			mv.visitInsn(ICONST_0);
			mv.visitFieldInsn(PUTSTATIC, className, "y", JVMType);
			
			mv.visitFieldInsn(GETSTATIC, className, "x", JVMType);
			mv.visitInsn(ICONST_1);
			mv.visitInsn(IADD);
			mv.visitFieldInsn(PUTSTATIC, className, "x", JVMType);
			mv.visitFieldInsn(GETSTATIC, className, "x", JVMType);
			mv.visitFieldInsn(GETSTATIC, className, "X", JVMType);
			mv.visitJumpInsn(IF_ICMPLT, l0);

		}


		/*//to get the value of X -> it will be 256 if default else some value will be stored.
			mv.visitFieldInsn(GETSTATIC, className, fieldName, "Ljava/awt/image/BufferedImage;");
			mv.visitMethodInsn(INVOKESTATIC, className, "getX","(Ljava/awt/image/BufferedImage;)",false);
			mv.visitFieldInsn(PUTSTATIC, className, "X", getJVMType(Type.INTEGER));

			//to get the value of Y -> it will be 256 if default else some value will be stored.
			mv.visitFieldInsn(GETSTATIC, className, fieldName, "Ljava/awt/image/BufferedImage;");
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getY", "(Ljava/awt/image/BufferedImage;)",false);
			mv.visitFieldInsn(PUTSTATIC, className, "Y", getJVMType(Type.INTEGER));

			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitInsn(ICONST_0);
			mv.visitFieldInsn(PUTSTATIC, className, "x", "I");
			Label l1 = new Label();
			mv.visitJumpInsn(GOTO, l1);
			Label l2 = new Label();
			mv.visitLabel(l2);
			mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			mv.visitInsn(ICONST_0);
			mv.visitFieldInsn(PUTSTATIC, className, "y", "I");
			Label l3 = new Label();
			mv.visitJumpInsn(GOTO, l3);
			Label l4 = new Label();
			mv.visitLabel(l4);
			mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			////
			mv.visitFieldInsn(GETSTATIC,className, "y", "I");
			mv.visitInsn(ICONST_1);
			mv.visitInsn(IADD);
			mv.visitFieldInsn(PUTSTATIC, className, "y", "I");
			mv.visitLabel(l3);
			mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			mv.visitFieldInsn(GETSTATIC, className, "y", "I");
			mv.visitFieldInsn(GETSTATIC, className, "Y", "I");
			mv.visitJumpInsn(IF_ICMPLT, l4);
			Label l5 = new Label();
			mv.visitLabel(l5);
			mv.visitFieldInsn(GETSTATIC,className, "x", "I");
			mv.visitInsn(ICONST_1);
			mv.visitInsn(IADD);
			mv.visitFieldInsn(PUTSTATIC, className, "x", "I");
			mv.visitLabel(l1);
			mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			mv.visitFieldInsn(GETSTATIC, className, "x", "I");
			mv.visitFieldInsn(GETSTATIC,className, "X", "I");
			mv.visitJumpInsn(IF_ICMPLT, l2);
			Label l6 = new Label();
			mv.visitLabel(l6);
			mv.visitInsn(RETURN);
			Label l7 = new Label();
			mv.visitLabel(l7);
		 */

		return null;
		//throw new UnsupportedOperationException();
	}

	/**
	 * In HW5, only handle INTEGER and BOOLEAN types.
	 */
	@Override
	public Object visitLHS(LHS lhs, Object arg) throws Exception {
		//TODO  (see comment)

		String fieldName = lhs.getname();
		String fieldType = getJVMType(lhs.type_lhs);
		//Object initValue = getInitVal(lhs.type_lhs);
		//CodeGenUtils.genLogTOS(GRADE, mv, lhs.type_lhs);
		if(fieldType == "Z" || fieldType == "I" ) {
			mv.visitFieldInsn(PUTSTATIC, className, fieldName, fieldType);
		}
		else if(fieldType == ImageSupport.ImageDesc) {
//			Index i = lhs.getindex();
//			i.visit(this, arg);
//			mv.visitFieldInsn(GETSTATIC, className, fieldName, fieldType); //doubt
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className , "setPixel" , ImageSupport.setPixelSig,false);
		}
		return null;
		//throw new UnsupportedOperationException();
	}


	@Override
	public Object visitSink_SCREEN(Sink_SCREEN sink_SCREEN, Object arg) throws Exception {
		//TODO HW6

		mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className , "makeFrame" , ImageSupport.makeFrameSig,false);
		mv.visitInsn(POP);
		return null;//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitSink_Ident(Sink_Ident sink_Ident, Object arg) throws Exception {
		//TODO HW6
		String fieldName = sink_Ident.getname();
		String fieldType = ImageSupport.StringDesc;
		mv.visitFieldInsn(GETSTATIC, className, fieldName, fieldType);
		mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className , "write" , ImageSupport.writeSig ,false);
		return null;//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpression_BooleanLit(Expression_BooleanLit expression_BooleanLit, Object arg) throws Exception {
		//TODO

		String fieldType= getJVMType(expression_BooleanLit.type_expr);
		Object initValue = getInitVal(expression_BooleanLit.type_expr);
		mv.visitLdcInsn(new Boolean(expression_BooleanLit.getvalue()));
		//throw new UnsupportedOperationException();
		//CodeGenUtils.genLogTOS(GRADE, mv, Type.BOOLEAN);
		return null;
	}

	@Override
	public Object visitExpression_Ident(Expression_Ident expression_Ident,
			Object arg) throws Exception {
		//TODO
		String fieldName = expression_Ident.name;
		String fieldType = getJVMType(expression_Ident.type_expr); 
		Object initValue = getInitVal(expression_Ident.type_expr);

		mv.visitFieldInsn(GETSTATIC, className, fieldName, fieldType);	//get is done

		//throw new UnsupportedOperationException();
		//CodeGenUtils.genLogTOS(GRADE, mv, expression_Ident.type_expr);
		return null;
	}

}
