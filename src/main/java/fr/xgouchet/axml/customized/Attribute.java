package fr.xgouchet.axml.customized;

public class Attribute {

    private String mName, mPrefix, mNamespace, mValue;

    public String getName() {
        return mName;
    }

    public void setName(final String name) {
        mName = name;
    }

    public String getPrefix() {
        return mPrefix;
    }

    public void setPrefix(final String prefix) {
        mPrefix = prefix;
    }

    public String getNamespace() {
        return mNamespace;
    }

    public void setNamespace(final String namespace) {
        mNamespace = namespace;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(final String value) {
        mValue = value;
    }
}