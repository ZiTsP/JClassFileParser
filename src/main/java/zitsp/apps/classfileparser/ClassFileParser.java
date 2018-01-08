package zitsp.apps.classfileparser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.IntStream;

import org.apache.bcel.classfile.AnnotationDefault;
import org.apache.bcel.classfile.AnnotationElementValue;
import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.Annotations;
import org.apache.bcel.classfile.ArrayElementValue;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.BootstrapMethods;
import org.apache.bcel.classfile.ClassElementValue;
import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantDouble;
import org.apache.bcel.classfile.ConstantFieldref;
import org.apache.bcel.classfile.ConstantFloat;
import org.apache.bcel.classfile.ConstantInteger;
import org.apache.bcel.classfile.ConstantInterfaceMethodref;
import org.apache.bcel.classfile.ConstantInvokeDynamic;
import org.apache.bcel.classfile.ConstantLong;
import org.apache.bcel.classfile.ConstantMethodHandle;
import org.apache.bcel.classfile.ConstantMethodType;
import org.apache.bcel.classfile.ConstantMethodref;
import org.apache.bcel.classfile.ConstantNameAndType;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantString;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.ConstantValue;
import org.apache.bcel.classfile.ElementValue;
import org.apache.bcel.classfile.EnclosingMethod;
import org.apache.bcel.classfile.EnumElementValue;
import org.apache.bcel.classfile.ExceptionTable;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.InnerClasses;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.LineNumberTable;
import org.apache.bcel.classfile.LocalVariableTable;
import org.apache.bcel.classfile.LocalVariableTypeTable;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.classfile.MethodParameters;
import org.apache.bcel.classfile.PMGClass;
import org.apache.bcel.classfile.ParameterAnnotationEntry;
import org.apache.bcel.classfile.ParameterAnnotations;
import org.apache.bcel.classfile.Signature;
import org.apache.bcel.classfile.SimpleElementValue;
import org.apache.bcel.classfile.SourceFile;
import org.apache.bcel.classfile.StackMap;
import org.apache.bcel.classfile.StackMapEntry;
import org.apache.bcel.classfile.StackMapType;
import org.apache.bcel.classfile.Unknown;

public class ClassFileParser {

	private static final String BEGIN = "{";
	private static final String END = "}";
	private static final String SPACE = " ";

	private Optional<JavaClass> javaClass = Optional.empty();
	private Optional<String> outputFile = Optional.empty();

	public ClassFileParser(String classFile) {
		ClassParser parser = new ClassParser(classFile);
		try {
			javaClass = Optional.of(parser.parse());
		} catch (ClassFormatException | IOException e) {
			e.printStackTrace();
		}
	}

	protected void parseTest() {
		enableOutputOnlyParsed(false);
		this.parsePrint();
	}

	public void setOutput(String outputFile) {
		setOutput(outputFile, false);
	}

	public void setOutput(String outputFile, boolean override) {
		Path path = Paths.get(outputFile);
		setOutput(path, override);
	}

	public void setOutput(Path outputFile) {
		setOutput(outputFile, false);
	}

