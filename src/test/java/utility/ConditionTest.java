package utility;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Condition Class Tests")
class ConditionTest {

    // Helper method to create a Runnable for testing if actions are executed
    private Runnable createAction(AtomicBoolean executed) {
        return () -> executed.set(true);
    }

    // Helper method to create a Supplier<Boolean> for testing conditions
    private Supplier<Boolean> createCondition(boolean value) {
        return () -> value;
    }

    @Nested
    @DisplayName("Instance Methods")
    class InstanceMethods {

        @Test
        @DisplayName("ifTrue executes action when condition is true")
        void testIfTrue_TrueCondition() {
            AtomicBoolean executed = new AtomicBoolean(false);
            new Condition(createCondition(true)).ifTrue(createAction(executed));
            assertTrue(executed.get(), "Action should be executed when condition is true");
        }

        @Test
        @DisplayName("ifTrue does not execute action when condition is false")
        void testIfTrue_FalseCondition() {
            AtomicBoolean executed = new AtomicBoolean(false);
            new Condition(createCondition(false)).ifTrue(createAction(executed));
            assertFalse(executed.get(), "Action should not be executed when condition is false");
        }

        @Test
        @DisplayName("ifFalse executes action when condition is false")
        void testIfFalse_FalseCondition() {
            AtomicBoolean executed = new AtomicBoolean(false);
            new Condition(createCondition(false)).ifFalse(createAction(executed));
            assertTrue(executed.get(), "Action should be executed when condition is false");
        }

        @Test
        @DisplayName("ifFalse does not execute action when condition is true")
        void testIfFalse_TrueCondition() {
            AtomicBoolean executed = new AtomicBoolean(false);
            new Condition(createCondition(true)).ifFalse(createAction(executed));
            assertFalse(executed.get(), "Action should not be executed when condition is true");
        }

        @Test
        @DisplayName("ifThen executes thenPassage when condition is true")
        void testIfThen_TrueCondition() {
            AtomicBoolean thenExecuted = new AtomicBoolean(false);
            Runnable thenAction = () -> thenExecuted.set(true);
            new Condition(createCondition(true)).ifThen(thenAction);
            assertTrue(thenExecuted.get(), "Then action should be executed when condition is true");
        }

        @Test
        @DisplayName("ifThen does not execute thenPassage when condition is false")
        void testIfThen_FalseCondition() {
            AtomicBoolean thenExecuted = new AtomicBoolean(false);
            Runnable thenAction = () -> thenExecuted.set(true);
            new Condition(createCondition(false)).ifThen(thenAction);
            assertFalse(thenExecuted.get(), "Then action should not be executed when condition is false");
        }

        @Test
        @DisplayName("ifElse executes elsePassage when condition is false")
        void testIfElse_FalseCondition() {
            AtomicBoolean elseExecuted = new AtomicBoolean(false);
            Runnable elseAction = () -> elseExecuted.set(true);
            new Condition(createCondition(false)).ifElse(elseAction);
            assertTrue(elseExecuted.get(), "Else action should be executed when condition is false");
        }

        @Test
        @DisplayName("ifElse does not execute elsePassage when condition is true")
        void testIfElse_TrueCondition() {
            AtomicBoolean elseExecuted = new AtomicBoolean(false);
            Runnable elseAction = () -> elseExecuted.set(true);
            new Condition(createCondition(true)).ifElse(elseAction);
            assertFalse(elseExecuted.get(), "Else action should not be executed when condition is true");
        }

        @Test
        @DisplayName("ifThenElse executes thenPassage when condition is true")
        void testIfThenElse_TrueCondition() {
            AtomicBoolean thenExecuted = new AtomicBoolean(false);
            AtomicBoolean elseExecuted = new AtomicBoolean(false);
            new Condition(createCondition(true)).ifThenElse(createAction(thenExecuted), createAction(elseExecuted));
            assertTrue(thenExecuted.get(), "Then action should be executed");
            assertFalse(elseExecuted.get(), "Else action should not be executed");
        }

