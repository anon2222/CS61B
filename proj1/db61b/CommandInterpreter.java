package db61b;

import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Scanner;

import static db61b.Utils.*;
import static db61b.Tokenizer.*;

/** An object that reads and interprets a sequence of commands from an
 *  input source.
 *  @author Paul Hilfinger and Bo Liu */
class CommandInterpreter {
    /** A new CommandInterpreter executing commands read from INP, writing
     *  prompts on PROMPTER, if it is non-null. */
    CommandInterpreter(Scanner inp, PrintStream prompter) {
        _input = new Tokenizer(inp, prompter);
        _database = new Database();
    }

    /** Parse and execute one statement from the token stream.  Return true
     *  iff the command is something other than quit or exit. */
    boolean statement() {
        switch (_input.peek()) {
        case "create":
            createStatement();
            break;
        case "load":
            loadStatement();
            break;
        case "exit": case "quit":
            exitStatement();
            return false;
        case "*EOF*":
            return false;
        case "insert":
            insertStatement();
            break;
        case "print":
            printStatement();
            break;
        case "select":
            selectStatement();
            break;
        case "store":
            storeStatement();
            break;
        default:
            throw error("unrecognizable command");
        }
        return true;
    }

    /** Parse and execute a create statement from the token stream. */
    void createStatement() {
        _input.next("create");
        _input.next("table");
        String name = name();
        Table table = tableDefinition();
        _input.next(";");
        _database.put(name, table);
    }

    /** Parse and execute an exit or quit statement. Actually does nothing
     *  except check syntax, since statement() handles the actual exiting. */
    void exitStatement() {
        if (!_input.nextIf("quit")) {
            _input.next("exit");
        }
        _input.next(";");
    }

    /** Parse and execute an insert statement from the token stream. */
    void insertStatement() {
        _input.next("insert");
        _input.next("into");
        Table table = tableName();
        _input.next("values");

        ArrayList<String> values = new ArrayList<>();
        values.add(literal());
        while (_input.nextIf(",")) {
            values.add(literal());
        }
        _input.next(";");

        table.add(new Row(values.toArray(new String[values.size()])));
    }

    /** Parse and execute a load statement from the token stream. */
    void loadStatement() {
        _input.next("load");
        String name = name();
        Table temp = Table.readTable(name);
        _database.put(name, temp);
        _input.next(";");
        System.out.printf("Loaded %s.db%n", name);
    }

    /** Parse and execute a store statement from the token stream. */
    void storeStatement() {
        _input.next("store");
        String name = _input.peek();
        Table table = tableName();
        table.writeTable(name);
        _input.next(";");
        System.out.printf("Stored %s.db%n", name);
    }

    /** Parse and execute a print statement from the token stream. */
    void printStatement() {
        _input.next("print");
        String name = _input.peek();
        Table table = tableName();
        _input.next(";");
        System.out.printf("Contents of %s:%n", name);
        table.print();
    }

    /** Parse and execute a select statement from the token stream. */
    void selectStatement() {
        Table table = selectClause();
        _input.next(";");
        System.out.println("Search results:");
        table.print();
    }

    /** Parse and execute a table definition, returning the specified
     *  table. */
    Table tableDefinition() {
        Table table;
        if (_input.nextIf("(")) {
            ArrayList<String> lst = new ArrayList<String>();
            lst.add(columnName());
            while (_input.nextIf(",")) {
                lst.add(columnName());
            }
            _input.next(")");
            table = new Table(lst);
        } else {
            _input.next("as");
            table = selectClause();
        }
        return table;
    }

    /** Parse and execute a select clause from the token stream, returning the
     *  resulting table. */
    Table selectClause() {
        _input.next("select");
        ArrayList<String> lst = new ArrayList<String>();
        lst.add(columnName());
        while (_input.nextIf(",")) {
            lst.add(columnName());
        }
        _input.next("from");
        Table t = tableName();
        Table t2 = null;
        if (_input.nextIf(",")) {
            t2 = tableName();
        }
        ArrayList<Condition> con = new ArrayList<Condition>();
        if (_input.nextIf("where")) {
            if (t2 == null) {
                con = conditionClause(t);
            } else {
                con = conditionClause(t, t2);
            }
        }
        if (t2 == null) {
            return t.select(lst, con);
        } else {
            return t.select(t2, lst, con);
        }
    }

    /** Parse and return a valid name (identifier) from the token stream. */
    String name() {
        return _input.next(Tokenizer.IDENTIFIER);
    }

    /** Parse and return a valid column name from the token stream. Column
     *  names are simply names; we use a different method name to clarify
     *  the intent of the code. */
    String columnName() {
        return name();
    }

    /** Parse a valid table name from the token stream, and return the Table
     *  that it designates, which must be loaded. */
    Table tableName() {
        String name = name();
        Table table = _database.get(name);
        if (table == null) {
            throw error("unknown table: %s", name);
        }
        return table;
    }

    /** Parse a literal and return the string it represents (i.e., without
     *  single quotes). */
    String literal() {
        String lit = _input.next(Tokenizer.LITERAL);
        return lit.substring(1, lit.length() - 1).trim();
    }

    /** Parse and return a list of Conditions that apply to TABLES from the
     *  token stream.  This denotes the conjunction (`and') zero
     *  or more Conditions. */
    ArrayList<Condition> conditionClause(Table... tables) {
        ArrayList<Condition> lst = new ArrayList<Condition>();
        lst.add(condition(tables));
        while (_input.nextIf("and")) {
            Condition next = condition(tables);
            lst.add(next);
        }
        return lst;
    }

    /** Parse and return a Condition that applies to TABLES from the
     *  token stream. */
    Condition condition(Table... tables) {
        String temp = columnName();
        Column col1 = new Column(temp, tables);
        String relation = _input.next(Tokenizer.RELATION);
        if (_input.nextIs(Tokenizer.LITERAL)) {
            return new Condition(col1, relation, literal());
        } else {
            Column col2 = new Column(columnName(), tables);
            return new Condition(col1, relation, col2);
        }
    }

    /** Advance the input past the next semicolon. */
    void skipCommand() {
        while (!_input.nextIf(";") && !_input.nextIf("*EOF*")) {
            _input.next();
        }
    }

    /** The command input source. */
    private Tokenizer _input;
    /** Database containing all tables. */
    private Database _database;
}
