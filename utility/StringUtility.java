package utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class StringUtility {
  public static String csvString(String paramString) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append('"');
    stringBuffer.append(',');
    stringBuffer.append(' ');
    stringBuffer.append('\t');
    stringBuffer.append('\r');
    stringBuffer.append('\n');
    stringBuffer.append('\f');
    String str1 = stringBuffer.toString();
    boolean bool = false;
    for (int b = 0; b < paramString.length(); b++) {
      Character character = Character.valueOf(paramString.charAt(b));
      if (str1.indexOf(character.charValue()) != -1)
        bool = true;
    }
    String str2 = paramString;
    if (bool) {
      stringBuffer = new StringBuffer();
      stringBuffer.append('"');
      for (int b1 = 0; b1 < paramString.length(); b1++) {
        Character character = Character.valueOf(paramString.charAt(b1));
        stringBuffer.append(character);
        if (character.charValue() == '"')
          stringBuffer.append('"');
      }
      stringBuffer.append('"');
      str2 = stringBuffer.toString();
    }
    return str2;
  }

  public static String encodingSymbol() {
    return "UTF-8";
  }

  public static Character getChar(BufferedReader paramBufferedReader) {
    try {
      int i = paramBufferedReader.read();
      if (i == -1)
        return null;
      Character character = Character.valueOf((char) i);
      if (character.charValue() == '\n')
        return Character.valueOf('\n');
      if (character.charValue() == '\r') {
        paramBufferedReader.mark(256);
        i = paramBufferedReader.read();
        if (i == -1) {
          paramBufferedReader.reset();
          return Character.valueOf('\n');
        }
        character = Character.valueOf((char) i);
        if (character.charValue() == '\n')
          return character;
        paramBufferedReader.reset();
        return Character.valueOf('\n');
      }
      return character;
    } catch (IOException iOException) {
      iOException.printStackTrace();
      return null;
    }
  }

  public static List<String> getRowCSV(BufferedReader paramBufferedReader) {
    ArrayList<String> arrayList = new ArrayList();
    StringBuffer stringBuffer = new StringBuffer();
    boolean bool = true;
    Character character = null;
    while (bool) {
      character = getChar(paramBufferedReader);
      if (character == null) {
        if (stringBuffer.length() == 0)
          return null;
        break;
      }
      if (character.charValue() == '\n') {
        bool = false;
        continue;
      }
      if (character.charValue() == ',') {
        arrayList.add(stringBuffer.toString());
        stringBuffer = new StringBuffer();
        continue;
      }
      if (character.charValue() == '"') {
        boolean bool1 = true;
        while (bool1) {
          character = getChar(paramBufferedReader);
          if (character == null) {
            if (stringBuffer.length() == 0)
              return null;
            break;
          }
          if (character.charValue() == '"') {
            try {
              paramBufferedReader.mark(256);
              character = getChar(paramBufferedReader);
              if (character == null) {
                if (stringBuffer.length() == 0)
                  return null;
                break;
              }
              if (character.charValue() == '"') {
                stringBuffer.append('"');
                continue;
              }
              paramBufferedReader.reset();
              bool1 = false;
            } catch (IOException iOException) {
              iOException.printStackTrace();
            }
            continue;
          }
          stringBuffer.append(character);
        }
        continue;
      }
      stringBuffer.append(character);
    }
    arrayList.add(stringBuffer.toString());
    return arrayList;
  }

  public static void putRowCSV(BufferedWriter paramBufferedWriter, List<String> paramList) {
    try {
      for (int b = 0; b < paramList.size(); b++) {
        if (b > 0)
          paramBufferedWriter.write(",");
        String str = paramList.get(b);
        paramBufferedWriter.write(csvString(str));
      }
      paramBufferedWriter.write(10);
    } catch (IOException iOException) {
      iOException.printStackTrace();
    }
  }

  public static List<List<String>> readRowsFromFile(File paramFile) {
    ArrayList<List<String>> arrayList = new ArrayList();
    try {
      FileInputStream fileInputStream = new FileInputStream(paramFile);
      InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, encodingSymbol());
      BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
      List<String> list = null;
      while ((list = getRowCSV(bufferedReader)) != null)
        arrayList.add(list);
      bufferedReader.close();
    } catch (FileNotFoundException fileNotFoundException) {
      fileNotFoundException.printStackTrace();
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      unsupportedEncodingException.printStackTrace();
    } catch (IOException iOException) {
      iOException.printStackTrace();
    }
    return arrayList;
  }

  public static List<List<String>> readRowsFromFile(String paramString) {
    File file = new File(paramString);
    return readRowsFromFile(file);
  }

  public static List<String> readTextFromFile(File paramFile) {
    ArrayList<String> arrayList = new ArrayList();
    try {
      FileInputStream fileInputStream = new FileInputStream(paramFile);
      InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, encodingSymbol());
      BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
      String str = null;
      while ((str = bufferedReader.readLine()) != null)
        arrayList.add(str);
      bufferedReader.close();
    } catch (FileNotFoundException fileNotFoundException) {
      fileNotFoundException.printStackTrace();
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      unsupportedEncodingException.printStackTrace();
    } catch (IOException iOException) {
      iOException.printStackTrace();
    }
    return arrayList;
  }

  public static List<String> readTextFromFile(String paramString) {
    File file = new File(paramString);
    return readTextFromFile(file);
  }

  public static List<String> readTextFromURL(String paramString) {
    URL uRL = null;
    try {
      uRL = new URL(paramString);
    } catch (MalformedURLException malformedURLException) {
      malformedURLException.printStackTrace();
    }
    return readTextFromURL(uRL);
  }

  public static List<String> readTextFromURL(URL paramURL) {
    ArrayList<String> arrayList = new ArrayList();
    try {
      InputStream inputStream = paramURL.openStream();
      InputStreamReader inputStreamReader = new InputStreamReader(inputStream, encodingSymbol());
      BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
      String str = null;
      while ((str = bufferedReader.readLine()) != null)
        arrayList.add(str);
      bufferedReader.close();
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      unsupportedEncodingException.printStackTrace();
    } catch (IOException iOException) {
      iOException.printStackTrace();
    }
    return arrayList;
  }

  public static List<String> splitString(String paramString1, String paramString2) {
    ArrayList<Integer> arrayList = new ArrayList();
    arrayList.add(Integer.valueOf(-1));
    int i = paramString1.length();
    int b;
    for (b = 0; b < i; b++) {
      if (paramString2.indexOf(paramString1.charAt(b)) >= 0)
        arrayList.add(Integer.valueOf(b));
    }
    arrayList.add(Integer.valueOf(i));
    i = arrayList.size() - 1;
    ArrayList<String> arrayList1 = new ArrayList();
    for (b = 0; b < i; b++) {
      int j = ((Integer) arrayList.get(b)).intValue() + 1;
      int k = ((Integer) arrayList.get(b + 1)).intValue() - 1;
      if (k >= j)
        arrayList1.add(paramString1.substring(j, k + 1));
    }
    return arrayList1;
  }

  public static void writeRows(List<List<String>> paramList, File paramFile) {
    try {
      FileOutputStream fileOutputStream = new FileOutputStream(paramFile);
      OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, encodingSymbol());
      BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
      for (List<String> list : paramList)
        putRowCSV(bufferedWriter, list);
      bufferedWriter.close();
    } catch (FileNotFoundException fileNotFoundException) {
      fileNotFoundException.printStackTrace();
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      unsupportedEncodingException.printStackTrace();
    } catch (IOException iOException) {
      iOException.printStackTrace();
    }
  }

  public static void writeRows(List<List<String>> paramList, String paramString) {
    File file = new File(paramString);
    writeRows(paramList, file);
  }

  public static void writeText(List<String> paramList, File paramFile) {
    try {
      FileOutputStream fileOutputStream = new FileOutputStream(paramFile);
      OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, encodingSymbol());
      BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
      for (String str : paramList)
        bufferedWriter.write(str + "\n");
      bufferedWriter.close();
    } catch (IOException iOException) {
      iOException.printStackTrace();
    }
  }

  public static void writeText(List<String> paramList, String paramString) {
    File file = new File(paramString);
    writeText(paramList, file);
  }
}
