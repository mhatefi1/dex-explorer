package com.apk.signature.Model;

public class StringModel {
    int start, end;
    String string;

    public StringModel(int start, int end, String s) {
        this.start = start;
        this.end = end;
        this.string = s;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public String getStringModelAsString() {
        return getString() + "[" + getStart() + "-" + getEnd() + "]";
    }
}
