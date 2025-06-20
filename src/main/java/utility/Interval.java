package utility;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * 形式型<T>を使用して、指定された開始値から条件に基づいて繰り返し処理を行うためのクラス。
 * 各クラスで次の値を計算、アクションを行う。
 */
public class Interval<T>
{
    private T startValue;
    private Predicate<T> loopCondition;
    private UnaryOperator<T> nextValueFunction;

    /**
     * コンストラクタ。
     */
    public Interval()
    {
        return;
    }

    /**
     * コンストラクタ。
     * @param startValue ループ処理の開始値
     * @param loopCondition ループを継続する条件
     * @param nextValueFunction 次の値を計算するための関数
     */
    public Interval(T startValue, Predicate<T> loopCondition, UnaryOperator<T> nextValueFunction) {
        this.startValue = startValue;
        this.loopCondition = loopCondition;
        this.nextValueFunction = nextValueFunction;
    }

    /**
     * 各ステップで指定されたアクションを行う。
     * @param action 各ステップのアクション
     */
    public void forEach(Consumer<T> action) {
        T currentValue = startValue;
        // loopCondition が true である限り処理をループする
        while (loopCondition.test(currentValue)) {
            action.accept(currentValue);
            currentValue = nextValueFunction.apply(currentValue);
        }
    }
    
}