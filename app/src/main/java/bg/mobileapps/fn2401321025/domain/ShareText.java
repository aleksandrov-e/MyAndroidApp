package bg.mobileapps.fn2401321025.domain;

/** Събира текста, който се изпраща при споделяне на задача. */
public final class ShareText {

    private ShareText() {
    }

    /** Редовете, които са празни или null, се пропускат. */
    public static String compose(String... lines) {
        StringBuilder builder = new StringBuilder();
        for (String line : lines) {
            if (line == null || line.trim().isEmpty()) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append('\n');
            }
            builder.append(line.trim());
        }
        return builder.toString();
    }
}
