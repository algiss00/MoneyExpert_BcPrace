package cz.cvut.fel.dto;

public enum SortAttribute {
    DATE("date");

    private final String columnName;

    SortAttribute(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnName() {
        return columnName;
    }

}
