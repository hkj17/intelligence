package com.is.gson;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.is.gson.JsonPolicyDef.Policy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GsonFactory {

    private static Map<Policy, Gson> cache = new ConcurrentHashMap<Policy, Gson>();
    private static Gson gson = createGsonBuilder().create();
    private static Gson commonGson = new Gson();

    public static Gson getGson(){
    	return commonGson;
    }
    
    public static Gson createGson() {
        return gson;
    }

    public static Gson createGson(Policy policy) {
        Gson gson = cache.get(policy);
        if (gson != null) {
            return gson;
        }
        gson = createGsonBuilder()
                .addDeserializationExclusionStrategy(new AnnotatedStrategy(policy))
                .addSerializationExclusionStrategy(new AnnotatedStrategy(policy))
                .create();
        cache.put(policy, gson);
        return gson;
    }


    private static GsonBuilder createGsonBuilder() {
        return new GsonBuilder();
//                .setPrettyPrinting();
//                .serializeNulls();

    }

}
