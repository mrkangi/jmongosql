package com.mrkangi.jmongosql;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.sun.xml.internal.ws.util.StringUtils;
import lombok.experimental.var;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;

class JSONExtension
{
    public static void foreach(Object json, BiConsumer<String,Object> action){
        if(json instanceof JSONObject){
            JSONObject jobj = (JSONObject)json;
            for(String key : jobj.keySet()){
                action.accept(key,jobj.get(key));
            }
        }else if(json instanceof JSONArray){
            JSONArray jarr = (JSONArray)json;
            for(int i = 0;i<jarr.size();i++){
                action.accept(Integer.toString(i),jarr.get(i));
            }
        }
    }
}

class IntegerParser{
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

public class ConditionBuilder {
    String where;
    List<String> table;
    List<Object> values;
    HelperManager helpers;
    private Utils utils;

    private List<Object> parameters;

    public List<Object> getParameters() {
        if (parameters == null) {
            parameters = new ArrayList<>(values);
        }
        return parameters;
    }


    public ConditionBuilder(String where, List<String> table, List<Object> values) {
        this.where = where;
        this.table = table;
        if (values == null)
            this.values = new ArrayList<>();
        else
            this.values = values;
        Conditional.Init();
        this.helpers = ConditionalHelpers.getInstance();
        this.utils = new Utils();
    }

    public String BuildConditions(Object _where, String condition, String column, String joiner) {
        final String _joiner = Strings.isNullOrEmpty(joiner) ? " and " : joiner;
        final String _column = !Strings.isNullOrEmpty(column) ? utils.QuoteObject(column, table) : column;
        List<String> conditions = new ArrayList<String>();
        JSONExtension.foreach(_where, (key, value) ->
        {
            String result;
            IntegerParser integerParser = new IntegerParser(key);
//            if (value == JTokenType.Undefined) return;
            if (value == null) {
                JSONObject tmp = new JSONObject();
                tmp.put(key, new JSONObject() {
                    {
                        put("$null", true);
                    }
                });
                conditions.add(BuildConditions(tmp, condition, _column, _joiner));
                return;
            }
            if ((value instanceof JSONObject || value instanceof JSONArray) && !(value instanceof Date) && !com.mrkangi.jmongosql.Buffer.isBuffer(value)) {
                if (helpers.has(key)) {
                    if (helpers.get(key).getOptions().getBoolean("cascade")) {
                        result = BuildConditions(value, key, _column, null);
                        if (!Strings.isNullOrEmpty(result)) conditions.add(result);
                    } else {
                        result = helpers.get(key).getFn().apply(_column, value, values);
                        if (!Strings.isNullOrEmpty(result)) conditions.add(result);
                    }
                } else if (key.equals("$or")) {
                    result = BuildConditions(value, condition, _column, " or ");
                    if (!Strings.isNullOrEmpty(result)) conditions.add(result);
                } else if (key.equals("$and")) {
                    result = BuildConditions(value, condition, _column, " and ");
                    if (!Strings.isNullOrEmpty(result)) conditions.add(result);
                } else if (integerParser.canParse() && integerParser.getParsedValue() >= 0) {
                    result = BuildConditions(value, condition, _column, null);
                    if (!Strings.isNullOrEmpty(result)) conditions.add(result);
                } else {
                    result = BuildConditions(value, condition, key, null);
                    if (!Strings.isNullOrEmpty(result)) conditions.add(result);
                }
                return;
            }

            if (helpers.has(key)) {
                conditions.add(helpers.get(key).getFn().apply(_column, utils.Parameterize(value, values), values));
            } else if (integerParser.canParse() && integerParser.getParsedValue() >= 0) {
                conditions.add(helpers.get(condition).getFn().apply(_column, utils.Parameterize(value, values), values));
            } else {
                conditions.add(helpers.get(condition).getFn().apply(utils.QuoteObject(key, table), utils.Parameterize(value, values), values));
            }
        });
        if (conditions.size() > 1) return String.format("(%s)", String.join(_joiner, conditions));
        if (conditions.size() == 1) return conditions.get(0);
        return "";
    }


    public String build() {
        String result = BuildConditions(JSON.parse(where), "$equals", null, null);
        if (Strings.isNullOrEmpty(result)) return "";
        if (result.indexOf(0) == '(') return result.substring(1, result.length() - 2);
        return result;
    }
}