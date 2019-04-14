package com.example.chrstock.lightappcomm.utils;

import com.example.chrstock.lightappcomm.temp.MicrocontrollerBitUtils;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

public class MicrocontrollerBitUtilsTest {

    @Test
    public void checkCalculateCountBitsBrings0ForZeroString(){
        String validBits = getValidBinaryString();

        int count = MicrocontrollerBitUtils.calculateCountBit(validBits);

        assertThat(count).isEqualTo(0);
    }

    @Test
    public void checkCalculateCountBitsBringsCorrectNumber(){
        String validBits = getValidBinaryString();
        char[] validBitsChars = validBits.toCharArray();
        validBitsChars[60] = '1';
        String validBitsWithCountFour = String.valueOf(validBitsChars);

        int count = MicrocontrollerBitUtils.calculateCountBit(validBitsWithCountFour);

        assertThat(count).isEqualTo(4);
    }

    private String getValidBinaryString(){
        StringBuilder bits = new StringBuilder();

        for(int i=0;i<120;i++){
            bits.append("0");
        }

        return bits.toString();
    }
}
