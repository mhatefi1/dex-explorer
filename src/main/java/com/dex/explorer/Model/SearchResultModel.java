package com.dex.explorer.Model;

public class SearchResultModel {

    String dex_name;
    String type;
    String hex;
    String utf8;
    String offset;
    int index;

    public SearchResultModel(String type, String hex, String utf8, String offset, int index) {
        this.type = type;
        this.hex = hex;
        this.utf8 = utf8;
        this.offset = offset;
        this.index = index;
    }

    public String getDex_name() {
        return dex_name;
    }

    public void setDex_name(String dex_name) {
        this.dex_name = dex_name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHex() {
        return hex;
    }

    public void setHex(String hex) {
        this.hex = hex;
    }

    public String getUtf8() {
        return utf8;
    }

    public void setUtf8(String utf8) {
        this.utf8 = utf8;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
