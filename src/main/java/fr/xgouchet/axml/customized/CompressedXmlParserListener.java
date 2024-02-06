package fr.xgouchet.axml.customized;

public interface CompressedXmlParserListener {
    void startElement(String uri, String localName, String qName, Attribute[] attrs);
}
