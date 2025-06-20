package pane;

import mvc.Controller;
import mvc.Model;
import mvc.View;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import utility.ImageUtility;
import utility.ValueHolder;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("PaneView Class Unit Tests")
class PaneViewTest {

    private PaneModel mockPaneModel;
    private PaneController mockPaneController;
    private PaneView paneView;
    private PaneView spyPaneView;

    @BeforeEach
    void setUp() {
        mockPaneModel = Mockito.mock(PaneModel.class);
        mockPaneController = Mockito.mock(PaneController.class);

        doNothing().when(mockPaneModel).addDependent(any(View.class));
        doNothing().when(mockPaneController).setModel(any(Model.class));
        doNothing().when(mockPaneController).setView(any(View.class));

        paneView = new PaneView(mockPaneModel, mockPaneController);
        spyPaneView = Mockito.spy(paneView);

        doReturn(mockPaneModel).when(spyPaneView).getModel();

        doReturn(800).when(spyPaneView).getWidth();
        doReturn(600).when(spyPaneView).getHeight();
        doNothing().when(spyPaneView).repaint();
    }

    // プライベートまたはプロテクテッドフィールドにアクセスするためのヘルパーメソッド
    private Field getAccessibleField(Object target, String fieldName) throws NoSuchFieldException {
        Class<?> currentClass = target.getClass();
        while (currentClass != null) {
            try {
                Field field = currentClass.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException e) {
                currentClass = currentClass.getSuperclass();
            }
        }
        throw new NoSuchFieldException("Field " + fieldName + " not found in class hierarchy.");
    }

    private void setPrivateField(Object target, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = getAccessibleField(target, fieldName);
        field.set(target, value);
    }

    private Object getPrivateField(Object target, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = getAccessibleField(target, fieldName);
        return field.get(target);
    }

    @Test
    @DisplayName("Constructor initializes originPoint and scaleFactor")
    void testConstructor() throws NoSuchFieldException, IllegalAccessException {
        PaneView newPaneView = new PaneView(mockPaneModel, mockPaneController);
        assertEquals(new Point2D.Double(0.0d, 0.0d), (Point2D.Double)getPrivateField(newPaneView, "originPoint"), "originPoint should be initialized to (0,0)");
        assertEquals(new Point2D.Double(1.0d, 1.0d), (Point2D.Double)getPrivateField(newPaneView, "scaleFactor"), "scaleFactor should be initialized to (1,1)");
    }

    @Test
    @DisplayName("initialize() resets originPoint and scaleFactor")
    void testInitialize() throws NoSuchFieldException, IllegalAccessException {
        setPrivateField(spyPaneView, "originPoint", new Point2D.Double(10.0d, 20.0d));
        setPrivateField(spyPaneView, "scaleFactor", new Point2D.Double(2.0d, 2.0d));

        spyPaneView.intialize();

        assertEquals(new Point2D.Double(0.0d, 0.0d), (Point2D.Double)getPrivateField(spyPaneView, "originPoint"), "originPoint should be reset to (0,0)");
        assertEquals(new Point2D.Double(1.0d, 1.0d), (Point2D.Double)getPrivateField(spyPaneView, "scaleFactor"), "scaleFactor should be reset to (1,1)");
    }

    @Test
    @DisplayName("getModel() returns the associated PaneModel")
    void testGetModel() {
        assertEquals(mockPaneModel, spyPaneView.getModel(), "getModel() should return the associated PaneModel");
    }

    @Test
    @DisplayName("convertViewPointToModelPoint() converts view coordinates to model coordinates")
    void testConvertViewPointToModelPoint() {
        Point viewPoint = new Point(100, 50);
        
        assertEquals(new Point(100, 50), spyPaneView.convertViewPointToModelPoint(viewPoint), "Converted model point should be view point + scroll amount");
    }

    @Test
    @DisplayName("convertViewPointToPicturePoint() converts view coordinates to picture coordinates")
    void testConvertViewPointToPicturePoint() {
        Point viewPoint = new Point(100, 50);
        Point expectedModelPoint = new Point(100, 50);
        Point expectedPicturePoint = new Point(10, 20);

        doReturn(expectedModelPoint).when(spyPaneView).convertViewPointToModelPoint(viewPoint);
        doReturn(expectedPicturePoint).when(spyPaneView).convertModelPointToPicturePoint(expectedModelPoint);

        Point actualPicturePoint = spyPaneView.convertViewPointToPicturePoint(viewPoint);
        assertEquals(expectedPicturePoint, actualPicturePoint, "convertViewPointToPicturePoint should return the correct picture point");
        verify(spyPaneView, times(1)).convertViewPointToModelPoint(viewPoint);
        verify(spyPaneView, times(1)).convertModelPointToPicturePoint(expectedModelPoint);
    }
    
