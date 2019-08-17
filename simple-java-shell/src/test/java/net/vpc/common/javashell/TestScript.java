package net.vpc.common.javashell;

import net.vpc.common.javashell.parser.JShellParser;
import net.vpc.common.javashell.parser.ParseException;
import net.vpc.common.javashell.parser.nodes.Node;
import org.junit.Test;

public class TestScript {
    @Test
    public void test() {
        JShellParser p = new JShellParser();
        Node n = null;
        try {
            n = p.parse("$(dirname $0)");
//            n = p.parse("base=$(dirname $0)");
            System.out.println(n);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
