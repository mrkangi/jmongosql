package com.mrkangi.jmongosql;

import com.alibaba.fastjson.JSONObject;

public class ConditionalHelpers {
    private ConditionalHelpers() { }
    private static HelperManager helperManager;
    public static HelperManager getInstance()
    {
        if (helperManager == null){
            JSONObject object = new JSONObject();
            object.put("cascade",true);
            helperManager = new HelperManager(object);
        }
        return helperManager;
    }
}
