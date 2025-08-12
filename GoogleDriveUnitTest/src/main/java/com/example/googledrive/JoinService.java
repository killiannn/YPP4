// Class to represent a simple record for join operations//
package com.example.googledrive;
import java.util.ArrayList;
import java.util.List;

class Record {
    int id;
    String value;

    Record(int id, String value) {
        this.id = id;
        this.value = value;
    }
}

public class JoinService {
    // Simulate INNER JOIN
    public List<String> innerJoin(List<Record> left, List<Record> right) {
        List<String> result = new ArrayList<>();
        for (Record l : left) {
            for (Record r : right) {
                if (l.id == r.id) {
                    result.add("ID: " + l.id + ", Left: " + l.value + ", Right: " + r.value);
                }
            }
        }
        return result;
    }

    // Simulate LEFT JOIN
    public List<String> leftJoin(List<Record> left, List<Record> right) {
        List<String> result = new ArrayList<>();
        for (Record l : left) {
            boolean matched = false;
            for (Record r : right) {
                if (l.id == r.id) {
                    result.add("ID: " + l.id + ", Left: " + l.value + ", Right: " + r.value);
                    matched = true;
                }
            }
            if (!matched) {
                result.add("ID: " + l.id + ", Left: " + l.value + ", Right: null");
            }
        }
        return result;
    }

    // Simulate CROSS JOIN
    public List<String> crossJoin(List<Record> left, List<Record> right) {
        List<String> result = new ArrayList<>();
        for (Record l : left) {
            for (Record r : right) {
                result.add("Left ID: " + l.id + ", Left: " + l.value + ", Right ID: " + r.id + ", Right: " + r.value);
            }
        }
        return result;
    }
}