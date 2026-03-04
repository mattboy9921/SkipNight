package net.mattlabs.skipnight.api.util;

/**
 * Utility methods for working with Minecraft/server version strings.
 *
 * <p>This class currently provides:</p>
 * <ul>
 *     <li>A simple dotted-numeric version comparator (e.g. {@code "1.8.8"} vs {@code "1.21"})</li>
 *     <li>A helper for extracting the Minecraft version from a formatted string containing {@code "MC: "}</li>
 * </ul>
 *
 * <p>These helpers are intended for internal use when selecting implementations
 * or displaying version information.</p>
 */
public class Versions {

    /**
     * Compares two dotted-numeric version strings.
     *
     * <p>Version strings are expected to contain numeric parts separated by periods
     * (for example {@code "1.8.8"} or {@code "1.21"}). Each numeric segment is compared
     * as an integer from left to right until a difference is found.</p>
     *
     * <p>Examples:</p>
     * <ul>
     *     <li>{@code versionCompare("1.8", "1.8.0")} returns {@code 0}</li>
     *     <li>{@code versionCompare("1.9", "1.10")} returns {@code -1}</li>
     *     <li>{@code versionCompare("2.0", "1.99")} returns {@code 1}</li>
     * </ul>
     *
     * @param v1 the first version string
     * @param v2 the second version string
     * @return {@code 1} if {@code v1} is greater than {@code v2},
     *         {@code -1} if {@code v1} is less than {@code v2},
     *         or {@code 0} if the versions are equal
     * @throws IndexOutOfBoundsException if the input strings are malformed in a way that
     *                                  breaks the parsing loop
     * @throws IllegalArgumentException if either version contains non-numeric characters
     *                                  other than '.'
     */
    public static int versionCompare(String v1, String v2)
    {
        // vnum stores each numeric part of version
        int vnum1 = 0, vnum2 = 0;

        // loop untill both String are processed
        for (int i = 0, j = 0; (i < v1.length()
                || j < v2.length());) {
            // Storing numeric part of
            // version 1 in vnum1
            while (i < v1.length()
                    && v1.charAt(i) != '.') {
                vnum1 = vnum1 * 10
                        + (v1.charAt(i) - '0');
                i++;
            }

            // storing numeric part
            // of version 2 in vnum2
            while (j < v2.length()
                    && v2.charAt(j) != '.') {
                vnum2 = vnum2 * 10
                        + (v2.charAt(j) - '0');
                j++;
            }

            if (vnum1 > vnum2)
                return 1;
            if (vnum2 > vnum1)
                return -1;

            // if equal, reset variables and
            // go for next numeric part
            vnum1 = vnum2 = 0;
            i++;
            j++;
        }
        return 0;
    }

    /**
     * Extracts the Minecraft version substring from a formatted string containing {@code "MC: "}.
     *
     * <p>This method assumes the input is formatted such that the Minecraft version begins
     * immediately after the substring {@code "MC: "} and ends one character before the end
     * of the string (for example, when the version is followed by a trailing {@code ')'}).</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     * String s = "Paper (MC: 1.21.1)";
     * String mc = versionSubstring(s); // "1.21.1"
     * }</pre>
     *
     * @param versionString a formatted string containing {@code "MC: "}
     * @return the extracted Minecraft version string
     * @throws StringIndexOutOfBoundsException if {@code "MC: "} is not present or the string
     *                                        does not match the expected format
     */
    public static String versionSubstring(String versionString) {
        int start = versionString.indexOf("MC: ") + 4;
        int end = versionString.length() - 1;
        return versionString.substring(start, end);
    }
}
