package mvc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.image.BufferedImage;
import java.util.ArrayList; // ModelクラスがArrayListを使用しているため

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Model Class Unit Tests")
class ModelTest {

    private Model model;

    @BeforeEach
    void setUp() {
        model = new Model();
    }

    @Test
    @DisplayName("Constructor initializes dependents and picture correctly")
    void testConstructorInitialization() {
        assertNotNull(model.dependents, "Dependents list should not be null after initialization");
        assertTrue(model.dependents.isEmpty(), "Dependents list should be empty initially");
        assertNull(model.picture(), "Picture should be null initially");
    }

    @Test
    @DisplayName("addDependent adds a View to the dependents list")
    void testAddDependent() {
        View mockView = Mockito.mock(View.class);
        model.addDependent(mockView);
        assertEquals(1, model.dependents.size(), "Dependents list should contain one view");
        assertTrue(model.dependents.contains(mockView), "Dependents list should contain the added view");
    }

    @Test
    @DisplayName("changed() notifies all dependent Views to update")
    void testChangedNotifiesDependents() {
        View mockView1 = Mockito.mock(View.class);
        View mockView2 = Mockito.mock(View.class);

        model.addDependent(mockView1);
        model.addDependent(mockView2);

        model.changed();

        // verify that update() method was called on both mock views
        verify(mockView1, times(1)).update();
        verify(mockView2, times(1)).update();
    }

    @Test
    @DisplayName("picture() returns the current BufferedImage")
    void testPictureGetter() {
        BufferedImage dummyImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        model.picture(dummyImage);
        assertEquals(dummyImage, model.picture(), "picture() should return the set image");
    }

    @Test
    @DisplayName("picture(BufferedImage) sets the BufferedImage")
    void testPictureSetter() {
        BufferedImage initialImage = model.picture();
        assertNull(initialImage, "Picture should be null initially");

        BufferedImage newImage = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        model.picture(newImage);
        assertEquals(newImage, model.picture(), "picture should be updated to the new image");
    }

    @Test
    @DisplayName("toString() returns a correct string representation")
    void testToString() {
        String expectedString = "mvc.Model[picture=null]";
        assertEquals(expectedString, model.toString(), "toString() should return the expected string");

        BufferedImage dummyImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        model.picture(dummyImage);
        // BufferedImage.toString()の結果は環境依存になりがちなので、部分的に検証
        assertTrue(model.toString().startsWith("mvc.Model[picture=BufferedImage@"), "toString() should contain image info");
    }
}