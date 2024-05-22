package github.com.mstepan.gossip.util;

/** Utility class that simplifies pre-conditions check. */
public final class Preconditions {

    private Preconditions() {
        throw new AssertionError("Can't instantiate utility-only class.");
    }

    public static <T> T checkNotNull(T objToCheck, String errorMessage) {
        if (objToCheck == null) {
            throw new IllegalArgumentException(errorMessage);
        }
        return objToCheck;
    }

    public static void checkArgument(boolean conditionToCheck, String errorMessage) {
        if (!conditionToCheck) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static void checkState(boolean conditionToCheck, String errorMessage) {
        if (!conditionToCheck) {
            throw new IllegalStateException(errorMessage);
        }
    }

    /**
     * Try to convert string parameter into int value, throwing 'IllegalArgumentException' if not
     * possible to convert.
     */
    public static int parseInt(String strToConvertToInt, String errorMessage) {
        try {
            return Integer.parseInt(strToConvertToInt);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
