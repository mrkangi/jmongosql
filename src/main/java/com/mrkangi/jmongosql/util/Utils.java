package com.mrkangi.jmongosql.util;

import com.mrkangi.jmongosql.primitives.TriFunction;
import com.mrkangi.jmongosql.util.Regs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private <TIn,TOut> List<TOut> map(List<TIn> list,BiFunction function){
        List<TOut> result = new ArrayList<>();
        for(int i = 0;i<list.size();i++){
            result.add((TOut)function.apply(list.get(i),i));
        }
        return result;
    }
    private <T> T reduce(List<T> list,TriFunction<T,T,Integer,T> function){
        if(list.size() == 0)return null;
        T prev = list.get(0);
        for(int i = 1;i<list.size();i++){
            prev = function.apply(prev,list.get(i),i);
        }
        return prev;
    }



    public String Parameterize(Object value, List<Object> values)
    {
        if (value instanceof Boolean) return (Boolean)value ? "true" : "false";
        if (!(value instanceof String))
        {
            values.add(value);
            return String.format("$%d",values.size());
        }
        String stringValue = (String)value;
        if (stringValue.indexOf(0) != '$')
        {
            values.add(value);
            return String.format("$%d",values.size());
        }
        if (stringValue.indexOf(stringValue.length() - 1) != '$')
        {
            values.add(value);
            return String.format("$%d",values.size());
        }
        return QuoteObject(stringValue.substring(1, stringValue.length() - 1),null);
    }

    public String QuoteObject(String field, List<String> collection)
    {
        List<String> rest = collection == null ? new ArrayList<String>() : new ArrayList<String>(collection);
        String[] split;
        Function<String, Boolean> checkIsNaN = value -> {
            try{
                Integer.parseInt(value);
                return true;
            }catch (NumberFormatException ex){
                return false;
            }
        };

        for (int i = 0; i < rest.size(); i++)
        {
            String resti = rest.get(i);
            if (resti.indexOf('.') != 0)
            {
                split = resti.split("\\.");
                rest.remove(0);
                Arrays.asList(split).forEach(s->{rest.add(0,s);});
            }
        }
        Matcher endsInCastMatcher = Regs.EndsInCast.matcher(field);
        if (endsInCastMatcher.find())
        {
            return QuoteObject(endsInCastMatcher.replaceAll(""), rest) + endsInCastMatcher.group();
        }
        Matcher dereferenceOperators = Regs.DereferenceOperators.matcher(field);
        if (dereferenceOperators.find()) {
            String operators = dereferenceOperators.group();
            String[] fields = Regs.DereferenceOperators.split(field);
            return reduce(map(Arrays.asList(fields), (part, i) -> {
                if (0 == (int) i) return QuoteObject((String) part, rest);
                if (checkIsNaN.apply((String) part) && !((String) part).contains("'")) {
                    return String.format("'%s'", part);
                }
                return part;
            }), (a, b, i) -> String.format("%s%d%s", a, dereferenceOperators.group(i - 1), b));
        }

        // Just using *, no collection
        if (field.indexOf('*') == 0 && collection.size() > 0)
        {
            Collections.reverse(rest);
            return "`" + String.join("`.`", rest) + "`.*";
        }
        // Using *, specified collection, used quotes
        else if (field.contains("`.*"))
            return field;

            // Using *, specified collection, didn't use quotes
        else if (field.contains(".*"))
            return '`' + field.split("\\.")[0] + "`.*";

            // No periods specified in field, use explicit `table[, schema[, database] ]`
        else if (field.indexOf('.') == -1)
        {
            Collections.reverse(rest);
            rest.add(Pattern.compile("`").matcher(field).replaceAll(""));
            return '`' + String.join("`.`", rest) + '`';
        }

        // Otherwise, a `.` was in there, just quote whatever was specified
        else
            return '`' + String.join("`.`",Pattern.compile("`").matcher(field).replaceAll("").split("\\.")) + '`';
    }
}
