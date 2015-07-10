/**
 * 序列化map供Bundle传递map使用
 * Created  on 13-12-9.
 */
package com.easivend.common;

import java.io.Serializable;
import java.util.Map;


public class SerializableMap implements Serializable {
	private Map<String,Integer> map;
	 
    public Map<String, Integer> getMap() {
        return map;
    }
 
    public void setMap(Map<String, Integer> map) {
        this.map = map;
    }
}
