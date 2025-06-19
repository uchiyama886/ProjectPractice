package wavelet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.Point;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("WaveletModel Class Unit Tests")
class WaveletModelTest {

    private WaveletModel waveletModel;

    // WaveletModelは具象クラスなので、直接インスタンス化してテストできます。
    // 各テストの前に新しいインスタンスを作成します。
    @BeforeEach
    void setUp() {
        waveletModel = new WaveletModel();
    }

    @Test
    @DisplayName("Constructor creates an instance and inherits from mvc.Model")
    void testConstructorAndInheritance() {
        assertNotNull(waveletModel, "WaveletModel instance should not be null.");
        assertTrue(waveletModel instanceof mvc.Model, "WaveletModel should inherit from mvc.Model.");
    }

    @Test
    @DisplayName("accuracy constant has the correct value")
    void testAccuracyConstant() {
        assertEquals(1.0E-5d, WaveletModel.accuracy, "Accuracy constant should be 1.0E-5.");
    }

    @Test
    @DisplayName("actionPerformed() does not throw exceptions and performs no explicit action")
    void testActionPerformed() {
        ActionEvent mockEvent = mock(ActionEvent.class); // ダミーのActionEvent
        assertDoesNotThrow(() -> waveletModel.actionPerformed(mockEvent), "actionPerformed() should not throw exceptions.");
        // デフォルトでは何も実行しないため、副作用がないことを確認するmockitoのverifyなどは不要です。
    }

    @Test
    @DisplayName("computeFromPoint() does not throw exceptions and performs no explicit action")
    void testComputeFromPoint() {
        Point dummyPoint = new Point(0, 0);
        boolean isAltDown = false;
        assertDoesNotThrow(() -> waveletModel.computeFromPoint(dummyPoint, isAltDown), "computeFromPoint() should not throw exceptions.");
    }

    @Test
    @DisplayName("computeRecomposedCoefficients() does not throw exceptions and performs no explicit action")
    void testComputeRecomposedCoefficients() {
        assertDoesNotThrow(() -> waveletModel.computeRecomposedCoefficients(), "computeRecomposedCoefficients() should not throw exceptions.");
    }

    @Test
    @DisplayName("mouseClicked() does not throw exceptions and performs no explicit action")
    void testMouseClicked() {
        Point dummyPoint = new Point(0, 0);
        MouseEvent mockEvent = mock(MouseEvent.class);
        assertDoesNotThrow(() -> waveletModel.mouseClicked(dummyPoint, mockEvent), "mouseClicked() should not throw exceptions.");
    }

    @Test
    @DisplayName("mouseDragged() does not throw exceptions and performs no explicit action")
    void testMouseDragged() {
        Point dummyPoint = new Point(0, 0);
        MouseEvent mockEvent = mock(MouseEvent.class);
        assertDoesNotThrow(() -> waveletModel.mouseDragged(dummyPoint, mockEvent), "mouseDragged() should not throw exceptions.");
    }

    @Test
    @DisplayName("open() does not throw exceptions and performs no explicit action")
    void testOpen() {
        assertDoesNotThrow(() -> waveletModel.open(), "open() should not throw exceptions.");
    }

    @Test
    @DisplayName("showPopupMenu() does not throw exceptions and performs no explicit action")
    void testShowPopupMenu() {
        MouseEvent mockEvent = mock(MouseEvent.class);
        WaveletPaneController mockController = mock(WaveletPaneController.class); // ダミーのWaveletPaneController
        assertDoesNotThrow(() -> waveletModel.showPopupMenu(mockEvent, mockController), "showPopupMenu() should not throw exceptions.");
    }
}