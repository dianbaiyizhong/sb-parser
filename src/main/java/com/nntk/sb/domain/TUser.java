package com.nntk.sb.domain;

import java.util.ArrayList;
import java.util.Arrays;
import lombok.Data;

/**
 * 商品信息（表：t_user）
 *
 * @author MQG
 * @date null
 */
@Data
public class TUser {
    private Integer id;

    private String username;

    private Byte sex;

    public static TUser.Builder builder() {
        return new TUser.Builder();
    }

    public static class Builder {
        private TUser obj;

        public Builder() {
            this.obj = new TUser();
        }

        public Builder id(Integer id) {
            obj.setId(id);
            return this;
        }

        public Builder username(String username) {
            obj.setUsername(username);
            return this;
        }

        public Builder sex(Byte sex) {
            obj.setSex(sex);
            return this;
        }

        public TUser build() {
            return this.obj;
        }
    }

    public enum Column {
        id("id", "id", "INTEGER", false),
        username("username", "username", "VARCHAR", false),
        sex("sex", "sex", "TINYINT", false);

        private static final String BEGINNING_DELIMITER = "\"";

        private static final String ENDING_DELIMITER = "\"";

        private final String column;

        private final boolean isColumnNameDelimited;

        private final String javaProperty;

        private final String jdbcType;

        public String value() {
            return this.column;
        }

        public String getValue() {
            return this.column;
        }

        public String getJavaProperty() {
            return this.javaProperty;
        }

        public String getJdbcType() {
            return this.jdbcType;
        }

        Column(String column, String javaProperty, String jdbcType, boolean isColumnNameDelimited) {
            this.column = column;
            this.javaProperty = javaProperty;
            this.jdbcType = jdbcType;
            this.isColumnNameDelimited = isColumnNameDelimited;
        }

        public String desc() {
            return this.getEscapedColumnName() + " DESC";
        }

        public String asc() {
            return this.getEscapedColumnName() + " ASC";
        }

        public static Column[] excludes(Column ... excludes) {
            ArrayList<Column> columns = new ArrayList<>(Arrays.asList(Column.values()));
            if (excludes != null && excludes.length > 0) {
                columns.removeAll(new ArrayList<>(Arrays.asList(excludes)));
            }
            return columns.toArray(new Column[]{});
        }

        public static Column[] all() {
            return Column.values();
        }

        public String getEscapedColumnName() {
            if (this.isColumnNameDelimited) {
                return new StringBuilder().append(BEGINNING_DELIMITER).append(this.column).append(ENDING_DELIMITER).toString();
            } else {
                return this.column;
            }
        }

        public String getAliasedEscapedColumnName() {
            return this.getEscapedColumnName();
        }
    }
}