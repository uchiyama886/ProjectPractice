// package wavelet;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.mockito.InOrder;
// import org.mockito.Mockito;

// import java.awt.Component;
// import java.awt.event.ActionEvent;
// import java.awt.event.MouseEvent;
// import java.awt.Point;
// import java.lang.reflect.Field;
// import mvc.View; 
// import pane.PaneView;
// import pane.PaneModel;
// import java.awt.image.BufferedImage;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;

// @DisplayName("WaveletPaneController Class Unit Tests")
// class WaveletPaneControllerTest {

//     private WaveletPaneController controller; // テスト対象のコントローラ (spy化される)
//     private WaveletPaneModel mockWaveletPaneModel; // モデルのモック
//     private PaneView mockPaneView; // ビューのモック (PaneView型)
//     private PaneModel mockPaneModel; // PaneView.getModel()が返すPaneModelのモック
//     private BufferedImage mockBufferedImage; // PaneModel.picture()が返すBufferedImageのモック
//     private Component mockComponent; // MouseEventのソースコンポーネントのモック
//     private WaveletModel mockWaveletModelListener; // WaveletPaneModelのlistener用モック

//     @BeforeEach
//     void setUp() throws NoSuchFieldException, IllegalAccessException { // throws宣言を修正
//         WaveletPaneController realController = new WaveletPaneController();
//         controller = Mockito.spy(realController); // realControllerをスパイ

//         mockWaveletPaneModel = Mockito.mock(WaveletPaneModel.class);
//         mockPaneView = Mockito.mock(PaneView.class); // mockPaneViewをPaneViewのモックに変更
//         mockPaneModel = Mockito.mock(PaneModel.class); // mockPaneModelを初期化
//         mockBufferedImage = Mockito.mock(BufferedImage.class); // mockBufferedImageを初期化
//         mockComponent = Mockito.mock(Component.class);
//         mockWaveletModelListener = Mockito.mock(WaveletModel.class); // WaveletPaneModelのlistener用モックを初期化

//         // PaneView.getModel()がmockPaneModelを返すようにスタブ
//         when(mockPaneView.getModel()).thenReturn(mockPaneModel);
//         // PaneModel.picture()がmockBufferedImageを返すようにスタブ
//         when(mockPaneModel.picture()).thenReturn(mockBufferedImage);
//         // PaneView.convertViewPointToPicturePoint もスタブしておく (PaneControllerが利用するため)
//         when(mockPaneView.convertViewPointToPicturePoint(any(Point.class))).thenReturn(new Point(1, 1));

//         // controller (spy) の model と view フィールドを設定
//         Field modelField = mvc.Controller.class.getDeclaredField("model");
//         modelField.setAccessible(true);
//         modelField.set(controller, mockWaveletPaneModel);

//         Field viewField = mvc.Controller.class.getDeclaredField("view");
//         viewField.setAccessible(true);
//         viewField.set(controller, mockPaneView);

//         // mockWaveletPaneModel の listener フィールドを初期化
//         // WaveletPaneModelのlistenerフィールドにアクセスしてmockWaveletModelListenerを設定
//         Field listenerField = WaveletPaneModel.class.getDeclaredField("listener");
//         listenerField.setAccessible(true);
//         listenerField.set(mockWaveletPaneModel, mockWaveletModelListener);
        
//         // mockWaveletPaneModel の isNotInteractive() が false を返すようにスタブ
//         // これにより、WaveletPaneModelのmouseClicked/Draggedがlistenerに委譲するパスを通過する
//         when(mockWaveletPaneModel.isNotInteractive()).thenReturn(false);

//         // mockWaveletPaneModel が持つメソッドをモック
//         doNothing().when(mockWaveletPaneModel).actionPerformed(any(ActionEvent.class));
//         doNothing().when(mockWaveletPaneModel).mouseClicked(any(Point.class), any(MouseEvent.class));
//         doNothing().when(mockWaveletPaneModel).mouseDragged(any(Point.class), any(MouseEvent.class));
//         doNothing().when(mockWaveletPaneModel).showPopupMenu(any(MouseEvent.class), any(WaveletPaneController.class));
        
//         // mockWaveletModelListener のメソッドをモック (WaveletPaneModelがイベントを委譲するため)
//         doNothing().when(mockWaveletModelListener).mouseClicked(any(Point.class), any(MouseEvent.class));
//         doNothing().when(mockWaveletModelListener).mouseDragged(any(Point.class), any(MouseEvent.class));
//         doNothing().when(mockWaveletModelListener).showPopupMenu(any(MouseEvent.class), any(WaveletPaneController.class));
//     }

