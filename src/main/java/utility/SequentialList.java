package utility;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Java標準ライブラリのArrayListを拡張したオブジェクト（クラス）です。
 */
@SuppressWarnings("serial")
public class SequentialList<Element> extends ArrayList<Element> {

    /**
     * 要素をインデックス（添字）と共に列挙します。
     *
     * @param aBiConsumer 要素とインデックスを消費するモノ
     */
    public void forEach(BiConsumer<Element, Integer> aBiConsumer) {
        this.forEachWithIndex(aBiConsumer);
        return;
    }

    /**
     * 要素をインデックス（添字）と共に列挙します。
     *
     * @param aBiConsumer 要素とインデックスを消費するモノ
     * @throws NullPointerException {@inheritDoc}
     */
    public void forEachWithIndex(BiConsumer<Element, Integer> aBiConsumer) {
        Objects.requireNonNull(aBiConsumer);
        AtomicInteger anIndex = new AtomicInteger(0);
        Consumer<Element> aConsumer = (Element anElement)
                -> {
            aBiConsumer.accept(anElement, anIndex.getAndIncrement());
        };
        this.forEach(aConsumer);
        return;
    }
}
