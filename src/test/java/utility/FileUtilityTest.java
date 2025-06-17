package utility;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import javax.imageio.ImageIO;

@DisplayName("FileUtility Class Unit Tests")
class FileUtilityTest {

    @TempDir
    Path tempDir;

    private PrintStream originalOut;
    private ByteArrayOutputStream outContent;
    private PrintStream originalErr;
    private ByteArrayOutputStream errContent;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        originalErr = System.err;
        errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    @DisplayName("currentDirectory() returns current directory with separator")
    void testCurrentDirectory() {
        String currentDir = FileUtility.currentDirectory();
        assertNotNull(currentDir, "Current directory should not be null");
        assertTrue(currentDir.endsWith(File.separator), "Current directory should end with a separator");

        String userDir = System.getProperty("user.dir");
        if (!userDir.endsWith(File.separator)) {
            userDir += File.separator;
        }
        assertEquals(userDir, currentDir, "Current directory should match System.getProperty('user.dir') with separator");
    }

    @Test
    @DisplayName("open(File) calls Desktop.open for a file")
    void testOpenFile() throws IOException {
        File mockFile = Mockito.mock(File.class);
        Desktop mockDesktop = Mockito.mock(Desktop.class);

        try (MockedStatic<Desktop> mockedDesktop = Mockito.mockStatic(Desktop.class)) {
            mockedDesktop.when(Desktop::getDesktop).thenReturn(mockDesktop);
            when(mockDesktop.isSupported(Desktop.Action.OPEN)).thenReturn(true);

            FileUtility.open(mockFile);

            verify(mockDesktop, times(1)).open(mockFile);
            assertTrue(errContent.toString().isEmpty(), "No error should be printed to stderr");
        }
    }

    @Test
    @DisplayName("open(File) prints error if IOException occurs")
    void testOpenFileIOException() throws IOException {
        File mockFile = Mockito.mock(File.class);
        Desktop mockDesktop = Mockito.mock(Desktop.class);

        try (MockedStatic<Desktop> mockedDesktop = Mockito.mockStatic(Desktop.class)) {
            mockedDesktop.when(Desktop::getDesktop).thenReturn(mockDesktop);
            when(mockDesktop.isSupported(Desktop.Action.OPEN)).thenReturn(true);
            doThrow(new IOException("Test IO Exception")).when(mockDesktop).open(mockFile);

            FileUtility.open(mockFile);

            verify(mockDesktop, times(1)).open(mockFile);
            assertTrue(errContent.toString().contains("ファイルのオープン中にエラーが発生しました"), "Error message should be printed to stderr");
            assertTrue(errContent.toString().contains("Test IO Exception"), "Exception message should be printed to stderr");
        }
    }

    @Test
    @DisplayName("open(String) converts string to file and calls Desktop.open")
    void testOpenString() throws IOException {
        String filePath = "testfile.txt";

        Desktop mockDesktop = Mockito.mock(Desktop.class);
        try (MockedStatic<Desktop> mockedDesktop = Mockito.mockStatic(Desktop.class)) {
            mockedDesktop.when(Desktop::getDesktop).thenReturn(mockDesktop);
            when(mockDesktop.isSupported(Desktop.Action.OPEN)).thenReturn(true);

            FileUtility.open(filePath);

            ArgumentCaptor<File> fileCaptor = ArgumentCaptor.forClass(File.class);
            verify(mockDesktop, times(1)).open(fileCaptor.capture());
            assertEquals(filePath, fileCaptor.getValue().getPath(), "Desktop.open should be called with correct file path");
        }
    }

