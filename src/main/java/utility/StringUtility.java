package utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import utility.Condition;
import utility.Interval;
import utility.ValueHolder;

/**
 * 文字列のユーティリティ。
 */
public class StringUtility extends Object
{
	/**
	 * 文字列(aString)をCSV文字列にして応答するクラスメソッド。
	 * スペースやカンマなどの特殊文字が含まれる場合にはダブルクォートで囲まれる。
	 * @param aString 文字列
	 * @return CSV文字列
	 */
	public static String csvString(String aString)
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append('"');
		buffer.append(',');
		buffer.append(' ');
		buffer.append('\t');
		buffer.append('\r');
		buffer.append('\n');
		buffer.append('\f');
		String specialCharacters = buffer.toString();

		ValueHolder<Boolean> needDoubleQuote = new ValueHolder<Boolean>(false);
		new Interval<Integer>(0,
			(Integer it) -> it < aString.length(),
		(Integer it) -> ++it
		).forEach(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer it) {
                        Character aCharacter = aString.charAt(it);
                        new Condition(() -> specialCharacters.indexOf(aCharacter) != -1).ifThen(() -> { needDoubleQuote.set(true);});
                    }
                });
		ValueHolder<String> theString = new ValueHolder<String>(aString);
		new Condition(() -> needDoubleQuote.get()).ifThen(() ->
		{
			ValueHolder<StringBuffer> aBuffer = new ValueHolder<StringBuffer>(new StringBuffer());
			aBuffer.get().append('"');
			new Interval<Integer>(0,
			                      (Integer it) -> it < aString.length(),
			                      (Integer it) -> ++it
			).forEach((Integer it) ->
			{
				Character aCharacter = aString.charAt(it);
				aBuffer.get().append(aCharacter);
				new Condition(() -> aCharacter == '"').ifThen(() -> { aBuffer.get().append('"');});
			});
			aBuffer.get().append('"');
			theString.set(aBuffer.get().toString());
		});

		return theString.get();
	}

	/**
	 * 入出力する際の文字コードを応答するクラスメソッド。
	 * @return 文字コード
	 */
	public static String encodingSymbol()
	{
		return "UTF-8";
	}

	/**
	 * バッファードリーダー(aBufferdReader)から一文字を読み込んで応答するクラスメソッド。
	 * @param aBufferdReader バッファードリーダー
	 * @return 一文字
	 */
	public static Character getChar(BufferedReader aBufferdReader)
	{
		ValueHolder<Character> aValue = new ValueHolder<Character>(null);
		try
		{
			ValueHolder<Integer> charValue = new ValueHolder<Integer>(null);
			try { charValue.set(aBufferdReader.read()); }
			catch (IOException anException) { anException.printStackTrace(); }
			new Condition(() -> charValue.get() == -1).ifTrue(() ->
			{
				aValue.set(null);
				throw new RuntimeException();
			});
			ValueHolder<Character> aCharacter = new ValueHolder<Character>(Character.valueOf((char)(int)charValue.get()));
			new Condition(() -> aCharacter.get() == '\n').ifThenElse(
			() -> // LF
			{
				aValue.set(Character.valueOf('\n'));
				throw new RuntimeException();
			},
			() ->
			{
				new Condition(() -> aCharacter.get() == '\r').ifThenElse(
				() -> // CR
				{
					try { aBufferdReader.mark(256); }
					catch (IOException anException) { anException.printStackTrace(); }
					try { charValue.set(aBufferdReader.read()); }
					catch (IOException anException) { anException.printStackTrace(); }
					new Condition(() -> charValue.get() == -1).ifTrue(() ->  // EOF
					{
						try { aBufferdReader.reset(); }
						catch (IOException anException) { anException.printStackTrace(); }
						aValue.set(Character.valueOf('\n'));
						throw new RuntimeException();
					});
					aCharacter.set(Character.valueOf((char)(int)charValue.get()));
					new Condition(() -> aCharacter.get() == '\n').ifThenElse(
					() ->  // CRLF
					{
						aValue.set(aCharacter.get());
						throw new RuntimeException();
					},
					() ->
					{
						try { aBufferdReader.reset(); }
						catch (IOException anException) { anException.printStackTrace(); }
						aValue.set(Character.valueOf('\n'));
						throw new RuntimeException();
					});
				},
				() ->
				{
					aValue.set(aCharacter.get());
					throw new RuntimeException();
				});
			});
		}
		catch (RuntimeException anException) { return aValue.get(); }

		return null;
	}

	/**
	 * バッファードリーダー(aBufferdReader)からCSVとして一行を読み込んで集まりにして応答するクラスメソッド。
	 * @param aBufferdReader バッファードリーダー
	 * @return 文字列の集まり：CSVの一行
	 */
	public static List<String> getRowCSV(BufferedReader aBufferdReader)
	{
		List<String> aCollection = new ArrayList<String>();
		ValueHolder<StringBuffer> aBuffer = new ValueHolder<StringBuffer>(new StringBuffer());
		ValueHolder<Character> aCharacter = new ValueHolder<Character>(null);
		try
		{
			try
			{
				ValueHolder<Boolean> aBoolean = new ValueHolder<Boolean>(true);
				new Condition(() -> aBoolean.get()).whileTrue(() ->
				{
					aCharacter.set(StringUtility.getChar(aBufferdReader));
					new Condition(() -> aCharacter.get() == null).ifTrue(() ->
					{
						new Condition(() -> aBuffer.get().length() == 0).ifThenElse(
						() -> { throw new RuntimeException("return"); },
						() -> { throw new RuntimeException("break"); });
					});
					new Condition(() -> aCharacter.get() == '\n').ifThenElse(
					() -> { aBoolean.set(false); },
					() ->
					{
						new Condition(() -> aCharacter.get() == ',').ifThenElse(
						() ->
						{
							aCollection.add(aBuffer.get().toString());
							aBuffer.set(new StringBuffer());
						},
						() ->
						{
							new Condition(() -> aCharacter.get() == '"').ifThenElse(
							() ->
							{
								try
								{
									ValueHolder<Boolean> aLoop = new ValueHolder<Boolean>(true);
									new Condition(() -> aLoop.get()).whileTrue(() ->
									{
										aCharacter.set(StringUtility.getChar(aBufferdReader));
										new Condition(() -> aCharacter.get() == null).ifTrue(() ->
										{
											new Condition(() -> aBuffer.get().length() == 0).ifThenElse(
											() -> { throw new RuntimeException("return"); },
											() -> { throw new RuntimeException("break"); });
										});
										new Condition(() -> aCharacter.get() == '"').ifThenElse(
										() ->
										{
											try { aBufferdReader.mark(256); }
											catch (IOException anException) { anException.printStackTrace(); }
											aCharacter.set(StringUtility.getChar(aBufferdReader));
											new Condition(() -> aCharacter.get() == null).ifTrue(() ->
											{
												new Condition(() -> aBuffer.get().length() == 0).ifThenElse(
												() -> { throw new RuntimeException("return"); },
												() -> { throw new RuntimeException("break"); });
											});
											new Condition(() -> aCharacter.get() == '"').ifThenElse(
											() -> { aBuffer.get().append('"'); },
											() ->
											{
												try { aBufferdReader.reset(); }
												catch (IOException anException) { anException.printStackTrace(); }
												aLoop.set(false);
											});
											
										},
										() ->
										{
											aBuffer.get().append(aCharacter.get());
										});
									});
								}
								catch (RuntimeException anException)
								{
									new Condition(() -> anException.getMessage() == "return").ifTrue(() -> { throw anException; });
									new Condition(() -> anException.getMessage() == "break").ifTrue(() -> { ; });
								}
							},
							() ->
							{
								aBuffer.get().append(aCharacter.get());
							});
						});
					});
				});
			}
			catch (RuntimeException anException)
			{
				new Condition(() -> anException.getMessage() == "return").ifTrue(() -> { throw anException; });
				new Condition(() -> anException.getMessage() == "break").ifTrue(() -> { ; });
			}
		}
		catch (RuntimeException anException) { return null; }
		aCollection.add(aBuffer.get().toString());

		return aCollection;
	}

	/**
	 * 文字列の集まり(aCollection)をCSVの一行としてバッファードライター(aBufferedWriter)へ書き込むクラスメソッド。
	 * @param aBufferedWriter バッファードライター
	 * @param aCollection 文字列の集まり：CSVの一行
	 */
	public static void putRowCSV(BufferedWriter aBufferedWriter, List<String> aCollection)
	{
		try
		{
			ValueHolder<Integer> index = new ValueHolder<Integer>(0);
			new Condition(() -> index.get() < aCollection.size()).whileTrue(() ->
			{
				new Condition(() -> index.get() > 0).ifTrue(() ->
				{ 
					try { aBufferedWriter.write(","); }
					catch (IOException anException) { anException.printStackTrace(); }
				});
				String aString = aCollection.get(index.get());
				try {  aBufferedWriter.write(StringUtility.csvString(aString)); }
				catch (IOException anException) { anException.printStackTrace(); }
				index.setDo((Integer it) -> it + 1);
			});
			aBufferedWriter.write('\n');
		}
		catch (IOException anException) { anException.printStackTrace(); }

		return;
	}

	/**
	 * 指定されたファイルからレコード(row)を読み込んで、それをレコードリストにして応答するクラスメソッド。
	 * @param aFile ファイル
	 * @return レコードリスト
	 */
	public static List<List<String>> readRowsFromFile(File aFile)
	{
		List<List<String>> aCollection = new ArrayList<List<String>>();
		try
		{
			FileInputStream inputStream = new FileInputStream(aFile);
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StringUtility.encodingSymbol());
			BufferedReader inputReader = new BufferedReader(inputStreamReader);

			ValueHolder<List<String>> aRow = new ValueHolder<List<String>>(null);
			new Condition(() ->
			              {
			                  aRow.set(StringUtility.getRowCSV(inputReader));
			                  return aRow.get() != null;
			              }
			).whileTrue(() -> { aCollection.add(aRow.get()); });

			inputReader.close();
		}
		catch (FileNotFoundException anException) { anException.printStackTrace(); }
		catch (UnsupportedEncodingException anException) { anException.printStackTrace(); }
		catch (IOException anException) { anException.printStackTrace(); }

		return aCollection;
	}

	/**
	 * 指定されたファイル文字列からレコード(row)を読み込んで、それをレコードリストにして応答するクラスメソッド。
	 * @param fileString ファイル名
	 * @return レコードリスト
	 */
	public static List<List<String>> readRowsFromFile(String fileString)
	{
		File aFile = new File(fileString);

		List<List<String>> aCollection = StringUtility.readRowsFromFile(aFile);

		return aCollection;
	}

	/**
	 * 指定されたファイルからテキストを読み込んで、それを行リストにして応答するクラスメソッド。
	 * @param aFile ファイル
	 * @return 行リスト
	 */
	public static List<String> readTextFromFile(File aFile)
	{
		List<String> aCollection = new ArrayList<String>();

		try
		{
			FileInputStream inputStream = new FileInputStream(aFile);
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StringUtility.encodingSymbol());
			BufferedReader inputReader = new BufferedReader(inputStreamReader);

			ValueHolder<String> aString = new ValueHolder<String>(null);
			new Condition(() ->
			              {
			                  try { aString.set(inputReader.readLine()); }
			                  catch (IOException anException) { anException.printStackTrace(); }
			                  return aString.get() != null;
			              }
			).whileTrue(() -> { aCollection.add(aString.get()); });

			inputReader.close();
		}
		catch (FileNotFoundException anException) { anException.printStackTrace(); }
		catch (UnsupportedEncodingException anException) { anException.printStackTrace(); }
		catch (IOException anException) { anException.printStackTrace(); }

		return aCollection;
	}

	/**
	 * 指定されたファイル文字列からテキストを読み込んで、それを行リストにして応答するクラスメソッド。
	 * @param fileString ファイル名
	 * @return 行リスト
	 */
	public static List<String> readTextFromFile(String fileString)
	{
		File aFile = new File(fileString);

		List<String> aCollection = StringUtility.readTextFromFile(aFile);

		return aCollection;
	}

	/**
	 * 指定されたURL文字列からテキストを読み込んで、それを行リストにして応答するクラスメソッド。
	 * @param urlString テキストのためのURL文字列
	 * @return 行リスト
	 */
	public static List<String> readTextFromURL(String urlString)
	{
		URL aURL = null;
		try { 
			URI aURI = new URI(urlString);
			aURL = aURI.toURL();
		}catch (URISyntaxException | MalformedURLException anException) {
			anException.printStackTrace(); 
			return null;
		}

		List<String> aCollection = StringUtility.readTextFromURL(aURL);

		return aCollection;
	}

	/**
	 * 指定されたURLからテキストを読み込んで、それを行リストにして応答するクラスメソッド。
	 * @param aURL テキストのためのURL
	 * @return 行リスト
	 */
	public static List<String> readTextFromURL(URL aURL)
	{
		List<String> aCollection = new ArrayList<String>();

		try
		{
			InputStream inputStream = aURL.openStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StringUtility.encodingSymbol());
			BufferedReader inputReader = new BufferedReader(inputStreamReader);

			ValueHolder<String> aString = new ValueHolder<String>(null);
			new Condition(() ->
			              {
			                  try { aString.set(inputReader.readLine()); }
			                  catch (IOException anException) { anException.printStackTrace(); }
			                  return aString.get() != null;
			              }
			).whileTrue(() -> { aCollection.add(aString.get()); });

			inputReader.close();
		}
		catch (UnsupportedEncodingException anException) { anException.printStackTrace(); }
		catch (IOException anException) { anException.printStackTrace(); }

		return aCollection;
	}

	/**
	 * 文字列をセパレータで分割したトークン列を応答するクラスメソッド。
	 * @param string 文字列
	 * @param separators 分割文字列
	 * @return セパレータで分割したトークン列
	 */
	public static List<String> splitString(String string, String separators)
	{
		List<Integer> indexes;
		List<String> result;

		indexes = new ArrayList<Integer>();
		indexes.add(-1);
		ValueHolder<Integer> stop = new ValueHolder<Integer>(string.length());
		new Interval<Integer>(0,
		                      (Integer it) -> it < stop.get(),
		                      (Integer it) -> ++it
		).forEach((Integer it) ->
		{
			new Condition(() -> (separators.indexOf(string.charAt(it))) >= 0).ifTrue(() ->
			{ indexes.add(it); });
		});
		indexes.add(stop.get());
		stop.set(indexes.size() - 1);
		result = new ArrayList<String>();
		new Interval<Integer>(0,
		                      (Integer it) -> it < stop.get(),
		                      (Integer it) -> ++it
		).forEach((Integer it) ->
		{
			Integer start;
			Integer end;

			start = indexes.get(it) + 1;
			end = indexes.get(it + 1) - 1;
			new Condition(() -> end >= start).ifTrue(() ->
			{ result.add(string.substring(start, end + 1)); });
		});

		return result;
	}

	/**
	 * 指定されたレコードリストを、指定されたファイルに書き出すクラスメソッド。
	 * @param aCollection レコードリスト
	 * @param aFile ファイル
	 */
	public static void writeRows(List<List<String>> aCollection, File aFile)
	{
		try
		{
			FileOutputStream outputStream = new FileOutputStream(aFile);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StringUtility.encodingSymbol());
			BufferedWriter outputWriter = new BufferedWriter(outputStreamWriter);

			aCollection.forEach((List<String> aRow) -> { StringUtility.putRowCSV(outputWriter, aRow); });

			outputWriter.close();
		}
		catch (FileNotFoundException anException) { anException.printStackTrace(); }
		catch (UnsupportedEncodingException anException) { anException.printStackTrace(); }
		catch (IOException anException) { anException.printStackTrace(); }

		return;
	}

	/**
	 * 指定されたレコードリストを、指定されたファイル名のファイルに書き出すクラスメソッド。
	 * @param aCollection レコードリスト
	 * @param fileString ファイル名
	 */
	public static void writeRows(List<List<String>> aCollection, String fileString)
	{
		File aFile = new File(fileString);
		StringUtility.writeRows(aCollection, aFile);

		return;
	}

	/**
	 * 指定された行リストを、指定されたファイルに書き出すクラスメソッド。
	 * @param aCollection 行リスト
	 * @param aFile ファイル
	 */
	public static void writeText(List<String> aCollection, File aFile)
	{
		try
		{
			FileOutputStream outputStream = new FileOutputStream(aFile);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StringUtility.encodingSymbol());
			BufferedWriter outputWriter = new BufferedWriter(outputStreamWriter);

			aCollection.forEach((String aString) ->
			{
				try { outputWriter.write(aString + "\n"); }
				catch (IOException anException) { anException.printStackTrace(); }
			});

			outputWriter.close();
		}
		catch (IOException anException) { anException.printStackTrace(); }

		return;
	}

	/**
	 * 指定された行リストを、指定されたファイル名のファイルに書き出すクラスメソッド。
	 * @param aCollection 行リスト
	 * @param fileString ファイル名
	 */
	public static void writeText(List<String> aCollection, String fileString)
	{
		File aFile = new File(fileString);
		StringUtility.writeText(aCollection, aFile);

		return;
	}
}
