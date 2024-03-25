package com.apk.signature;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.apk.signature.Util.Util.print;

public class jniManifest {

    public static final int WORD_START_DOCUMENT = 0x00080003;
    public static final int WORD_STRING_TABLE = 0x001C0001;
    public static final int WORD_RES_TABLE = 0x00080180;
    public static final int WORD_START_NS = 0x00100100;
    public static final int WORD_END_NS = 0x00100101;
    public static final int WORD_START_TAG = 0x00100102;
    public static final int WORD_END_TAG = 0x00100103;
    public static final int WORD_TEXT = 0x00100104;
    public static final int WORD_EOS = 0xFFFFFFFF;
    public static final int WORD_SIZE = 4;

    private static final int TYPE_ID_REF = 0x01000008;
    private static final int TYPE_ATTR_REF = 0x02000008;
    private static final int TYPE_STRING = 0x03000008;
    private static final int TYPE_DIMEN = 0x05000008;
    private static final int TYPE_FRACTION = 0x06000008;
    private static final int TYPE_INT = 0x10000008;
    private static final int TYPE_FLOAT = 0x04000008;

    private static final int TYPE_FLAGS = 0x11000008;
    private static final int TYPE_BOOL = 0x12000008;
    private static final int TYPE_COLOR = 0x1C000008;
    private static final int TYPE_COLOR2 = 0x1D000008;

    private static final String[] DIMEN = new String[]{"px", "dp", "sp", "pt", "in", "mm"};

    private byte[] mData;
    private String[] mStringsTable;
    private int mStringsCount;
    private int mParserOffset;

    private ArrayList<String> list;

    public void parse(final byte[] bs) {
        mData = bs;
        parseCompressedXml();
    }

    private void parseCompressedXml() {
        int word0;

        while (mParserOffset < mData.length) {
            word0 = getLEWord(mParserOffset);
            switch (word0) {
                case WORD_START_DOCUMENT:
                    mParserOffset += (2 * WORD_SIZE);
                    break;
                case WORD_STRING_TABLE:
                    parseStringTable();
                    break;
                case WORD_RES_TABLE:
                    int chunk = getLEWord(mParserOffset + (WORD_SIZE));
                    mParserOffset += chunk;
                    break;
                case WORD_START_NS:
                case WORD_END_NS:
                case WORD_END_TAG:
                    mParserOffset += (6 * WORD_SIZE);
                    break;
                case WORD_START_TAG:
                    parseStartTag();
                    break;
                case WORD_TEXT:
                    mParserOffset += (7 * WORD_SIZE);
                    break;
                case WORD_EOS:
                    return;
                default:
                    mParserOffset += WORD_SIZE;
                    break;
            }
        }
    }

    private void parseStringTable() {
        int chunk = getLEWord(mParserOffset + (WORD_SIZE));
        mStringsCount = getLEWord(mParserOffset + (2 * WORD_SIZE));
        int strOffset = mParserOffset + getLEWord(mParserOffset + (5 * WORD_SIZE));
        mStringsTable = new String[mStringsCount];
        int offset;
        for (int i = 0; i < mStringsCount; ++i) {
            offset = strOffset
                    + getLEWord(mParserOffset + ((i + 7) * WORD_SIZE));
            mStringsTable[i] = getStringFromStringTable(offset);
        }
        mParserOffset += chunk;
    }

    private void parseStartTag() {
        final int attrCount = getLEShort(mParserOffset + (7 * WORD_SIZE));
        mParserOffset += (9 * WORD_SIZE);
        List<String[]> attrs = new ArrayList<>();
        for (int a = 0; a < attrCount; a++) {
            attrs.add(parseAttribute());
            mParserOffset += (5 * 4);
        }
        setAttributeInHex(attrs);
    }

    private String[] parseAttribute() {
        final int attrNameIdx = getLEWord(mParserOffset + (WORD_SIZE));
        final int attrValueIdx = getLEWord(mParserOffset + (2 * WORD_SIZE));
        final int attrType = getLEWord(mParserOffset + (3 * WORD_SIZE));
        final int attrData = getLEWord(mParserOffset + (4 * WORD_SIZE));
        final String[] attr = new String[2];
        attr[0] = getString(attrNameIdx);
        if (attrValueIdx == 0xFFFFFFFF) {
            attr[1] = getAttributeValue(attrType, attrData);
        } else {
            attr[1] = (getString(attrValueIdx));
        }
        return attr;
    }

    private String getString(final int index) {
        String res;
        if ((index >= 0) && (index < mStringsCount)) {
            res = mStringsTable[index];
        } else {
            res = null;
        }
        return res;
    }

    private String getStringFromStringTable(final int offset) {
        int strLength;
        byte[] chars;
        if (offset > 0 && offset < mData.length) {
            if (mData[offset + 1] == mData[offset]) {
                strLength = mData[offset];
                chars = new byte[strLength];
                for (int i = 0; i < strLength; i++) {
                    int temp = offset + 2 + i;
                    if (temp < mData.length) {
                        chars[i] = mData[temp];
                    } else {
                        break;
                    }
                }
            } else {
                strLength = ((mData[offset + 1] << 8) & 0xFF00) | (mData[offset] & 0xFF);
                chars = new byte[strLength];
                for (int i = 0; i < strLength; i++) {
                    int temp = offset + 2 + (i * 2);
                    if (temp < mData.length) {
                        chars[i] = mData[temp];
                    } else {
                        break;
                    }
                }

            }
            return new String(chars);
        }
        return "";
    }

    private int getLEWord(final int off) {
        return ((mData[off + 3] << 24) & 0xff000000) | ((mData[off + 2] << 16) & 0x00ff0000)
                | ((mData[off + 1] << 8) & 0x0000ff00) | ((mData[off]) & 0x000000ff);
    }

    private int getLEShort(final int off) {
        return ((mData[off + 1] << 8) & 0xff00) | ((mData[off]) & 0x00ff);
    }


    private String getAttributeValue(final int type, final int data) {
        String res;

        switch (type) {
            case TYPE_STRING:
                res = getString(data);
                break;
            case TYPE_DIMEN:
                res = (data >> 8) + DIMEN[data & 0xFF];
                break;
            case TYPE_FRACTION:
                double fracValue = (((double) data) / ((double) 0x7FFFFFFF));
                res = new DecimalFormat("#.##%").format(fracValue);
                break;
            case TYPE_FLOAT:
                res = Float.toString(Float.intBitsToFloat(data));
                break;
            case TYPE_INT:
            case TYPE_FLAGS:
                res = Integer.toString(data);
                break;
            case TYPE_BOOL:
                res = Boolean.toString(data != 0);
                break;
            case TYPE_COLOR:
            case TYPE_COLOR2:
                res = String.format("#%08X", data);
                break;
            case TYPE_ID_REF:
                res = String.format("@id/0x%08X", data);
                break;
            case TYPE_ATTR_REF:
                res = String.format("?id/0x%08X", data);
                break;
            default:
                res = String.format("%08X/0x%08X", type, data);
                break;
        }

        return res;
    }

    public ArrayList<String> calcManifestInNew(byte[] bs) {
        list = new ArrayList<>();
        parse(bs);
        return list;
    }

    private void setAttributeInHex(List<String[]> atts) {
        if (atts != null) {
            for (String[] att : atts) {
                if (att[0] != null) {
                    if (att[0].equals("name") || att[0].isEmpty()) {
                        list.add(byteToStringHex(att[1].getBytes()));
                        break;
                    }
                }
            }
        }
    }

    public String byteToStringHex(byte[] byteArray) {
        StringBuilder sb = new StringBuilder();
        for (byte b : byteArray) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}