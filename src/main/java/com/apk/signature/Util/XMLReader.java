package com.apk.signature.Util;

public class XMLReader {
    public static int endDocTag = 0x00100101;
    public static int startTag = 0x00100102;
    public static int endTag = 0x00100103;
    public static String spaces = "                                             ";

    public String decompressXML(byte[] xml) {
        StringBuilder result = new StringBuilder();
        int numbStrings = LEW(xml, 4 * 4);
        int sitOff = 0x24;
        int stOff = sitOff + numbStrings * 4;
        int xmlTagOff = LEW(xml, 3 * 4);
        for (int ii = xmlTagOff; ii < xml.length - 4; ii += 4) {
            if (LEW(xml, ii) == startTag) {
                xmlTagOff = ii;
                break;
            }
        }

        int off = xmlTagOff;
        int indent = 0;
        while (off < xml.length) {
            int tag0 = LEW(xml, off);
            int nameSi = LEW(xml, off + 5 * 4);
            if (tag0 == startTag) {
                int numbAttrs = LEW(xml, off + 7 * 4);

                off += 9 * 4;
                String name = compXmlString(xml, sitOff, stOff, nameSi);

                StringBuilder sb = new StringBuilder();
                for (int ii = 0; ii < numbAttrs; ii++) {
                    int attrNameSi = LEW(xml, off + 4);
                    int attrValueSi = LEW(xml, off + 2 * 4);
                    int attrResId = LEW(xml, off + 4 * 4);
                    off += 5 * 4;

                    String attrName = compXmlString(xml, sitOff, stOff, attrNameSi);
                    String attrValue;
                    if (attrValueSi != -1) {
                        attrValue = compXmlString(xml, sitOff, stOff, attrValueSi);
                    } else {
                        attrValue = "resourceID 0x" + Integer.toHexString(attrResId);
                    }
                    sb.append(" ").append(attrName).append("=\"").append(attrValue).append("\"");

                }
                prtIndent(indent, "<" + name + sb + ">", result);
                indent++;

            } else if (tag0 == endTag) {
                indent--;
                off += 6 * 4;
                String name = compXmlString(xml, sitOff, stOff, nameSi);
                prtIndent(indent, "</" + name + ">", result);


            } else if (tag0 == endDocTag) {
                break;

            } else {
                result.append("  Unrecognized tag code '").append(Integer.toHexString(tag0)).append("' at offset ").append(off);
                break;
            }
        }
        return result.toString();
    }

    public String compXmlString(byte[] xml, int sitOff, int stOff, int strInd) {
        if (strInd < 0) return null;
        int strOff = stOff + LEW(xml, sitOff + strInd * 4);
        return compXmlStringAt(xml, strOff);
    }

    public void prtIndent(int indent, String str, StringBuilder builder) {
        builder.append(spaces, 0, Math.min(indent * 2, spaces.length())).append(str);
    }

    public String compXmlStringAt(byte[] arr, int strOff) {
        int strLen = arr[strOff + 1] << 8 & 0xff00 | arr[strOff] & 0xff;
        byte[] chars = new byte[strLen];
        for (int ii = 0; ii < strLen; ii++) {
            chars[ii] = arr[strOff + 2 + ii * 2];
        }
        return new String(chars);
    }

    public int LEW(byte[] arr, int off) {
        return arr[off + 3] << 24 & 0xff000000 | arr[off + 2] << 16 & 0xff0000
                | arr[off + 1] << 8 & 0xff00 | arr[off] & 0xFF;
    }
}
