package com.mrkangi.jmongosql.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mrkangi.jmongosql.primitives.TriFunction;
import lombok.Data;

import java.util.List;
@Data
public class HelperObj extends JSONObject {
    private JSONObject options;
    private TriFunction<String,Object,List<Object>,String> fn;
}
