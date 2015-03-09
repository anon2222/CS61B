package db61b;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import static db61b.Utils.*;

/** A single table in a database.
 *  @author Bo Liu
 */
class Table implements Iterable<Row> {
    /** A new Table whose columns are given by COLUMNTITLES, which may
     *  not contain dupliace names. */
    Table(String[] columnTitles) {
        for (int i = columnTitles.length - 1; i >= 1; i -= 1) {
            for (int j = i - 1; j >= 0; j -= 1) {
                if (columnTitles[i].equals(columnTitles[j])) {
                    throw error("duplicate column name: %s",
                                columnTitles[i]);
                }
            }
        }
        _columnTitles = columnTitles;
    }

    /** A new Table whose columns are give by COLUMNTITLES. */
    Table(List<String> columnTitles) {
        this(columnTitles.toArray(new String[columnTitles.size()]));
    }

    /** Return the number of columns in this table. */
    public int columns() {
        return _columnTitles.length;
    }

    /** Return the title of the Kth column.  Requires 0 <= K < columns(). */
    public String getTitle(int k) {
        if (k >= 0 && k < columns()) {
            return _columnTitles[k];
        }
        throw error("IndexOutOfBounds");
    }

    /** Return the number of the column whose title is TITLE, or -1 if
     *  there isn't one. */
    public int findColumn(String title) {
        for (int i = 0; i < columns(); i += 1) {
            if (_columnTitles[i].equals(title)) {
                return i;
            }
        }
        return -1;
    }

    /** Return the number of Rows in this table. */
    public int size() {
        return _rows.size();
    }

    /** Returns an iterator that returns my rows in an unspecfied order. */
    @Override
    public Iterator<Row> iterator() {
        return _rows.iterator();
    }

    /** Add ROW to THIS if no equal row already exists.  Return true if anything
     *  was added, false otherwise. */
    public boolean add(Row row) {
        return _rows.add(row);
    }

    /** Read the contents of the file NAME.db, and return as a Table.
     *  Format errors in the .db file cause a DBException. */
    static Table readTable(String name) {
        BufferedReader input;
        Table table;
        input = null;
        table = null;
        try {
            input = new BufferedReader(new FileReader(name + ".db"));
            String header = input.readLine();
            if (header == null) {
                throw error("missing header in DB file");
            }
            String[] columnNames = header.split(",");
            table = new Table(columnNames);
            String line = input.readLine();
            while (line != null) {
                Row r = new Row(line.split(","));
                table.add(r);
                line = input.readLine();
            }
        } catch (FileNotFoundException e) {
            throw error("could not find %s.db", name);
        } catch (IOException e) {
            throw error("problem reading from %s.db", name);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    /* Ignore IOException */
                }
            }
        }
        return table;
    }

    /** Write the contents of TABLE into the file NAME.db. Any I/O errors
     *  cause a DBException. */
    void writeTable(String name) {
        PrintStream output;
        output = null;
        try {
            String sep;
            sep = "";
            output = new PrintStream(name + ".db");
            for (int i = 0; i < columns(); i += 1) {
                sep += getTitle(i);
                if (i < columns() - 1) {
                    sep += ",";
                }
            }
            for (Row row : this) {
                sep += "\n";
                for (int i = 0; i < row.size(); i += 1) {
                    sep += row.get(i);
                    if (i < row.size() - 1) {
                        sep += ",";
                    }
                }
            }
            output.append(sep);
        } catch (IOException e) {
            throw error("trouble writing to %s.db", name);
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    /** Print my contents on the standard output. */
    void print() {
        for (Row row : _rows) {
            String s = " ";
            for (int i = 0; i < row.size(); i += 1) {
                s += " " + row.get(i);
            }
            System.out.println(s);
        }
    }

    /** Return a new Table whose columns are COLUMNNAMES, selected from
     *  rows of this table that satisfy CONDITIONS. */
    Table select(List<String> columnNames, List<Condition> conditions) {
        Table result = new Table(columnNames);
        ArrayList<Column> lst = new ArrayList<Column>();
        for (String colN : columnNames) {
            lst.add(new Column(colN, this));
        }
        for (Row row : this) {
            if (helper(conditions, row)) {
                result.add(new Row(lst, row));
            }
        }
        return result;
    }

    /** Helper method that checks if the ROWS satisfies the CONDITIONS.
     *  return true if the row satisfies the conditions */
    boolean helper(List<Condition> conditions, Row... rows) {
        for (Condition con : conditions) {
            if (con.test(rows)) {
                continue;
            }
            return false;
        }
        return true;
    }

    /** Return a new Table whose columns are COLUMNNAMES, selected
     *  from pairs of rows from this table and from TABLE2 that match
     *  on all columns with identical names and satisfy CONDITIONS. */
    Table select(Table table2, List<String> columnNames,
                 List<Condition> conditions) {
        Table result = new Table(columnNames);
        ArrayList<Column> lst = new ArrayList<Column>();
        for (String colN : columnNames) {
            lst.add(new Column(colN, this, table2));
        }
        ArrayList<Column> common1 = new ArrayList<Column>();
        ArrayList<Column> common2 = new ArrayList<Column>();
        for (int i = 0; i < columns(); i += 1) {
            String col1 = getTitle(i);
            for (int j = 0; j < table2.columns(); j += 1) {
                String col2 = table2.getTitle(j);
                if (col1.equals(col2)) {
                    common1.add(new Column(col1, this));
                    common2.add(new Column(col2, table2));
                }
            }
        }
        for (Row row1 : this) {
            for (Row row2 : table2) {
                if (!helper(conditions, row1, row2)) {
                    continue;
                }
                if (!equijoin(common1, common2, row1, row2)) {
                    continue;
                }
                result.add(new Row(lst, row1, row2));
            }
        }
        return result;
    }

    /** Return true if the columns COMMON1 from ROW1 and COMMON2 from
     *  ROW2 all have identical values.  Assumes that COMMON1 and
     *  COMMON2 have the same number of elements and the same names,
     *  that the columns in COMMON1 apply to this table, those in
     *  COMMON2 to another, and that ROW1 and ROW2 come, respectively,
     *  from those tables. */
    private static boolean equijoin(List<Column> common1, List<Column> common2,
                                    Row row1, Row row2) {
        for (int i = 0; i < common1.size(); i += 1) {
            Column col1 = common1.get(i);
            Column col2 = common2.get(i);
            if (col1.getFrom(row1).equals(col2.getFrom(row2))) {
                continue;
            }
            return false;
        }
        return true;
    }

    /** My rows. */
    private HashSet<Row> _rows = new HashSet<>();

    /** My columnTitles. */
    private String[] _columnTitles;
}

