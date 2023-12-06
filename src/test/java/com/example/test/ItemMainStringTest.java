package com.example.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.ArrayList;

/**
 * Unit test for simple App.
 */
public class ItemMainStringTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }

    @Test
    public void getCommonOfArrayList() {
        ArrayList<String> result = new ArrayList<>();
        ArrayList<String> first = new ArrayList<>();
        ArrayList<String> second = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            first.add(i + "");
        }
        for (int i = 5; i < 14; i++) {
            second.add(i + "");
        }

        printArray(first);
        printArray(second);

        for (String s : first) {
            for (String s2 : second) {
                if (s.equals(s2)) {
                    result.add(s);
                }
            }
        }
        printArray(result);
    }

    public void printArray(ArrayList<String> d) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        for (String s : d
        ) {
            stringBuilder.append(s).append(",");
        }
        stringBuilder.append("]");
        System.out.println(stringBuilder);
    }
}
