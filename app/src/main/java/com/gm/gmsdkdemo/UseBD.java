package com.gm.gmsdkdemo;

import com.google.gson.Gson;

public class UseBD {
    /**
     * 检查是否包含百度开关
     *
     * @param jsonString
     * @return
     */
    public static boolean checkOpenDatasdkWithBd(String jsonString) {
        Gson gson = new Gson();
        DataObject dataObject = gson.fromJson(jsonString, DataObject.class);

        if (dataObject != null && dataObject.open_datasdk != null && dataObject.open_datasdk.contains("bd")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 提取百度开关的字段
     *
     * @param jsonString
     * @return
     */
    public static BdBean extractBdFields(String jsonString) {
        Gson gson = new Gson();
        DataObject dataObject = gson.fromJson(jsonString, DataObject.class);

        if (dataObject != null && dataObject.data != null && dataObject.data.bd != null) {
            return dataObject.data.bd;
        } else {
            return null;
        }
    }

    public static class DataObject {
        private String open_datasdk;
        private DataBean data;
    }

    public static class DataBean {
        private Object jrtt;
        private Object uc;
        private Object tc;
        private Object ks;
        private Object pdd;
        private BdBean bd;
    }

    public static class BdBean {
        public String bdAppId;
        public String bdAppSecret;
    }
}
