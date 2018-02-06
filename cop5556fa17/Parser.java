package cop5556fa17;

import cop5556fa17.AST.*;
import java.util.*;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;
import cop5556fa17.Parser.SyntaxException;

import static cop5556fa17.Scanner.Kind.*;

public class Parser {

	@SuppressWarnings("serial")
	public class SyntaxException extends Exception {
		Token t;

		public SyntaxException(Token t, String message) {
			super(message);
			this.t = t;
		}

	}

	Scanner scanner;
	Token t;
	ArrayList<ASTNode> ast = new ArrayList<ASTNode>();

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}

	/**
	 * Main method called by compiler to parser input. Checks for EOF
	 * 
	 * @throws SyntaxException
	 */
	public Program parse() throws SyntaxException {
		Program p = program();
		matchEOF();
		return p;
	}

	/**
	 * Program ::= IDENTIFIER ( Declaration SEMI | Statement SEMI )*
	 * 
	 * Program is start symbol of our grammar.
	 * 
	 * @throws SyntaxException
	 */
	Program program() throws SyntaxException {
		// TODO implement this
		Token firstToken = null;
		if (t.kind == IDENTIFIER) {
			firstToken = t;
			match(IDENTIFIER);
		} else
			throw new SyntaxException(t, "Error because this is not legal" + t.line + ":" + t.pos_in_line);
		if (isKind(KW_int) || isKind(KW_boolean) || isKind(KW_image) || isKind(KW_url) || isKind(KW_file)
				|| isKind(IDENTIFIER)) {
			while (isKind(KW_int) || isKind(KW_boolean) || isKind(KW_image) || isKind(KW_url) || isKind(KW_file)
					|| isKind(IDENTIFIER)) {
				// check predict set of declaration
				if (isKind(KW_int) || isKind(KW_boolean) || isKind(KW_image) || isKind(KW_url) || isKind(KW_file)) {
					declaration();
					if (t.kind == SEMI) {
						match(SEMI);
					} else
						throw new SyntaxException(t, "error in program 1" + t.line + ":" + t.pos_in_line);

				}
				// check predict set of statement
				else if (isKind(IDENTIFIER)) {
					ast.add(statement());
					if (t.kind == SEMI) {
						match(SEMI);
					} else
						throw new SyntaxException(t, "error in program 2" + t.line + ":" + t.pos_in_line);
				} else
					throw new SyntaxException(t, "error in program 3" + t.line + ":" + t.pos_in_line);
			}

		} 
		
		else if (t.kind == EOF) {
			}
		else
			throw new SyntaxException(t, "error after IDENTIFIER" + t.line + ":" + t.pos_in_line);
		

		// throw new UnsupportedOperationException();
		return new Program(firstToken, firstToken, ast);
	}

	void declaration() throws SyntaxException {
		// TODO implement this.
		if (isKind(KW_int) || isKind(KW_boolean)) {
			ast.add(variabledeclaration());
		} else if (isKind(KW_image)) {
			ast.add(imagedeclaration());
		} else if (isKind(KW_url) || isKind(KW_file)) {
			ast.add(sourcesinkdeclaration());
		} else
			throw new SyntaxException(t, "Error in declaration" + t.line + ":" + t.pos_in_line);
		// throw new UnsupportedOperationException();
	}

	Declaration_Variable variabledeclaration() throws SyntaxException {
		// TODO implement this.
		Token firstToken = t;
		Token op = null;
		Expression e0 = null;
		vartype();

		if (t.kind == IDENTIFIER) {
			op = t;
			match(IDENTIFIER);
			if (t.kind == OP_ASSIGN) {
				match(OP_ASSIGN);
				e0 = expression();
			} else
				return new Declaration_Variable(firstToken, firstToken, op, e0);
		} else
			throw new SyntaxException(t, "Error in variabledeclaration" + t.line + ":" + t.pos_in_line);


		return new Declaration_Variable(firstToken, firstToken, op, e0);
		// throw new UnsupportedOperationException();
	}

	void vartype() throws SyntaxException {
		// TODO implement this.
		if (t.kind == KW_int)
			match(KW_int);
		else if (t.kind == KW_boolean)
			match(KW_boolean);
		else
			throw new SyntaxException(t, "Error in vartype" + t.line + ":" + t.pos_in_line);
		// throw new UnsupportedOperationException();
	}

	Declaration_Image imagedeclaration() throws SyntaxException {
		// TODO implement this.
		Token firstToken = null;
		Token op = null;
		Expression e0 = null;
		Expression e1 = null;
		Source s0 = null;
		if (t.kind == KW_image) {
			firstToken = t;
			match(KW_image);
		}
		if (t.kind == LSQUARE) {
			match(LSQUARE);
			e0 = expression();
			if (t.kind == COMMA) {
				match(COMMA);
			} else
				throw new SyntaxException(t, "Error in imagedeclaration" + t.line + ":" + t.pos_in_line);
			e1 = expression();
			if (t.kind == RSQUARE) {
				match(RSQUARE);
			} else
				throw new SyntaxException(t, "Error in imagedeclaration" + t.line + ":" + t.pos_in_line);
		}
		if (t.kind == IDENTIFIER) {
			op = t;
			match(IDENTIFIER);
		} else
			throw new SyntaxException(t, "Error in imagedeclaration" + t.line + ":" + t.pos_in_line);
		if (t.kind == OP_LARROW) {
			match(OP_LARROW);
			s0 = source();
		}

		return new Declaration_Image(firstToken, e0, e1, op, s0);
		// throw new UnsupportedOperationException();
	}

	Source source() throws SyntaxException {
		// TODO implement this.
		Token firstToken = null;
		Expression e0 = null;
		if (t.kind == STRING_LITERAL) {
			firstToken = t;
			match(STRING_LITERAL);
			return new Source_StringLiteral(firstToken, firstToken.getText());
		} else if (t.kind == OP_AT) {
			firstToken = t;
			match(OP_AT);
			e0 = expression();
			return new Source_CommandLineParam(firstToken, e0);
		} else if (t.kind == IDENTIFIER) {
			firstToken = t;
			match(IDENTIFIER);
			return new Source_Ident(firstToken, firstToken);
		} else
			throw new SyntaxException(t, "error in source" + t.line + ":" + t.pos_in_line);
		// throw new UnsupportedOperationException();
	}

	Declaration_SourceSink sourcesinkdeclaration() throws SyntaxException {
		// TODO implement this.
		Token firstToken = t;
		Token type = null;
		Token name = null;
		Source s0 = null;

		type = t;
		sourcesinktype();
		if (t.kind == IDENTIFIER) {
			name = t;
			match(IDENTIFIER);
		} else
			throw new SyntaxException(t, "Error in sourcesinkdeclaration" + t.line + ":" + t.pos_in_line);
		if (t.kind == OP_ASSIGN) {
			match(OP_ASSIGN);
		} else
			throw new SyntaxException(t, "Error in sourcesinkdeclaration" + t.line + ":" + t.pos_in_line);

		s0 = source();

		return new Declaration_SourceSink(firstToken, type, name, s0);
		// throw new UnsupportedOperationException();
	}

	void sourcesinktype() throws SyntaxException {
		// TODO implement this.
		if (t.kind == KW_url)
			match(KW_url);
		else if (t.kind == KW_file)
			match(KW_file);
		else
			throw new SyntaxException(t, "Error in sourcesinktype" + t.line + ":" + t.pos_in_line);
		// throw new UnsupportedOperationException();
	}

	Statement statement() throws SyntaxException {
		//TODO implement this.
		Token firstToken = t;
		//Token op = null;
		Expression e0 = null;
		Sink s0 = null;
		Source s1 = null;
		Index i0 = null;
		LHS l=null;
		Statement st = null;
		if(t.kind == IDENTIFIER) {
			match(IDENTIFIER);
			if(isKind(LSQUARE)||isKind(OP_ASSIGN)) {
				if(t.kind == LSQUARE) {
					match(LSQUARE);
					i0 = lhsselector();
					if(t.kind == RSQUARE) 
						match(RSQUARE);
					else throw new SyntaxException(t, "Error in statement");
				}
				if(t.kind == OP_ASSIGN)
					match(OP_ASSIGN);
				else throw new SyntaxException(t, "Error in statement");
				e0 = expression();
				l=new LHS(firstToken, firstToken, i0);
				st = new Statement_Assign(firstToken, l, e0);
			}
			else if (t.kind == OP_RARROW) {
				match(OP_RARROW);
				s0 = sink();
				st = new Statement_Out(firstToken, firstToken, s0);
			}
			else if(t.kind == OP_LARROW) {
				match(OP_LARROW);
				s1 = source();
				st = new Statement_In(firstToken, firstToken, s1);
			}
			else throw new SyntaxException(t, "Error in statement");
		}
		else throw new SyntaxException(t, "Error in statement");
		return st;
		//throw new UnsupportedOperationException();
	}

	Index lhsselector() throws SyntaxException {
		// TODO implement this.
		Index i0 = null;

		if (t.kind == LSQUARE)
			match(LSQUARE);
		if (isKind(KW_x) || isKind(KW_r)) {
			if (t.kind == KW_x)
				i0 = xyselector();
			else
				i0 = raselector();
		} else
			throw new SyntaxException(t, "Error in lhsselector" + t.line + ":" + t.pos_in_line);
		if (t.kind == RSQUARE)
			match(RSQUARE);
		else
			throw new SyntaxException(t, "Error in lhsselector" + t.line + ":" + t.pos_in_line);
		return i0;
		// throw new UnsupportedOperationException();
	}

	Index xyselector() throws SyntaxException {
		// TODO implement this.
		Token firstToken = t;
		Token op = null;
		Expression e0 = null;
		Expression e1 = null;
		if (t.kind == KW_x) {
			e0 = new Expression_PredefinedName(firstToken, firstToken.kind);
			match(KW_x);
		}
		if (t.kind == COMMA) {
			match(COMMA);
		} else
			throw new SyntaxException(t, "Error in xyselector" + t.line + ":" + t.pos_in_line);
		if (t.kind == KW_y) {
			op = t;
			e1 = new Expression_PredefinedName(op, op.kind);
			match(KW_y);
		} else
			throw new SyntaxException(t, "Error in xyselector" + t.line + ":" + t.pos_in_line);

		return new Index(firstToken, e0, e1);
		// throw new UnsupportedOperationException();
	}

	Index raselector() throws SyntaxException {
		// TODO implement this.
		Token firstToken = t;
		Token op = null;
		Expression e0 = null;
		Expression e1 = null;
		if (t.kind == KW_r) {
			e0 = new Expression_PredefinedName(firstToken, firstToken.kind);
			match(KW_r);
		}
		if (t.kind == COMMA) {
			match(COMMA);
		} else
			throw new SyntaxException(t, "Error in raselector" + t.line + ":" + t.pos_in_line);
		if (t.kind == KW_a) {
			op = t;
			e1 = new Expression_PredefinedName(op, op.kind);
			match(KW_a);
		} else
			throw new SyntaxException(t, "Error in raselector" + t.line + ":" + t.pos_in_line);
		return new Index(firstToken, e0, e1);
		// throw new UnsupportedOperationException();
	}

	Sink sink() throws SyntaxException {
		// TODO implement this.
		Token firstToken = t;
		Token op = null;
		if (t.kind == IDENTIFIER) {
			op = t;
			match(IDENTIFIER);
			return new Sink_Ident(firstToken, op);
		} else if (t.kind == KW_SCREEN) {
			match(KW_SCREEN);
			return new Sink_SCREEN(firstToken);
		} else
			throw new SyntaxException(t, "Error in sink" + t.line + ":" + t.pos_in_line);
		// throw new UnsupportedOperationException();
	}

	/**
	 * Expression ::= OrExpression OP_Q Expression OP_COLON Expression |
	 * OrExpression
	 * 
	 * Our test cases may invoke this routine directly to support incremental
	 * development.
	 * 
	 * @throws SyntaxException
	 */
	Expression expression() throws SyntaxException {
		// TODO implement this..
		Token firstToken = t;
		Expression e0 = null;
		Expression e1 = null;
		Expression e2 = null;

		e0 = orexpression();
		if (t.kind == OP_Q) {
			match(OP_Q);
			e1 = expression();
			if (t.kind == OP_COLON) {
				match(OP_COLON);
			} else
				throw new SyntaxException(t, "Error in expression" + t.line + ":" + t.pos_in_line);
			e2 = expression();
		}
		if(e1==null && e2 == null)
			return e0;
		else
			return new Expression_Conditional(firstToken, e0, e1, e2);
		// throw new UnsupportedOperationException();
	}

	Expression orexpression() throws SyntaxException {
		// TODO implement this.
		Token firstToken = t;
		Token op = null;
		Expression e0 = null;
		Expression e1 = null;
		e0 = andexpression();
		while (t.kind == OP_OR) {
			op = t;
			match(OP_OR);
			e1 = andexpression();
			e0 = new Expression_Binary(firstToken, e0, op, e1);
		}
		return e0;
		// throw new UnsupportedOperationException();
	}

	Expression andexpression() throws SyntaxException {
		// TODO implement this.
		Token firstToken = t;
		Token op = null;
		Expression e0 = null;
		Expression e1 = null;
		e0 = eqexpression();
		while (t.kind == OP_AND) {
			op = t;
			match(OP_AND);
			e1 = eqexpression();
			e0 = new Expression_Binary(firstToken, e0, op, e1);
		}
		return e0;
		// throw new UnsupportedOperationException();
	}

	Expression eqexpression() throws SyntaxException {
		// TODO implement this.
		Token firstToken = null;
		Token op = null;
		Expression e0 = null;
		Expression e1 = null;
		e0 = relexpression();
		while (isKind(OP_EQ) || isKind(OP_NEQ)) {
			if (t.kind == OP_EQ) {
				op = t;
				match(OP_EQ);
			} else if (t.kind == OP_NEQ) {
				op = t;
				match(OP_NEQ);
			}
			e1 = relexpression();
			e0 = new Expression_Binary(firstToken, e0, op, e1);
		}
		return e0;
		// throw new UnsupportedOperationException();
	}

	Expression relexpression() throws SyntaxException {
		// TODO implement this.
		Token firstToken = t;
		Token op = null;
		Expression e0 = null;
		Expression e1 = null;
		e0 = addexpression();
		while (isKind(OP_LT) || isKind(OP_GT) || isKind(OP_LE) || isKind(OP_GE)) {
			if (t.kind == OP_LT) {
				op = t;
				match(OP_LT);
			} else if (t.kind == OP_GT) {
				op = t;
				match(OP_GT);
			} else if (t.kind == OP_LE) {
				op = t;
				match(OP_LE);
			} else if (t.kind == OP_GE) {
				op = t;
				match(OP_GE);
			}
			e1 = addexpression();
			e0 = new Expression_Binary(firstToken, e0, op, e1);
		}
		return e0;
		// throw new UnsupportedOperationException();
	}

	Expression addexpression() throws SyntaxException {
		// TODO implement this.
		Token firstToken = t;
		Token op = null;
		Expression e0 = null;
		Expression e1 = null;
		e0 = multexpression();

		while (isKind(OP_PLUS) || isKind(OP_MINUS)) {
			if (t.kind == OP_PLUS) {
				op = t;
				match(OP_PLUS);
			} else if (t.kind == OP_MINUS) {
				op = t;
				match(OP_MINUS);
			}

			e1 = multexpression();
			e0 = new Expression_Binary(firstToken, e0, op, e1);
		}
		return e0;
		// throw new UnsupportedOperationException();
	}

	Expression multexpression() throws SyntaxException {
		// TODO implement this.
		Token firstToken = t;
		Token op = null;
		Expression e0 = null;
		Expression e1 = null;
		e0 = unaryexpression();

		while (isKind(OP_TIMES) || isKind(OP_DIV) || isKind(OP_MOD)) {
			if (t.kind == OP_TIMES) {
				op=t;
				match(OP_TIMES);

			} else if (t.kind == OP_DIV) {
				op = t;
				match(OP_DIV);

			} else if (t.kind == OP_MOD) {
				op = t;
				match(OP_MOD);

			}
			e1 = unaryexpression();
			e0 = new Expression_Binary(firstToken, e0, op, e1);
		}
		return e0;
		// throw new UnsupportedOperationException();
	}

	Expression unaryexpression() throws SyntaxException {
		// TODO implement this.
		Token firsttoken = t;
		Token op = t;

		Expression e0 = null;

		if (t.kind == OP_PLUS) {

			match(OP_PLUS);
			e0 = unaryexpression();
			return new Expression_Unary(firsttoken, op, e0);

		} else if (t.kind == OP_MINUS) {

			match(OP_MINUS);
			e0 = unaryexpression();
			return new Expression_Unary(firsttoken, op, e0);

		} else {
			e0 = unaryexpressionnotplusminus();
		}
		return e0;
		// throw new UnsupportedOperationException();
	}

	Expression unaryexpressionnotplusminus() throws SyntaxException {
		// TODO implement this.
		Token op = null;
		Expression e0 = null;
		Expression e = null;
		if (t.kind == OP_EXCL) {
			op = t;
			match(OP_EXCL);
			e0 = unaryexpression();
			e = new Expression_Unary(op, op, e0);

		} else if (isKind(IDENTIFIER)) {
			e = identorpixelselectorexpression();
		} else if (isKind(KW_x) || isKind(KW_y) || isKind(KW_r) || isKind(KW_a) || isKind(KW_X) || isKind(KW_Y)||isKind(KW_Z)
				|| isKind(KW_A) || isKind(KW_R) || isKind(KW_DEF_X) || isKind(KW_DEF_Y)) {
			op = t;
			if (t.kind == KW_x) {
				match(KW_x);
			} else if (t.kind == KW_y) {
				match(KW_y);
			} else if (t.kind == KW_r) {
				match(KW_r);
			} else if (t.kind == KW_a) {
				match(KW_a);
			} else if (t.kind == KW_X) {
				match(KW_X);
			} else if (t.kind == KW_Y) {				
				match(KW_Y);
			} else if (t.kind == KW_Z) {				
				match(KW_Z);
			} else if (t.kind == KW_A) {				
				match(KW_A);
			} else if (t.kind == KW_R) {				
				match(KW_R);
			} else if (t.kind == KW_DEF_X) {				
				match(KW_DEF_X);
			} else if (t.kind == KW_DEF_Y) {				
				match(KW_DEF_Y);
			}

			e = new Expression_PredefinedName(op, op.kind);
		} else {
			return primary();

		}
		return e;
		// throw new UnsupportedOperationException();
	}

	Expression identorpixelselectorexpression() throws SyntaxException {
		// TODO implement this.
		Token firstToken = t;
		Index i0 = null;
		Expression e0 = null;
		if (t.kind == IDENTIFIER) {
			match(IDENTIFIER);
			if (t.kind == LSQUARE) {
				match(LSQUARE);
				i0 = selector();
				if (t.kind == RSQUARE) {
					match(RSQUARE);
				}
				else
					throw new SyntaxException(t,"error in identorpixelselectorexpression" + t.line + ":" + t.pos_in_line);
				e0 = new Expression_PixelSelector(firstToken, firstToken, i0);
			}else
				e0 = new Expression_Ident(firstToken, firstToken);
		}

		return e0;
		// throw new UnsupportedOperationException();
	}

	Expression primary() throws SyntaxException {
		// TODO implement this.
		Token op = null;
		Expression e = null;
		if (t.kind == INTEGER_LITERAL) {
			op = t;
			match(INTEGER_LITERAL);
			e =new Expression_IntLit(op, op.intVal());
		} else if (t.kind == LPAREN) {
			match(LPAREN);
			e = expression();
			if (t.kind == RPAREN)
				match(RPAREN);
			else
				throw new SyntaxException(t, "error in primary" + t.line + ":" + t.pos_in_line);
		} else if (t.kind == BOOLEAN_LITERAL) {
			op = t;
			match(BOOLEAN_LITERAL);
			if (op.length == 4)
				e = new Expression_BooleanLit(op, true);
			else if (op.length == 5)
				e = new Expression_BooleanLit(op, false);
		} else
			e = functionapplication();
		return e;
		// throw new UnsupportedOperationException();
	}

	Index selector() throws SyntaxException {
		// TODO implement this.
		Token op = t;
		Expression e0 = null;
		Expression e1 = null;
		e0 = expression();
		if (t.kind == COMMA) {
			match(COMMA);
		} else
			throw new SyntaxException(t, "Error in selector" + t.line + ":" + t.pos_in_line);
		e1 = expression();

		return new Index(op, e0, e1);
		// throw new UnsupportedOperationException();
	}

	Expression functionapplication() throws SyntaxException {
		// TODO implement this.
		Token firstToken = t;
		Expression e0 =null;
		Index i0 = null;
		Expression e = null;
		functionname();

		if (isKind(LPAREN) || isKind(LSQUARE)) {
			if (t.kind == LPAREN) {
				match(LPAREN);
				e0 = expression();
				if (t.kind == RPAREN)
					match(RPAREN);
				else
					throw new SyntaxException(t, "error in functionapplication" + t.line + ":" + t.pos_in_line);
				e = new Expression_FunctionAppWithExprArg(firstToken, firstToken.kind, e0);
			}


			else if (t.kind == LSQUARE) {
				match(LSQUARE);
				i0 = selector();
				if (t.kind == RSQUARE)
					match(RSQUARE);
				else
					throw new SyntaxException(t, "error in functionapplication" + t.line + ":" + t.pos_in_line);
				e =  new Expression_FunctionAppWithIndexArg(firstToken, firstToken.kind, i0);
			}
		} else
			throw new SyntaxException(t, "error in functionapplication" + t.line + ":" + t.pos_in_line);
		return e;
		// throw new UnsupportedOperationException();
	}

	void functionname() throws SyntaxException {
		// TODO implement this.
		if (t.kind == KW_sin)
			match(KW_sin);
		else if (t.kind == KW_cos)
			match(KW_cos);
		else if (t.kind == KW_atan)
			match(KW_atan);
		else if (t.kind == KW_abs)
			match(KW_abs);
		else if (t.kind == KW_cart_x)
			match(KW_cart_x);
		else if (t.kind == KW_cart_y)
			match(KW_cart_y);
		else if (t.kind == KW_polar_a)
			match(KW_polar_a);
		else if (t.kind == KW_polar_r)
			match(KW_polar_r);
		else
			throw new SyntaxException(t, "Error in functionname" + t.line + ":" + t.pos_in_line);
		// throw new UnsupportedOperationException();
	}

	/**
	 * Only for check at end of program. Does not "consume" EOF so no attempt to get
	 * nonexistent next Token.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (t.kind == EOF) {
			return t;
		}
		String message = "Expected EOL at " + t.line + ":" + t.pos_in_line;
		throw new SyntaxException(t, message);
	}

	public Token match(Kind kind) throws SyntaxException {
		if (t.kind == kind) {
			return consume();
		} else
			throw new SyntaxException(t, "Error at matching" + t.line + ":" + t.pos_in_line);
	}

	public Token consume() throws SyntaxException {
		Token temp = t;
		t = scanner.nextToken();
		return temp;
	}

	public boolean isKind(Kind kind) {
		if (t.kind == kind) {
			return true;
		} else
			return false;
	}

}
