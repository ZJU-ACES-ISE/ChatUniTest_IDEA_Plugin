import org.junit.Test;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class TestCompiler {
    @Test
    public void test() {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        String name = compiler.name();
        System.out.println(name);
    }
}
