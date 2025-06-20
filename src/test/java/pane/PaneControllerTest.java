package pane;

import mvc.Controller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.Point;
import java.awt.event.MouseEvent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("PaneController Class Unit Tests")
class PaneControllerTest extends Controller {

    private PaneController paneController;
    private PaneModel mockPaneModel;
    private PaneView mockPaneView;
    private MouseEvent mockMouseEvent;

    @BeforeEach
    void setUp() {
        paneController = new PaneController();
        mockPaneModel = Mockito.mock(PaneModel.class);
        mockPaneView = Mockito.mock(PaneView.class);
        mockMouseEvent = Mockito.mock(MouseEvent.class);

        // PaneControllerのsetModelとsetViewを呼び出すことで、依存関係を設定
        paneController.setModel(mockPaneModel);
        paneController.setView(mockPaneView);

        // mockPaneView.getModel()がmockPaneModelを返すようにスタブ
        when(mockPaneView.getModel()).thenReturn(mockPaneModel);
        
        // mockMouseEvent.getPoint()が特定のPointを返すようにスタブ
        when(mockMouseEvent.getPoint()).thenReturn(new Point(100, 50));
        
        // convertViewPointToPicturePointのデフォルトの振る舞いを設定
        when(mockPaneView.convertViewPointToPicturePoint(any(Point.class)))
            .thenReturn(new Point(10, 20));
    }

    @Test
    @DisplayName("getView() returns the associated PaneView")
    void testGetView() {
        assertEquals(mockPaneView, paneController.getView(), "getView() should return the associated PaneView");
    }

    @Test
    @DisplayName("mouseClicked() calls convertViewPointToPicturePoint and then mouseClicked on model")
    void testMouseClicked() {
        paneController.mouseClicked(mockMouseEvent);

        verify(mockPaneView, times(1)).convertViewPointToPicturePoint(new Point(100, 50));
        verify(mockPaneModel, times(1)).mouseClicked(new Point(10, 20), mockMouseEvent);
    }

    @Test
    @DisplayName("mouseClicked() does nothing if convertViewPointToPicturePoint returns null")
    void testMouseClickedNullPoint() {
        when(mockPaneView.convertViewPointToPicturePoint(any(Point.class))).thenReturn(null);

        paneController.mouseClicked(mockMouseEvent);

        verify(mockPaneView, times(1)).convertViewPointToPicturePoint(new Point(100, 50));
        verify(mockPaneModel, never()).mouseClicked(any(Point.class), any(MouseEvent.class));
    }

    @Test
    @DisplayName("mouseDragged() calls convertViewPointToPicturePoint and then mouseDragged on model")
    void testMouseDragged() {
        paneController.mouseDragged(mockMouseEvent);

        verify(mockPaneView, times(1)).convertViewPointToPicturePoint(new Point(100, 50));
        verify(mockPaneModel, times(1)).mouseDragged(new Point(10, 20), mockMouseEvent);
    }

    @Test
    @DisplayName("mouseDragged() does nothing if convertViewPointToPicturePoint returns null")
    void testMouseDraggedNullPoint() {
        when(mockPaneView.convertViewPointToPicturePoint(any(Point.class))).thenReturn(null);

        paneController.mouseDragged(mockMouseEvent);

        verify(mockPaneView, times(1)).convertViewPointToPicturePoint(new Point(100, 50));
        verify(mockPaneModel, never()).mouseDragged(any(Point.class), any(MouseEvent.class));
    }
}