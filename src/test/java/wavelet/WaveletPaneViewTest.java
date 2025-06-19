package wavelet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.InOrder;
import org.mockito.MockedStatic;
import pane.PaneView;
import pane.PaneModel;
import mvc.View;
import utility.ImageUtility;

import java.awt.Graphics;
import java.awt.Font;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.image.ImageObserver;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("WaveletPaneView Class Unit Tests")
class WaveletPaneViewTest {

    private WaveletPaneModel mockWaveletPaneModel;
    private WaveletPaneController mockWaveletPaneController;
    private WaveletPaneView waveletPaneView;
    private Graphics mockGraphics;
    private BufferedImage mockPicture;
    private BufferedImage mockAdjustedPicture;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        mockWaveletPaneModel = Mockito.mock(WaveletPaneModel.class);
        mockWaveletPaneController = Mockito.mock(WaveletPaneController.class);
        mockGraphics = Mockito.mock(Graphics.class);
        mockPicture = Mockito.mock(BufferedImage.class);
        mockAdjustedPicture = Mockito.mock(BufferedImage.class);

        doNothing().when(mockWaveletPaneModel).addDependent(any(PaneView.class));
        doNothing().when(mockWaveletPaneController).setModel(any(PaneModel.class));
        doNothing().when(mockWaveletPaneController).setView(any(PaneView.class));

        when(mockWaveletPaneModel.picture()).thenReturn(mockPicture);
        when(mockWaveletPaneModel.label()).thenReturn("Test Label");
        when(mockPicture.getWidth()).thenReturn(100);
        when(mockPicture.getHeight()).thenReturn(100);

        waveletPaneView = new WaveletPaneView(mockWaveletPaneModel, mockWaveletPaneController);

        WaveletPaneView spyWaveletPaneView = Mockito.spy(waveletPaneView);
        
        doNothing().when(spyWaveletPaneView).repaint(anyInt(), anyInt(), anyInt(), anyInt());

        when(spyWaveletPaneView.getWidth()).thenReturn(800);
        when(spyWaveletPaneView.getHeight()).thenReturn(600);

        doReturn(mockWaveletPaneModel).when(spyWaveletPaneView).getModel();

        Field originPointField = getInheritedField(PaneView.class, "originPoint");
        originPointField.setAccessible(true);
        originPointField.set(spyWaveletPaneView, new Point2D.Double(0, 0));

        waveletPaneView = spyWaveletPaneView;
    }

    private Field getInheritedField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Class<?> currentClass = clazz;
        while (currentClass != null) {
            try {
                return currentClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                currentClass = currentClass.getSuperclass();
            }
        }
        throw new NoSuchFieldException("Field " + fieldName + " not found in " + clazz.getName() + " or its superclasses.");
    }

    private PaneModel getModelField(PaneView view) throws NoSuchFieldException, IllegalAccessException {
        Field field = View.class.getDeclaredField("model");
        field.setAccessible(true);
        return (PaneModel) field.get(view);
    }

    private pane.PaneController getControllerField(PaneView view) throws NoSuchFieldException, IllegalAccessException {
        Field field = View.class.getDeclaredField("controller");
        field.setAccessible(true);
        return (pane.PaneController) field.get(view);
    }

    @Test
    @DisplayName("Constructor sets model and controller and calls super constructor")
    void testConstructor() throws NoSuchFieldException, IllegalAccessException {
        assertNotNull(getModelField(waveletPaneView), "Model should be set.");
        assertNotNull(getControllerField(waveletPaneView), "Controller should be set.");
        assertTrue(getModelField(waveletPaneView) instanceof WaveletPaneModel, "Model should be of type WaveletPaneModel.");
        assertTrue(getControllerField(waveletPaneView) instanceof WaveletPaneController, "Controller should be of type WaveletPaneController.");

        verify(mockWaveletPaneModel, times(1)).addDependent(any(PaneView.class));
        verify(mockWaveletPaneController, times(1)).setModel(mockWaveletPaneModel);
        verify(mockWaveletPaneController, times(1)).setView(any(PaneView.class));
    }

    @Test
    @DisplayName("paintComponent() draws background and image from model, and draws label")
    void testPaintComponent() {
        try (MockedStatic<ImageUtility> mockedStatic = mockStatic(ImageUtility.class)) {
            mockedStatic.when(() -> ImageUtility.adjustImage(any(BufferedImage.class), anyInt(), anyInt()))
                        .thenReturn(mockAdjustedPicture);

            InOrder inOrder = inOrder(mockGraphics, mockWaveletPaneModel);

            waveletPaneView.paintComponent(mockGraphics);

            // 背景の描画
            inOrder.verify(mockGraphics).setColor(Color.white);
            inOrder.verify(mockGraphics).fillRect(0, 0, waveletPaneView.getWidth(), waveletPaneView.getHeight());

            // モデルから画像の取得
            inOrder.verify(mockWaveletPaneModel).picture();
            // adjustImage が呼び出され、その結果が描画されることを確認
            verify(mockGraphics).drawImage(eq(mockAdjustedPicture), anyInt(), anyInt(), eq(null));

            // フォントの設定 (WaveletPaneView 固有の描画より前)
            ArgumentCaptor<Font> fontCaptor = ArgumentCaptor.forClass(Font.class);
            inOrder.verify(mockGraphics).setFont(fontCaptor.capture());
            Font capturedFont = fontCaptor.getValue();
            assertEquals("MonoSpaced", capturedFont.getName());
            assertEquals(Font.PLAIN, capturedFont.getStyle());
            assertEquals(12, capturedFont.getSize());

            // モデルからラベルの取得
            inOrder.verify(mockWaveletPaneModel).label();
            
            int expectedFontSize = 12;
            String expectedLabel = "Test Label";

            // ラベルの縁取り (白色)
            inOrder.verify(mockGraphics, times(1)).setColor(Color.white);
            inOrder.verify(mockGraphics).drawString(expectedLabel, 1, expectedFontSize + 1);
            inOrder.verify(mockGraphics).drawString(expectedLabel, 2, expectedFontSize + 1);
            inOrder.verify(mockGraphics).drawString(expectedLabel, 3, expectedFontSize + 1);
            inOrder.verify(mockGraphics).drawString(expectedLabel, 1, expectedFontSize + 2);
            inOrder.verify(mockGraphics).drawString(expectedLabel, 3, expectedFontSize + 2);
            inOrder.verify(mockGraphics).drawString(expectedLabel, 1, expectedFontSize + 3);
            inOrder.verify(mockGraphics).drawString(expectedLabel, 2, expectedFontSize + 3);
            inOrder.verify(mockGraphics).drawString(expectedLabel, 3, expectedFontSize + 3);

            // ラベルの中心 (黒色)
            inOrder.verify(mockGraphics, times(1)).setColor(Color.black);
            inOrder.verify(mockGraphics).drawString(expectedLabel, 2, expectedFontSize + 2);
        }
    }
}