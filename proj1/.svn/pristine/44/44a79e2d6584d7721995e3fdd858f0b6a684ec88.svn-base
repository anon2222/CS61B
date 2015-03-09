package db61b;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import java.util.ArrayList;

/** The major tests for the db61b project.
 *  @author Bo Liu
 */
public class Test2 {
    /** Setting up the table before the tests. */
    @Before
    public void setUp() {
        t1 = Table.readTable("test");
        t2 = Table.readTable("test2");
    }

    /** First Test for the Condition class.
     */
    @Test
    public void testCondition() {
        System.out.println("testCondition");
        Column col0 = new Column("SID", t1);
        Condition con0 = new Condition(col0, "=", "101");
        Table t0 = new Table(new String[] {"SID", "CCN", "Grade"});
        for (Row row : t1) {
            if (con0.test(row)) {
                t0.add(row);
            }
        }
        t0.print();
    }

    /** Tests for the Selection class. */
    @Test
    public void testSelect() {
        System.out.println("testSelect");
        ArrayList<String> lst = new ArrayList<String>();
        lst.add("Firstname");
        lst.add("Major");
        Table temp = t2.select(lst, new ArrayList<Condition>());
        temp.print();

        System.out.println("testSelect with Condition");
        ArrayList<Condition> con = new ArrayList<Condition>();
        con.add(new Condition(new Column("Major", t2), "=", "EECS"));
        temp = t2.select(lst, con);
        temp.print();

        System.out.println("testing two tables");
        lst.add("Grade");
        temp = t2.select(t1, lst, con);
        temp.print();
    }

    public static void main(String... args) {
        System.exit(ucb.junit.textui.runClasses(Test2.class));
    }

    /** the tables we use to test.*/
    private Table t1, t2;
}
