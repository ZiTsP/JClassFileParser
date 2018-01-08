package zitsp.apps.classfileparser;

import java.util.Optional;

public class AttributeNames {
	protected static final String ATTR_NAME_INDEX = "attribute_name_index ";
	protected static final String ATTR_LEN = "attribute_length ";

	protected static final String CONSTANTVALUE_INDEX = "constantvalue_index ";

	protected static final String MAX_STACK = "max_stack ";
	protected static final String MAX_LOCALS = "max_locals ";
	protected static final String CODE_LEN = "code_length ";
	protected static final String CODE_STR = "code ";
	protected static final String EXCEPTION_TABLE_LEN = "exception_table_length ";
	protected static final String EXCEPTION_TABLE = "exception_table_ ";
	protected static final String ATTR_COUNTS = "attributes_count ";
	protected static final String ATTRS = "attributes ";

	protected static final String NUM_OF_ENTRIES = "number_of_entries ";
	protected static final String ENTRIES = "entries ";
	protected static final String STACK_MAP_FRAME = "stack_map_frame ";

	protected static final String NUM_OF_EXSEPTION = "number_of_exception ";
	protected static final String EXCEPTION_INDEX_TABLE = "exception_index_table ";

	protected static final String NUM_OF_CLASSES = "number_of_classes ";
	protected static final String CLASSES = "classes ";

	protected static final String CLASS_INDEX = "class_index ";
	protected static final String METHOD_INDEX = "method_index ";

	protected static final String SIGNATURE_INDEX = "signature_index ";

	protected static final String SRCFILE_INDEX = "sourcefile_index ";

	protected static final String LINE_NUM_TABLE_LEN = "line_number_table_length ";
	protected static final String LINE_NUM_TABLE = "line_number_table ";

	protected static final String LOCAL_VAR_TABLE_LEN = "local_variable_table_length ";
	protected static final String LOCAL_VAR_TABLE = "local_variable_table ";

	protected static final String LOCAL_VAR_TYPE_TABLE_LEN = "local_variable_type_table_length ";
	protected static final String LOCAL_VAR_TYPE_TABLE = "local_variable_type_table ";

	protected static final String NUM_ANNOTATIONS = "num_annotations ";
	protected static final String ANNOTATIONS = "annotations ";

	protected static final String NUM_PARAMS = "num_parameters ";
	protected static final String PARAM_ANNOTATIONS = "parameter_annoations ";

	protected static final String DEFAULT_VALUE = "default_value ";

	protected static final String NUM_BOOTSTRAP_METHOD = "num_bootstrap_methods ";
	protected static final String BOOTSTRAP_METHOD = "bootstrap_methods ";

	protected static final String PARAM_COUNT= "parameters_count ";
	protected static final String PARAMS = "parameters ";

	protected static final String PMG_INDEX = "pmg_index ";
	protected static final String PMG_CLASS_INDEX = "pmg_class_index ";

	protected static final String UNK_ATTR_DATA = "unknown_attribute_data ";

	public enum Type {

