import java.util.HashMap;
import java.util.Map;

public enum DisplayTypeEnum {
    RELATIVE("relative"),
    ABSOLUTE("absolute");

    public final String type;

    DisplayTypeEnum(String type) {
        this.type = type;
    }

    private static final Map<String, DisplayTypeEnum> BY_TYPE = new HashMap<>();

    static {
        for (DisplayTypeEnum e: values()) {
            BY_TYPE.put(e.type, e);
        }
    }

    public static DisplayTypeEnum valueOfType(String type) {
        return BY_TYPE.get(type);
    }
}