        @Test
        @DisplayName("ifThenElse executes elsePassage when condition is false")
        void testIfThenElse_FalseCondition() {
            AtomicBoolean thenExecuted = new AtomicBoolean(false);
            AtomicBoolean elseExecuted = new AtomicBoolean(false);
            new Condition(createCondition(false)).ifThenElse(createAction(thenExecuted), createAction(elseExecuted));
            assertFalse(thenExecuted.get(), "Then action should not be executed");
            assertTrue(elseExecuted.get(), "Else action should be executed");
        }

        @Test
        @DisplayName("and returns true if both conditions are true")
        void testAnd_BothTrue() {
            Condition cond = new Condition(createCondition(true));
            assertTrue(cond.and(createCondition(true)), "and should return true when both are true");
        }

        @Test
        @DisplayName("and returns false if first condition is false")
        void testAnd_FirstFalse() {
            Condition cond = new Condition(createCondition(false));
            assertFalse(cond.and(createCondition(true)), "and should return false when first is false");
        }

        @Test
        @DisplayName("and returns false if second condition is false")
        void testAnd_SecondFalse() {
            Condition cond = new Condition(createCondition(true));
            assertFalse(cond.and(createCondition(false)), "and should return false when second is false");
        }

        @Test
        @DisplayName("or returns true if first condition is true")
        void testOr_FirstTrue() {
            Condition cond = new Condition(createCondition(true));
            assertTrue(cond.or(createCondition(false)), "or should return true when first is true");
        }

        @Test
        @DisplayName("or returns true if second condition is true")
        void testOr_SecondTrue() {
            Condition cond = new Condition(createCondition(false));
            assertTrue(cond.or(createCondition(true)), "or should return true when second is true");
        }

        @Test
        @DisplayName("or returns false if both conditions are false")
        void testOr_BothFalse() {
            Condition cond = new Condition(createCondition(false));
            assertFalse(cond.or(createCondition(false)), "or should return false when both are false");
        }

        @Test
        @DisplayName("whileTrue executes loop body while condition is true")
        void testWhileTrue() {
            AtomicInteger counter = new AtomicInteger(3);
            Supplier<Boolean> condition = () -> counter.get() > 0;
            Runnable loopBody = () -> counter.decrementAndGet();

            new Condition(condition).whileTrue(loopBody);

            assertEquals(0, counter.get(), "Loop should execute until condition is false");
        }

        @Test
        @DisplayName("whileTrue does not execute loop body if condition is initially false")
        void testWhileTrue_InitiallyFalse() {
            AtomicInteger counter = new AtomicInteger(0);
            Supplier<Boolean> condition = () -> counter.get() > 0;
            Runnable loopBody = () -> counter.incrementAndGet();

            new Condition(condition).whileTrue(loopBody);

            assertEquals(0, counter.get(), "Loop body should not execute if condition is initially false");
        }
    }

    @Nested
    @DisplayName("Static Methods")
    class StaticMethods {

        @Test
        @DisplayName("static ifTrue executes action when condition is true")
        void testStaticIfTrue_TrueCondition() {
            AtomicBoolean executed = new AtomicBoolean(false);
            Condition.ifTrue(createCondition(true), createAction(executed));
            assertTrue(executed.get(), "Static ifTrue action should be executed");
        }

        @Test
        @DisplayName("static ifTrue does not execute action when condition is false")
        void testStaticIfTrue_FalseCondition() {
            AtomicBoolean executed = new AtomicBoolean(false);
            Condition.ifTrue(createCondition(false), createAction(executed));
            assertFalse(executed.get(), "Static ifTrue action should not be executed");
        }

        @Test
        @DisplayName("static ifFalse executes action when condition is false")
        void testStaticIfFalse_FalseCondition() {
            AtomicBoolean executed = new AtomicBoolean(false);
            Condition.ifFalse(createCondition(false), createAction(executed));
            assertTrue(executed.get(), "Static ifFalse action should be executed");
        }

        @Test
        @DisplayName("static ifFalse does not execute action when condition is true")
        void testStaticIfFalse_TrueCondition() {
            AtomicBoolean executed = new AtomicBoolean(false);
            Condition.ifFalse(createCondition(true), createAction(executed));
            assertFalse(executed.get(), "Static ifFalse action should not be executed");
        }

