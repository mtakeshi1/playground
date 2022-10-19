package mtakeshi1.playground;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class JavaDemo {

    public record User(long id, String name) {
    }

    public record UserPref(long id, boolean darkMode) {
    }

    @FunctionalInterface  interface Generator<A> {
        A get();

        default <B> Generator<B> map(Function<A, B> function) {
            return () -> function.apply(get());
        }

        default <B> Generator<B> flatMap(Function<A, Generator<B>> function) {
            return () -> function.apply(get()).get();
        }

    }

    public static Optional<User> userById(long id) {
        throw new RuntimeException();
    }

    public static Generator<UserPref> pref(long id) {
        throw new RuntimeException();
    }

    public static String render(String userName, boolean darkMode) {
        throw new RuntimeException();
    }

    public static Optional<Generator<String>> toHtml(long id) {
        return userById(id).map(user -> pref(id).map(p -> render(user.name(), p.darkMode())));
    }



}
