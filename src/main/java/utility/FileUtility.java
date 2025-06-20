package utility;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

// ConditionとValueHolderが同じutilityパッケージにあることを前提とします
/**
 * ファイルのユーティリティのクラス。
 * ファイルシステム上のファイル操作と、JAR/クラスパス内のリソース操作を提供する。
 */
public class FileUtility extends Object 
{

    /**
     * カレントディレクトリを文字列（最後が必ずパス区切り文字となる）として応答する。
     * @return カレントディレクトリの文字列
     */
    public static String currentDirectory() 
    {
        ValueHolder<String> aString = new ValueHolder<>(System.getProperty("user.dir"));
        // 最初のConditionは不要な重複の可能性があるので削除するか、ロジックを整理すると良いでしょう。
        // ここでは original のままにしておきますが、通常は new File(".").getAbsoluteFile().getParent() は null を返しません。
        new Condition(() -> aString.get() == null).ifTrue(()-> 
        {
            aString.set(new File(".").getAbsoluteFile().getParent());
        });
        new Condition(() -> aString.get() == null).ifTrue(() -> 
        {
            aString.set(new File(".").getAbsoluteFile().getParent());
        });
        StringBuffer aBuffer = new StringBuffer();
        aBuffer.append(aString.get());
        Character aCharacter = aString.get().charAt(aString.get().length() - 1);
        new Condition(() -> aCharacter != File.separatorChar).ifTrue(()-> 
        {
            aBuffer.append(File.separator);
        });
        aString.set(aBuffer.toString());

        return aString.get();
    }

    /**
     * 開こうとするファイルを受け取り、そのファイルに関連付けられたアプリケーションを起動してファイルを開く。
     * @param aFile 開こうとするファイル
     */
    public static void open(File aFile) 
    {
        Desktop aDesktop = Desktop.getDesktop();
        try 
        {
            aDesktop.open(aFile);
        } catch (IOException anException) 
        {
            System.err.println("ファイルのオープン中にエラーが発生しました: " + aFile.getAbsolutePath());
            anException.printStackTrace();
        }
    }

    /**
     * 開こうとするファイル名を文字列として受け取り、そのファイルに関連付けられたアプリケーションを起動してファイルを開く。
     * @param aString 開こうとするファイル名の文字列
     */
    public static void open(String aString) 
    {
        File aFile = new File(aString);
        FileUtility.open(aFile);
    }

    /**
     * 指定されたパスのリソースを InputStream として読み込む。
     * 主にJARファイル内やクラスパス上のリソースにアクセスするために使用。
     * @param resourcePath クラスパス上のリソースのパス (例: "images/icon.png","data/config.txt")
     * @return リソースへの InputStream(リソースが見つからない場合は null)
     */
    public static InputStream getResourceAsStream(String resourcePath) 
    {
        ValueHolder<InputStream> is = new ValueHolder<>(FileUtility.class.getClassLoader().getResourceAsStream(resourcePath));
        new Condition(() -> is.get() == null).ifTrue(() -> 
        {
            System.err.println("警告: リソースが見つかりません: " + resourcePath);
        });
        return is.get();
    }

    /**
     * 指定されたパスのリソースとして画像を読み込む。
     * このメソッドは getResourceAsStream()を内部で使用し、JARファイル内の画像を安全にロードする。
     *
     * @param resourcePath クラスパス上の画像ファイルのパス (例:"SampleImages/imageEarth512x256.jpg")
     * @return 読み込まれたBufferedImage(読み込みに失敗した場合は null)
     */
    public static BufferedImage readImageFromResource(String resourcePath) 
    {
        try (InputStream input = getResourceAsStream(resourcePath)) 
        {
            ValueHolder<BufferedImage> imageHolder = new ValueHolder<>(null);
            Condition.ifThenElse(() -> input != null, () -> 
            {
                try 
                {
                    BufferedImage image = ImageIO.read(input);
                    Condition.ifThenElse(() -> image != null,() -> 
                        {
                            System.out.println("画像が正常にロードされました: " + resourcePath);
                            imageHolder.set(image);
                        },() -> 
                        {
                            System.out.println("resourcePath = " + input);
                            System.err.println("エラー: 画像ファイルとして読み込めませんでした。パスが正しいか、画像が破損していないか確認してください: " + resourcePath);
                        }
                    );
                } catch (IOException e) {
                    System.err.println("エラー: 画像 '" + resourcePath + "' の読み込み中にIO例外が発生しました: " + e.getMessage()); //
                    e.printStackTrace(); 
                }
            },() -> {});
            return imageHolder.get();
        } catch (IOException e) {
            System.err.println("エラー: 画像 '" + resourcePath + "' の読み込み中にIO例外が発生しました: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (RuntimeException e) { // Catch the RuntimeException re-thrown from the lambda
            System.err.println("エラー: 画像 '" + resourcePath + "' の読み込み中に予期せぬエラーが発生しました: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.err.println("エラー: 画像 '" + resourcePath + "' の読み込み中に予期せぬエラーが発生しました: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}

