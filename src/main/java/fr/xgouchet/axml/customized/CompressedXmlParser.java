package fr.xgouchet.axml.customized;

import com.apk.signature.Util.Util;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class CompressedXmlParser {
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
    private final Map<String, String> mNamespaces;
    private CompressedXmlParserListener mListener;
    private byte[] mData;
    private String[] mStringsTable;
    private int mStringsCount;
    private int mParserOffset;

    public CompressedXmlParser() {
        mNamespaces = new HashMap<>();
    }

    public boolean parse(final byte[] bs, final CompressedXmlParserListener listener) {
        mListener = listener;
        mData = bs;
        parseCompressedXml();
        return mParserOffset == mData.length;
    }

    private void parseCompressedXml() {
        int word0;

        while (mParserOffset < mData.length) {
            word0 = getLEWord(mParserOffset);
            switch (word0) {
                case WORD_START_DOCUMENT:
                    parseStartDocument();
                    break;
                case WORD_STRING_TABLE:
                    parseStringTable();
                    break;
                case WORD_RES_TABLE:
                    parseResourceTable();
                    break;
                case WORD_START_NS:
                    parseNamespace(true);
                    break;
                case WORD_END_NS:
                    parseNamespace(false);
                    break;
                case WORD_START_TAG:
                    parseStartTag();
                    break;
                case WORD_END_TAG:
                    parseEndTag();
                    break;
                case WORD_TEXT:
                    parseText();
                    break;
                case WORD_EOS:
                    return;
                default:
                    mParserOffset += WORD_SIZE;
                    break;
            }
        }
    }

    private void parseStartDocument() {
        mParserOffset += (2 * WORD_SIZE);
    }

    private void parseStringTable() {

        int chunk = getLEWord(mParserOffset + (WORD_SIZE));
        mStringsCount = getLEWord(mParserOffset + (2 * WORD_SIZE));
        int mStylesCount = getLEWord(mParserOffset + (3 * WORD_SIZE));
        int strOffset = mParserOffset + getLEWord(mParserOffset + (5 * WORD_SIZE));
        int styleOffset = getLEWord(mParserOffset + (6 * WORD_SIZE));

        mStringsTable = new String[mStringsCount];
        int offset;
        for (int i = 0; i < mStringsCount; ++i) {
            offset = strOffset
                    + getLEWord(mParserOffset + ((i + 7) * WORD_SIZE));
            mStringsTable[i] = getStringFromStringTable(offset);
        }
        mParserOffset += chunk;
    }

    private void parseResourceTable() {
        int chunk = getLEWord(mParserOffset + (WORD_SIZE));
        int mResCount = (chunk / 4) - 2;

        int[] mResourcesIds = new int[mResCount];
        for (int i = 0; i < mResCount; ++i) {
            mResourcesIds[i] = getLEWord(mParserOffset + ((i + 2) * WORD_SIZE));
        }

        mParserOffset += chunk;
    }

    private void parseNamespace(boolean start) {
        final int prefixIdx = getLEWord(mParserOffset + (4 * WORD_SIZE));
        final int uriIdx = getLEWord(mParserOffset + (5 * WORD_SIZE));

        final String uri = getString(uriIdx);
        final String prefix = getString(prefixIdx);

        if (start) {
            mNamespaces.put(uri, prefix);
        } else {
            mNamespaces.remove(uri);
        }
        mParserOffset += (6 * WORD_SIZE);
    }

    private void parseStartTag() {
        final int uriIdx = getLEWord(mParserOffset + (4 * WORD_SIZE));
        final int nameIdx = getLEWord(mParserOffset + (5 * WORD_SIZE));
        final int attrCount = getLEShort(mParserOffset + (7 * WORD_SIZE));

        final String name = getString(nameIdx);
        String uri, qname;
        if (uriIdx == 0xFFFFFFFF) {
            uri = "";
            qname = name;
        } else {
            uri = getString(uriIdx);
            if (mNamespaces.containsKey(uri)) {
                qname = mNamespaces.get(uri) + ':' + name;
            } else {
                qname = name;
            }
        }

        mParserOffset += (9 * WORD_SIZE);

        final Attribute[] attrs = new Attribute[attrCount];
        for (int a = 0; a < attrCount; a++) {
            attrs[a] = parseAttribute();

            mParserOffset += (5 * 4);
        }

        mListener.startElement(uri, name, qname, attrs);
    }

    private Attribute parseAttribute() {
        final int attrNSIdx = getLEWord(mParserOffset);
        final int attrNameIdx = getLEWord(mParserOffset + (WORD_SIZE));
        final int attrValueIdx = getLEWord(mParserOffset + (2 * WORD_SIZE));
        final int attrType = getLEWord(mParserOffset + (3 * WORD_SIZE));
        final int attrData = getLEWord(mParserOffset + (4 * WORD_SIZE));

        final Attribute attr = new Attribute();
        attr.setName(getString(attrNameIdx));

        if (attrNSIdx == 0xFFFFFFFF) {
            attr.setNamespace(null);
            attr.setPrefix(null);
        } else {
            String uri = getString(attrNSIdx);
            if (mNamespaces.containsKey(uri)) {
                attr.setNamespace(uri);
                attr.setPrefix(mNamespaces.get(uri));
            }
        }

        if (attrValueIdx == 0xFFFFFFFF) {
            attr.setValue(getAttributeValue(attrType, attrData));
        } else {
            attr.setValue(getString(attrValueIdx));
        }

        return attr;

    }

    private void parseText() {
        final int strIndex = getLEWord(mParserOffset + (4 * WORD_SIZE));
        String data = getString(strIndex);
        mParserOffset += (7 * WORD_SIZE);
    }

    private void parseEndTag() {
        final int uriIdx = getLEWord(mParserOffset + (4 * WORD_SIZE));
        final int nameIdx = getLEWord(mParserOffset + (5 * WORD_SIZE));

        final String name = getString(nameIdx);
        String uri;
        if (uriIdx == 0xFFFFFFFF) {
            uri = "";
        } else {
            uri = getString(uriIdx);
        }

        mParserOffset += (6 * WORD_SIZE);
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
        return switch (type) {
            case TYPE_STRING -> getString(data);
            case TYPE_DIMEN -> (data >> 8) + DIMEN[data & 0xFF];
            case TYPE_FRACTION -> {
                double v = (((double) data) / ((double) 0x7FFFFFFF));
                //yield String.format("%.2f%%", v);
                yield new DecimalFormat("#.##%").format(v);
            }
            case TYPE_FLOAT -> Float.toString(Float.intBitsToFloat(data));
            case TYPE_INT, TYPE_FLAGS -> Integer.toString(data);
            case TYPE_BOOL -> Boolean.toString(data != 0);
            case TYPE_COLOR, TYPE_COLOR2 -> String.format("#%08X", data);
            case TYPE_ID_REF -> String.format("@id/0x%08X", data);
            case TYPE_ATTR_REF -> String.format("?id/0x%08X", data);
            default -> String.format("%08X/0x%08X", type, data);
        };
    }

}