//     // isMenuPopuping フィールドにアクセスするためのヘルパーメソッド
//     private boolean getIsMenuPopuping(WaveletPaneController controller) throws NoSuchFieldException, IllegalAccessException {
//         Field field = WaveletPaneController.class.getDeclaredField("isMenuPopuping");
//         field.setAccessible(true);
//         return (boolean) field.get(controller);
//     }

//     private void setIsMenuPopuping(WaveletPaneController controller, boolean value) throws NoSuchFieldException, IllegalAccessException {
//         Field field = WaveletPaneController.class.getDeclaredField("isMenuPopuping");
//         field.setAccessible(true);
//         field.set(controller, value);
//     }

//     // MouseEvent をモックするヘルパーメソッド
//     private MouseEvent createMockMouseEvent(Component source, int id, long when, int modifiers, int x, int y, int clickCount, boolean popupTrigger, int button) {
//         MouseEvent mockEvent = mock(MouseEvent.class);
//         when(mockEvent.getSource()).thenReturn(source);
//         when(mockEvent.getID()).thenReturn(id);
//         when(mockEvent.getWhen()).thenReturn(when);
//         when(mockEvent.getModifiers()).thenReturn(modifiers); // Deprecatedだが、テスト対象メソッドが使用するなら必要
//         when(mockEvent.getX()).thenReturn(x);
//         when(mockEvent.getY()).thenReturn(y);
//         when(mockEvent.getClickCount()).thenReturn(clickCount);
//         when(mockEvent.isPopupTrigger()).thenReturn(popupTrigger);
//         when(mockEvent.getButton()).thenReturn(button);
//         when(mockEvent.getComponent()).thenReturn(source);
//         return mockEvent;
//     }

//     @Test
//     @DisplayName("Constructor initializes without throwing exceptions")
//     void testConstructor() {
//         assertDoesNotThrow(() -> new WaveletPaneController(), "Constructor should not throw exceptions.");
//     }

//     @Test
//     @DisplayName("actionPerformed() delegates to WaveletPaneModel's actionPerformed()")
//     void testActionPerformed() throws NoSuchFieldException, IllegalAccessException {
//         ActionEvent mockActionEvent = mock(ActionEvent.class);
//         when(mockActionEvent.getActionCommand()).thenReturn("testCommand");

//         controller.actionPerformed(mockActionEvent);

//         verify(mockWaveletPaneModel, times(1)).actionPerformed(mockActionEvent);
//     }

//     @Test
//     @DisplayName("mouseClicked() delegates to super.mouseClicked() if not menu popuping")
//     void testMouseClicked_notPopuping() throws NoSuchFieldException, IllegalAccessException {
//         setIsMenuPopuping(controller, false);
        
//         MouseEvent mockMouseEvent = createMockMouseEvent(mockComponent, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, 10, 10, 1, false, MouseEvent.BUTTON1);

//         controller.mouseClicked(mockMouseEvent); // controllerはsetUpでスパイ済み

//         verify(mockWaveletPaneModel, times(1)).mouseClicked(any(Point.class), eq(mockMouseEvent));
//         // WaveletPaneModelがイベントをWaveletModelListenerに委譲することも検証
//         verify(mockWaveletModelListener, times(1)).mouseClicked(any(Point.class), eq(mockMouseEvent));
//     }

//     @Test
//     @DisplayName("mouseClicked() ignores event if menu popuping")
//     void testMouseClicked_popuping() throws NoSuchFieldException, IllegalAccessException {
//         setIsMenuPopuping(controller, true);

//         MouseEvent mockMouseEvent = createMockMouseEvent(mockComponent, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, 10, 10, 1, false, MouseEvent.BUTTON1);

//         controller.mouseClicked(mockMouseEvent);

//         verify(mockWaveletPaneModel, never()).mouseClicked(any(Point.class), any(MouseEvent.class));
//         verify(mockWaveletModelListener, never()).mouseClicked(any(Point.class), any(MouseEvent.class)); // Listenerも呼ばれないことを確認

//         assertFalse(getIsMenuPopuping(controller), "isMenuPopuping should be reset to false after click.");
//     }

//     @Test
//     @DisplayName("mouseDragged() delegates to super.mouseDragged() if not menu popuping")
//     void testMouseDragged_notPopuping() throws NoSuchFieldException, IllegalAccessException {
//         setIsMenuPopuping(controller, false);

//         MouseEvent mockMouseEvent = createMockMouseEvent(mockComponent, MouseEvent.MOUSE_DRAGGED, System.currentTimeMillis(), 0, 10, 10, 0, false, 0);
        
//         controller.mouseDragged(mockMouseEvent); // controllerはsetUpでスパイ済み
        