    @Test
    @DisplayName("convertModelPointToPicturePoint() converts model coordinates to picture coordinates correctly")
    void testConvertModelPointToPicturePoint() throws NoSuchFieldException, IllegalAccessException {
        BufferedImage dummyImage = new BufferedImage(200, 150, BufferedImage.TYPE_INT_RGB);
        when(mockPaneModel.picture()).thenReturn(dummyImage);
        
        spyPaneView.intialize();
        setPrivateField(spyPaneView, "originPoint", new Point2D.Double(50.0, 30.0));
        setPrivateField(spyPaneView, "scaleFactor", new Point2D.Double(0.5, 0.5));

        Point modelPoint = new Point(60, 40);

        Point picturePoint = spyPaneView.convertModelPointToPicturePoint(modelPoint);

        assertNotNull(picturePoint, "Picture point should not be null");
        assertEquals(20, picturePoint.x, "X coordinate should be correctly converted");
        assertEquals(20, picturePoint.y, "Y coordinate should be correctly converted");
    }
    
    @Test
    @DisplayName("convertModelPointToPicturePoint() returns null if model's picture is null")
    void testConvertModelPointToPicturePointWithNullImage() {
        when(mockPaneModel.picture()).thenReturn(null);
        Point modelPoint = new Point(10, 10);
        assertNull(spyPaneView.convertModelPointToPicturePoint(modelPoint), "Should return null if picture is null");
    }

    @Test
    @DisplayName("convertModelPointToPicturePoint() returns null if converted coordinates are out of bounds (negative)")
    void testConvertModelPointToPicturePointWithOutOfRangeCoordinatesNegative() throws NoSuchFieldException, IllegalAccessException {
        BufferedImage dummyImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        when(mockPaneModel.picture()).thenReturn(dummyImage);
        spyPaneView.intialize();
        setPrivateField(spyPaneView, "originPoint", new Point2D.Double(50.0, 50.0));
        setPrivateField(spyPaneView, "scaleFactor", new Point2D.Double(1.0, 1.0));

        Point modelPoint = new Point(10, 10);
        assertNull(spyPaneView.convertModelPointToPicturePoint(modelPoint), "Should return null if x or y is negative");
    }

    @Test
    @DisplayName("convertModelPointToPicturePoint() returns null if converted coordinates are out of bounds (exceeds image dimensions)")
    void testConvertModelPointToPicturePointWithOutOfRangeCoordinatesPositive() throws NoSuchFieldException, IllegalAccessException {
        BufferedImage dummyImage = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
        when(mockPaneModel.picture()).thenReturn(dummyImage);
        spyPaneView.intialize();
        setPrivateField(spyPaneView, "originPoint", new Point2D.Double(0.0, 0.0));
        setPrivateField(spyPaneView, "scaleFactor", new Point2D.Double(1.0, 1.0));

        Point modelPoint = new Point(60, 60);
        assertNull(spyPaneView.convertModelPointToPicturePoint(modelPoint), "Should return null if x or y exceeds image dimensions");
    }
    
    @Test
    @DisplayName("paintComponent() draws background, scales and draws image")
    void testPaintComponent() throws NoSuchFieldException, IllegalAccessException {
        Graphics mockGraphics = Mockito.mock(Graphics.class);
        BufferedImage dummyImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        BufferedImage adjustedImage = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        
        when(mockPaneModel.picture()).thenReturn(dummyImage);
        
        try (MockedStatic<ImageUtility> mockedImageUtility = Mockito.mockStatic(ImageUtility.class)) {
            mockedImageUtility.when(() -> ImageUtility.adjustImage(any(BufferedImage.class), anyInt(), anyInt()))
                              .thenReturn(adjustedImage);

            spyPaneView.paintComponent(mockGraphics);

            verify(mockGraphics, times(1)).setColor(Color.white);
            verify(mockGraphics, times(1)).fillRect(0, 0, 800, 600);

            verify(mockGraphics, times(1)).drawImage(eq(adjustedImage), anyInt(), anyInt(), eq(null));
            
            assertNotEquals(new Point2D.Double(1.0d, 1.0d), (Point2D.Double)getPrivateField(spyPaneView, "scaleFactor"), "Scale factor should be adjusted");
            assertNotEquals(new Point2D.Double(0.0d, 0.0d), (Point2D.Double)getPrivateField(spyPaneView, "originPoint"), "Origin point should be adjusted");
        }
    }

