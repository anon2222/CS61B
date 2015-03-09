package db61b;

import java.util.HashMap;


/** A collection of Tables, indexed by name.
 *  @author Bo Liu*/
class Database {
    /** An empty database. */
    public Database() {
        _map = new HashMap<String, Table>();
    }

    /** Return the Table whose name is NAME stored in this database, or null
     *  if there is no such table. */
    public Table get(String name) {
        Table temp = _map.get(name);
        return temp;
    }

    /** Set or replace the table named NAME in THIS to TABLE.  TABLE and
     *  NAME must not be null, and NAME must be a valid name for a table. */
    public void put(String name, Table table) {
        if (name == null || table == null) {
            throw new IllegalArgumentException("null argument");
        }
        _map.put(name, table);
    }

    /** The database. */
    private HashMap<String, Table> _map;
}
