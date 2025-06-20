package wavelet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import utility.Condition;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field; // Reflectionのために追加

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("WaveletPaneModel Class Unit Tests")
class WaveletPaneModelTest {

    private WaveletPaneModel waveletPaneModel;
    private WaveletModel mockListener; // WaveletPaneModel の listener をモック
    private BufferedImage mockImage; // コンストラクタで渡すダミー画像

    @BeforeEach
    void setUp() {
        mockListener = Mockito.mock(WaveletModel.class);
        mockImage = Mockito.mock(BufferedImage.class); // ダミー画像を作成
        
        // void メソッドが例外をスローするように設定するには doThrow().when() を使う
        doThrow(new RuntimeException("Listener should not be called in non-interactive mode."))
            .when(mockListener).actionPerformed(any(ActionEvent.class));
        doThrow(new RuntimeException("Listener should not be called in non-interactive mode."))
            .when(mockListener).mouseClicked(any(Point.class), any(MouseEvent.class));
        doThrow(new RuntimeException("Listener should not be called in non-interactive mode."))
            .when(mockListener).mouseDragged(any(Point.class), any(MouseEvent.class));
        doThrow(new RuntimeException("Listener should not be called in non-interactive mode."))
            .when(mockListener).showPopupMenu(any(MouseEvent.class), any(WaveletPaneController.class));
    }

    // private listener フィールドにアクセスするためのヘルパーメソッド
    private WaveletModel getListener(WaveletPaneModel model) {
        try {
            Field listenerField = WaveletPaneModel.class.getDeclaredField("listener");
            listenerField.setAccessible(true);
            return (WaveletModel) listenerField.get(model);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Failed to access listener field: " + e.getMessage());
            return null; // unreachable
        }
    }

    @Test
    @DisplayName("Default constructor initializes with null listener and no label")
    void testDefaultConstructor() {
        waveletPaneModel = new WaveletPaneModel();
        assertNull(waveletPaneModel.label(), "Label should be null after default construction.");
        assertTrue(waveletPaneModel.isNotInteractive(), "Should be non-interactive after default construction.");
        assertNull(waveletPaneModel.picture(), "Picture should be null after default construction.");
    }

    @Test
    @DisplayName("Constructor with String argument initializes label and null listener")
    void testConstructorWithString() {
        String testLabel = "Test Label";
        waveletPaneModel = new WaveletPaneModel(testLabel);
        assertEquals(testLabel, waveletPaneModel.label(), "Label should be set correctly.");
        assertTrue(waveletPaneModel.isNotInteractive(), "Should be non-interactive.");
        assertNull(waveletPaneModel.picture(), "Picture should be null.");
    }

    @Test
    @DisplayName("Constructor with BufferedImage and String initializes label and null listener")
    void testConstructorWithImageAndString() {
        String testLabel = "Image Label";
        waveletPaneModel = new WaveletPaneModel(mockImage, testLabel);
        assertEquals(testLabel, waveletPaneModel.label(), "Label should be set correctly.");
        assertSame(mockImage, waveletPaneModel.picture(), "Picture should be set correctly.");
        assertTrue(waveletPaneModel.isNotInteractive(), "Should be non-interactive.");
    }

    @Test
    @DisplayName("Constructor with BufferedImage, String, and WaveletModel initializes all fields")
    void testConstructorWithImageStringAndListener() {
        String testLabel = "Interactive Label";
        waveletPaneModel = new WaveletPaneModel(mockImage, testLabel, mockListener);
        assertEquals(testLabel, waveletPaneModel.label(), "Label should be set correctly.");
        assertSame(mockImage, waveletPaneModel.picture(), "Picture should be set correctly.");
        assertSame(mockListener, getListener(waveletPaneModel), "Listener should be set correctly.");
        assertTrue(waveletPaneModel.isInteractive(), "Should be interactive.");
    }

    @Test
    @DisplayName("actionPerformed() delegates to listener if interactive")
    void testActionPerformed_interactive() {
        waveletPaneModel = new WaveletPaneModel(mockImage, "label", mockListener);
        ActionEvent mockActionEvent = mock(ActionEvent.class);
        
        // リスナーのactionPerformedが呼ばれたら例外を投げないように設定し直す
        doNothing().when(mockListener).actionPerformed(any(ActionEvent.class));

        assertDoesNotThrow(() -> waveletPaneModel.actionPerformed(mockActionEvent), "Should delegate to listener without throwing exception.");
        verify(mockListener, times(1)).actionPerformed(mockActionEvent);
    }

    @Test
    @DisplayName("actionPerformed() does nothing if not interactive")
    void testActionPerformed_notInteractive() {
        waveletPaneModel = new WaveletPaneModel(); // デフォルトで非インタラクティブ
        ActionEvent mockActionEvent = mock(ActionEvent.class);

        // リスナーのactionPerformedが呼ばれないことを検証
        assertDoesNotThrow(() -> waveletPaneModel.actionPerformed(mockActionEvent), "Should not throw exception if not interactive.");
        verify(mockListener, never()).actionPerformed(any(ActionEvent.class));
    }

