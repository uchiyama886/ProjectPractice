package mvc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.Point;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.Color; // Colorクラスを使用するため

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("View Class Unit Tests")
class ViewTest {

    private Model mockModel;
    private Controller mockController;
    private View view; // 実際のViewインスタンスを保持
    private View spyView; // repaintの検証用にspyViewを導入

    @BeforeEach
    void setUp() {
        mockModel = Mockito.mock(Model.class);
        mockController = Mockito.mock(Controller.class);

        doNothing().when(mockModel).addDependent(any(View.class));
        doNothing().when(mockController).setModel(any(Model.class));
        doNothing().when(mockController).setView(any(View.class));

        view = new View(mockModel, mockController);
        spyView = Mockito.spy(view);
        
        // spyViewのgetWidth/getHeightをスタブ (paintComponentが利用するため)
        doReturn(800).when(spyView).getWidth();
        doReturn(600).when(spyView).getHeight();
        
        // spyViewのrepaint()が呼ばれても実際の描画は行わないようにスタブ
        doNothing().when(spyView).repaint(anyInt(), anyInt(), anyInt(), anyInt());
        
        // ControllerにspyViewを設定し直す (重要: setUpでviewが生成された後でspyViewに置き換えているため)
        spyView.controller.setView(spyView);
    }

    @Test
    @DisplayName("Constructor with Model initializes correctly and creates Controller")
    void testConstructorWithModel() {
        Model anotherMockModel = Mockito.mock(Model.class);
        doNothing().when(anotherMockModel).addDependent(any(View.class));
        
        View newView = new View(anotherMockModel);
        
        assertNotNull(newView.model, "Model should be set");
        assertEquals(anotherMockModel, newView.model, "Model should be the provided mockModel");
        assertNotNull(newView.controller, "Controller should be initialized internally"); 
        // newView.controllerは新しいインスタンスなので、mockControllerとは異なる
        assertNotEquals(mockController, newView.controller, "Controller should be a new instance, not the mock used in other tests");
        assertEquals(new Point(0, 0), newView.scrollAmount(), "Initial scroll amount should be (0, 0)");
        verify(anotherMockModel, times(1)).addDependent(newView); 
    }

    @Test
    @DisplayName("Constructor with Model and Controller initializes correctly")
    void testConstructorWithModelAndController() {
        assertNotNull(view.model, "Model should be set");
        assertEquals(mockModel, view.model, "Model should be the provided mockModel");
        assertNotNull(view.controller, "Controller should be set");
        assertEquals(mockController, view.controller, "Controller should be the provided mockController");
        assertEquals(new Point(0, 0), view.scrollAmount(), "Initial scroll amount should be (0, 0)");
        
        // setUpでaddDependent, setModel, setViewが呼ばれているので、少なくとも1回呼ばれたことを確認
        verify(mockModel, atLeastOnce()).addDependent(view); 
        verify(mockController, atLeastOnce()).setModel(mockModel); 
        verify(mockController, atLeastOnce()).setView(view);     
    }

    @Test
    @DisplayName("scrollAmount() returns the inverse of internal offset")
    void testScrollAmount() {
        spyView.scrollTo(new Point(-10, -20));
        assertEquals(new Point(10, 20), spyView.scrollAmount(), "scrollAmount should be inverse of offset");

        spyView.scrollTo(new Point(50, 30));
        assertEquals(new Point(-50, -30), spyView.scrollAmount(), "scrollAmount should be inverse of offset for positive values");
    }

    @Test
    @DisplayName("scrollBy() adjusts offset relative to current offset")
    void testScrollBy() {
        spyView.scrollBy(new Point(10, 20));
        assertEquals(new Point(-10, -20), spyView.scrollAmount(), "Offset should be updated by relative amount");

        spyView.scrollBy(new Point(-5, 10));
        assertEquals(new Point(-5, -30), spyView.scrollAmount(), "Offset should be further updated by relative amount");
    }

    @Test
    @DisplayName("scrollTo() sets offset to the absolute given point")
    void testScrollTo() {
        spyView.scrollTo(new Point(100, 200));
        assertEquals(new Point(-100, -200), spyView.scrollAmount(), "Offset should be set to the absolute point");

        spyView.scrollTo(new Point(-50, -70));
        assertEquals(new Point(50, 70), spyView.scrollAmount(), "Offset should be set to the absolute negative point");
    }

    @Test
    @DisplayName("update() calls repaint on the full view area")
    void testUpdate() {
        spyView.update();
        verify(spyView, times(1)).repaint(0, 0, 800, 600);
    }

    @Test
    @DisplayName("paintComponent handles null model gracefully (background painted, no image drawn)")
    void testPaintComponentNullModel() {
        Graphics mockGraphics = Mockito.mock(Graphics.class);
        spyView.model = null; // テストのためにModelをnullにする

        assertDoesNotThrow(() -> spyView.paintComponent(mockGraphics));
        // Modelがnullの場合でもfillRectは呼ばれるため、times(1)で検証
        verify(mockGraphics, times(1)).fillRect(0, 0, 800, 600); 
        verify(mockGraphics, never()).drawImage(any(BufferedImage.class), anyInt(), anyInt(), any()); 
    }

    @Test
    @DisplayName("paintComponent handles null image in model gracefully (background painted, no image drawn)")
    void testPaintComponentNullImage() {
        Graphics mockGraphics = Mockito.mock(Graphics.class);
        when(mockModel.picture()).thenReturn(null); // Modelがnull画像を返すように設定

        assertDoesNotThrow(() -> spyView.paintComponent(mockGraphics));
        verify(mockGraphics, times(1)).fillRect(0, 0, 800, 600); 
        verify(mockGraphics, never()).drawImage(any(BufferedImage.class), anyInt(), anyInt(), any()); 
    }

    @Test
    @DisplayName("paintComponent draws image with offset when model and image exist")
    void testPaintComponentDrawImage() {
        Graphics mockGraphics = Mockito.mock(Graphics.class);
        BufferedImage dummyImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        when(mockModel.picture()).thenReturn(dummyImage); // Modelが画像を返すように設定

        spyView.scrollTo(new Point(50, 30)); // offsetは(50, 30)

        spyView.paintComponent(mockGraphics);

        verify(mockGraphics, times(1)).fillRect(0, 0, 800, 600);
        verify(mockGraphics, times(1)).drawImage(dummyImage, 50, 30, null); // offsetを考慮して描画
    }

    @Test
    @DisplayName("toString() returns a correct string representation")
    void testToString() {
        spyView.scrollTo(new Point(10, 20));
        when(mockModel.toString()).thenReturn("MockModelToString");
        
        String actualString = spyView.toString();
        assertTrue(actualString.contains("model=MockModelToString"), "toString() should contain model info");
        assertTrue(actualString.contains("offset=java.awt.Point[x=10,y=20]"), "toString() should contain offset info");
        assertTrue(actualString.startsWith("mvc.View["), "toString() should start with class name");
        assertTrue(actualString.endsWith("]"), "toString() should end correctly");
    }
}