        @Test
        @DisplayName("static ifThen executes thenPassage when condition is true")
        void testStaticIfThen_TrueCondition() {
            AtomicBoolean thenExecuted = new AtomicBoolean(false);
            Runnable thenAction = () -> thenExecuted.set(true);
            Condition.ifThen(createCondition(true), thenAction);
            assertTrue(thenExecuted.get(), "Static ifThen action should be executed");
        }

        @Test
        @DisplayName("static ifThen does not execute thenPassage when condition is false")
        void testStaticIfThen_FalseCondition() {
            AtomicBoolean thenExecuted = new AtomicBoolean(false);
            Runnable thenAction = () -> thenExecuted.set(true);
            Condition.ifThen(createCondition(false), thenAction);
            assertFalse(thenExecuted.get(), "Static ifThen action should not be executed");
        }

        @Test
        @DisplayName("static ifElse executes elsePassage when condition is false")
        void testStaticIfElse_FalseCondition() {
            AtomicBoolean elseExecuted = new AtomicBoolean(false);
            Runnable elseAction = () -> elseExecuted.set(true);
            Condition.ifElse(createCondition(false), elseAction);
            assertTrue(elseExecuted.get(), "Static ifElse action should be executed");
        }

        @Test
        @DisplayName("static ifElse does not execute elsePassage when condition is true")
        void testStaticIfElse_TrueCondition() {
            AtomicBoolean elseExecuted = new AtomicBoolean(false);
            Runnable elseAction = () -> elseExecuted.set(true);
            Condition.ifElse(createCondition(true), elseAction);
            assertFalse(elseExecuted.get(), "Static ifElse action should not be executed");
        }

        @Test
        @DisplayName("static ifThenElse executes thenPassage when condition is true")
        void testStaticIfThenElse_TrueCondition() {
            AtomicBoolean thenExecuted = new AtomicBoolean(false);
            AtomicBoolean elseExecuted = new AtomicBoolean(false);
            Condition.ifThenElse(createCondition(true), createAction(thenExecuted), createAction(elseExecuted));
            assertTrue(thenExecuted.get(), "Static ifThenElse then action should be executed");
            assertFalse(elseExecuted.get(), "Static ifThenElse else action should not be executed");
        }

        @Test
        @DisplayName("static ifThenElse executes elsePassage when condition is false")
        void testStaticIfThenElse_FalseCondition() {
            AtomicBoolean thenExecuted = new AtomicBoolean(false);
            AtomicBoolean elseExecuted = new AtomicBoolean(false);
            Condition.ifThenElse(createCondition(false), createAction(thenExecuted), createAction(elseExecuted));
            assertFalse(thenExecuted.get(), "Static ifThenElse then action should not be executed");
            assertTrue(elseExecuted.get(), "Static ifThenElse else action should be executed");
        }

        @Test
        @DisplayName("static and returns true if both conditions are true")
        void testStaticAnd_BothTrue() {
            assertTrue(Condition.and(createCondition(true), createCondition(true)), "Static and should return true");
        }

        @Test
        @DisplayName("static and returns false if first condition is false")
        void testStaticAnd_FirstFalse() {
            assertFalse(Condition.and(createCondition(false), createCondition(true)), "Static and should return false");
        }

        @Test
        @DisplayName("static and returns false if second condition is false")
        void testStaticAnd_SecondFalse() {
            assertFalse(Condition.and(createCondition(true), createCondition(false)), "Static and should return false");
        }

        @Test
        @DisplayName("static or returns true if first condition is true")
        void testStaticOr_FirstTrue() {
            assertTrue(Condition.or(createCondition(true), createCondition(false)), "Static or should return true");
        }

        @Test
        @DisplayName("static or returns true if second condition is true")
        void testStaticOr_SecondTrue() {
            assertTrue(Condition.or(createCondition(false), createCondition(true)), "Static or should return true");
        }

        @Test
        @DisplayName("static or returns false if both conditions are false")
        void testStaticOr_BothFalse() {
            assertFalse(Condition.or(createCondition(false), createCondition(false)), "Static or should return false");
        }
    }

    @Nested
    @DisplayName("Case Class Tests")
    class CaseTest {

        @Test
        @DisplayName("evaluate returns true if condition is true")
        void testEvaluate_True() {
            Condition.Case c = new Condition.Case(() -> true, () -> {});
            assertTrue(c.evaluate());
        }

