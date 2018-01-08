package zitsp.apps.classfileparser;

import java.util.Optional;

import org.apache.bcel.classfile.ElementValue;

public class ElementValueNames {
	
	protected static final String NAME = "element_value ";

	protected static final String TAG = "tag ";
	protected static final String CPOOL_INDEX = "cpool_index ";
	protected static final String OFFSET = "offset ";

	protected static final String CONST_VAL_INDEX = "const_value_index";
	protected static final String ENUMN_CONST_VAL = "enum_const_value";
	protected static final String TYPE_NAME_INDEX = "type_name_index";
	protected static final String CONST_NAME_INDEX = "const_name_index";
	protected static final String CLASS_INFO_INDEX = "class_info_index";
	protected static final String ANNOTATION_VAL = "annotation_value";
	protected static final String ARRAY_VAL = "array_value";
	protected static final String NUM_VAL = "num_values";
	protected static final String VALS = "values";
    
	public enum Type {
		BYTE("byte ", ElementValue.PRIMITIVE_BYTE, CONST_VAL_INDEX),
		CHAR("char ", ElementValue.PRIMITIVE_BYTE, CONST_VAL_INDEX),
		DOUBLE("double ", ElementValue.PRIMITIVE_BYTE, CONST_VAL_INDEX),
		FLOAT("float ", ElementValue.PRIMITIVE_BYTE, CONST_VAL_INDEX),
		INT("int ", ElementValue.PRIMITIVE_BYTE, CONST_VAL_INDEX),
		LONG("long ", ElementValue.PRIMITIVE_BYTE, CONST_VAL_INDEX),
		SHORT("short ", ElementValue.PRIMITIVE_BYTE, CONST_VAL_INDEX),
		BOOLEAN("boolean ", ElementValue.PRIMITIVE_BYTE, CONST_VAL_INDEX),
		STRING("String ", ElementValue.STRING, CONST_VAL_INDEX),
		ENUM("Enum_type ", ElementValue.ENUM_CONSTANT, TYPE_NAME_INDEX, CONST_NAME_INDEX),
		CLASS("Class ", ElementValue.CLASS, CLASS_INFO_INDEX),
		ANNOTATION("Annotation_type ", ElementValue.ANNOTATION, ANNOTATION_VAL),
		ARRAY("Array_type ", ElementValue.ARRAY, NUM_VAL, VALS),
		;

		private final String typeName;
		private final byte tag;
		private final String[] typeItems;
		private Type(String name, int tag, String... itemNames) {
			this.typeName = name;
			this.tag = (byte) tag;
			this.typeItems = itemNames;
		}

		public String getName() {
			return typeName;
		}

		public byte getTag() {
			return tag;
		}

		public String[] getItemNames() {
			return typeItems;
		}

		public String getItemName(int index) {
			return (index < typeItems.length) ? typeItems[index] : "";
		}
	}

	public static Optional<Type> getType(int tag) {
		for (Type type : Type.values()) {
			if (tag == type.tag) {
				return Optional.of(type);
			}
		}
		return Optional.empty();
	}

}
