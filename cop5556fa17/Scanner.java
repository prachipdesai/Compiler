/* *
 * Scanner for the class project in COP5556 Programming Language Principles 
 * at the University of Florida, Fall 2017.
 * 
 * This software is solely for the educational benefit of students 
 * enrolled in the course during the Fall 2017 semester.  
 * 
 * This software, and any software derived from it,  may not be shared with others or posted to public web sites,
 * either during the course or afterwards.
 * 
 *  @Beverly A. Sanders, 2017
 */

package cop5556fa17;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.*;

public class Scanner {

	@SuppressWarnings("serial")
	public static class LexicalException extends Exception {

		int pos;

		public LexicalException(String message, int pos) {
			super(message);
			this.pos = pos;
		}

		public int getPos() {
			return pos;
		}

	}

	public static enum Kind {
		IDENTIFIER, INTEGER_LITERAL, BOOLEAN_LITERAL, STRING_LITERAL, KW_x/* x */, KW_X/* X */, KW_y/* y */, KW_Y/* Y */, KW_r/*
																																 * r
																																 */, KW_R/*
																																			 * R
																																			 */, KW_a/*
																																						 * a
																																						 */, KW_A/*
																																									 * A
																																									 */, KW_Z/*
																																												 * Z
																																												 */, KW_DEF_X/*
																																																 * DEF_X
																																																 */, KW_DEF_Y/*
																																																				 * DEF_Y
																																																				 */, KW_SCREEN/*
																																																								 * SCREEN
																																																								 */, KW_cart_x/*
																																																												 * cart_x
																																																												 */, KW_cart_y/*
																																																																 * cart_y
																																																																 */, KW_polar_a/*
																																																																				 * polar_a
																																																																				 */, KW_polar_r/*
																																																																								 * polar_r
																																																																								 */, KW_abs/*
																																																																											 * abs
																																																																											 */, KW_sin/*
																																																																														 * sin
																																																																														 */, KW_cos/*
																																																																																	 * cos
																																																																																	 */, KW_atan/*
																																																																																				 * atan
																																																																																				 */, KW_log/*
																																																																																							 * log
																																																																																							 */, KW_image/*
																																																																																											 * image
																																																																																											 */, KW_int/*
																																																																																														 * int
																																																																																														 */, KW_boolean/*
																																																																																																		 * boolean
																																																																																																		 */, KW_url/*
																																																																																																					 * url
																																																																																																					 */, KW_file/*
																																																																																																								 * file
																																																																																																								 */, OP_ASSIGN/*
																																																																																																												 * =
																																																																																																												 */, OP_GT/*
																																																																																																															 * >
																																																																																																															 */, OP_LT/*
																																																																																																																		 * <
																																																																																																																		 */, OP_EXCL/*
																																																																																																																					 * !
																																																																																																																					 */, OP_Q/*
																																																																																																																								 * ?
																																																																																																																								 */, OP_COLON/*
																																																																																																																												 * :
																																																																																																																												 */, OP_EQ/*
																																																																																																																															 * ==
																																																																																																																															 */, OP_NEQ/*
																																																																																																																																		 * !=
																																																																																																																																		 */, OP_GE/*
																																																																																																																																					 * >=
																																																																																																																																					 */, OP_LE/*
																																																																																																																																								 * <=
																																																																																																																																								 */, OP_AND/*
																																																																																																																																											 * &
																																																																																																																																											 */, OP_OR/*
																																																																																																																																														 * |
																																																																																																																																														 */, OP_PLUS/*
																																																																																																																																																	 * +
																																																																																																																																																	 */, OP_MINUS/*
																																																																																																																																																					 * -
																																																																																																																																																					 */, OP_TIMES/*
																																																																																																																																																									 * *
																																																																																																																																																									 */, OP_DIV/*
																																																																																																																																																												 * /
																																																																																																																																																												 */, OP_MOD/*
																																																																																																																																																															 * %
																																																																																																																																																															 */, OP_POWER/*
																																																																																																																																																																			 * **
																																																																																																																																																																			 */, OP_AT/*
																																																																																																																																																																						 * @
																																																																																																																																																																						 */, OP_RARROW/*
																																																																																																																																																																										 * ->
																																																																																																																																																																										 */, OP_LARROW/*
																																																																																																																																																																														 * <-
																																																																																																																																																																														 */, LPAREN/*
																																																																																																																																																																																	 * (
																																																																																																																																																																																	 */, RPAREN/*
																																																																																																																																																																																				 * )
																																																																																																																																																																																				 */, LSQUARE/*
																																																																																																																																																																																							 * [
																																																																																																																																																																																							 */, RSQUARE/*
																																																																																																																																																																																										 * ]
																																																																																																																																																																																										 */, SEMI/*
																																																																																																																																																																																													 * ;
																																																																																																																																																																																													 */, COMMA/*
																																																																																																																																																																																																 * ,
																																																																																																																																																																																																 */, EOF;
	}

