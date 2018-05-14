package com.mrkangi.jmongosql.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.function.BiConsumer;

public class JSONExtension
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
