package zitsp.apps.classfileparser;

import java.util.Optional;

public class ConstantPoolNames {
	
	protected static final String NAME = "const_type ";
	
	protected static final String TAG = "tag ";
	protected static final String NAME_INDEX = "name_index ";
	protected static final String CLASS_INDEX = "class_index ";
	protected static final String NAME_AND_TYPE_INDEX = "name_and_type_index ";
	protected static final String STRING_INDEX = "string_index ";
	protected static final String DESCRIPTOR_INDEX = "descriptor_index ";
	protected static final String BYTES = "bytes ";
	protected static final String LENGTH = "length ";
	protected static final String REF_KIND = "reference_kind ";
	protected static final String REF_INDEX = "reference_index ";
	protected static final String BOOTSTRAP_METHOD_ATTR_INDEX = "bootstrap_method_attr_index ";

	
	public enum Type {
		UTF8("CONSTANT_Utf8",
				TAG, LENGTH, BYTES),
		INTEGER("CONSTANT_Integer",
				TAG, BYTES),
		FLOAT("CONSTANT_Float",
				TAG, BYTES),
		LONG("CONSTANT_Long",
				TAG, BYTES),
		DOUBLE("CONSTANT_Double",
				TAG, BYTES),
		CLASS("CONSTANT_Class",
				TAG, NAME_INDEX),
		STRING("CONSTANT_String",
				TAG, STRING_INDEX),
		FIELD_REF("CONSTANT_Fieldref",
				TAG, CLASS_INDEX, NAME_AND_TYPE_INDEX),
		METHOD_REF("CONSTANT_Methodref",
				TAG, CLASS_INDEX, NAME_AND_TYPE_INDEX),
		INTERFACE_METHOD_REF("CONSTANT_InterfaceMethodref",
				TAG, CLASS_INDEX, NAME_AND_TYPE_INDEX),
		NAME_AND_TYPE("CONSTANT_NameAndType",
				TAG, NAME_INDEX, DESCRIPTOR_INDEX),
		METHOD_HANDLE("CONSTANT_MethodHandle",
				TAG, REF_KIND, REF_INDEX),
		METHOD_TYPE("CONSTANT_MethodType", 
				TAG, DESCRIPTOR_INDEX),
		INVOKE_DYNAMIC("CONSTANT_InvokeDynamic",
				TAG, BOOTSTRAP_METHOD_ATTR_INDEX, NAME_AND_TYPE_INDEX),;

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
	
	public static Optional<Type> getType(int tag) {
		switch (tag) {
			case 1:
				return Optional.of(Type.UTF8);
			case 3:
				return Optional.of(Type.INTEGER);
			case 4:
				return Optional.of(Type.FLOAT);
			case 5:
				return Optional.of(Type.LONG);
			case 6:
				return Optional.of(Type.DOUBLE);
			case 7:
				return Optional.of(Type.CLASS);
			case 8:
				return Optional.of(Type.STRING);
			case 9:
				return Optional.of(Type.FIELD_REF);
			case 10:
				return Optional.of(Type.METHOD_REF);
			case 11:
				return Optional.of(Type.INTERFACE_METHOD_REF);
			case 12:
				return Optional.of(Type.NAME_AND_TYPE);
			case 15:
				return Optional.of(Type.METHOD_HANDLE);
			case 16:
				return Optional.of(Type.METHOD_TYPE);
			case 18:
				return Optional.of(Type.INVOKE_DYNAMIC);
			default:
				return Optional.empty();
		}
	}

	
	
	
}
