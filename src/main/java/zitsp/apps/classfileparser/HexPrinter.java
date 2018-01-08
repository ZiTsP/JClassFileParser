package zitsp.apps.classfileparser;

import java.io.FileNotFoundException;
import java.io.PrintStream;

public class HexPrinter implements AutoCloseable {
	
	private final PrintStream out;

	private final boolean printToFile;
	
	private static final boolean TEXT_PRINTING_DEFAULT = true;
	private boolean enableTextPrint;

	private static final String INDENT_STYLE = "  ";
	private int indent = 0;
	private String indentText = INDENT_STYLE;
	
	public HexPrinter() {
		this(TEXT_PRINTING_DEFAULT);
	}
	
	public HexPrinter(boolean enableTextPrint) {
		this(System.out, false, enableTextPrint);
	}
	
	public HexPrinter(String file) throws FileNotFoundException {
		this(file, TEXT_PRINTING_DEFAULT);
	}
	
	public HexPrinter(String file, boolean enableTextPrint) throws FileNotFoundException {
		this(new PrintStream(file), true, enableTextPrint);
	}
	
	private HexPrinter(PrintStream out, boolean printToFile, boolean enableTextPrint) {
		this.out = out;
		this.printToFile = printToFile;
		this.enableTextPrint = enableTextPrint;
	}
	
	@Override
	public void close() throws Exception {
		if (printToFile) {
			out.close();
		}
	}

	public void setEnableTextPrint(boolean enable) {
		this.enableTextPrint = enable;
	}
	
	public HexPrinter setIndentText(String indentText) {
		this.indentText = indentText;
		return this;
	}
	
	public HexPrinter setIndent(int indent) {
		this.indent = indent;
		return this;
	}

	public HexPrinter indentUp() {
		return indentUp(1);
	}
	
	public HexPrinter indentUp(int up) {
		indent -= up;
		return this;
	}
	
	public HexPrinter indentDown() {
		return indentDown(1);
	}
	
	public HexPrinter indentDown(int down) {
		indent += down;
		return this;
	}
	
	private int lineLength = 0;

	private String printIndent() {
		StringBuilder sb = new StringBuilder();
		if (lineLength == 0) {
			for (int i = 0; i < indent; i += 1) {
				sb.append(indentText);
			}
		}
		lineLength += 1;
		return sb.toString();
	}
	
	public HexPrinter write(String str) {
		StringBuilder sb = new StringBuilder();
		sb.append(printIndent());
		sb.append(str);
		out.print(sb.toString());
		return this;
	}
	
	public HexPrinter writeln(String str) {
		StringBuilder sb = new StringBuilder();
		sb.append(printIndent());
		sb.append(str);
		out.println(sb.toString());
		lineLength = 0;
		return this;
	}
	
	public HexPrinter print() {
		return write(" ");
	}
	
	public HexPrinter println() {
		lineLength += 1;
		write("\n");
		lineLength = 0;
		return this;
	}
	
	public HexPrinter print(byte val) {
		return write(String.format("%02x", val));
	}
	
	public HexPrinter print(int val) {
		return write(String.format("%08x", val));
	}
	
	public HexPrinter print(short val) {
		return write(String.format("%04x", val));
	}
	
	public HexPrinter print(long val) {
		return write(String.format("%016x", val));
	}
	
	public HexPrinter print(float val) {
		return write(String.format("%08x", Float.floatToRawIntBits(val)));
	}
	
	public HexPrinter print(double val) {
		return write(String.format("%016x", Double.doubleToRawLongBits(val)));
	}

	public HexPrinter print(String val) {
		return print(val.getBytes());
	}
	
	public HexPrinter print(byte[] val) {
		StringBuilder sb = new StringBuilder();
		for (byte b : val) {
			sb.append(String.format("%02x", b));
		}
		return write(sb.toString());
	}
	
	public HexPrinter println(byte val) {
		return writeln(String.format("%02x", val));
	}
	
	public HexPrinter println(int val) {
		return writeln(String.format("%08x", val));
	}
	
	public HexPrinter println(short val) {
		return writeln(String.format("%04x", val));
	}
	
	public HexPrinter println(long val) {
		return writeln(String.format("%016x", val));
	}
	
	public HexPrinter println(float val) {
		return writeln(String.format("%08x", Float.floatToRawIntBits(val)));
	}
	
	public HexPrinter println(double val) {
		return writeln(String.format("%016x", Double.doubleToRawLongBits(val)));
	}

	public HexPrinter println(String val) {
		return println(val.getBytes());
	}
	
	public HexPrinter println(byte[] val) {
		StringBuilder sb = new StringBuilder();
		for (byte b : val) {
			sb.append(String.format("%02x", b));
		}
		return writeln(sb.toString());
	}

	public HexPrinter textPrint() {
		return textPrint(" ");
	}
	
	public HexPrinter textPrint(String str) {
		if (enableTextPrint) {
			write(str);
		}
		return this;
	}

	public HexPrinter textPrint(String... strs) {
		StringBuilder sb = new StringBuilder();
		for (String s : strs) {
			sb.append(s);
		}
		return textPrint(sb.toString());
	}
	
	public HexPrinter textPrintln() {
		return textPrint("\n");
	}
	
	public HexPrinter textPrintln(String str) {
		if (enableTextPrint) {
			writeln(str);
		}
		return this;
	}

	public HexPrinter textPrintln(String... strs) {
		StringBuilder sb = new StringBuilder();
		for (String s : strs) {
			sb.append(s);
		}
		return textPrintln(sb.toString());
	}
}