	public static enum State {
		START, IN_IDENT, IN_DIGIT, IN_STRING_LIT, COMMENT;
	}

	public HashMap<String, Kind> KW_BL_HashMap() {
		HashMap<String, Kind> map = new HashMap<String, Kind>();
		// keywords
		map.put("x", Kind.KW_x);
		map.put("X", Kind.KW_X);
		map.put("y", Kind.KW_y);
		map.put("Y", Kind.KW_Y);
		map.put("r", Kind.KW_r);
		map.put("R", Kind.KW_R);
		map.put("a", Kind.KW_a);
		map.put("A", Kind.KW_A);
		map.put("Z", Kind.KW_Z);
		map.put("DEF_X", Kind.KW_DEF_X);
		map.put("DEF_Y", Kind.KW_DEF_Y);
		map.put("SCREEN", Kind.KW_SCREEN);
		map.put("cart_x", Kind.KW_cart_x);
		map.put("cart_y", Kind.KW_cart_y);
		map.put("polar_a", Kind.KW_polar_a);
		map.put("polar_r", Kind.KW_polar_r);
		map.put("abs", Kind.KW_abs);
		map.put("sin", Kind.KW_sin);
		map.put("cos", Kind.KW_cos);
		map.put("atan", Kind.KW_atan);
		map.put("log", Kind.KW_log);
		map.put("image", Kind.KW_image);
		map.put("int", Kind.KW_int);
		map.put("boolean", Kind.KW_boolean);
		map.put("url", Kind.KW_url);
		map.put("file", Kind.KW_file);

		// boolean literals
		map.put("true", Kind.BOOLEAN_LITERAL);
		map.put("false", Kind.BOOLEAN_LITERAL);
		return map;
	}

	public HashMap<Character, Kind> OP_SE_HashMap() {
		HashMap<Character, Kind> hmap = new HashMap();

		// single operators
		hmap.put('?', Kind.OP_Q);
		hmap.put(':', Kind.OP_COLON);
		hmap.put('&', Kind.OP_AND);
		hmap.put('|', Kind.OP_OR);
		hmap.put('+', Kind.OP_PLUS);
		hmap.put('%', Kind.OP_MOD);
		hmap.put('@', Kind.OP_AT);

		// Separators
		hmap.put(';', Kind.SEMI);
		hmap.put('(', Kind.LPAREN);
		hmap.put(')', Kind.RPAREN);
		hmap.put('[', Kind.LSQUARE);
		hmap.put(']', Kind.RSQUARE);
		hmap.put(',', Kind.COMMA);

		return hmap;
	}

	public HashSet<Character> ES_HashSet() {
		HashSet<Character> ES = new HashSet();

		// escape sequences
		ES.add('\b');
		ES.add('\t');
		ES.add('\n');
		ES.add('\f');
		ES.add('\r');
		ES.add('\"');
		ES.add('\'');
		ES.add('\\');

		return ES;
	}

