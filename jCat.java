import  java.io.*;

public class jCat {
	static int intLF = 0x0a;
	static boolean fgUseOptionB = false;
	static boolean fgUseOptionN = false;
	static boolean fgUseOptionS = false;
	static String strCmdName = new Object(){}.getClass().getEnclosingClass().getName();

	static boolean printLineNumber(int lineNumber, int firstChar) {
		boolean returnValue = true;	// 行数をインクリメントするか
		if(fgUseOptionN && (!fgUseOptionB || (firstChar != intLF))) {
			System.out.printf("%6d\t", lineNumber);
		} else if(fgUseOptionB) {
			if(firstChar != intLF) {
				System.out.printf("%6s\t", "");
			}
			returnValue = false;
		}
		return returnValue;
	}

	static void printFile(BufferedReader src, boolean fgClose) {
		int  ch;
		int intLine = 1;
		int previousCh = -1;
		boolean fgLFContimueTwice = false;
		try {
			while ((ch = src.read()) != -1)  {
				// sオプション指定時、0x0aの連続3回目以降は何もしない - 下記条件はその逆(何かする条件)
				if(!fgUseOptionS || !fgLFContimueTwice || (ch != intLF)) {
					if((previousCh == intLF) || (previousCh == -1)) {
						if(printLineNumber(intLine, ch)) {
							intLine++;
						}
					}
					System.out.print((char)ch);
				}
				if((ch == intLF) && (previousCh == intLF)) {
					fgLFContimueTwice = true;
				} else {
					fgLFContimueTwice = false;
				}
				previousCh = ch;
			}
			if(fgClose) {	// stdinはcloseしちゃダメ
				src.close();
			}
			System.out.flush();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();	// stderrに出力らしい
		}
	}

	public static void main(String[] args) {
		// オプション判定
		int posArg = 0;
		boolean fgOptionEnd = false;
		while(!fgOptionEnd && (args.length > posArg) && args[posArg].substring(0, 1).equals("-")) {
			if(args[posArg].equals("-")) {	// posArgのインクリメントなし
				fgOptionEnd = true;
			} else {
				if(args[posArg].equals("--")) {
					fgOptionEnd = true;
				} else {
					char[] arrayOptChar = args[posArg].toCharArray();
					for(int pos2 = 1; arrayOptChar.length > pos2; pos2++) {
						switch (arrayOptChar[pos2]) {
							case 'b':	// 'n'も含めるのでbreakなし
								fgUseOptionB = true;
							case 'n':
								fgUseOptionN = true;
								break;
							case 's':
								fgUseOptionS = true;
								break;
							default:
								System.err.println(strCmdName + ": illegal option -- " + arrayOptChar[pos2]);
								System.err.println("usage: java " + strCmdName + " [-bns] [file ...]");
								System.exit(0);
						}
					}
				}
				posArg++;
			}
		}
		// メイン部分
		if (args.length <= posArg) {
			// パイプやリダイレクト用 // stdinからのEOF入力はCtrl+dなそうだ
			printFile(new BufferedReader(new InputStreamReader(System.in)), false);
		} else {
			// ファイルを順次
			for (int i = posArg; i < args.length; i++) {
				if(args[i].equals("-")) {	// ファイル名が'-'のときはstdinから
					printFile(new BufferedReader(new InputStreamReader(System.in)), false);
				} else {
					try {
						printFile(new BufferedReader(new FileReader(args[i])), true);
					} catch (FileNotFoundException e) {
						System.err.println(strCmdName + ": " + args[i] + ": No such file or directory");
					}
				}
			}
		}
	}
}
