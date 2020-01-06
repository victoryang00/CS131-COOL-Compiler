%{
#include <cool-parse.h>
#include <stringtab.h>
#include <utilities.h>
#include <string>
using namespace std;
/* The compiler assumes these identifiers. */
#define yylval cool_yylval
#define yylex  cool_yylex

/* Max size of string constants */
#define MAX_STR_CONST 1025
#define YY_NO_UNPUT   /* keep g++ happy */

extern FILE *fin; /* we read from this file */

/* define YY_INPUT so we read from the FILE fin:
 * This change makes it possible to use this scanner in
 * the Cool compiler.
 */
#undef YY_INPUT
#define YY_INPUT(buf,result,max_size) \
  if ( (result = fread( (char*)buf, sizeof(char), max_size, fin)) < 0) \
    YY_FATAL_ERROR( "read() in flex scanner failed");

char string_buf[MAX_STR_CONST]; /* to assemble string constants */
char *string_buf_ptr;

extern int curr_lineno;

extern YYSTYPE cool_yylval;

/*
 *  Add Your own definitions here
 */
extern int comment_layer=0;
%}

%option noyywrap

/*Define names for regular expressions here.*/

digit       [0-9]
darrow      =>
%Start      COMMENT
%Start      START_COMMENT
%Start      STRING
/*
#define CLASS 258
#define ELSE 259
#define FI 260
#define IF 261
#define IN 262
#define INHERITS 263
#define LET 264
#define LOOP 265
#define POOL 266
#define THEN 267
#define WHILE 268
#define CASE 269
#define ESAC 270
#define OF 271
#define DARROW 272
#define NEW 273
#define ISVOID 274
#define STR_CONST 275
#define INT_CONST 276
#define BOOL_CONST 277
#define TYPEID 278
#define OBJECTID 279
#define ASSIGN 280
#define NOT 281
#define LE 282
#define ERROR 283
#define LET_STMT 285
*/
%%

 /*
  * Define regular expressions for the tokens of COOL here. Make sure, you
  * handle correctly special cases, like:
  *   - Nested comments
  *   - String constants: They use C like systax and can contain escape
  *     sequences. Escape sequence \c is accepted for all characters c. Except
  *     for \n \t \b \f, the result is c.
  *   - Keywords: They are case-insensitive except for the values true and
  *     false, which must begin with a lower-case letter.
  *   - Multiple-character operators (like <-): The scanner should produce a
  *     single token for every such operator.
  *   - Line counting: You should keep the global variable curr_lineno updated
  *     with the correct line number
  */
 /* This is for Initial func for comment, the function puts in the comment and adds up the layer */
<INITIAL,COMMENT,START_COMMENT>"(*" {
  comment_layer++;
  BEGIN COMMENT;
}

 /* Define different kind of comment to pair.(special case: '\n') */