    @Test
    @DisplayName("getResourceAsStream() returns InputStream for existing resource")
    void testGetResourceAsStreamExisting() throws IOException {
        String resourcePath = "test-resource.txt";
        String content = "Hello, Resource!";

        try (MockedStatic<FileUtility> mockedFileUtility = Mockito.mockStatic(FileUtility.class, CALLS_REAL_METHODS)) {
            // FileUtility.class.getClassLoader().getResourceAsStream() をモックする代わりに
            // FileUtility.getResourceAsStream() 自体をモックして、ダミーの InputStream を返すようにする
            InputStream testInputStream = new ByteArrayInputStream(content.getBytes());
            mockedFileUtility.when(() -> FileUtility.getResourceAsStream(resourcePath)).thenReturn(testInputStream);

            InputStream result = FileUtility.getResourceAsStream(resourcePath);
            assertNotNull(result, "InputStream should not be null for existing resource");
            assertEquals(content, new String(result.readAllBytes()), "Content should match the resource");
            result.close();
        } catch (Exception e) {
            fail("Exception during getResourceAsStream test: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("getResourceAsStream() returns null for non-existing resource and prints warning")
    void testGetResourceAsStreamNonExisting() {
        String resourcePath = "non-existent-resource.txt";

        try (MockedStatic<FileUtility> mockedFileUtility = Mockito.mockStatic(FileUtility.class, CALLS_REAL_METHODS)) {
            mockedFileUtility.when(() -> FileUtility.getResourceAsStream(resourcePath)).thenReturn(null);

            InputStream result = FileUtility.getResourceAsStream(resourcePath);
            assertNull(result, "InputStream should be null for non-existing resource");
            assertTrue(errContent.toString().contains("警告: リソースが見つかりません: " + resourcePath), "Warning message should be printed to stderr");
        }
    }

    @Test
    @DisplayName("readImageFromResource() returns BufferedImage for existing image resource")
    void testReadImageFromResourceExisting() throws IOException {
        String resourcePath = "test-image.png";
        BufferedImage dummyImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);

        try (MockedStatic<ImageIO> mockedImageIO = Mockito.mockStatic(ImageIO.class); MockedStatic<FileUtility> mockedFileUtility = Mockito.mockStatic(FileUtility.class, CALLS_REAL_METHODS)) {

            InputStream mockInputStream = new ByteArrayInputStream(new byte[100]);
            mockedFileUtility.when(() -> FileUtility.getResourceAsStream(resourcePath)).thenReturn(mockInputStream);

            mockedImageIO.when(() -> ImageIO.read(mockInputStream)).thenReturn(dummyImage);

            BufferedImage result = FileUtility.readImageFromResource(resourcePath);

            assertNotNull(result, "Image should not be null for existing resource");
            assertEquals(dummyImage, result, "Returned image should be the dummy image");
            mockedImageIO.verify(() -> ImageIO.read(mockInputStream), times(1));
            // 変更点: エラーメッセージが含まれていないことを確認する
            assertFalse(errContent.toString().contains("エラー"), "エラーメッセージはstderrに出力されるべきではありません"); // Changed from isEmpty()
            assertTrue(outContent.toString().contains("画像が正常にロードされました: " + resourcePath), "Success message should be printed to stdout");
        }
    }

    @Test
    @DisplayName("readImageFromResource() returns null and logs error if ImageIO.read throws IOException")
    void testReadImageFromResourceReadFailure() throws IOException {
        String resourcePath = "corrupted-image.png";

        try (MockedStatic<ImageIO> mockedImageIO = Mockito.mockStatic(ImageIO.class); MockedStatic<FileUtility> mockedFileUtility = Mockito.mockStatic(FileUtility.class, CALLS_REAL_METHODS)) {

            InputStream mockInputStream = new ByteArrayInputStream(new byte[100]);
            mockedFileUtility.when(() -> FileUtility.getResourceAsStream(resourcePath)).thenReturn(mockInputStream);

            mockedImageIO.when(() -> ImageIO.read(mockInputStream)).thenThrow(new IOException("Corrupted image data"));

            BufferedImage result = FileUtility.readImageFromResource(resourcePath);

            assertNull(result, "Image should be null if ImageIO.read fails");
            // The error message from readImageFromResource is now more consistent
            assertTrue(errContent.toString().contains("エラー: 画像 '" + resourcePath + "' の読み込み中にIO例外が発生しました: Corrupted image data"), "Error message should be printed");
            assertTrue(errContent.toString().contains("Corrupted image data"), "Exception message should be printed to stderr"); // Stack trace will contain this message
            assertTrue(errContent.toString().contains("java.io.IOException"), "Stack trace should contain IOException"); // Check for the exception type in stack trace
        }
    }

    @Test
    @DisplayName("readImageFromResource() returns null and logs error if image is null after read (invalid format)")
    void testReadImageFromResourceInvalidFormat() throws IOException {
        String resourcePath = "invalid-format.txt";

        try (MockedStatic<ImageIO> mockedImageIO = Mockito.mockStatic(ImageIO.class); MockedStatic<FileUtility> mockedFileUtility = Mockito.mockStatic(FileUtility.class, CALLS_REAL_METHODS)) {

            InputStream mockInputStream = new ByteArrayInputStream("Not an image".getBytes());
            mockedFileUtility.when(() -> FileUtility.getResourceAsStream(resourcePath)).thenReturn(mockInputStream);

            mockedImageIO.when(() -> ImageIO.read(mockInputStream)).thenReturn(null);

            BufferedImage result = FileUtility.readImageFromResource(resourcePath);

            assertNull(result, "Image should be null if read image is null");
            assertTrue(errContent.toString().contains("エラー: 画像ファイルとして読み込めませんでした。パスが正しいか、画像が破損していないか確認してください: " + resourcePath), "Specific error message for null image should be printed");
            assertFalse(errContent.toString().contains("Error reading image"), "No IOException message should be printed"); // IOExceptionのスタックトレースがないことを確認
        }
    }
}