	/**
	 * Class to represent Tokens.
	 * 
	 * This is defined as a (non-static) inner class which means that each Token
	 * instance is associated with a specific Scanner instance. We use this when
	 * some token methods access the chars array in the associated Scanner.
	 * 
	 * 
	 * @author Beverly Sanders
	 *
	 */
	public class Token {
		public final Kind kind;
		public final int pos;
		public final int length;
		public final int line;
		public final int pos_in_line;

		public Token(Kind kind, int pos, int length, int line, int pos_in_line) {
			super();
			this.kind = kind;
			this.pos = pos;
			this.length = length;
			this.line = line;
			this.pos_in_line = pos_in_line;
		}

		public String getText() {
			if (kind == Kind.STRING_LITERAL) {
				return chars2String(chars, pos, length);
			} else
				return String.copyValueOf(chars, pos, length);
		}

		/**
		 * To get the text of a StringLiteral, we need to remove the enclosing "
		 * characters and convert escaped characters to the represented character. For
		 * example the two characters \ t in the char array should be converted to a
		 * single tab character in the returned String
		 * 
		 * @param chars
		 * @param pos
		 * @param length
		 * @return
		 */
		private String chars2String(char[] chars, int pos, int length) {
			StringBuilder sb = new StringBuilder();
			for (int i = pos + 1; i < pos + length - 1; ++i) {// omit initial and final "
				char ch = chars[i];
				if (ch == '\\') { // handle escape
					i++;
					ch = chars[i];
					switch (ch) {
					case 'b':
						sb.append('\b');
						break;
					case 't':
						sb.append('\t');
						break;
					case 'f':
						sb.append('\f');
						break;
					case 'r':
						sb.append('\r'); // for completeness, line termination chars not allowed in String literals
						break;
					case 'n':
						sb.append('\n'); // for completeness, line termination chars not allowed in String literals
						break;
					case '\"':
						sb.append('\"');
						break;
					case '\'':
						sb.append('\'');
						break;
					case '\\':
						sb.append('\\');
						break;
					default:
						assert false;
						break;
					}
				} else {
					sb.append(ch);
				}
			}
			return sb.toString();
		}

		/**
		 * precondition: This Token is an INTEGER_LITERAL
		 * 
		 * @returns the integer value represented by the token
		 */
		public int intVal() {
			assert kind == Kind.INTEGER_LITERAL;
			return Integer.valueOf(String.copyValueOf(chars, pos, length));
		}

		public String toString() {
			return "[" + kind + "," + String.copyValueOf(chars, pos, length) + "," + pos + "," + length + "," + line
					+ "," + pos_in_line + "]";
		}

		/**
		 * Since we overrode equals, we need to override hashCode.
		 * https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html#equals-java.lang.Object-
		 * 
		 * Both the equals and hashCode method were generated by eclipse
		 * 
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((kind == null) ? 0 : kind.hashCode());
			result = prime * result + length;
			result = prime * result + line;
			result = prime * result + pos;
			result = prime * result + pos_in_line;
			return result;
		}

		/**
		 * Override equals method to return true if other object is the same class and
		 * all fields are equal.
		 * 
		 * Overriding this creates an obligation to override hashCode.
		 * 
		 * Both hashCode and equals were generated by eclipse.
		 * 
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Token other = (Token) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (kind != other.kind)
				return false;
			if (length != other.length)
				return false;
			if (line != other.line)
				return false;
			if (pos != other.pos)
				return false;
			if (pos_in_line != other.pos_in_line)
				return false;
			return true;
		}

		/**
		 * used in equals to get the Scanner object this Token is associated with.
		 * 
		 * @return
		 */
		private Scanner getOuterType() {
			return Scanner.this;
		}

	}

	/**
	 * Extra character added to the end of the input characters to simplify the
	 * Scanner.
	 */
	static final char EOFchar = 0;

	/**
	 * The list of tokens created by the scan method.
	 */
	final ArrayList<Token> tokens;

	/**
	 * An array of characters representing the input. These are the characters from
	 * the input string plus and additional EOFchar at the end.
	 */
	final char[] chars;

	/**
	 * position of the next token to be returned by a call to nextToken
	 */
	private int nextTokenPos = 0;

