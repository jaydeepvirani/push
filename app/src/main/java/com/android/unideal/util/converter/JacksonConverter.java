package com.android.unideal.util.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.orhanobut.logger.Logger;
import java.io.IOException;
import java.util.List;

public class JacksonConverter {

  private static final String TAG = "JacksonConverter";

  public static String getStringFromObject(Object inputObject) {
    try {
      String jsonStr = new ObjectMapper().writeValueAsString(inputObject);
      return jsonStr;
    } catch (IOException e) {
      Logger.t(TAG).e(e.getMessage());
    }
    return null;
  }

  public static <T> T getObjectFromJSON(String json, Class<T> genericType) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(json, genericType);
    } catch (Exception e) {
      Logger.t(TAG).e(e.getMessage());
    }
    return null;
  }

  public static <T> List<T> getListTypeFromJSON(String jsonString, Class<?> genericType) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(jsonString,
          TypeFactory.defaultInstance().constructCollectionType(List.class, genericType));
    } catch (IOException e) {
      Logger.t(TAG).e(e.getMessage());
    }
    return null;
  }
}
