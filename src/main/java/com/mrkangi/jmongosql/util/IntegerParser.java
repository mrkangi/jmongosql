package com.mrkangi.jmongosql.util;

 public class IntegerParser{
    private String value;
    private Integer parsedValue;

    public IntegerParser(String value) {
        this.value = value;
        this.parsedValue = null;
    }

    public boolean canParse(){
        try{
            this.parsedValue = Integer.parseInt(value);
            return true;
        }catch (NumberFormatException ex){
            return false;
        }
    }

    public Integer getParsedValue() {
        return parsedValue;
    }
}
