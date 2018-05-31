package com.mrkangi.jmongosql;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class Conditional {
    private static <T> List<T> JSONArraySelect(JSONArray array, BiFunction<Object, Integer, T> function) {
        List<T> results = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            results.add(function.apply(array.get(i), i));
        }
        return results;
    }

    public static void Init() {
        HelperManager conditionals = ConditionalHelpers.getInstance();


        /**
         * Querying where column is equal to a value
         * @param column {String}  - Column name either table.column or column
         * @param value  {Mixed}   - What the column should be equal to
         */
        conditionals.add("$equals", (column, value, values) ->
        {
            String equator = "=";
            return column + " " + ((value.toString().equals("true") || value.toString().equals("false")) ? "is" : "=") + " " + value;
        });

        /**
         * Querying where column is not equal to a value
         * @param column {String}  - Column name either table.column or column
         * @param value  {Mixed}   - What the column should be equal to
         */
        conditionals.add("$ne", (column, value, values) ->
                column + " != " + value);

        /**
         * Querying where column is greater than a value
         * @param column {String}  - Column name either table.column or column
         * @param value  {Mixed}   - What the column should be greater than
         */
        conditionals.add("$gt", (column, value, values) ->
                column + " > " + value);

        /**
         * Querying where column is greater than a value
         * @param column {String}  - Column name either table.column or column
         * @param value  {Mixed}   - What the column should be greater than
         */
        conditionals.add("$gte", (column, value, values) ->
        {
            return column + " >= " + value;
        });

        /**
         * Querying where column is less than a value
         * @param column {String}  - Column name either table.column or column
         * @param value  {Mixed}   - What the column should be less than
         */
        conditionals.add("$lt", (column, value, values) ->
        {
            return column + " < " + value;
        });

        /**
         * Querying where column is less than or equal to a value
         * @param column {String}  - Column name either table.column or column
         * @param value  {Mixed}   - What the column should be lte to
         */
        conditionals.add("$lte", (column, value, values) ->
        {
            return column + " <= " + value;
        });

        /**
         * Querying where value is null
         * @param column {String}  - Column name either table.column or column
         */
        conditionals.add("$null", (column, value, values) ->
        {
            return column + " is" + (value.toString().equals("false") ? " not" : "") + " null";
        });

        /**
         * Querying where value is null
         * @param column {String}  - Column name either table.column or column
         */
        conditionals.add("$notNull", (column, value, values) ->
        {
            return column + " is" + (value.toString().equals("false") ? "" : " not") + " null";
        });

        /**
         * Querying where column is like a value
         * @param column {String}  - Column name either table.column or column
         * @param value  {Mixed}   - What the column should be like
         */
        conditionals.add("$like", (column, value, values) ->
        {
            return column + " like " + value;
        });

        /**
         * Querying where column is like a value (case insensitive)
         * @param column {String}  - Column name either table.column or column
         * @param value  {Mixed}   - What the column should be like
         */
        conditionals.add("$ilike", (column, value, values) ->
        {
            return column + " not like " + value;
        });

        /**
         * Querying where column is in a set
         *
         * Values
         * - String, no explaination necessary
         * - Array, joins escaped values with a comma
         * - , executes , expects String in correct format
         *  |- Useful for sub-queries
         *
         * @param column {String}  - Column name either table.column or column
         * @param value  {Mixed}   - String|Array|
         */
        conditionals.add("$in", (column, set, values) ->
        {
            if (set instanceof JSONArray) {
                JSONArray _set = (JSONArray) set;
                return column + " in (" + String.join(", ", JSONArraySelect(_set, (val, i) ->
                {
                    values.add(val);
                    return String.format("$%d", values.size());
                })) + ")";
            }

            throw new RuntimeException("不支持的类型");
        }, new JSONObject() {
            {
                put("cascade", false);
            }
        });

        /**
         * Querying where column is not in a set
         *
         * Values
         * - String, no explaination necessary
         * - Array, joins escaped values with a comma
         * - , executes , expects String in correct format
         *  |- Useful for sub-queries
         *
         * @param column {String}  - Column name either table.column or column
         * @param value  {Mixed}   - String|Array|
         */
        conditionals.add("$nin", (column, set, values) ->
        {
            if (set instanceof JSONArray) {
                JSONArray _set = (JSONArray) set;
                return column + " not in (" + String.join(", ", JSONArraySelect(_set, (val, i) ->
                {
                    values.add(val);
                    return String.format("$%d", values.size());
                })) + ")";
            }
            throw new RuntimeException("不支持的类型");
        }, new JSONObject() {
            {
                put("cascade", false);
            }
        });


        conditionals.add("$years_ago", (column, value, values) ->
        {
            return column + " >= now() - interval " + value + " year";
        });

        conditionals.add("$months_ago", (column, value, values) ->
        {
            return column + " >= now() - interval " + value + " month";
        });

        conditionals.add("$days_ago", (column, value, values) ->
        {
            return column + " >= now() - interval " + value + " day";
        });

        conditionals.add("$hours_ago", (column, value, values) ->
        {
            return column + " >= now() - interval " + value + " hour";
        });

        conditionals.add("$minutes_ago", (column, value, values) ->
        {
            return column + " >= now() - interval " + value + " minute";
        });

        conditionals.add("$seconds_ago", (column, value, values) ->
        {
            return column + " >= now() - interval " + value + " second";
        });

    }
}
