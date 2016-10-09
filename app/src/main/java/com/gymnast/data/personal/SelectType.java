package com.gymnast.data.personal;

import java.io.Serializable;

/**
 * Created by Cymbi on 2016/8/29.
 */
public class SelectType implements Serializable {
    private String typeName;
    private int id;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTypeName() {
        return typeName;
    }
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