	Scanner(String inputString) {
		int numChars = inputString.length();
		this.chars = Arrays.copyOf(inputString.toCharArray(), numChars + 1); // input string terminated with null char
		chars[numChars] = EOFchar;
		tokens = new ArrayList<Token>();
	}

	/**
	 * Method to scan the input and create a list of Tokens.
	 * 
	 * If an error is encountered during scanning, throw a LexicalException.
	 * 
	 * @return
	 * @throws LexicalException
	 */
	public Scanner scan() throws LexicalException {
		/* TODO Replace this with a correct and complete implementation!!! */

		// HashMaps and HashSets are called
		HashMap<String, Kind> map = KW_BL_HashMap();
		HashMap<Character, Kind> hmap = OP_SE_HashMap();
		HashSet<Character> ES = ES_HashSet();

		int pos = 0;
		int line = 1;
		int posInLine = 1;
		State state = State.START;
		int startPos = 0;
		while (pos < chars.length - 1) {
			char ch = chars[pos];
			switch (state) {
			case START: {
				startPos = pos;
				switch (ch) {

				// OPERATORS which are not single characters
				case '=': {
					if ((pos + 1) != chars.length) {
						if (chars[pos + 1] == '=') {
							tokens.add(new Token(Kind.OP_EQ, startPos, 2, line, posInLine));
							pos += 2;
							posInLine += 2;
						} else {
							tokens.add(new Token(Kind.OP_ASSIGN, startPos, 1, line, posInLine));
							pos++;
							posInLine++;
						}
					} else {
						tokens.add(new Token(Kind.OP_ASSIGN, startPos, 1, line, posInLine));
						pos++;
						posInLine++;
					}
					break;
				}
				case '>': {
					if ((pos + 1) != chars.length) {
						if (chars[pos + 1] == '=') {
							tokens.add(new Token(Kind.OP_GE, startPos, 2, line, posInLine));
							pos += 2;
							posInLine += 2;
						} else {
							tokens.add(new Token(Kind.OP_GT, startPos, 1, line, posInLine));
							pos++;
							posInLine++;
						}
					} else {
						tokens.add(new Token(Kind.OP_GT, startPos, 1, line, posInLine));
						pos++;
						posInLine++;
					}

					break;
				}
				case '<': {
					if ((pos + 1) != chars.length) {
						if (chars[pos + 1] == '=') {
							tokens.add(new Token(Kind.OP_LE, startPos, 2, line, posInLine));
							pos += 2;
							posInLine += 2;
						} else if (chars[pos + 1] == '-') {
							tokens.add(new Token(Kind.OP_LARROW, startPos, 2, line, posInLine));
							pos += 2;
							posInLine += 2;
						} else {
							tokens.add(new Token(Kind.OP_LT, startPos, 1, line, posInLine));
							pos++;
							posInLine++;
						}
					} else {
						tokens.add(new Token(Kind.OP_LT, startPos, 1, line, posInLine));
						pos++;
						posInLine++;
					}
					break;
				}
				case '!': {
					if ((pos + 1) != chars.length) {
						if (chars[pos + 1] == '=') {
							tokens.add(new Token(Kind.OP_NEQ, startPos, 2, line, posInLine));
							pos += 2;
							posInLine += 2;
						} else {
							tokens.add(new Token(Kind.OP_EXCL, startPos, 1, line, posInLine));
							pos++;
							posInLine++;
						}
					} else {
						tokens.add(new Token(Kind.OP_EXCL, startPos, 1, line, posInLine));
						pos++;
						posInLine++;
					}
					break;
				}

				case '-': {
					if ((pos + 1) != chars.length) {
						if (chars[pos + 1] == '>') {
							tokens.add(new Token(Kind.OP_RARROW, startPos, 2, line, posInLine));
							pos += 2;
							posInLine += 2;
						} else {
							tokens.add(new Token(Kind.OP_MINUS, startPos, 1, line, posInLine));
							pos++;
							posInLine++;
						}
					} else {
						tokens.add(new Token(Kind.OP_MINUS, startPos, 1, line, posInLine));
						pos++;
						posInLine++;
					}
					break;
				}
				case '*': {
					if ((pos + 1) != chars.length) {
						if (chars[pos + 1] == '*') {
							tokens.add(new Token(Kind.OP_POWER, startPos, 2, line, posInLine));
							pos += 2;
							posInLine += 2;
						} else {
							tokens.add(new Token(Kind.OP_TIMES, startPos, 1, line, posInLine));
							pos++;
							posInLine++;
						}
					} else {
						tokens.add(new Token(Kind.OP_TIMES, startPos, 1, line, posInLine));
						pos++;
						posInLine++;
					}
					break;
				}
				case '/': {
					if ((pos + 1) != chars.length) {
						if (chars[pos + 1] == '/') {
							state = State.COMMENT;
							pos += 2;
							posInLine += 2;
						} else {
							tokens.add(new Token(Kind.OP_DIV, startPos, 1, line, posInLine));
							pos += 1;
							posInLine += 1;
						}
					} else {
						tokens.add(new Token(Kind.OP_DIV, startPos, 1, line, posInLine));
						pos += 1;
						posInLine += 2;
					}
					break;
				}
				// OPERATORS END

				// 0 START
				case '0': {
					tokens.add(new Token(Kind.INTEGER_LITERAL, startPos, 1, line, posInLine));
					pos++;
					posInLine++;
					break;
				}
				// 0 END

				// STRING LITERALS START
				case '\"': {
					// System.out.println("entered1");
					state = State.IN_STRING_LIT;
					pos++;
					break;
				}
				case '\n': {
					posInLine = 1;
					line++;
					pos++;
					break;
				}
				case '\r': {
					posInLine = 1;
					line++;
					pos++;
					if (pos <= chars.length && chars[pos] == '\n') {
						pos++;
					}
					break;
				}
				// STRING LITERALS END

				default: {
					// single character operators and separators are check in the HashMap
					if (hmap.containsKey(ch)) {
						Kind kind_of = hmap.get(ch);
						tokens.add(new Token(kind_of, startPos, 1, line, posInLine));
						pos++;
						posInLine++;
					}

					else if (Character.isDigit(ch)) {
						state = State.IN_DIGIT;

					} else if (Character.isLetter(ch) || chars[pos] == '$' || chars[pos] == '_') {
						state = State.IN_IDENT;
					} else if (Character.isWhitespace(ch)) {
						pos++;
						posInLine++;
					}

					// \t, \b, \', \\, \f are not valid, hence throw error
					else if (chars[pos] == '\t' || chars[pos] == '\b' || chars[pos] == '\'' || chars[pos] == '\\'
							|| chars[pos] == '\f') {
						throw new LexicalException("Error has been ecountered, may be invalid character", pos);
					}

					else {
						throw new LexicalException("Error, oops wrong much", pos);
					}
				}

				}
			}
				break;

			case IN_STRING_LIT: {
				while (pos < chars.length) {
					if (chars[pos] == '\n' || chars[pos] == '\r' || chars[pos] == EOFchar) {
						throw new LexicalException("IN_STRING_LIT Error " + pos, pos);
					}

					// end of the string literal found
					else if (chars[pos] == '\"') {
						pos++;
						tokens.add(new Token(Kind.STRING_LITERAL, startPos, pos - startPos, line, posInLine));
						int l = pos - startPos;
						startPos += l;
						posInLine += l;
						state = State.START;
						break;
					}

					// escape sequence within the string literal
					else if (chars[pos] == '\\') {
						pos++;
						if (chars[pos] == 'n' || chars[pos] == 'r' || chars[pos] == 't' || chars[pos] == 'f'
								|| chars[pos] == '\'' || chars[pos] == 'b' || chars[pos] == '\"'
								|| chars[pos] == '\\') {
							pos++;
						} else {
							throw new LexicalException("IN_STRING_LIT Error  " + pos, pos);
						}
					}

					// check of escape sequence
					else if (ES.contains(chars[pos])) {
						pos++;
					}

					// check of ASCII character input
					else if (isASCIIChar(chars[pos])) {
						pos++;
					}

				}

			}
				break;

			case IN_IDENT: {

				while (Character.isLetterOrDigit(chars[pos]) || chars[pos] == '_' || chars[pos] == '$') {
					pos++;

				}
				// once identifier is found check if its a keyword
				String key1 = new String(chars, startPos, pos - startPos);
				if (map.containsKey(key1)) {
					Kind kind_of_key = map.get(key1);
					tokens.add(new Token(kind_of_key, startPos, pos - startPos, line, posInLine));
					int l = pos - startPos;
					posInLine += l;
					startPos += l;
				} else {
					tokens.add(new Token(Kind.IDENTIFIER, startPos, pos - startPos, line, posInLine));
					int l = pos - startPos;
					posInLine += l;
					startPos += l;
				}
				state = State.START;
			}
				break;

			case IN_DIGIT: {
				while (Character.isDigit(chars[pos])) {
					pos++;
				}

				String integ = new String(chars, startPos, pos - startPos);
				try {
					Integer.parseInt(integ);
					tokens.add(new Token(Kind.INTEGER_LITERAL, startPos, (pos - startPos), line, posInLine));
					int l = pos - startPos;
					startPos += l;
					posInLine += l;
					state = State.START;
				} catch (NumberFormatException e) {
					throw new LexicalException("INTEGER_LITERAL Error " + startPos, startPos);
				}

			}
				break;

			case COMMENT: {
				while (pos + 1 < chars.length && chars[pos] != '\n' && chars[pos] != '\r' && chars[pos] != EOFchar) {
					posInLine++;
					pos++;
				}
				if (chars[pos] == '\n' || chars[pos] == '\r' || chars[pos] == EOFchar) {
					posInLine = 1;
					line++;
					if (chars[pos] == '\r') {
						pos++;
						if (chars[pos] == '\n') {
							pos++;// \n followed by \r is considered as on line terminator but two characters
						}
					} else
						pos++;

				}
				state = State.START;
			}
				break;

			default:
				assert false;

			}
		}

		tokens.add(new Token(Kind.EOF, pos, 0, line, posInLine));
		return this;

	}

