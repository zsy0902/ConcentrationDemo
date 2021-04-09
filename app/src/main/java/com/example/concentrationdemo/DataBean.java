package com.example.concentrationdemo;
public  class DataBean {
    /**
     * ID : 0
     * categoryName : 目录名称
     * state : 1
     */

    private String ID;
    private String categoryName;
    private String state;

    public DataBean(String ID, String categoryName, String state) {
        this.ID = ID;
        this.categoryName = categoryName;
        this.state = state;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}


