package com.example.googledrive;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JoinTests {
    // Sample data for testing
    private List<Record> createLeftTable() {
        List<Record> left = new ArrayList<>();
        left.add(new Record(1, "A"));
        left.add(new Record(2, "B"));
        left.add(new Record(3, "C"));
        return left;
    }

    private List<Record> createRightTable() {
        List<Record> right = new ArrayList<>();
        right.add(new Record(1, "X"));
        right.add(new Record(2, "Y"));
        right.add(new Record(4, "Z"));
        return right;
    }

    @Test
    public void testInnerJoin() {
        JoinService service = new JoinService();
        List<Record> left = createLeftTable();
        List<Record> right = createRightTable();
        List<String> result = service.innerJoin(left, right);

        assertEquals(2, result.size());
        assertEquals("ID: 1, Left: A, Right: X", result.get(0));
        assertEquals("ID: 2, Left: B, Right: Y", result.get(1));
    }

    @Test
    public void testLeftJoin() {
        JoinService service = new JoinService();
        List<Record> left = createLeftTable();
        List<Record> right = createRightTable();
        List<String> result = service.leftJoin(left, right);

        assertEquals(3, result.size());
        assertEquals("ID: 1, Left: A, Right: X", result.get(0));
        assertEquals("ID: 2, Left: B, Right: Y", result.get(1));
        assertEquals("ID: 3, Left: C, Right: null", result.get(2));
    }

    @Test
    public void testCrossJoin() {
        JoinService service = new JoinService();
        List<Record> left = createLeftTable();
        List<Record> right = createRightTable();
        List<String> result = service.crossJoin(left, right);

        assertEquals(9, result.size()); // 3 * 3 = 9 combinations
        assertEquals("Left ID: 1, Left: A, Right ID: 1, Right: X", result.get(0));
        assertEquals("Left ID: 1, Left: A, Right ID: 2, Right: Y", result.get(1));
        assertEquals("Left ID: 1, Left: A, Right ID: 4, Right: Z", result.get(2));
    }
}