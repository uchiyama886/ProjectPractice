package utility;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;

import utility.Condition;
import utility.ValueHolder;

/**
 * ファイルのユーティリティ。
 */
public class FileUtility extends Object
{
	/**
	 * カレントディレクトリを文字列（最後が必ずパス区切り文字となる）として応答する。
	 * @return カレントディレクトリの文字列
	 */
	public static String currentDirectory()
	{
		ValueHolder<String> aString = new ValueHolder<String>(System.getProperty("user.dir"));
		new Condition(() -> aString.get() == null).ifTrue(() ->
		{ aString.set(new File(".").getAbsoluteFile().getParent());});
		new Condition(() -> aString.get() == null).ifTrue(() -> {
        aString.set(new File(".").getAbsoluteFile().getParent());
		});
		StringBuffer aBuffer = new StringBuffer();
		aBuffer.append(aString.get());
		Character aCharacter = aString.get().charAt(aString.get().length() - 1);
		new Condition(() -> aCharacter != File.separatorChar).ifTrue(() ->
		{ aBuffer.append(File.separator);});
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
		try { aDesktop.open(aFile); }
		catch (IOException anException) { anException.printStackTrace(); }

		return;
	}

	/**
	 * 開こうとするファイル名を文字列として受け取り、そのファイルに関連付けられたアプリケーションを起動してファイルを開く。
	 * @param aString 開こうとするファイル名の文字列
	 */
	public static void open(String aString)
	{
		File aFile = new File(aString);
		FileUtility.open(aFile);

		return;
	}
}
