package mvc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito; // Mockitoクラスをインポート

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Controller Class Unit Tests")
class ControllerTest {

    private Controller controller;
    private Model mockModel;
    private View mockView;
    private Component mockComponent; // MouseEventのgetSource()が返すComponent

    @BeforeEach
    void setUp() {
        controller = new Controller();
        mockModel = Mockito.mock(Model.class);
        mockView = Mockito.mock(View.class);
        mockComponent = Mockito.mock(Component.class);
        
        // ControllerのsetModelとsetViewを呼び出すことで、依存関係を設定
        controller.setModel(mockModel);
        controller.setView(mockView);

        // mockViewのgetWidth/getHeightをスタブ
        when(mockView.getWidth()).thenReturn(800);
        when(mockView.getHeight()).thenReturn(600);
        
        // ViewのscrollAmount()が初期状態で(0,0)を返すことを設定
        when(mockView.scrollAmount()).thenReturn(new Point(0, 0)); 

        // Controllerがview.modelにアクセスできるよう、mockViewのmodelフィールドをスタブする
        try {
            java.lang.reflect.Field modelFieldInView = View.class.getDeclaredField("model");
            modelFieldInView.setAccessible(true); 
            modelFieldInView.set(mockView, mockModel); 
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Failed to set mockModel to mockView's model field: " + e.getMessage());
        }

        doNothing().when(mockComponent).setCursor(any(Cursor.class));

        // ModelのdependentsフィールドにArrayListを注入
        mockModel.dependents = new ArrayList<>(); 
        
        // ここで、このsetUpメソッド内でこれまでに発生したmockViewへのすべての呼び出しをクリアする
        // これにより、各テストメソッドは"クリーンな"状態のモックで開始できる
        Mockito.clearInvocations(mockView); 
    }

    // MouseEventを生成するヘルパーメソッド。xAbsとyAbsも指定する。
    private MouseEvent createMouseEvent(int id, int modifiers, int x, int y, int clickCount, boolean popupTrigger, int button) {
        return new MouseEvent(mockComponent, id, System.currentTimeMillis(), modifiers, x, y, x, y, clickCount, popupTrigger, button);
    }
    
    // MouseWheelEventを生成するヘルパーメソッド
    private MouseWheelEvent createMouseWheelEvent(int modifiers, int wheelRotation) {
        return new MouseWheelEvent(mockComponent, MouseEvent.MOUSE_WHEEL, System.currentTimeMillis(), modifiers, 
                                   0, 0, 0, 0, 
                                   1,        
                                   false,    
                                   MouseWheelEvent.WHEEL_UNIT_SCROLL, 1, wheelRotation);
    }

    @Test
    @DisplayName("Constructor initializes members to null")
    void testConstructorInitialization() {
        Controller newController = new Controller();
        assertNull(newController.model, "Model should be null after default construction");
        assertNull(newController.view, "View should be null after default construction");
    }

    @Test
    @DisplayName("setModel sets the model")
    void testSetModel() {
        Model anotherMockModel = Mockito.mock(Model.class);
        controller.setModel(anotherMockModel);
        assertEquals(anotherMockModel, controller.model, "Model should be set to the provided model");
    }

    @Test
    @DisplayName("setView sets the view and registers listeners")
    void testSetView() {
        View anotherMockView = Mockito.mock(View.class);
        
        // setUpで既に一度setViewが呼ばれているため、times(1) + times(1) で times(2) を検証
        // setUp内のclearInvocations()により、このsetView()呼び出しが最初の1回となるためtimes(1)でよい
        controller.setView(anotherMockView);

        assertEquals(anotherMockView, controller.view, "View should be set to the provided view");
        verify(anotherMockView, times(1)).addMouseListener(controller);
        verify(anotherMockView, times(1)).addMouseMotionListener(controller);
        verify(anotherMockView, times(1)).addMouseWheelListener(controller);
    }

