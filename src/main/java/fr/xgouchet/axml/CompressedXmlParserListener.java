package fr.xgouchet.axml;

public interface CompressedXmlParserListener {
    void startElement(String uri, String localName, String qName, Attribute[] attrs);
}
