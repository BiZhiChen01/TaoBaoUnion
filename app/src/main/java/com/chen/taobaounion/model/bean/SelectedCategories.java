package com.chen.taobaounion.model.bean;

import java.util.List;

public class SelectedCategories {

    private Boolean success;
    private Integer code;
    private String message;
    private List<DataBean> data;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "SelectedPageCategory{" +
                "success=" + success +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

    public static class DataBean {
        private Integer type;
        private Integer favorites_id;
        private String favorites_title;

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public Integer getFavorites_id() {
            return favorites_id;
        }

        public void setFavorites_id(Integer favorites_id) {
            this.favorites_id = favorites_id;
        }

        public String getFavorites_title() {
            return favorites_title;
        }

        public void setFavorites_title(String favorites_title) {
            this.favorites_title = favorites_title;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "type=" + type +
                    ", favorites_id=" + favorites_id +
                    ", favorites_title='" + favorites_title + '\'' +
                    '}';
        }
    }
}