    @Test
    @DisplayName("paintComponent() handles null model gracefully")
    void testPaintComponentNullModel() throws NoSuchFieldException, IllegalAccessException {
        Graphics mockGraphics = Mockito.mock(Graphics.class);
        setPrivateField(spyPaneView, "model", null);

        assertDoesNotThrow(() -> spyPaneView.paintComponent(mockGraphics));
        verify(mockGraphics, times(1)).fillRect(0, 0, 800, 600);
        verify(mockGraphics, never()).drawImage(any(BufferedImage.class), anyInt(), anyInt(), any());
    }

    @Test
    @DisplayName("paintComponent() handles null picture gracefully")
    void testPaintComponentNullPicture() {
        Graphics mockGraphics = Mockito.mock(Graphics.class);
        when(mockPaneModel.picture()).thenReturn(null);

        assertDoesNotThrow(() -> spyPaneView.paintComponent(mockGraphics));
        verify(mockGraphics, times(1)).fillRect(0, 0, 800, 600);
        verify(mockGraphics, never()).drawImage(any(BufferedImage.class), anyInt(), anyInt(), any());
    }

    @Test
    @DisplayName("paintComponent() adjusts image scale to fit the view")
    void testPaintComponentAdjustsImageScale() throws NoSuchFieldException, IllegalAccessException {
        Graphics mockGraphics = Mockito.mock(Graphics.class);
        BufferedImage wideImage = new BufferedImage(1600, 300, BufferedImage.TYPE_INT_RGB);
        BufferedImage tallImage = new BufferedImage(200, 1200, BufferedImage.TYPE_INT_RGB);
        
        when(mockPaneModel.picture()).thenReturn(wideImage);
        try (MockedStatic<ImageUtility> mockedImageUtility = Mockito.mockStatic(ImageUtility.class)) {
            mockedImageUtility.when(() -> ImageUtility.adjustImage(any(BufferedImage.class), anyInt(), anyInt()))
                              .thenReturn(new BufferedImage(800, 150, BufferedImage.TYPE_INT_RGB));
            spyPaneView.paintComponent(mockGraphics);
            verify(mockGraphics, times(1)).drawImage(any(BufferedImage.class), eq(0), eq((600 - 150) / 2), eq(null));
            assertEquals(0.5, ((Point2D.Double)getPrivateField(spyPaneView, "scaleFactor")).x, 0.001);
            assertEquals(0.5, ((Point2D.Double)getPrivateField(spyPaneView, "scaleFactor")).y, 0.001);
            assertEquals(0.0, ((Point2D.Double)getPrivateField(spyPaneView, "originPoint")).x, 0.001);
            assertEquals(225.0, ((Point2D.Double)getPrivateField(spyPaneView, "originPoint")).y, 0.001);
        }
        
        Mockito.reset(mockGraphics);
        
        when(mockPaneModel.picture()).thenReturn(tallImage);
        try (MockedStatic<ImageUtility> mockedImageUtility = Mockito.mockStatic(ImageUtility.class)) {
            mockedImageUtility.when(() -> ImageUtility.adjustImage(any(BufferedImage.class), anyInt(), anyInt()))
                              .thenReturn(new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB));
            spyPaneView.paintComponent(mockGraphics);
            verify(mockGraphics, times(1)).drawImage(any(BufferedImage.class), eq((800 - 100) / 2), eq(0), eq(null));
            assertEquals(0.5, ((Point2D.Double)getPrivateField(spyPaneView, "scaleFactor")).x, 0.001);
            assertEquals(0.5, ((Point2D.Double)getPrivateField(spyPaneView, "scaleFactor")).y, 0.001);
            assertEquals(350.0, ((Point2D.Double)getPrivateField(spyPaneView, "originPoint")).x, 0.001);
            assertEquals(0.0, ((Point2D.Double)getPrivateField(spyPaneView, "originPoint")).y, 0.001);
        }
    }

    @Test
    @DisplayName("scrollAmount() always returns (0, 0)")
    void testScrollAmount() {
        assertEquals(new Point(0, 0), spyPaneView.scrollAmount(), "scrollAmount should always be (0,0)");
    }

    @Test
    @DisplayName("scrollBy() does nothing")
    void testScrollBy() {
        Point initialAmount = spyPaneView.scrollAmount();
        spyPaneView.scrollBy(new Point(10, 20));
        assertEquals(initialAmount, spyPaneView.scrollAmount(), "scrollBy should not change scroll amount");
    }

    @Test
    @DisplayName("scrollTo() does nothing")
    void testScrollTo() {
        Point initialAmount = spyPaneView.scrollAmount();
        spyPaneView.scrollTo(new Point(100, 200));
        assertEquals(initialAmount, spyPaneView.scrollAmount(), "scrollTo should not change scroll amount");
    }
}