package zitsp.apps.classfileparser;

import java.util.Optional;

public class StackMapFrameNames {
	
	protected static final String NAME = "stack_map_frame ";
	
	protected static final String FRAME_TYPE = "frame_type ";
	protected static final String OFFSET_DELTA = "offset_delta";
	protected static final String STACK = "stack";
	protected static final String NUM_OF_STACK = "number_of_stack_items";
	protected static final String LOCAL = "locals";
	protected static final String NUM_OF_LOCAL = "number_of_locals";

	public enum Type {
		SAME("same_frame", FRAME_TYPE),
		SAME_LOCAL_A_STACK("same_locals_1_stack_item_frame", FRAME_TYPE,
				STACK),
		SAME_LOCAL_A_STACK_EXTENDED("same_locals_1_stack_item_frame_extended", FRAME_TYPE,
				OFFSET_DELTA, STACK),
		CHOP("chop_frame", FRAME_TYPE,
				OFFSET_DELTA),
		SAME_EXTENDES("same_frame_extended", FRAME_TYPE,
				OFFSET_DELTA),
		APPEND("append_frame", FRAME_TYPE,
				OFFSET_DELTA, LOCAL),
		FULL("full_frame", FRAME_TYPE,
				OFFSET_DELTA, NUM_OF_LOCAL, LOCAL, NUM_OF_STACK, STACK),
		;

		private final String typeName;
			private final String[] typeItems;
			private Type(String name, String... itemNames) {
				this.typeName = name;
				this.typeItems = itemNames;
			}

			public String getName() {
				return typeName;
			}

			public String[] getItemNames() {
				return typeItems;
			}

			public String getItemName(int index) {
				return (index < typeItems.length) ? typeItems[index] : "";
			}
	}

	public static Optional<Type> getType(int frameType) {
		if (0 <= frameType && frameType <= 63) {
			return Optional.of(Type.SAME);
		} else if (64 <= frameType && frameType <= 127) {
			return Optional.of(Type.SAME_LOCAL_A_STACK);
		} else if (frameType == 247) {
			return Optional.of(Type.SAME_LOCAL_A_STACK_EXTENDED);
		} else if (248 <= frameType && frameType <= 250) {
			return Optional.of(Type.CHOP);
		} else if (frameType == 251) {
			return Optional.of(Type.SAME_EXTENDES);
		} else if (252 <= frameType && frameType <= 254) {
			return Optional.of(Type.APPEND);
		} else if (frameType == 255) {
			return Optional.of(Type.FULL);
		} else {
			return Optional.empty();
		}
	}
	
}