//         verify(mockWaveletPaneModel, times(1)).mouseDragged(any(Point.class), eq(mockMouseEvent));
//         // WaveletPaneModelがイベントをWaveletModelListenerに委譲することも検証
//         verify(mockWaveletModelListener, times(1)).mouseDragged(any(Point.class), eq(mockMouseEvent));
//     }

//     @Test
//     @DisplayName("mouseDragged() ignores event if menu popuping")
//     void testMouseDragged_popuping() throws NoSuchFieldException, IllegalAccessException {
//         setIsMenuPopuping(controller, true);

//         MouseEvent mockMouseEvent = createMockMouseEvent(mockComponent, MouseEvent.MOUSE_DRAGGED, System.currentTimeMillis(), 0, 10, 10, 0, false, 0);
//         controller.mouseDragged(mockMouseEvent);

//         verify(mockWaveletPaneModel, never()).mouseDragged(any(Point.class), any(MouseEvent.class));
//         verify(mockWaveletModelListener, never()).mouseDragged(any(Point.class), any(MouseEvent.class)); // Listenerも呼ばれないことを確認
//     }

//     @Test
//     @DisplayName("mouseMoved() performs no explicit action")
//     void testMouseMoved() {
//         MouseEvent mockMouseEvent = createMockMouseEvent(mockComponent, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, 10, 10, 0, false, 0);
//         assertDoesNotThrow(() -> controller.mouseMoved(mockMouseEvent), "mouseMoved() should not throw exceptions.");
//     }

//     @Test
//     @DisplayName("mousePressed() calls super.mousePressed() and handles popup trigger")
//     void testMousePressed() throws NoSuchFieldException, IllegalAccessException {
//         MouseEvent mockPopupTriggerEvent = createMockMouseEvent(mockComponent, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, 10, 10, 1, true, MouseEvent.BUTTON3);
//         MouseEvent mockNormalPressEvent = createMockMouseEvent(mockComponent, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, 10, 10, 1, false, MouseEvent.BUTTON1);

//         // ----- ポップアップトリガーの場合 -----
//         controller.mousePressed(mockPopupTriggerEvent); // controllerはsetUpでスパイ済み

//         verify(controller, times(1)).showPopupMenu(mockPopupTriggerEvent);
        
//         assertTrue(getIsMenuPopuping(controller), "isMenuPopuping should be true after popup trigger.");

//         // ----- 通常のプレスの場合は showPopupMenu が呼ばれないこと -----
//         Mockito.reset(controller); // controllerの呼び出し履歴をリセット
//         // setUp() でフィールド設定は行われているため、再設定は不要
        
//         controller.mousePressed(mockNormalPressEvent);

//         verify(controller, never()).showPopupMenu(any(MouseEvent.class));

//         assertFalse(getIsMenuPopuping(controller), "isMenuPopuping should be false after normal press.");
//     }

//     @Test
//     @DisplayName("mouseReleased() calls super.mouseReleased() and handles popup trigger")
//     void testMouseReleased() throws NoSuchFieldException, IllegalAccessException {
//         MouseEvent mockPopupTriggerEvent = createMockMouseEvent(mockComponent, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0, 10, 10, 1, true, MouseEvent.BUTTON3);
//         MouseEvent mockNormalReleaseEvent = createMockMouseEvent(mockComponent, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0, 10, 10, 1, false, MouseEvent.BUTTON1);

//         // ----- ポップアップトリガーの場合 -----
//         controller.mouseReleased(mockPopupTriggerEvent); // controllerはsetUpでスパイ済み

//         verify(controller, times(1)).showPopupMenu(mockPopupTriggerEvent);

//         // ----- 通常のリリースの場合は showPopupMenu が呼ばれないこと -----
//         Mockito.reset(controller); // controllerの呼び出し履歴をリセット
//         // setUp() でフィールド設定は行われているため、再設定は不要
        
//         controller.mouseReleased(mockNormalReleaseEvent);

//         verify(controller, never()).showPopupMenu(any(MouseEvent.class));
//     }

//     @Test
//     @DisplayName("showPopupMenu() delegates to WaveletPaneModel's showPopupMenu() and sets isMenuPopuping flag")
//     void testShowPopupMenu() throws NoSuchFieldException, IllegalAccessException {
//         MouseEvent mockMouseEvent = createMockMouseEvent(mockComponent, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, 10, 10, 1, true, MouseEvent.BUTTON3);

//         controller.showPopupMenu(mockMouseEvent);

//         verify(mockWaveletPaneModel, times(1)).showPopupMenu(eq(mockMouseEvent), eq(controller));
        
//         assertTrue(getIsMenuPopuping(controller), "isMenuPopuping should be true after showPopupMenu.");
//     }
// }