	public void setOutput(Path outputFile, boolean override) {
		try {
			if (Files.notExists(outputFile)) {
				Files.createFile(outputFile);
			} else if (override == true) {
				Files.delete(outputFile);
				Files.createFile(outputFile);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.outputFile = Optional.of(outputFile.toAbsolutePath().toString());
	}

	private boolean outputOnlyBytes = false;

	public void enableOutputOnlyParsed() {
		enableOutputOnlyParsed(true);
	}

	public void enableOutputOnlyParsed(boolean enable) {
		this.outputOnlyBytes = enable;
	}

	public void parsePrint() {
		javaClass.ifPresent(jc -> {
			try (HexPrinter out = outputFile.isPresent() ?
					new HexPrinter(outputFile.get()) : new HexPrinter()) {
				out.setEnableTextPrint(outputOnlyBytes == false);
				String[] itemNames = MiscNames.Type.CLASS_FILE.getItemNames();
				out.textPrint(itemNames[0]).println(0xcafebabe);
				out.textPrint(itemNames[1]).println((short) jc.getMinor());
				out.textPrint(itemNames[2]).println((short) jc.getMajor());
				printConsatnPool(out);
				out.textPrint(itemNames[5]).println((short) jc.getAccessFlags());
				out.textPrint(itemNames[6]).println((short) jc.getClassNameIndex());
				out.textPrint(itemNames[7]).println((short) jc.getSuperclassNameIndex());
				printInterFaces(out);
				printFields(out);
				printMethods(out);
				printAttributes(out);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public void printConsatnPool(HexPrinter out) {
		javaClass.ifPresent(jc -> {
			ConstantPool cp = jc.getConstantPool();
			out.textPrint(MiscNames.Type.CLASS_FILE.getItemName(3)).println((short) cp.getLength());
			out.textPrintln(MiscNames.Type.CLASS_FILE.getItemName(4), BEGIN).indentDown();
			for (int i = 1; i < cp.getLength(); i += 1) {
				out.textPrint(String.format("cp#%02d ", i));
				printConstant(cp.getConstant(i), out);
			}
			out.indentUp().textPrintln(END);
		});
	}

	private void printConstant(Constant c, HexPrinter out) {
		if (c == null) {
			return;
		}
		byte tag = c.getTag();
		Optional<ConstantPoolNames.Type> cpType = ConstantPoolNames.getType(tag);
		cpType.ifPresent(type -> {
			out.textPrintln(ConstantPoolNames.NAME, type.getTypeName(), BEGIN).indentDown();
			String[] itemNames = type.getTypeItemNames();
			out.textPrint(itemNames[0]).print(tag);
			if (tag == 1) {
				ConstantUtf8 constant = (ConstantUtf8) c;
				String bytes = constant.getBytes();
				out.textPrint(itemNames[1]).println((short) bytes.getBytes().length);
				out.textPrint(itemNames[2]).print(bytes).textPrintln(String.format("    //%s", bytes.replace("\n", "\\n")));
			} else if (tag == 3) {
				ConstantInteger constant = (ConstantInteger) c;
				out.textPrint(itemNames[1]).println(constant.getBytes());
			} else if (tag == 4) {
				ConstantFloat constant = (ConstantFloat) c;
				out.textPrint(itemNames[1]).println(constant.getBytes());
			} else if (tag == 5) {
				ConstantLong constant = (ConstantLong) c;
				out.textPrint(itemNames[1]).println(constant.getBytes());
			} else if (tag == 6) {
				ConstantDouble constant = (ConstantDouble) c;
				out.textPrint(itemNames[1]).println(constant.getBytes());
			} else if (tag == 7) {
				ConstantClass constant = (ConstantClass) c;
				out.textPrint(itemNames[1]).println((short) constant.getNameIndex());
			} else if (tag == 8) {
				ConstantString constant = (ConstantString) c;
				out.textPrint(itemNames[1]).println((short) constant.getStringIndex());
			} else if (tag == 9) {
				ConstantFieldref constant = (ConstantFieldref) c;
				out.textPrint(itemNames[1]).println((short) constant.getClassIndex());
				out.textPrint(itemNames[2]).println((short) constant.getNameAndTypeIndex());
			} else if (tag == 10) {
				ConstantMethodref constant = (ConstantMethodref) c;
				out.textPrint(itemNames[1]).println((short) constant.getClassIndex());
				out.textPrint(itemNames[2]).println((short) constant.getNameAndTypeIndex());
			} else if (tag == 11) {
				ConstantInterfaceMethodref constant = (ConstantInterfaceMethodref) c;
				out.textPrint(itemNames[1]).println((short) constant.getClassIndex());
				out.textPrint(itemNames[2]).println((short) constant.getNameAndTypeIndex());
			} else if (tag == 12) {
				ConstantNameAndType constant = (ConstantNameAndType) c;
				out.textPrint(itemNames[1]).println((short) constant.getNameIndex());
				out.textPrint(itemNames[2]).println((short) constant.getSignatureIndex());
			} else if (tag == 15) {
				ConstantMethodHandle constant = (ConstantMethodHandle) c;
				out.textPrint(itemNames[1]).println((byte) constant.getReferenceKind());
				out.textPrint(itemNames[2]).println((short) constant.getReferenceIndex());
			} else if (tag == 16) {
				ConstantMethodType constant = (ConstantMethodType) c;
				out.textPrint(itemNames[1]).println((short) constant.getDescriptorIndex());
			} else if (tag == 18) {
				ConstantInvokeDynamic constant = (ConstantInvokeDynamic) c;
				out.textPrint(itemNames[1]).println((short) constant.getBootstrapMethodAttrIndex());
				out.textPrint(itemNames[2]).println((short) constant.getNameAndTypeIndex());
			}
			out.indentUp().textPrintln(END);
		});
	}

	private void printInterFaces(HexPrinter out) {
		javaClass.ifPresent(jc -> {
			int[] interfaces = jc.getInterfaceIndices();
			out.textPrint(MiscNames.Type.CLASS_FILE.getItemName(8)).println((short) interfaces.length);
			if (0 < interfaces.length) {
				out.textPrint(MiscNames.Type.CLASS_FILE.getItemName(9), BEGIN).indentDown();
				IntStream.of(interfaces).forEach(i -> out.println((short) i));
				out.indentUp().textPrintln(END);
			}
		});
	}

	private void printFields(HexPrinter out) {
		javaClass.ifPresent(jc -> {
			Field[] fields = jc.getFields();
			out.textPrint(MiscNames.Type.CLASS_FILE.getItemName(10)).println((short) fields.length);
			if (0 < fields.length) {
				out.textPrintln(MiscNames.Type.CLASS_FILE.getItemName(11), BEGIN).indentDown();
				Arrays.asList(fields).forEach(e -> {
					out.textPrint("access_flags ").println((short) e.getAccessFlags());
					out.textPrint("name_index ").println((short) e.getNameIndex());
					out.textPrint("descriptor_index ").println((short) e.getSignatureIndex());
					out.textPrint("attributes_count ").println((short) e.getAttributes().length);
					for (Attribute attr: e.getAttributes()) {
						printAttributes(attr,out);
					}
				});
				out.indentUp().textPrintln(END);
			}
		});
	}

	private void printMethods(HexPrinter out) {
		javaClass.ifPresent(jc -> {
			Method[] methods = jc.getMethods();
			out.textPrint(MiscNames.Type.CLASS_FILE.getItemName(12)).println((short) methods.length);
			if (0 < methods.length) {
				out.textPrintln(MiscNames.Type.CLASS_FILE.getItemName(13), BEGIN).indentDown();
				Arrays.asList(methods).forEach(e -> {
					out.textPrint("access_flags ").println((short) e.getAccessFlags());
					out.textPrint("name_index ").println((short) e.getNameIndex());
					out.textPrint("descriptor_index ").println((short) e.getSignatureIndex());
					out.textPrint("attributes_count ").println((short) e.getAttributes().length);
					for (Attribute attr: e.getAttributes()) {
						printAttributes(attr, out);
					}
				});
				out.indentUp().textPrintln(END);
			}
		});
	}

	private void printAttributes(HexPrinter out) {
		javaClass.ifPresent(jc -> {
			Attribute[] attributes = jc.getAttributes();
			out.textPrint(MiscNames.Type.CLASS_FILE.getItemName(14)).println((short) attributes.length);
			if (0 < attributes.length) {
				Arrays.asList(attributes).forEach(e -> {
					printAttributes(e, out);
				});
			}
		});
	}

	private void printAttributes(Attribute attr, HexPrinter out) {
		String attrTypeName = attr.getName();
		AttributeNames.getType(attrTypeName).ifPresent(attrType -> {
			out.textPrintln(MiscNames.Type.CLASS_FILE.getItemName(15), attrType.getTypeName(), SPACE, BEGIN).indentDown();
			String[] itemNames = attrType.getTypeItemNames();
			out.textPrint(itemNames[0]).println((short) attr.getNameIndex());
			out.textPrint(itemNames[1]).println(attr.getLength());
			if (attrType == AttributeNames.Type.CONSTANT_VALUE) {
				ConstantValue typedAttr = (ConstantValue) attr;
				out.textPrint(itemNames[2]).println((short) typedAttr.getConstantValueIndex());
			} else if (attrType == AttributeNames.Type.CODE) {
				Code typedAttr = (Code) attr;
				out.textPrint(itemNames[2]).println((short) typedAttr.getMaxStack());
				out.textPrint(itemNames[3]).println((short) typedAttr.getMaxLocals());
				out.textPrint(itemNames[4]).println(typedAttr.getCode().length);
				if (0 < typedAttr.getCode().length) {
					out.textPrint(itemNames[5]).println(typedAttr.getCode());
				}
				out.textPrint(itemNames[6]).println((short) typedAttr.getExceptionTable().length);
				if (0 < typedAttr.getExceptionTable().length) {
					out.textPrintln(itemNames[7], BEGIN).indentDown();
					Arrays.asList(typedAttr.getExceptionTable()).forEach(e -> {
						String[] strs = MiscNames.Type.EXCEPTION_TABLE.getItemNames();
						out.textPrint(strs[0]).println((short) e.getStartPC());
						out.textPrint(strs[1]).println((short) e.getEndPC());
						out.textPrint(strs[2]).println((short) e.getHandlerPC());
						out.textPrint(strs[3]).println((short) e.getCatchType());
					});
					out.indentUp().textPrintln(END);
				}
				out.textPrint(itemNames[8]).println((short) typedAttr.getAttributes().length);
				if (0 < typedAttr.getAttributes().length) {
					out.textPrintln(itemNames[9], BEGIN).indentDown();
					Arrays.asList(typedAttr.getAttributes()).forEach(e -> printAttributes(e, out));
					out.indentUp().textPrintln(END);
				}
			} else if (attrType == AttributeNames.Type.STACK_MAP_TABLE) {
				StackMap typedAttr = (StackMap) attr;
				out.textPrint(itemNames[2]).println((short) typedAttr.getStackMap().length);
				if (0 < typedAttr.getStackMap().length) {
					out.textPrintln(itemNames[3], BEGIN).indentDown();
					Arrays.asList(typedAttr.getStackMap()).forEach(e -> {
						printStackMapFrame(e, out);
					});
					out.indentUp().textPrintln(END);
				}
			} else if (attrType == AttributeNames.Type.EXCEPTION) {
				ExceptionTable typedAttr = (ExceptionTable) attr;
				out.textPrint(itemNames[2]).println((short) typedAttr.getNumberOfExceptions());
				if (0 < typedAttr.getNumberOfExceptions()) {
					out.textPrintln(itemNames[3], BEGIN);
					out.indentDown();
					IntStream.of(typedAttr.getExceptionIndexTable()).forEach(e -> out.println((short) e));
					out.indentUp().textPrintln(END);
				}
			} else if (attrType == AttributeNames.Type.INNER_CLASSES) {
				InnerClasses typedAttr = (InnerClasses) attr;
				out.textPrint(itemNames[2]).println((short) typedAttr.getInnerClasses().length);
				if (0 < typedAttr.getInnerClasses().length) {
					String[] strs = MiscNames.Type.INNER_CLASSES.getItemNames();
					out.textPrintln(itemNames[3], BEGIN).indentDown();
					Arrays.asList(typedAttr.getInnerClasses()).forEach(e -> {
						out.textPrint(strs[0]).println((short) e.getInnerClassIndex());
						out.textPrint(strs[1]).println((short) e.getOuterClassIndex());
						out.textPrint(strs[2]).println((short) e.getInnerNameIndex());
						out.textPrint(strs[3]).println((short) e.getInnerAccessFlags());
					});
					out.indentUp().textPrintln(END);
				}
			} else if (attrType == AttributeNames.Type.ENCLOSING_METHOD) {
				EnclosingMethod typedAttr = (EnclosingMethod) attr;
				out.textPrint(itemNames[2]).println((short) typedAttr.getEnclosingClassIndex());
				out.textPrint(itemNames[3]).println((short) typedAttr.getEnclosingMethodIndex());
			} else if (attrType == AttributeNames.Type.SYNTHETIC ||
					attrType == AttributeNames.Type.DEPRECATED) {
			} else if (attrType == AttributeNames.Type.SIGNATURE) {
				Signature typedAttr = (Signature) attr;
				out.textPrint(itemNames[2]).println((short) typedAttr.getSignatureIndex());
			} else if (attrType == AttributeNames.Type.SOURCE_FILE) {
				SourceFile typedAttr = (SourceFile) attr;
				out.textPrint(itemNames[2]).println((short) typedAttr.getSourceFileIndex());
			} else if (attrType == AttributeNames.Type.LINE_NUMBER_TABEL) {
				LineNumberTable typedAttr = (LineNumberTable) attr;
				out.textPrint(itemNames[2]).println((short) typedAttr.getLineNumberTable().length);
				if (0 < typedAttr.getLineNumberTable().length) {
					out.textPrintln(itemNames[3], BEGIN).indentDown();
					String[] strs = MiscNames.Type.LINE_NUM_TABLE.getItemNames();
					Arrays.asList(typedAttr.getLineNumberTable()).forEach(e -> {
						out.textPrint(strs[0]).println((short) e.getStartPC());
						out.textPrint(strs[1]).println((short) e.getLineNumber());
					});
					out.indentUp().textPrintln(END);
				}
			} else if (attrType == AttributeNames.Type.LOCAL_VARIABLE_TABLE) {
				LocalVariableTable typedAttr = (LocalVariableTable) attr;
				out.textPrint(itemNames[2]).println((short) typedAttr.getLocalVariableTable().length);
				if (0 < typedAttr.getLocalVariableTable().length) {
					out.textPrintln(itemNames[3], BEGIN).indentDown();
					String[] strs = MiscNames.Type.LOCAL_VAL_TABLE.getItemNames();
					Arrays.asList(typedAttr.getLocalVariableTable()).forEach(e -> {
						out.textPrint(strs[0]).println((short) e.getStartPC());
						out.textPrint(strs[1]).println((short) e.getLength());
						out.textPrint(strs[2]).println((short) e.getNameIndex());
						out.textPrint(strs[3]).println((short) e.getSignatureIndex());
						out.textPrint(strs[4]).println((short) e.getIndex());
					});
					out.indentUp().textPrintln(END);
				}
			} else if (attrType == AttributeNames.Type.LOCAL_VARIABLE_TYPE_TABLE) {
				LocalVariableTypeTable typedAttr = (LocalVariableTypeTable) attr;
				out.textPrint(itemNames[2]).println((short) typedAttr.getLocalVariableTypeTable().length);
				if (0 < typedAttr.getLocalVariableTypeTable().length) {
					out.textPrintln(itemNames[3], BEGIN).indentDown();
					String[] strs = MiscNames.Type.LOCAL_VAL_TABLE.getItemNames();
					Arrays.asList(typedAttr.getLocalVariableTypeTable()).forEach(e -> {
						out.textPrint(strs[0]).println((short) e.getStartPC());
						out.textPrint(strs[1]).println((short) e.getLength());
						out.textPrint(strs[2]).println((short) e.getNameIndex());
						out.textPrint(strs[3]).println((short) e.getSignatureIndex());
						out.textPrint(strs[4]).println((short) e.getIndex());
					});
					out.indentUp().textPrintln(END);
				}
			} else if (attrType == AttributeNames.Type.RUNTIME_VISIBLE_ANNOTATIONS ||
					attrType == AttributeNames.Type.RUNTIME_INVISIBLE_ANNOTATIONS) {
				Annotations typedAttr = (Annotations) attr;
				out.textPrint(itemNames[2]).println((short) typedAttr.getNumAnnotations());
				if (0 < typedAttr.getNumAnnotations()) {
					out.textPrintln(itemNames[3], BEGIN).indentDown();
					Arrays.asList(typedAttr.getAnnotationEntries()).forEach(e -> printAnnoation(e, out));
					out.indentUp().textPrintln(END);
				}
			} else if (attrType == AttributeNames.Type.RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS ||
					attrType == AttributeNames.Type.RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS) {
				ParameterAnnotations typedAttr = (ParameterAnnotations) attr;
				out.textPrint(itemNames[2]).println((short) typedAttr.getParameterAnnotationEntries().length);
				if (0 < typedAttr.getParameterAnnotationEntries().length) {
					out.textPrintln(itemNames[3], BEGIN).indentDown();
					for (ParameterAnnotationEntry entry : typedAttr.getParameterAnnotationEntries()) {
						out.textPrint(itemNames[4]).println((short) entry.getAnnotationEntries().length);
						if (0 < entry.getAnnotationEntries().length) {
							out.textPrintln(itemNames[5], BEGIN).indentDown();
							Arrays.asList(entry.getAnnotationEntries()).forEach(e -> printAnnoation(e, out));
							out.indentUp().textPrintln(END);
						}
					}
					out.indentUp().textPrintln(END);
				}
			} else if (attrType == AttributeNames.Type.ANNOTATION_DEFAULT) {
				AnnotationDefault typedAttr = (AnnotationDefault) attr;
				out.textPrintln(itemNames[2], BEGIN).indentDown();
				printElementValue(typedAttr.getDefaultValue(), out);
				out.indentUp().textPrintln(END);
			} else if (attrType == AttributeNames.Type.BOOTSTRAP_METHODS) {
				BootstrapMethods typedAttr = (BootstrapMethods) attr;
				out.textPrint(itemNames[2]).println((short) typedAttr.getBootstrapMethods().length);
				if (0 < typedAttr.getBootstrapMethods().length) {
					out.textPrintln(itemNames[3], BEGIN).indentDown();
					String[] strs = MiscNames.Type.BOOTSTRAP_METHOD.getItemNames();
					Arrays.asList(typedAttr.getBootstrapMethods()).forEach(e -> {
						out.textPrint(strs[0]).println((short) e.getBootstrapMethodRef());
						out.textPrint(strs[1]).println((short) e.getBootstrapArguments().length);
						if (0 < e.getBootstrapArguments().length) {
							out.textPrintln(strs[2], BEGIN).indentDown();
							IntStream.of(e.getBootstrapArguments()).forEach(v -> {
								out.println((short) v);
							});
							out.indentUp().textPrintln(END);
						}
					});
					out.indentUp().textPrintln(END);
				}
			} else if (attrType == AttributeNames.Type.METHOD_PARAMETERS) {
				MethodParameters typedAttr = (MethodParameters) attr;
				out.textPrint(itemNames[2]).println((short) typedAttr.getParameters().length);
				if (0 < typedAttr.getParameters().length) {
					out.textPrintln(itemNames[3], BEGIN).indentDown();
					String[] strs = MiscNames.Type.METHOD_PARAMS.getItemNames();
					Arrays.asList(typedAttr.getParameters()).forEach(e -> {
						out.textPrint(strs[0]).println((short) e.getNameIndex());
						out.textPrint(strs[1]).println((short) e.getAccessFlags());
					});
					out.indentUp().textPrintln(END);
				}
			} else if (attrType == AttributeNames.Type.PMG_CLASS) {
				PMGClass typedAttr = (PMGClass) attr;
				out.textPrint(itemNames[2]).println((short) typedAttr.getPMGIndex());
				out.textPrint(itemNames[3]).println((short) typedAttr.getPMGClassIndex());
			} else if (attrType == AttributeNames.Type.UNKNOWN) {
				Unknown typedAttr = (Unknown) attr;
				out.textPrint(itemNames[2]).println(typedAttr.getBytes());
			}
			out.indentUp().textPrintln(END);
		});
	}

	private void printAnnoation(AnnotationEntry entry, HexPrinter out) {
		String[] strs = MiscNames.Type.ANNOTATION.getItemNames();
		out.textPrint(strs[0]).println((short) entry.getAnnotationTypeIndex());
		out.textPrint(strs[1]).println((short) entry.getNumElementValuePairs());
		if (0 < entry.getNumElementValuePairs()) {
			out.textPrintln(strs[2], BEGIN).indentDown();
			Arrays.asList(entry.getElementValuePairs()).forEach(e -> {
				out.textPrint(strs[0]).println((short) e.getNameIndex());
				printElementValue(e.getValue(), out);
			});
			out.indentUp().textPrintln(END);
		}
	}

	private void printElementValue(ElementValue entry, HexPrinter out) {
		ElementValueNames.getType(entry.getElementValueType()).ifPresent(type -> {
			out.textPrintln(StackMapFrameNames.NAME, type.getName(), SPACE, BEGIN).indentDown();
			String[] itemNames = type.getItemNames();
			out.textPrint(itemNames[0]).println(type.getTag());
			if ((type == ElementValueNames.Type.BYTE)
					|| (type == ElementValueNames.Type.CHAR)
					|| (type == ElementValueNames.Type.DOUBLE)
					|| (type == ElementValueNames.Type.FLOAT)
					|| (type == ElementValueNames.Type.INT)
					|| (type == ElementValueNames.Type.LONG)
					|| (type == ElementValueNames.Type.SHORT)
					|| (type == ElementValueNames.Type.BOOLEAN)
					|| (type == ElementValueNames.Type.STRING)) {
				SimpleElementValue elem = (SimpleElementValue) entry;
				out.textPrint(itemNames[1]).println((short) elem.getIndex());
			} else if (type == ElementValueNames.Type.ENUM) {
				EnumElementValue elem = (EnumElementValue) entry;
				out.textPrint(itemNames[1]).println((short) elem.getTypeIndex());
				out.textPrint(itemNames[2]).println((short) elem.getValueIndex());
			} else if (type == ElementValueNames.Type.CLASS) {
				ClassElementValue elem = (ClassElementValue) entry;
				out.textPrint(itemNames[1]).println((short) elem.getIndex());
			} else if (type == ElementValueNames.Type.ANNOTATION) {
				AnnotationElementValue elem = (AnnotationElementValue) entry;
				printAnnoation(elem.getAnnotationEntry(), out);
			} else if (type == ElementValueNames.Type.ARRAY) {
				ArrayElementValue elem = (ArrayElementValue) entry;
				out.textPrint(itemNames[1]).println((short) elem.getElementValuesArray().length);
				if (0 < elem.getElementValuesArray().length) {
					out.textPrintln(itemNames[2], BEGIN).indentDown();
					Arrays.asList(elem.getElementValuesArray()).forEach(e -> printElementValue(e, out));
					out.indentUp().textPrintln(END);
				}
			}
			out.indentUp().textPrintln(END);
		});
	}

	private void printStackMapFrame(StackMapEntry entry, HexPrinter out) {
		int frameType = entry.getFrameType();
		StackMapFrameNames.getType(frameType).ifPresent(frame -> {
			out.textPrintln(StackMapFrameNames.NAME, frame.getName(), SPACE, BEGIN).indentDown();
			String[] itemNames = frame.getItemNames();
			out.textPrint(itemNames[0]).println((byte) frameType);
			if (frame == StackMapFrameNames.Type.SAME) {
			} else if (frame == StackMapFrameNames.Type.SAME_LOCAL_A_STACK) {
				out.textPrintln(itemNames[1], BEGIN).indentDown();
				printVerificationType(entry.getTypesOfStackItems(), 1, out);
				out.indentUp().textPrintln(END);
			} else if (frame == StackMapFrameNames.Type.SAME_LOCAL_A_STACK_EXTENDED) {
				out.textPrint(itemNames[1]).println((short) entry.getByteCodeOffset());
				out.textPrintln(itemNames[2], BEGIN).indentDown();
				printVerificationType(entry.getTypesOfStackItems(), 1, out);
				out.indentUp().textPrintln(END);
			} else if ((frame == StackMapFrameNames.Type.CHOP)
					|| (frame == StackMapFrameNames.Type.SAME_EXTENDES)) {
				out.textPrint(itemNames[1]).println((short) entry.getByteCodeOffset());
			} else if (frame == StackMapFrameNames.Type.APPEND) {
				out.textPrint(itemNames[1]).println((short) entry.getByteCodeOffset());
				out.textPrintln(itemNames[2], BEGIN).indentDown();
				printVerificationType(entry.getTypesOfLocals(), frameType - 251, out);
				out.indentUp().textPrintln(END);
			} else if (frame == StackMapFrameNames.Type.FULL) {
				out.textPrint(itemNames[1]).println((short) entry.getByteCodeOffset());
				out.textPrint(itemNames[2]).println((short) entry.getNumberOfLocals());
				out.textPrintln(itemNames[3], BEGIN).indentDown();
				printVerificationType(entry.getTypesOfLocals(), entry.getNumberOfLocals(), out);
				out.indentUp().textPrintln(END);
				out.textPrint(itemNames[4]).println((short) entry.getNumberOfStackItems());
				out.textPrintln(itemNames[5], BEGIN).indentDown();
				printVerificationType(entry.getTypesOfStackItems(), entry.getNumberOfStackItems(), out);
				out.indentUp().textPrintln(END);
			}
		});
	}

	private static void printVerificationType(StackMapType[] stackMapTypes, int size, HexPrinter out) {
		for (int i = 0; i < size; i += 1) {
			StackMapType stackMapType = stackMapTypes[i];
			VerificationTypeNmaes.getType(stackMapType.getType()).ifPresent(type -> {
				out.textPrintln(VerificationTypeNmaes.NAME, type.getName(), SPACE, BEGIN).indentDown();
				String[] itemNames = type.getItemNames();
				out.textPrint(itemNames[0]).println(type.getTag());
				if ((type == VerificationTypeNmaes.Type.OBJECT) ||
						(type == VerificationTypeNmaes.Type.UNINIT) ) {
					out.textPrint(itemNames[1]).println((short) stackMapType.getIndex());
				}
				out.indentUp().textPrintln(END);
			});
		}
	}

}