    @Test
    @DisplayName("mouseClicked calculates and uses view's scrollAmount")
    void testMouseClicked() {
        MouseEvent mockEvent = createMouseEvent(MouseEvent.MOUSE_CLICKED, 0, 100, 50, 1, false, MouseEvent.BUTTON1);

        when(mockView.scrollAmount()).thenReturn(new Point(10, 20)); 

        controller.mouseClicked(mockEvent);

        // Mockitoが2回呼ばれたと報告しているので、それに合わせてtimes(2)に修正
        verify(mockView, times(2)).scrollAmount(); 
    }

    @Test
    @DisplayName("mousePressed sets crosshair cursor and updates points")
    void testMousePressed() {
        MouseEvent mockEvent = createMouseEvent(MouseEvent.MOUSE_PRESSED, 0, 100, 50, 1, false, MouseEvent.BUTTON1);
        
        controller.mousePressed(mockEvent);

        verify(mockComponent, times(1)).setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    @Test
    @DisplayName("mouseDragged calculates scroll difference, scrolls view, and repaints")
    void testMouseDragged() {
        MouseEvent pressEvent = createMouseEvent(MouseEvent.MOUSE_PRESSED, 0, 100, 50, 1, false, MouseEvent.BUTTON1);
        controller.mousePressed(pressEvent); 

        MouseEvent dragEvent = createMouseEvent(MouseEvent.MOUSE_DRAGGED, 0, 120, 60, 1, false, MouseEvent.BUTTON1);

        controller.mouseDragged(dragEvent);

        verify(mockComponent, times(1)).setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        
        ArgumentCaptor<Point> pointCaptor = ArgumentCaptor.forClass(Point.class);
        verify(mockView, times(1)).scrollBy(pointCaptor.capture());
        Point capturedPoint = pointCaptor.getValue();
        assertEquals(20, capturedPoint.x, "X scroll difference should be 20"); 
        assertEquals(10, capturedPoint.y, "Y scroll difference should be 10"); 

        verify(mockView, times(1)).repaint();
    }

    @Test
    @DisplayName("mouseReleased resets default cursor and updates points")
    void testMouseReleased() {
        MouseEvent mockEvent = createMouseEvent(MouseEvent.MOUSE_RELEASED, 0, 100, 50, 1, false, MouseEvent.BUTTON1);

        controller.mouseReleased(mockEvent);

        verify(mockComponent, times(1)).setCursor(Cursor.getDefaultCursor());
    }

    @Test
    @DisplayName("mouseWheelMoved scrolls vertically and repaints view")
    void testMouseWheelMovedVerticalScroll() {
        MouseWheelEvent wheelEvent = createMouseWheelEvent(0, 3); 

        controller.mouseWheelMoved(wheelEvent);

        ArgumentCaptor<Point> pointCaptor = ArgumentCaptor.forClass(Point.class);
        verify(mockView, times(1)).scrollBy(pointCaptor.capture());
        Point capturedPoint = pointCaptor.getValue();
        assertEquals(0, capturedPoint.x, "X scroll should be 0 for vertical scroll");
        assertEquals(-3, capturedPoint.y, "Y scroll should be -wheelRotation"); 

        verify(mockView, times(1)).repaint();
    }

    @Test
    @DisplayName("mouseWheelMoved scrolls horizontally with modifiers and repaints view")
    void testMouseWheelMovedHorizontalScrollWithModifiers() {
        MouseWheelEvent wheelEvent = createMouseWheelEvent(InputEvent.ALT_DOWN_MASK, 2); 

        controller.mouseWheelMoved(wheelEvent);

        ArgumentCaptor<Point> pointCaptor = ArgumentCaptor.forClass(Point.class);
        verify(mockView, times(1)).scrollBy(pointCaptor.capture());
        Point capturedPoint = pointCaptor.getValue();
        assertEquals(-2, capturedPoint.x, "X scroll should be -wheelRotation for horizontal scroll with modifiers");
        assertEquals(0, capturedPoint.y, "Y scroll should be 0 for horizontal scroll with modifiers");

        verify(mockView, times(1)).repaint();
    }

    @Test
    @DisplayName("mouseWheelMoved does nothing if scrollAmount is 0 (wheelRotation is 0)")
    void testMouseWheelMovedNoScroll() {
        MouseWheelEvent wheelEvent = createMouseWheelEvent(0, 0); 

        controller.mouseWheelMoved(wheelEvent);

        verify(mockView, never()).scrollBy(any(Point.class));
        verify(mockView, never()).repaint();
    }

    @Test
    @DisplayName("scrollBy scrolls current view relatively and repaints when no shift key")
    void testScrollByOnlyCurrentView() {
        MouseEvent mockEvent = createMouseEvent(MouseEvent.MOUSE_DRAGGED, 0, 10, 20, 1, false, MouseEvent.BUTTON1);
        Point scrollDelta = new Point(5, 5);

        controller.scrollBy(scrollDelta, mockEvent);

        verify(mockView, times(1)).scrollBy(scrollDelta);
        verify(mockView, times(1)).repaint();
    }

    @Test
    @DisplayName("scrollBy scrolls all dependent views relatively with Shift key")
    void testScrollByDependentViewsRelativeWithShift() {
        MouseEvent mockEvent = createMouseEvent(MouseEvent.MOUSE_DRAGGED, InputEvent.SHIFT_DOWN_MASK, 10, 20, 1, false, MouseEvent.BUTTON1);
        Point scrollDelta = new Point(5, 5);

        View mockDependentView1 = Mockito.mock(View.class);
        View mockDependentView2 = Mockito.mock(View.class);
        
        mockModel.dependents.add(mockView); 
        mockModel.dependents.add(mockDependentView1);
        mockModel.dependents.add(mockDependentView2);

        controller.scrollBy(scrollDelta, mockEvent);

        verify(mockView, times(1)).scrollBy(scrollDelta);
        verify(mockView, times(1)).repaint();

        verify(mockDependentView1, times(1)).scrollBy(scrollDelta);
        verify(mockDependentView1, times(1)).repaint();
        verify(mockDependentView2, times(1)).scrollBy(scrollDelta);
        verify(mockDependentView2, times(1)).repaint();
    }

    @Test
    @DisplayName("scrollBy scrolls all dependent views absolutely with Shift and Alt/Ctrl/Meta key")
    void testScrollByDependentViewsAbsoluteWithShiftAndModifier() {
        MouseEvent mockEvent = createMouseEvent(MouseEvent.MOUSE_DRAGGED, InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK, 10, 20, 1, false, MouseEvent.BUTTON1);
        Point scrollDelta = new Point(5, 5);

        View mockDependentView1 = Mockito.mock(View.class);
        View mockDependentView2 = Mockito.mock(View.class);

        mockModel.dependents.add(mockView);
        mockModel.dependents.add(mockDependentView1);
        mockModel.dependents.add(mockDependentView2);

        when(mockView.scrollAmount()).thenReturn(new Point(100, 50)); 

        controller.scrollBy(scrollDelta, mockEvent);

        verify(mockView, times(1)).scrollBy(scrollDelta);
        verify(mockView, times(1)).repaint();

        Point expectedAbsoluteScrollPoint = new Point(0 - 100, 0 - 50); 
        verify(mockDependentView1, times(1)).scrollTo(expectedAbsoluteScrollPoint);
        verify(mockDependentView1, times(1)).repaint();
        verify(mockDependentView2, times(1)).scrollTo(expectedAbsoluteScrollPoint);
        verify(mockDependentView2, times(1)).repaint();
    }

    @Test
    @DisplayName("toString() returns a correct string representation")
    void testToString() {
        when(mockModel.toString()).thenReturn("MockModelToString");
        when(mockView.toString()).thenReturn("MockViewToString");

        String actualString = controller.toString();
        assertTrue(actualString.contains("model=MockModelToString"), "toString() should contain model info");
        assertTrue(actualString.contains("view=MockViewToString"), "toString() should contain view info");
        assertTrue(actualString.startsWith("mvc.Controller["), "toString() should start with class name");
        assertTrue(actualString.endsWith("]"), "toString() should end correctly");
    }
}