package com.mrkangi.jmongosql;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mrkangi.jmongosql.model.HelperObj;
import com.mrkangi.jmongosql.primitives.TriFunction;

import java.util.List;

public class HelperManager
{
    private static HelperManager helperManager;

    JSONObject defaults;
    JSONObject helpers;

    public HelperManager(JSONObject defaults)
    {
        this.defaults = defaults;
        this.helpers = new JSONObject();
    }

    public HelperObj get(String name)
    {
        if (!this.has(name)) throw new RuntimeException("找不到helper: " + name);
        return (HelperObj)this.helpers.get(name);
    }
    public boolean has(String name)
    {
        return this.helpers.get(name) != null;
    }
    public HelperManager add(String name, TriFunction<String, Object, List<Object>, String> fn, JSONObject options)
    {
        if (options == null) options = new JSONObject();

        for (String key : this.defaults.keySet()) {
            if (options.get(key) == null) options.put(key,this.defaults.get(key));
        }
        HelperObj helperObj = new HelperObj();
        helperObj.setFn(fn);
        helperObj.setOptions(options);
        this.helpers.put(name, helperObj);
        return this;
    }
    public HelperManager add(String name, TriFunction<String, Object, List<Object>, String> fn){
        return this.add(name,fn,null);
    }
    public HelperManager register(String name, TriFunction<String, Object, List<Object>, String> fn, JSONObject options)
    {
        return this.add(name, fn, options);
    }
}