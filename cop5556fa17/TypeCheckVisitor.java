package cop5556fa17;

import java.util.HashMap;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;
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
import cop5556fa17.AST.Statement_Assign;
import cop5556fa17.AST.Statement_In;
import cop5556fa17.AST.Statement_Out;

public class TypeCheckVisitor implements ASTVisitor {

	HashMap<String, Declaration> map = new HashMap<String, Declaration>();

	@SuppressWarnings("serial")
	public static class SemanticException extends Exception {
		Token t;

		public SemanticException(Token t, String message) {
			super("line " + t.line + " pos " + t.pos_in_line + ": "+  message);
			this.t = t;
		}

	}		



	/**
	 * The program name is only used for naming the class.  It does not rule out
	 * variables with the same name.  It is returned for convenience.
	 * 
	 * @throws Exception 
	 */
	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		for (ASTNode node: program.decsAndStatements) {
			node.visit(this, arg);
		}
		return program.name;
	}

	@Override
	public Object visitDeclaration_Variable(
			Declaration_Variable declaration_Variable, Object arg)
					throws Exception {
		// TODO Auto-generated method stub
		Expression e = declaration_Variable.gete();
		//Token type = declaration_Variable.gettype();
		String name = declaration_Variable.getname();

		if (!map.containsKey(name)) {
			//map.put(name, declaration_Variable);
			declaration_Variable.type_declaration = TypeUtils.getType(declaration_Variable.firstToken);
			if (e != null) {
				e.visit(this, arg);
				//map.put(name, declaration_Variable);
				if(declaration_Variable.type_declaration != e.type_expr) {
					throw new SemanticException(declaration_Variable.firstToken, "Declaration Variable error");
				} 
			}map.put(name, declaration_Variable);
		}
		else 
			throw new SemanticException(declaration_Variable.firstToken, "Declaration Variable error");


		return declaration_Variable.type_declaration;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpression_Binary(Expression_Binary expression_Binary,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		Expression e0 = expression_Binary.gete0();
		e0.visit(this, arg);
		Expression e1 = expression_Binary.gete1();
		e1.visit(this, arg);
		Kind op =  expression_Binary.getop();

		if(e0.type_expr == e1.type_expr) {
			if((op == Kind.OP_EQ) || (op == Kind.OP_NEQ)) {
				expression_Binary.type_expr = Type.BOOLEAN; 
			}
			else if(((op == Kind.OP_GE )||(op == Kind.OP_GT )||(op == Kind.OP_LT )||(op == Kind.OP_LE )) && e0.type_expr == Type.INTEGER) {
				expression_Binary.type_expr = Type.BOOLEAN;
			}
			else if(((op == Kind.OP_AND) || (op == Kind.OP_OR)) && (e0.type_expr==Type.INTEGER || e0.type_expr == Type.BOOLEAN)) {
				expression_Binary.type_expr = e0.type_expr;
			}
			else if(((op == Kind.OP_DIV)||(op == Kind.OP_MINUS)||(op == Kind.OP_MOD)||(op == Kind.OP_PLUS)||(op == Kind.OP_POWER) || (op == Kind.OP_TIMES))&&(e0.type_expr == Type.INTEGER)) {
				expression_Binary.type_expr = Type.INTEGER;
			}
			else expression_Binary.type_expr = Type.NONE;
		}
		else throw new SemanticException(expression_Binary.firstToken, "error in binary expression");
		System.out.println(expression_Binary.type_expr);
		if(expression_Binary.type_expr== Type.NONE)
			throw new SemanticException(expression_Binary.firstToken, "error in binary expression");
		return expression_Binary.type_expr;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpression_Unary(Expression_Unary expression_Unary,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		Kind op = expression_Unary.getop();
		Expression e = expression_Unary.gete();
		e.visit(this, arg);

		if (op == Kind.OP_EXCL && (e.type_expr == Type.BOOLEAN ||e.type_expr == Type.INTEGER)) {
			expression_Unary.type_expr = e.type_expr;
		}
		else if (op ==Kind.OP_PLUS || op ==Kind.OP_MINUS && e.type_expr == Type.INTEGER) {
			expression_Unary.type_expr = Type.INTEGER;
		}
		else expression_Unary.type_expr = Type.NONE;

		if (expression_Unary.type_expr== Type.NONE) {
			throw new SemanticException(expression_Unary.firstToken,"Error in expression unary");
		}
		return expression_Unary.type_expr;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitIndex(Index index, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Expression e0 = index.gete0();
		e0.visit(this, arg);
		Expression e1 = index.gete1();
		e1.visit(this, arg);
		if(e0.type_expr == Type.INTEGER && e1.type_expr == Type.INTEGER) {
			index.setCartesian(!(e0.toString().contains("KW_r") && e1.toString().contains("KW_a")));
		}
		else throw new SemanticException(index.firstToken, "Error in index");
		return null;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpression_PixelSelector(
			Expression_PixelSelector expression_PixelSelector, Object arg)
					throws Exception {
		// TODO Auto-generated method stub
		String name = expression_PixelSelector.getname();
		Index index = expression_PixelSelector.getindex();
		Type nametype = map.get(name).type_declaration;
		
		if(nametype == Type.IMAGE) {
			expression_PixelSelector.type_expr = Type.INTEGER;
		}
		else if (index != null) {
			expression_PixelSelector.type_expr = Type.NONE;
		}
		if (index != null) {
			index.visit(this, arg);
			}
		else expression_PixelSelector.type_expr = nametype;

		if (expression_PixelSelector.type_expr == Type.NONE) {
			throw new SemanticException(expression_PixelSelector.firstToken,"Error in expression_PixelSelector");
		}
		
		return expression_PixelSelector.type_expr;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpression_Conditional(
			Expression_Conditional expression_Conditional, Object arg)
					throws Exception {
		// TODO Auto-generated method stub
		Expression condition = expression_Conditional.getcondition();
		condition.visit(this, arg);
		Expression trueExpression = expression_Conditional.gettrueExpression();
		trueExpression.visit(this, arg);
		Expression falseExpression = expression_Conditional.getfalseExpression();
		falseExpression.visit(this, arg);

		if(condition.type_expr == Type.BOOLEAN && trueExpression.type_expr == falseExpression.type_expr) {
			expression_Conditional.type_expr = trueExpression.type_expr;
		}
		else throw new SemanticException(expression_Conditional.firstToken,"Error in Expression Condition");
		return expression_Conditional.type_expr;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitDeclaration_Image(Declaration_Image declaration_Image,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		Expression xSize = declaration_Image.getxSize();
		Expression ySize = declaration_Image.getySize();
		String name = declaration_Image.getname();
		Source source = declaration_Image.getsource();

		if (!map.containsKey(name)) {
			map.put(name, declaration_Image);
			declaration_Image.type_declaration = Type.IMAGE;
		}
		else 
			throw new SemanticException(declaration_Image.firstToken, "error in declaration_image");

		if (xSize != null) {
			xSize.visit(this, arg);
			if(ySize != null ){
				ySize.visit(this, arg);
				if(!(xSize.type_expr == Type.INTEGER && ySize.type_expr == Type.INTEGER)){
					throw new SemanticException(declaration_Image.firstToken, "error in declaration_image");
				}
			}
		}
		if (source != null)
			source.visit(this, arg);

		return declaration_Image.type_declaration;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitSource_StringLiteral(
			Source_StringLiteral source_StringLiteral, Object arg)
					throws Exception {
		// TODO Auto-generated method stub
		String fileOrUrl = source_StringLiteral.getfileOrUrl();

		if (source_StringLiteral.isValidURL(fileOrUrl)) {
			source_StringLiteral.type_source = Type.URL;
		}
		else
			source_StringLiteral.type_source = Type.FILE;
		return source_StringLiteral.type_source;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitSource_CommandLineParam(
			Source_CommandLineParam source_CommandLineParam, Object arg)
					throws Exception {
		// TODO Auto-generated method stub
		Expression paramNum = source_CommandLineParam.getparamNum();
		paramNum.visit(this, arg);
		source_CommandLineParam.type_source = null;

		if(paramNum.type_expr != Type.INTEGER) {
			throw new SemanticException(source_CommandLineParam.firstToken, "Error in source_CommandLineParam");
		}
		return source_CommandLineParam.type_source;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitSource_Ident(Source_Ident source_Ident, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		String name = source_Ident.getname();
		if(map.containsKey(name)) {
			source_Ident.type_source = map.get(name).type_declaration;
		}else throw new SemanticException(source_Ident.firstToken,"error in source ident");
		if (source_Ident.type_source == Type.FILE || source_Ident.type_source ==Type.URL) {

		}else
			throw new SemanticException(source_Ident.firstToken,"error in source ident");
		return source_Ident.type_source;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitDeclaration_SourceSink(
			Declaration_SourceSink declaration_SourceSink, Object arg)
					throws Exception {
		// TODO Auto-generated method stub
		Kind type = declaration_SourceSink.gettype();
		String name = declaration_SourceSink.getname();
		Source source = declaration_SourceSink.getsource();
		source.visit(this, arg);

		if(!map.containsKey(name)) {
			map.put(name, declaration_SourceSink);
			declaration_SourceSink.type_declaration = TypeUtils.getType(declaration_SourceSink.firstToken);
		}
		else
			throw new SemanticException(declaration_SourceSink.firstToken, "error in declaration_SourceSink");
		if(!(source.type_source == declaration_SourceSink.type_declaration || source.type_source == null)) {
			throw new SemanticException(declaration_SourceSink.firstToken, "error in declaration_SourceSink");
		}
		return declaration_SourceSink.type_declaration;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpression_IntLit(Expression_IntLit expression_IntLit,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		expression_IntLit.type_expr = Type.INTEGER;
		return expression_IntLit.type_expr;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpression_FunctionAppWithExprArg(
			Expression_FunctionAppWithExprArg expression_FunctionAppWithExprArg,
			Object arg) throws Exception {
		// TODO Auto-generated method stub 
		Kind function =expression_FunctionAppWithExprArg.getfunction();
		Expression arg1 = expression_FunctionAppWithExprArg.getarg();
		arg1.visit(this, arg);

		if(arg1.type_expr == Type.INTEGER){
			expression_FunctionAppWithExprArg.type_expr = Type.INTEGER;
		}
		else 
			throw new SemanticException(expression_FunctionAppWithExprArg.firstToken, "error in expression_FunctionAppWithExprArg"); 
		return expression_FunctionAppWithExprArg.type_expr;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpression_FunctionAppWithIndexArg(
			Expression_FunctionAppWithIndexArg expression_FunctionAppWithIndexArg,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		expression_FunctionAppWithIndexArg.type_expr = Type.INTEGER;
		return expression_FunctionAppWithIndexArg.type_expr;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpression_PredefinedName(
			Expression_PredefinedName expression_PredefinedName, Object arg)
					throws Exception {
		// TODO Auto-generated method stub
		expression_PredefinedName.type_expr = Type.INTEGER;
		return expression_PredefinedName.type_expr;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatement_Out(Statement_Out statement_Out, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		String name = statement_Out.getname();
		Sink sink = statement_Out.getsink();
		sink.visit(this, arg);

		statement_Out.setDec(map.get(name));
		if(map.get(name) == null) {
			throw new SemanticException(statement_Out.firstToken,"Error in statement_out");
		}
		if(!((map.get(name).type_declaration == Type.INTEGER || map.get(name).type_declaration == Type.BOOLEAN ) && (sink.type_sink == Type.SCREEN) || (map.get(name).type_declaration == Type.IMAGE &&(sink.type_sink == Type.FILE || sink.type_sink == Type.SCREEN)))) {
			throw new SemanticException(statement_Out.firstToken,"Error in statement_out");
		}
		return null;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatement_In(Statement_In statement_In, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		String name = statement_In.getname();
		Source source = statement_In.getsource();
		source.visit(this, arg);

		if (map.containsKey(name)) {
			statement_In.setDec(map.get(name));
		}
//		if(!(statement_In.getDec() != null)&& !(map.get(name).type_declaration == source.type_source)) {
//			throw new SemanticException(statement_In.firstToken, "Error in statement_In");
//		}
		return null;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatement_Assign(Statement_Assign statement_Assign,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		LHS lhs = statement_Assign.getlhs();
		lhs.visit(this, arg);
		Expression e = statement_Assign.gete();
		e.visit(this, arg);

		if((lhs.type_lhs == e.type_expr)|| (lhs.type_lhs == Type.IMAGE && e.type_expr == Type.INTEGER)) {
			statement_Assign.setCartesian(lhs.isCartesian());
		}
		else throw new SemanticException(statement_Assign.firstToken,"Error in statement_assign");
		return null;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitLHS(LHS lhs, Object arg) throws Exception {
		// TODO Auto-generated method stub
		String name = lhs.getname();
		Index index = lhs.getindex();
		if (map.containsKey(name)) {
			lhs.dec = map.get(name);
		}
		else throw new SemanticException(lhs.firstToken, "name does not exist in lhs");

		lhs.type_lhs = lhs.dec.type_declaration;
		if(index == null) {
			lhs.setCartesian(false);
		}
		else {
			index.visit(this, arg);
			lhs.setCartesian(index.isCartesian());
		}
		return lhs.type_lhs;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitSink_SCREEN(Sink_SCREEN sink_SCREEN, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		Kind kind = sink_SCREEN.getkind();
		sink_SCREEN.type_sink = Type.SCREEN;
		return sink_SCREEN.type_sink;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitSink_Ident(Sink_Ident sink_Ident, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		String name = sink_Ident.getname();

		sink_Ident.type_sink = map.get(name).type_declaration;

		if(sink_Ident.type_sink !=Type.FILE) {
			throw new SemanticException(sink_Ident.firstToken,"Error in Sink Ident");
		}
		return sink_Ident.type_sink;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpression_BooleanLit(
			Expression_BooleanLit expression_BooleanLit, Object arg)
					throws Exception {
		// TODO Auto-generated method stub
		Boolean value = expression_BooleanLit.getvalue();
		expression_BooleanLit.type_expr = Type.BOOLEAN;
		return expression_BooleanLit.type_expr;
		//throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpression_Ident(Expression_Ident expression_Ident,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		String name = expression_Ident.getname();
		if(map.containsKey(name)) {
			expression_Ident.type_expr = map.get(name).type_declaration;
		}else throw new SemanticException(expression_Ident.firstToken,"error in expression ident");
		return expression_Ident.type_expr;
		//throw new UnsupportedOperationException();
	}
}
