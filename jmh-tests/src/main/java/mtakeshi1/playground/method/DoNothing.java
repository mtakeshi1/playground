package mtakeshi1.playground.method;

import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.CompilerControl.Mode;

public interface DoNothing {
    @CompilerControl(Mode.DONT_INLINE)
    Object doNothing(Integer x);
}
