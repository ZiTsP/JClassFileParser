package zitsp.apps.classfileparser;

public class MiscNames {

	protected static final String START_PC = "start_pc ";
	protected static final String END_PC = "end_pc ";
	protected static final String HANDLER_PC = "handler_pc ";
	protected static final String CATHC_TYPE = "catch_type ";
	protected static final String INNER_CLASS_INFO_INDEX = "inner_class_info_index ";
	protected static final String OUTER_CLASS_INFO_INDEX = "outer_class_info_index ";
	protected static final String INNER_NAME_INDEX = "inner_name_index ";
	protected static final String INNER_ACC_FLAG = "inner_class_access_flags ";
	protected static final String LINE_NUM = "line_number ";
	protected static final String LEN = "length ";
	protected static final String NAME_INDEX = "name_index ";
	protected static final String SIGNATURE_INDEX = "signature_index ";
	protected static final String INDEX = "index ";
	protected static final String BOOTSTRAP_METHOD_REF = "bootstrap_method_ref ";
	protected static final String NUM_BOOTSTRAP_METHOD_ARGS = "num_bootstrap_arguments ";
	protected static final String BOOTSTRAP_METHOD_ARGS = "bootstrap_arguments ";
	protected static final String ACC_FLAG = "acc_flags ";

	protected static final String TYPE_INDEX = "type_index ";
	protected static final String NUM_ELEM_VAL_PAIRS = "num_element_value_pairs ";
	protected static final String ELEM_VAL_PAIRS = "element_value_pairs ";

	protected static final String MAGIC = "magic ";
	protected static final String MINOR = "minor ";
	protected static final String MAJOR = "major ";
	protected static final String CONSTANT_POOL_COUNT = "constant_pool_count ";
	protected static final String CONSTANT_POOL = "constant_pool ";
	protected static final String ACC_FLAGS = "access_flags ";
	protected static final String THIS_CLASS = "this_class ";
	protected static final String SUPER_CLASS = "super_class ";
	protected static final String INTERFACES_COUNT = "interfaces_count ";
	protected static final String INTERFACES = "interfaces ";
	protected static final String FIELDS_COUNT = "fields_count ";
	protected static final String FIELDS = "fields ";
	protected static final String METHODS_COUNT = "methods_count ";
	protected static final String METHODS = "methods ";
	protected static final String ATTRS_COUNT = "attributes_count ";
	protected static final String ATTRS = "attributes ";
	
	protected static final String CP_INDEX_FORMAT = "cp#%02d ";
	
	public enum Type {
		CLASS_FILE(MAGIC, MINOR, MAJOR,
				CONSTANT_POOL_COUNT, CONSTANT_POOL,
				ACC_FLAGS, THIS_CLASS, SUPER_CLASS,
				INTERFACES_COUNT, INTERFACES, FIELDS_COUNT, FIELDS,
				METHODS_COUNT, METHODS, ATTRS_COUNT, ATTRS),
		EXCEPTION_TABLE(START_PC, END_PC, HANDLER_PC, CATHC_TYPE),
		INNER_CLASSES(INNER_CLASS_INFO_INDEX, OUTER_CLASS_INFO_INDEX, INNER_NAME_INDEX, INNER_ACC_FLAG),
		LINE_NUM_TABLE(START_PC, LINE_NUM),
		LOCAL_VAL_TABLE(START_PC, LEN, NAME_INDEX, SIGNATURE_INDEX, INDEX),
		BOOTSTRAP_METHOD(BOOTSTRAP_METHOD_REF, NUM_BOOTSTRAP_METHOD_ARGS, BOOTSTRAP_METHOD_ARGS),
		METHOD_PARAMS(NAME_INDEX, ACC_FLAG),
		ANNOTATION(TYPE_INDEX, NUM_ELEM_VAL_PAIRS, ELEM_VAL_PAIRS),
		;

		private final String[] itemNames;
		private Type(String... itemNames) {
			this.itemNames = itemNames;
		}

		public String[] getItemNames() {
			return itemNames;
		}

		public String getItemName(int index) {
			return (index < itemNames.length) ? itemNames[index] : "";
		}
	}
	

}
