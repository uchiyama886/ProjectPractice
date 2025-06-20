package utility;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ValueHolder Class Tests")
class ValueHolderTest {

    @Test
    @DisplayName("Default constructor initializes value to null")
    void testDefaultConstructor() {
        ValueHolder<String> holder = new ValueHolder<>();
        assertNull(holder.get(), "Value should be null after default construction");
    }

    @Test
    @DisplayName("Constructor with initial value sets the value correctly")
    void testConstructorWithInitialValue() {
        ValueHolder<Integer> holder = new ValueHolder<>(100);
        assertEquals(100, holder.get(), "Value should be set to the initial value");

        ValueHolder<String> stringHolder = new ValueHolder<>("Hello");
        assertEquals("Hello", stringHolder.get(), "String value should be set correctly");
    }

    @Test
    @DisplayName("get() returns the held value")
    void testGet() {
        String testString = "Test Value";
        ValueHolder<String> holder = new ValueHolder<>(testString);
        assertEquals(testString, holder.get(), "get() should return the stored value");
    }

    @Test
    @DisplayName("set() updates the value and returns the previous value")
    void testSet() {
        ValueHolder<Double> holder = new ValueHolder<>(10.5);
        
        Double previous = holder.set(20.5);
        assertEquals(10.5, previous, "set() should return the previous value");
        assertEquals(20.5, holder.get(), "set() should update the current value");

        previous = holder.set(null);
        assertEquals(20.5, previous, "set() should return the previous value even if new value is null");
        assertNull(holder.get(), "set() should allow setting null value");
    }

    @Test
    @DisplayName("setDo() updates the value using a provided function")
    void testSetDo() {
        ValueHolder<Integer> holder = new ValueHolder<>(5);
        
        // 値を2倍にする関数
        Function<Integer, Integer> doubleFunction = val -> val * 2;
        holder.setDo(doubleFunction);
        assertEquals(10, holder.get(), "setDo() should update value by doubling it");

        // 値を文字列に変換して連結する関数
        ValueHolder<String> stringHolder = new ValueHolder<>("initial");
        Function<String, String> appendFunction = str -> str + "_appended";
        stringHolder.setDo(appendFunction);
        assertEquals("initial_appended", stringHolder.get(), "setDo() should update string value by appending");
    }

    @Test
    @DisplayName("toString() returns a correct string representation")
    void testToString() {
        ValueHolder<String> holder = new ValueHolder<>("MyValue");
        String expected = "utility.ValueHolder[MyValue]";
        assertEquals(expected, holder.toString(), "toString() should return correct format for non-null value");

        ValueHolder<Integer> nullHolder = new ValueHolder<>();
        String expectedNull = "utility.ValueHolder[null]";
        assertEquals(expectedNull, nullHolder.toString(), "toString() should return correct format for null value");
    }
}