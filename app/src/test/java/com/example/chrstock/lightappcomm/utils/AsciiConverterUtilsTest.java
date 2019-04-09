package com.example.chrstock.lightappcomm.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AsciiConverterUtilsTest {

    @Test
    public void checkThatConverterHandlesNullObject(){
        assertThat(AsciiConverterUtils.convertToAscii(null)).isEqualTo("");
    }

    @Test
    public void checkThatConverterHandlesEmptyString(){
        assertThat(AsciiConverterUtils.convertToAscii("")).isEqualTo("");
    }

    @Test(expected = NumberFormatException.class)
    public void checkThatConverterHandlesNonbinaryString(){
        final String NON_BINARY_SEQUENCE = "101ABC1DSAG0101";

        List<String> test = new ArrayList<>();
        test.add("1");

        test.forEach(abc ->System.out.print(abc));


        assertThat(AsciiConverterUtils.convertToAscii(NON_BINARY_SEQUENCE)).isEqualTo("");
    }

    @Test
    public void checkThatConverterHandlesOneLetter(){

        final String BITSEQUENCE_A = "01000001";

        assertThat(AsciiConverterUtils.convertToAscii(BITSEQUENCE_A)).isEqualTo("A");
    }

    @Test
    public void checkThatConverterHandlesSmallBitSequences(){

        final String BITSEQENCE_HALLO = "0100100001000001010011000100110001001111";

        assertThat(AsciiConverterUtils.convertToAscii(BITSEQENCE_HALLO)).isEqualTo("HALLO");
    }

    @Test
    public void checkThatConverterHandlesMediumBitSequences(){

        final String BITSEQENCE_ICH_BIN_EINE_ANDROID_APP = "010010010110001101101000001000000110" +
                "0010011010010110111000100000011001010110100101101110011001010010000001000001011" +
                "01110011001000111001001101111011010010110010000100000010000010111000001110000";

        assertThat(AsciiConverterUtils.convertToAscii(BITSEQENCE_ICH_BIN_EINE_ANDROID_APP)).isEqualTo("Ich bin eine Android App");
    }


}
