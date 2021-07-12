

[![](https://jitpack.io/v/laim0nas100/ConfigurableLexer.svg)](https://jitpack.io/#laim0nas100/ConfigurableLexer)

Simple-ish configurable lexer framework. Tokenizer -> Lexer -> Parser (token matcher).

# Features
Doesn't use BNF. Is faster for small inputs. way less complicated and reconfigurable on the run without recompilation.
Every part of this is streamed.
## Tokenizer
- Character streaming
- Character filtering
- Full UTF-8 codepoint support
## Lexer
- Assign meaning to the token, or any other.
- Break down break-able tokens. For example "10+10" could be interpreted as 1 literal or 3 separate tokens, if '+' symbol is assigned 'break' property.
### Using char listeners:
- Comment parser (with ability to ignore them)
--Line
--Multiline (with optional nesting)
- String parser
- Position tracker

## Parser
The most basic parser. For anything more complicated just program logic yourself.
- Ability to group tokens into expressions of predetermined (optionally repeatable) size, to further simplify token combination logic.
## Example
For full example (tokenizing, lexing, and token matching) check out.
[MainParse01.java ](https://github.com/laim0nas100/ConfigurableLexer/blob/master/src/test/java/test/MAINParse01.java) 