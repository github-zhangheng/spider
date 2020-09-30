package com.zh.administrativedivision.type;

public enum AreaLevelEnum {
    // 省
    PROVINCE("province", 1),
    // 市
    CITY("city", 2),
    // 县
    COUNTY("county", 3),
    // 镇
    TOWN("town", 4),
    // 村
    VILLAGE("village", 5);

    private String name;
    private int code;

    private AreaLevelEnum(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}