<COMMENT>[^\n(*]* { }

<COMMENT>[()*] { }

  /* This is the end of comment, logically return to final automachine mode. */
<COMMENT>"*)" {
    comment_layer--;
    if (comment_layer == 0) {
        BEGIN 0;
    }
}

  /* Define the EOF and return error.(special case) */
<COMMENT><<EOF>> {
    yylval.error_msg = "EOF in comment";
    BEGIN 0;
    return ERROR;
}

  /* Define the unmateched end sign recognition. */
"*)" {
    yylval.error_msg = "Unmatched *)";
    return ERROR;
}

  /* -------------
   * START COMMENT 
   *  Definition
   * -------------
   */


<INITIAL>"--" { BEGIN START_COMMENT; }

 /* Pair any character.(special case: '\n') */ 
<START_COMMENT>[^\n]* { }

 /* If seen '\n' in the end, the comment ends */
<START_COMMENT>\n {
    curr_lineno++;
    BEGIN 0;
}


  /* -------------
   *  STR_CONST
   *  Definition
   * -------------
   * String constants: They use C like systax and can contain escape
   *     sequences. Escape sequence \c is accepted for all characters c. Except
   *     for \n \t \b \f, the result is c.
   */

<INITIAL>(\") {
    BEGIN STRING;
    yymore();
}

 /* If '\\' '\"' '\n' is read, it may cause some uncertainty */
<STRING>[^\\\"\n]* { yymore(); }

 /* Normally escape characters*/
<STRING>\\[^\n] { yymore(); }

 /* If lexer see a '\\' at the end of a line, the string will still continues. */
<STRING>\\\n {
    curr_lineno++;
    yymore(); //always check the ability.
}

 /* If meet EOF in the middle of a string, error */
<STRING><<EOF>> {
    yylval.error_msg = "EOF in string constant";
    BEGIN 0;
    yyrestart(yyin);
    return ERROR;
}

 /* To meet a '\n' in the middle of a string without a '\\', error */
<STRING>\n {
    yylval.error_msg = "Unterminated string constant for \n";
    BEGIN 0;
    curr_lineno++;
    return ERROR;
}

 /* To meet a "\\0"*/
<STRING>\\0 {
    yylval.error_msg = "Unterminated string constant for 0";
    BEGIN 0;
    return ERROR;
}

 /* string ends, we need to deal with some escape characters */
<STRING>\" {
    std::string input(yytext, yyleng);

    // remove the '\"'s on both sizes.
    input = input.substr(1, input.length() - 2);

    std::string output = "";
    std::string::size_type pos;
    
    if (input.find_first_of('\0') != std::string::npos) {
        yylval.error_msg = "String contains null character";
        BEGIN 0;
        return ERROR;    
    }

    while ((pos = input.find_first_of("\\")) != std::string::npos) {
        output += input.substr(0, pos);

        switch (input[pos + 1]) {
        case 'b':
            output += "\b";
            break;
        case 't':
            output += "\t";
            break;
        case 'n':
            output += "\n";
            break;
        case 'f':
            output += "\f";
            break;
        default:
            output += input[pos + 1];
            break;
        }

        input = input.substr(pos + 2, input.length() - 2);
    }

    output += input;

    if (output.length() > 1024) {
        yylval.error_msg = "String constant too long";
        BEGIN 0;
        return ERROR;    
    }

    cool_yylval.symbol = stringtable.add_string((char*)output.c_str());
    BEGIN 0;
    return STR_CONST;

}


 /* ----------
  *  Keywords
  * Definition
  * ----------
  * Keywords: They are case-insensitive except for the values true and
  *     false, which must begin with a lower-case letter.
  */

 /* CLASS */
(?i:class) { return CLASS; }

 /* ELSE */
(?i:else) { return ELSE; }

 /* FI */
(?i:fi) { return FI; }

 /* IF */
(?i:if) { return IF; }

 /* IN*/
(?i:in) { return IN; }

 /* INHERITS */
(?i:inherits) { return INHERITS; }

 /* LET */
(?i:let) { return LET; }

 /* LOOP */
(?i:loop) { return LOOP; }

 /* POOL */
(?i:pool) { return POOL; }

 /* THEN */
(?i:then) { return THEN; }

 /* WHILE */
(?i:while) { return WHILE; }

 /* CASE */
(?i:case) { return CASE; }

 /* ESAC */
(?i:esac) { return ESAC; }

 /* OF */
(?i:of) { return OF; }

 /* NEW */
(?i:new) { return NEW; }

 /* ISVOID */
(?i:isvoid) { return ISVOID; }

 /* NOT */
(?i:not) { return NOT; }


 /* INT_CONST */
{digit}+ {
    cool_yylval.symbol = inttable.add_string(yytext);
    return INT_CONST;
}

 /* BOOL_CONST */
t(?i:rue) {
    cool_yylval.boolean = 1;
    return BOOL_CONST;
}

f(?i:alse) {
    cool_yylval.boolean = 0;
    return BOOL_CONST;
}

 /* White Space */
[ \f\r\t\v]+ { }

 /* TYPEID */
[A-Z][A-Za-z0-9_]* {
    cool_yylval.symbol = idtable.add_string(yytext);
    return TYPEID;
}

 /* To treat lines. */
"\n" {
    curr_lineno++;
}

 /* OBJECTID */
[a-z][A-Za-z0-9_]* {
    cool_yylval.symbol = idtable.add_string(yytext);
    return OBJECTID;
}

 /* =========
  * operators
  * =========
  */

 /* ASSIGN */
"<-" { return ASSIGN; }

 /* LE */
"<=" { return LE; }

 /* DARROW */
"=>" { return DARROW; }
"+" { return int('+'); }
"-" { return int('-'); }
"*" { return int('*'); }
"/" { return int('/'); }
"<" { return int('<'); }
"=" { return int('='); }
"." { return int('.'); }
";" { return int(';'); }
"~" { return int('~'); }
"{" { return int('{'); }
"}" { return int('}'); }
"(" { return int('('); }
")" { return int(')'); }
":" { return int(':'); }
"@" { return int('@'); }
"," { return int(','); }

[^\n] {
    //error
    yylval.error_msg = yytext;
    return ERROR;
}

%%
