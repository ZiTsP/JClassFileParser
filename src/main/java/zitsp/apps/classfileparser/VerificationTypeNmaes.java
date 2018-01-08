package zitsp.apps.classfileparser;

import java.util.Optional;

public class VerificationTypeNmaes {
	
	protected static final String NAME = "verification_type ";

	protected static final String TAG = "tag ";
	protected static final String CPOOL_INDEX = "cpool_index ";
	protected static final String OFFSET = "offset ";

	public enum Type {
	    TOP("Top_variable_info", 0, TAG),
	    INT("Integer_variable_info", 1, TAG),
	    FLOAT("Float_variable_info", 2, TAG),
	    LONG("Long_variable_info", 3, TAG),
	    DOUBLE("Double_variable_info", 4, TAG),
	    NULL("Null_variable_info", 5, TAG),
	    UNINIT_THIS("UninitializedThis_variable_info", 6, TAG),
	    OBJECT("Object_variable_info", 7, TAG, CPOOL_INDEX),
	    UNINIT("Uninitialized_variable_info", 8, TAG, OFFSET),
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

	public static Optional<Type> getType(byte tag) {
		switch (tag) {
		case 0:
			return Optional.of(VerificationTypeNmaes.Type.TOP);
		case 1:
			return Optional.of(VerificationTypeNmaes.Type.INT);
		case 2:
			return Optional.of(VerificationTypeNmaes.Type.FLOAT);
		case 3:
			return Optional.of(VerificationTypeNmaes.Type.LONG);
		case 4:
			return Optional.of(VerificationTypeNmaes.Type.DOUBLE);
		case 5:
			return Optional.of(VerificationTypeNmaes.Type.NULL);
		case 6:
			return Optional.of(VerificationTypeNmaes.Type.UNINIT_THIS);
		case 7:
			return Optional.of(VerificationTypeNmaes.Type.OBJECT);
		case 8:
			return Optional.of(VerificationTypeNmaes.Type.UNINIT);
		default :
			return Optional.empty();
		}
	}

}
