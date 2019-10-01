# Compiler
Designed a Compiler for a programming language given by a context free grammar. 

Implementation of a compiler for a programming language given by the following context free grammar:

program ::= IDENT block program ::= IDENT param_dec ( , param_dec )* block paramDec ::= ( KW_URL | KW_FILE | KW_INTEGER | KW_BOOLEAN) IDENT block ::= { ( dec | statement) * } dec ::= ( KW_INTEGER | KW_BOOLEAN | KW_IMAGE | KW_FRAME) IDENT statement ::= OP_SLEEP expression ; | whileStatement | ifStatement | chain ; | assign ; assign ::= IDENT ASSIGN expression chain ::= chainElem arrowOp chainElem ( arrowOp chainElem)* whileStatement ::= KW_WHILE ( expression ) block ifStatement ::= KW_IF ( expression ) block arrowOp ∷= ARROW | BARARROW chainElem ::= IDENT | filterOp arg | frameOp arg | imageOp arg filterOp ::= OP_BLUR |OP_GRAY | OP_CONVOLVE frameOp ::= KW_SHOW | KW_HIDE | KW_MOVE | KW_XLOC |KW_YLOC imageOp ::= OP_WIDTH |OP_HEIGHT | KW_SCALE arg ::= ε | ( expression ( ,expression)* ) expression ∷= term ( relOp term)* term ∷= elem ( weakOp elem)* elem ∷= factor ( strongOp factor)* factor ∷= IDENT | INT_LIT | KW_TRUE | KW_FALSE | KW_SCREENWIDTH | KW_SCREENHEIGHT | ( expression ) relOp ∷= LT | LE | GT | GE | EQUAL | NOTEQUAL weakOp ∷= PLUS | MINUS | OR
strongOp ∷= TIMES | DIV | AND | MOD

The basic data types in this programming language are integer, boolean, image, file, frame and image.

Implementation of the compiler is done in JAVA and to generate the class files in java byte code, I have used ASM (java bytecode framework)

All the steps are followed in this project for the compiler design:

1. Scanning
2. Parsing
3. Generating abstract syntax tree
4. Type Checking
5. Generating bytecode

Further implementation of this project will include optimising the code to efficiently use JAVA JVM stack.