        @Test
        @DisplayName("evaluate returns false if condition is false")
        void testEvaluate_False() {
            Condition.Case c = new Condition.Case(() -> false, () -> {});
            assertFalse(c.evaluate());
        }

        @Test
        @DisplayName("execute runs the associated action")
        void testExecute() {
            AtomicBoolean executed = new AtomicBoolean(false);
            Condition.Case c = new Condition.Case(() -> true, () -> executed.set(true));
            c.execute();
            assertTrue(executed.get());
        }
    }

    @Nested
    @DisplayName("Switch Class Tests")
    class SwitchTest {

        @Test
        @DisplayName("evaluate executes the first matching case")
        void testEvaluate_FirstMatch() {
            AtomicBoolean case1Executed = new AtomicBoolean(false);
            AtomicBoolean case2Executed = new AtomicBoolean(false);
            AtomicBoolean defaultExecuted = new AtomicBoolean(false);

            new Condition.Switch()
                .addCase(() -> false, () -> case1Executed.set(true))
                .addCase(() -> true, () -> case2Executed.set(true)) // This should execute
                .defaultCase(() -> defaultExecuted.set(true))
                .evaluate();

            assertFalse(case1Executed.get(), "Case 1 should not be executed");
            assertTrue(case2Executed.get(), "Case 2 should be executed");
            assertFalse(defaultExecuted.get(), "Default should not be executed");
        }

        @Test
        @DisplayName("evaluate executes default case if no case matches")
        void testEvaluate_NoMatch() {
            AtomicBoolean case1Executed = new AtomicBoolean(false);
            AtomicBoolean defaultExecuted = new AtomicBoolean(false);

            new Condition.Switch()
                .addCase(() -> false, () -> case1Executed.set(true))
                .defaultCase(() -> defaultExecuted.set(true))
                .evaluate();

            assertFalse(case1Executed.get(), "Case 1 should not be executed");
            assertTrue(defaultExecuted.get(), "Default should be executed");
        }

        @Test
        @DisplayName("evaluate executes no action if no case matches and no default is set")
        void testEvaluate_NoMatchNoDefault() {
            AtomicBoolean case1Executed = new AtomicBoolean(false);

            new Condition.Switch()
                .addCase(() -> false, () -> case1Executed.set(true))
                .evaluate();

            assertFalse(case1Executed.get(), "Case 1 should not be executed");
        }

        @Test
        @DisplayName("evaluate only executes the first matching case and stops")
        void testEvaluate_OnlyFirstMatch() {
            AtomicInteger executedCount = new AtomicInteger(0);
            AtomicBoolean firstCaseExecuted = new AtomicBoolean(false);
            AtomicBoolean secondCaseExecuted = new AtomicBoolean(false);

            new Condition.Switch()
                .addCase(() -> true, () -> { firstCaseExecuted.set(true); executedCount.incrementAndGet(); })
                .addCase(() -> true, () -> { secondCaseExecuted.set(true); executedCount.incrementAndGet(); }) // This should NOT execute
                .evaluate();

            assertTrue(firstCaseExecuted.get(), "First matching case should be executed");
            assertFalse(secondCaseExecuted.get(), "Subsequent matching cases should not be executed");
            assertEquals(1, executedCount.get(), "Only one case should be executed");
        }

        @Test
        @DisplayName("defaultCase can be added at any point")
        void testDefaultCasePlacement() {
            AtomicBoolean case1Executed = new AtomicBoolean(false);
            AtomicBoolean defaultExecuted = new AtomicBoolean(false);

            new Condition.Switch()
                .defaultCase(() -> defaultExecuted.set(true)) // defaultを先に設定
                .addCase(() -> false, () -> case1Executed.set(true))
                .evaluate();

            assertFalse(case1Executed.get());
            assertTrue(defaultExecuted.get());

            // Reset and try with default after case
            case1Executed.set(false);
            defaultExecuted.set(false);

            new Condition.Switch()
                .addCase(() -> false, () -> case1Executed.set(true))
                .defaultCase(() -> defaultExecuted.set(true))
                .evaluate();

            assertFalse(case1Executed.get());
            assertTrue(defaultExecuted.get());
        }
    }
}