		CONSTANT_VALUE("ConstantValue",
				ATTR_NAME_INDEX, ATTR_LEN,
				CONSTANTVALUE_INDEX),
		CODE("Code",
				ATTR_NAME_INDEX, ATTR_LEN,
				MAX_STACK, MAX_LOCALS, CODE_LEN, CODE_STR,
				EXCEPTION_TABLE_LEN, EXCEPTION_TABLE, ATTR_COUNTS, ATTRS),
		STACK_MAP_TABLE("StackMapTable",
				ATTR_NAME_INDEX, ATTR_LEN,
				NUM_OF_ENTRIES, ENTRIES),
		EXCEPTION("Exceptions",
				ATTR_NAME_INDEX, ATTR_LEN,
				NUM_OF_EXSEPTION, EXCEPTION_INDEX_TABLE),
		INNER_CLASSES("InnerClasses",
				ATTR_NAME_INDEX, ATTR_LEN,
				NUM_OF_CLASSES, CLASSES),
		ENCLOSING_METHOD("EnclosingMethod",
				ATTR_NAME_INDEX, ATTR_LEN,
				CLASS_INDEX, METHOD_INDEX),
		SYNTHETIC("Synthetic",
				ATTR_NAME_INDEX, ATTR_LEN),
		SIGNATURE("Signature",
				ATTR_NAME_INDEX, ATTR_LEN,
				SIGNATURE_INDEX),
		SOURCE_FILE("SourceFile",
				ATTR_NAME_INDEX, ATTR_LEN,
				SRCFILE_INDEX),
		LINE_NUMBER_TABEL("LineNumberTable",
				ATTR_NAME_INDEX, ATTR_LEN,
				LINE_NUM_TABLE_LEN, LINE_NUM_TABLE),
		LOCAL_VARIABLE_TABLE("LocalVariableTable",
				ATTR_NAME_INDEX, ATTR_LEN,
				LOCAL_VAR_TABLE_LEN, LOCAL_VAR_TABLE),
		LOCAL_VARIABLE_TYPE_TABLE("LocalVariableTypeTable",
				ATTR_NAME_INDEX, ATTR_LEN,
				LOCAL_VAR_TYPE_TABLE_LEN, LOCAL_VAR_TYPE_TABLE),
		DEPRECATED("Deprecated",
				ATTR_NAME_INDEX, ATTR_LEN),
		RUNTIME_VISIBLE_ANNOTATIONS("RuntimeVisibleAnnotations",
				ATTR_NAME_INDEX, ATTR_LEN,
				NUM_ANNOTATIONS, ANNOTATIONS),
		RUNTIME_INVISIBLE_ANNOTATIONS("RuntimeInvisibleAnnotations",
				ATTR_NAME_INDEX, ATTR_LEN,
				NUM_ANNOTATIONS, ANNOTATIONS),
		RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS("RuntimeVisibleParameterAnnotations",
				ATTR_NAME_INDEX, ATTR_LEN,
				NUM_PARAMS, PARAM_ANNOTATIONS, NUM_ANNOTATIONS, ANNOTATIONS),
		RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS("RuntimeInvisibleParameterAnnotations",
				ATTR_NAME_INDEX, ATTR_LEN,
				NUM_PARAMS, PARAM_ANNOTATIONS, NUM_ANNOTATIONS, ANNOTATIONS),
		ANNOTATION_DEFAULT("AnnotationDefault",
				ATTR_NAME_INDEX, ATTR_LEN, DEFAULT_VALUE),
		BOOTSTRAP_METHODS("BootstrapMethods",
				ATTR_NAME_INDEX, ATTR_LEN,
				NUM_BOOTSTRAP_METHOD, BOOTSTRAP_METHOD),
		METHOD_PARAMETERS("MethodParameters",
				ATTR_NAME_INDEX, ATTR_LEN,
				PARAM_COUNT, PARAMS),
		PMG_CLASS("PMGClass",
				ATTR_NAME_INDEX, ATTR_LEN,
				PMG_INDEX, PMG_CLASS_INDEX),
		UNKNOWN("Unknown",
				ATTR_NAME_INDEX, ATTR_LEN, UNK_ATTR_DATA);

		private final String typeName;
			private final String[] typeItems;
			private Type(String name, String... itemNames) {
				this.typeName = name;
				this.typeItems = itemNames;
			}

			public String getTypeName() {
				return typeName;
			}

			public String[] getTypeItemNames() {
				return typeItems;
			}

			public String getTypeItemName(int index) {
				return (index < typeItems.length) ? typeItems[index] : "";
			}
	}

	public static Optional<Type> getType(String name) {
		for (Type t : Type.values()) {
			if (name.equals(t.getTypeName())) {
				return Optional.of(t);
			}
		}
		return Optional.of(Type.UNKNOWN);
	}




}
