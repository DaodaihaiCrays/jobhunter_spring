package vn.hoidanit.jobhunter.util.enum_package;

public enum GenderEnum {
    FEMALE, MALE, OTHER;

    public static boolean isValid(String value) {
        for (GenderEnum gender : GenderEnum.values()) {
            if (gender.name().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}

