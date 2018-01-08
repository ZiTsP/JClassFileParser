package zitsp.apps.classfileparser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class ClassParserMain {
	private static final String EXT_CLASS = ".class";
	private static final String EXT_CLASS_PARSED_TEXT = ".pctxt";
	
	
	public static void main(String[] args) {
		ArgOption opt = argParse(args);
		for (String file : opt.classFiles) {
			ClassFileParser parser = new ClassFileParser(file);
			if (opt.test == true) {
				parser.parseTest();
			} else {
				if (opt.auto == true) {
					StringBuilder sb = new StringBuilder();
					sb.append(file.substring(0, file.length() - EXT_CLASS.length()));
					sb.append(EXT_CLASS_PARSED_TEXT);
					parser.setOutput(sb.toString(), true);
				} else {
					opt.outPath.ifPresent(out -> parser.setOutput(out));
				}
				if (opt.auto || (!opt.debugOut && opt.byteOut)) {
					parser.enableOutputOnlyParsed();
				}
				parser.parsePrint();
			}
		}
	}

	private static final String OPT_PREF = "-";

	private static final String OPT_AUTO = "-a";
	private static final String LOPT_AUTO = "--auto";

	private static final String OPT_BYTEOUT = "-b";
	private static final String LOPT_BYTEOUT = "--byte";
	private static final String OPT_DEBUGOUT = "-d";
	private static final String LOPT_DEBUGOUT = "--debug";

	private static final String OPT_OUTPUT = "-o";
	private static final String LOPT_OUTPUT = "--output";

	private static final String OPT_TEST = "-t";
	private static final String LOPT_TEST = "--test";
	
	private static ArgOption argParse(String[] args) {
		ArgOption argOpt = new ArgOption();
		for (int i = 0; i < args.length; i += 1) {
			if (args[i].startsWith(OPT_PREF)) {
				if (args[i].equals(OPT_AUTO) || args[i].equals(LOPT_AUTO)) {
					argOpt.auto = true;
				} else if (args[i].equals(OPT_BYTEOUT) || args[i].equals(LOPT_BYTEOUT) ) {
					argOpt.byteOut = true;
				} else if (args[i].equals(OPT_DEBUGOUT) || args[i].equals(LOPT_DEBUGOUT) ) {
					argOpt.debugOut = true;
				} else if (args[i].equals(OPT_OUTPUT) || args[i].equals(LOPT_OUTPUT) ) {
					i += 1;
					Path outPath = Paths.get(args[i]);
					argOpt.outPath = Optional.of(outPath);
				} else if (args[i].equals(OPT_TEST) || args[i].equals(LOPT_TEST) ) {
					argOpt.test = true;
				}
			} else if (args[i].endsWith(EXT_CLASS) && Files.exists(Paths.get(args[i]))) {
				argOpt.classFiles.add(args[i]);
			}
		}
		return argOpt;
	}
	
	private static class ArgOption {
		private boolean auto = false;
		private boolean byteOut = false;
		private boolean debugOut = false;
		private Optional<Path> outPath = Optional.empty();
		private boolean test = false;
		private List<String> classFiles = new ArrayList<>();
		private ArgOption() {
		}
	}
	
}