	public static boolean isASCIIChar(char c) {
		// Auto-generated method stub
		return ((int) c) >= 0 && ((int) c) < 127;
	}

	public int skipWhiteSpace(int pos) {
		while (chars[pos] == ' ' || chars[pos] == '\t' || chars[pos] == '\r') {
			pos++;
			if (pos >= chars.length)
				break;
		}
		return pos;
	}

	/**
	 * Returns true if the internal interator has more Tokens
	 * 
	 * @return
	 */
	public boolean hasTokens() {
		return nextTokenPos < tokens.size();
	}

	/**
	 * Returns the next Token and updates the internal iterator so that the next
	 * call to nextToken will return the next token in the list.
	 * 
	 * It is the callers responsibility to ensure that there is another Token.
	 * 
	 * Precondition: hasTokens()
	 * 
	 * @return
	 */
	public Token nextToken() {
		return tokens.get(nextTokenPos++);
	}

	/**
	 * Returns the next Token, but does not update the internal iterator. This means
	 * that the next call to nextToken or peek will return the same Token as
	 * returned by this methods.
	 * 
	 * It is the callers responsibility to ensure that there is another Token.
	 * 
	 * Precondition: hasTokens()
	 * 
	 * @return next Token.
	 */
	public Token peek() {
		return tokens.get(nextTokenPos);
	}

	/**
	 * Resets the internal iterator so that the next call to peek or nextToken will
	 * return the first Token.
	 */
	public void reset() {
		nextTokenPos = 0;
	}

	/**
	 * Returns a String representation of the list of Tokens
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Tokens:\n");
		for (int i = 0; i < tokens.size(); i++) {
			sb.append(tokens.get(i)).append('\n');
		}
		return sb.toString();
	}

}