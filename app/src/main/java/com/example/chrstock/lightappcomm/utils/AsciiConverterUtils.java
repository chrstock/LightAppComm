package com.example.chrstock.lightappcomm.utils;

import java.util.stream.Collectors;

/**
 * Class to convert 8Bit-Strings to Ascii-Character
 *
 * @author chrstock
 */
public final class AsciiConverterUtils {
    private static final int LOWEST_ASCII_CHAR = 31;
    private static final int HIGHEST_ASCII_CHAR = 126;

    /**
     * converts String with 0 and 1 to Ascii
     *
     * @return Ascii-character
     */
    public static String convertToAscii(String bits) throws NumberFormatException {
        if (bits != null && !bits.equals("")) {

            StringBuilder resultedString = new StringBuilder();
            String nextSubString;
            char letter;

            for (int i = 0; i <= bits.length() - 8; i += 8) {
                nextSubString = bits.substring(i, i + 8);

                if (nextSubString.length() == 8) {
                    letter = (char) Integer.parseInt(nextSubString, 2);

                    if (letter > LOWEST_ASCII_CHAR && letter < HIGHEST_ASCII_CHAR) {
                        resultedString.append(letter);
                    }
                }
            }
            return resultedString.toString();
        }
        return "";
    }

    private static String removeLetterOutOfRange(String bitSsequence) {

        return bitSsequence
                .chars()
                .filter(letter -> letter > LOWEST_ASCII_CHAR && letter < HIGHEST_ASCII_CHAR)
                .mapToObj(num -> Integer.toBinaryString(num))
                .collect(Collectors.joining());

    }
}
