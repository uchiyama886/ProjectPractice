package pane;

import mvc.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import utility.ImageUtility;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("PaneModel Class Unit Tests")
class PaneModelTest extends Model {

    private PaneModel paneModel;
    private PrintStream originalOut;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        paneModel = new PaneModel();
        
        // System.outをキャプチャするための設定
        originalOut = System.out;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    // 各テストの後にSystem.outを元に戻す
    @org.junit.jupiter.api.AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Default constructor initializes picture to null")
    void testDefaultConstructor() {
        assertNull(paneModel.picture(), "Picture should be null after default construction");
    }

    @Test
    @DisplayName("Constructor with string path loads image and sets it to picture")
    void testConstructorWithStringPath() {
        String testPath = "test/path/image.png";
        BufferedImage mockImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);

        // ImageUtility.readImageをモック化
        try (MockedStatic<ImageUtility> mockedImageUtility = Mockito.mockStatic(ImageUtility.class)) {
            mockedImageUtility.when(() -> ImageUtility.readImage(testPath)).thenReturn(mockImage);

            PaneModel modelWithPath = new PaneModel(testPath);
            assertEquals(mockImage, modelWithPath.picture(), "Constructor with path should set the loaded image");
            mockedImageUtility.verify(() -> ImageUtility.readImage(testPath), times(1));
        }
    }

    @Test
    @DisplayName("Constructor with BufferedImage sets the provided image to picture")
    void testConstructorWithBufferedImage() {
        BufferedImage providedImage = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        PaneModel modelWithImage = new PaneModel(providedImage);
        assertEquals(providedImage, modelWithImage.picture(), "Constructor with BufferedImage should set the provided image");
    }

    @Test
    @DisplayName("mouseClicked() prints the clicked point to System.out")
    void testMouseClicked() {
        Point clickedPoint = new Point(50, 75);
        MouseEvent mockMouseEvent = Mockito.mock(MouseEvent.class);

        paneModel.mouseClicked(clickedPoint, mockMouseEvent);

        String expectedOutput = clickedPoint.toString() + System.lineSeparator();
        assertEquals(expectedOutput, outContent.toString(), "mouseClicked should print the point");
    }

    @Test
    @DisplayName("mouseDragged() prints the dragged point to System.out")
    void testMouseDragged() {
        Point draggedPoint = new Point(120, 90);
        MouseEvent mockMouseEvent = Mockito.mock(MouseEvent.class);

        paneModel.mouseDragged(draggedPoint, mockMouseEvent);

        String expectedOutput = draggedPoint.toString() + System.lineSeparator();
        assertEquals(expectedOutput, outContent.toString(), "mouseDragged should print the point");
    }
}