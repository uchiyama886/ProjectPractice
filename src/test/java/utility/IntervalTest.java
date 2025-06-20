package utility;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger; // AtomicIntegerをインポート
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.lang.reflect.Field; // Fieldをインポート

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Interval Class Unit Tests")
class IntervalTest {

    // プライベートフィールドにアクセスするためのヘルパーメソッド
    private Object getPrivateField(Object target, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target);
    }

    @Test
    @DisplayName("Default constructor initializes with nulls")
    void testDefaultConstructor() throws NoSuchFieldException, IllegalAccessException {
        Interval<Integer> interval = new Interval<>();
        assertNull(getPrivateField(interval, "startValue"), "startValue should be null");
        assertNull(getPrivateField(interval, "loopCondition"), "loopCondition should be null");
        assertNull(getPrivateField(interval, "nextValueFunction"), "nextValueFunction should be null");
    }

    @Test
    @DisplayName("Parameterized constructor initializes all fields")
    void testParameterizedConstructor() throws NoSuchFieldException, IllegalAccessException {
        Integer start = 0;
        Predicate<Integer> condition = i -> i < 5;
        UnaryOperator<Integer> next = i -> i + 1;

        Interval<Integer> interval = new Interval<>(start, condition, next);
        assertEquals(start, getPrivateField(interval, "startValue"), "startValue should be set");
        assertEquals(condition, getPrivateField(interval, "loopCondition"), "loopCondition should be set");
        assertEquals(next, getPrivateField(interval, "nextValueFunction"), "nextValueFunction should be set");
    }

    @Test
    @DisplayName("forEach() executes action for each value in range")
    void testForEachBasicIteration() {
        List<Integer> visitedValues = new ArrayList<>();
        Integer start = 0;
        Predicate<Integer> condition = i -> i < 3;
        UnaryOperator<Integer> next = i -> i + 1;

        Interval<Integer> interval = new Interval<>(start, condition, next);
        interval.forEach(visitedValues::add);

        assertEquals(3, visitedValues.size(), "Should visit 3 values");
        assertEquals(0, visitedValues.get(0), "First value should be 0");
        assertEquals(1, visitedValues.get(1), "Second value should be 1");
        assertEquals(2, visitedValues.get(2), "Third value should be 2");
    }

    @Test
    @DisplayName("forEach() does not execute action if loopCondition is initially false")
    void testForEachNoIteration() {
        List<Integer> visitedValues = new ArrayList<>();
        Integer start = 5;
        Predicate<Integer> condition = i -> i < 3;
        UnaryOperator<Integer> next = i -> i + 1;

        Interval<Integer> interval = new Interval<>(start, condition, next);
        interval.forEach(visitedValues::add);

        assertTrue(visitedValues.isEmpty(), "Should not execute any action if condition is initially false");
    }

    @Test
    @DisplayName("forEach() works with custom types and functions")
    void testForEachCustomType() {
        List<String> transformedStrings = new ArrayList<>();
        String start = "A";
        Predicate<String> condition = s -> s.length() < 4;
        UnaryOperator<String> next = s -> s + "A";

        Interval<String> interval = new Interval<>(start, condition, next);
        interval.forEach(transformedStrings::add);

        assertEquals(3, transformedStrings.size(), "Should visit 3 strings");
        assertEquals("A", transformedStrings.get(0));
        assertEquals("AA", transformedStrings.get(1));
        assertEquals("AAA", transformedStrings.get(2));
    }

    @Test
    @DisplayName("forEach() handles large ranges efficiently (without exhausting memory)")
    void testForEachLargeRange() {
        int start = 0;
        int end = 100_000;
        Predicate<Integer> condition = i -> i < end;
        UnaryOperator<Integer> next = i -> i + 1;

        AtomicInteger counter = new AtomicInteger(0);
        Interval<Integer> interval = new Interval<>(start, condition, next);
        interval.forEach(i -> counter.incrementAndGet());

        assertEquals(end, counter.get(), "Should count up to the end value");
    }

    @Test
    @DisplayName("forEach() terminates when loopCondition becomes false")
    void testForEachTermination() {
        List<Integer> visitedValues = new ArrayList<>();
        ValueHolder<Integer> counter = new ValueHolder<>(0);
        Integer start = 0;
        Predicate<Integer> condition = i -> counter.get() < 3;
        UnaryOperator<Integer> next = i -> i + 1;

        Interval<Integer> interval = new Interval<>(start, condition, next);
        interval.forEach(i -> {
            visitedValues.add(i);
            counter.set(counter.get() + 1);
        });

        assertEquals(3, visitedValues.size(), "Should execute action 3 times");
        assertEquals(0, visitedValues.get(0));
        assertEquals(1, visitedValues.get(1));
        assertEquals(2, visitedValues.get(2));
    }
    
    @Test
    @DisplayName("forEach() handles null startValue if condition and next can handle it")
    void testForEachWithNullStartValue() {
        List<String> visitedValues = new ArrayList<>();
        String initialValue = null;
        Predicate<String> condition = s -> s == null || s.length() < 2;
        UnaryOperator<String> next = s -> s == null ? "A" : s + "B";

        Interval<String> interval = new Interval<>(initialValue, condition, next);
        interval.forEach(visitedValues::add);

        assertEquals(2, visitedValues.size(), "Should execute action two times");
        assertNull(visitedValues.get(0), "First visited value should be null");
        assertEquals("A", visitedValues.get(1), "Second visited value should be 'A'");
    }
}