    @Test
    @DisplayName("isInteractive() returns true when listener is set")
    void testIsInteractive_true() {
        waveletPaneModel = new WaveletPaneModel(null, null, mockListener);
        assertTrue(waveletPaneModel.isInteractive(), "Should be interactive when listener is set.");
        assertFalse(waveletPaneModel.isNotInteractive(), "Should not be non-interactive when listener is set.");
    }

    @Test
    @DisplayName("isInteractive() returns false when listener is null")
    void testIsInteractive_false() {
        waveletPaneModel = new WaveletPaneModel(); // デフォルトで listener は null
        assertFalse(waveletPaneModel.isInteractive(), "Should not be interactive when listener is null.");
        assertTrue(waveletPaneModel.isNotInteractive(), "Should be non-interactive when listener is null.");
    }

    @Test
    @DisplayName("label() returns the stored label")
    void testLabel() {
        String testLabel = "My Label";
        waveletPaneModel = new WaveletPaneModel(testLabel);
        assertEquals(testLabel, waveletPaneModel.label(), "Should return the correct label.");
    }

    @Test
    @DisplayName("mouseClicked() delegates to listener if interactive")
    void testMouseClicked_interactive() {
        waveletPaneModel = new WaveletPaneModel(mockImage, "label", mockListener);
        Point dummyPoint = new Point(10, 20);
        MouseEvent mockMouseEvent = mock(MouseEvent.class);
        
        doNothing().when(mockListener).mouseClicked(any(Point.class), any(MouseEvent.class));

        assertDoesNotThrow(() -> waveletPaneModel.mouseClicked(dummyPoint, mockMouseEvent), "Should delegate to listener without throwing exception.");
        verify(mockListener, times(1)).mouseClicked(dummyPoint, mockMouseEvent);
    }

    @Test
    @DisplayName("mouseClicked() does nothing if not interactive")
    void testMouseClicked_notInteractive() {
        waveletPaneModel = new WaveletPaneModel(); // デフォルトで非インタラクティブ
        Point dummyPoint = new Point(10, 20);
        MouseEvent mockMouseEvent = mock(MouseEvent.class);

        assertDoesNotThrow(() -> waveletPaneModel.mouseClicked(dummyPoint, mockMouseEvent), "Should not throw exception if not interactive.");
        verify(mockListener, never()).mouseClicked(any(Point.class), any(MouseEvent.class));
    }

    @Test
    @DisplayName("mouseDragged() delegates to listener if interactive")
    void testMouseDragged_interactive() {
        waveletPaneModel = new WaveletPaneModel(mockImage, "label", mockListener);
        Point dummyPoint = new Point(30, 40);
        MouseEvent mockMouseEvent = mock(MouseEvent.class);

        doNothing().when(mockListener).mouseDragged(any(Point.class), any(MouseEvent.class));

        assertDoesNotThrow(() -> waveletPaneModel.mouseDragged(dummyPoint, mockMouseEvent), "Should delegate to listener without throwing exception.");
        verify(mockListener, times(1)).mouseDragged(dummyPoint, mockMouseEvent);
    }

    @Test
    @DisplayName("mouseDragged() does nothing if not interactive")
    void testMouseDragged_notInteractive() {
        waveletPaneModel = new WaveletPaneModel(); // デフォルトで非インタラクティブ
        Point dummyPoint = new Point(30, 40);
        MouseEvent mockMouseEvent = mock(MouseEvent.class);

        assertDoesNotThrow(() -> waveletPaneModel.mouseDragged(dummyPoint, mockMouseEvent), "Should not throw exception if not interactive.");
        verify(mockListener, never()).mouseDragged(any(Point.class), any(MouseEvent.class));
    }

    @Test
    @DisplayName("showPopupMenu() delegates to listener if interactive")
    void testShowPopupMenu_interactive() {
        waveletPaneModel = new WaveletPaneModel(mockImage, "label", mockListener);
        MouseEvent mockMouseEvent = mock(MouseEvent.class);
        WaveletPaneController mockController = mock(WaveletPaneController.class);

        doNothing().when(mockListener).showPopupMenu(any(MouseEvent.class), any(WaveletPaneController.class));

        assertDoesNotThrow(() -> waveletPaneModel.showPopupMenu(mockMouseEvent, mockController), "Should delegate to listener without throwing exception.");
        verify(mockListener, times(1)).showPopupMenu(mockMouseEvent, mockController);
    }

    @Test
    @DisplayName("showPopupMenu() does nothing if not interactive")
    void testShowPopupMenu_notInteractive() {
        waveletPaneModel = new WaveletPaneModel(); // デフォルトで非インタラクティブ
        MouseEvent mockMouseEvent = mock(MouseEvent.class);
        WaveletPaneController mockController = mock(WaveletPaneController.class);

        assertDoesNotThrow(() -> waveletPaneModel.showPopupMenu(mockMouseEvent, mockController), "Should not throw exception if not interactive.");
        verify(mockListener, never()).showPopupMenu(any(MouseEvent.class), any(WaveletPaneController.class));
    }
}