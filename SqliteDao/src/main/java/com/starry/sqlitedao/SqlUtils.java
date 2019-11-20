package com.starry.sqlitedao;

import android.net.Uri;

/**
 * Helper class to create SQL statements as used by greenDAO internally.
 */
public class SqlUtils {
    private final static char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static StringBuilder appendProperty(StringBuilder builder, Property property) {
        builder.append(property.columnName);
        return builder;
    }

    public static StringBuilder appendColumn(StringBuilder builder, String column) {
        builder.append('"').append(column).append('"');
        return builder;
    }

    public static StringBuilder appendColumns(StringBuilder builder, String[] columns) {
        int length = columns.length;
        for (int i = 0; i < length; i++) {
            builder.append('"').append(columns[i]).append('"');
            if (i < length - 1) {
                builder.append(',');
            }
        }
        return builder;
    }

    public static StringBuilder appendPlaceholders(StringBuilder builder, int count) {
        for (int i = 0; i < count; i++) {
            if (i < count - 1) {
                builder.append("?,");
            } else {
                builder.append('?');
            }
        }
        return builder;
    }

    public static StringBuilder appendColumnsEqualPlaceholders(StringBuilder builder, String[] columns) {
        for (int i = 0; i < columns.length; i++) {
            appendColumn(builder, columns[i]).append("=?");
            if (i < columns.length - 1) {
                builder.append(',');
            }
        }
        return builder;
    }

    public static String escapeBlobArgument(byte[] bytes) {
        return "X'" + toHex(bytes) + '\'';
    }

    public static String toHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int byteValue = bytes[i] & 0xFF;
            hexChars[i * 2] = HEX_ARRAY[byteValue >>> 4];
            hexChars[i * 2 + 1] = HEX_ARRAY[byteValue & 0x0F];
        }
        return new String(hexChars);
    }

    public static long parseUri(Uri uri) {
        long id = -1;
        String lastPathSegment = uri.getLastPathSegment();
        try {
            id = Long.parseLong(lastPathSegment);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return id;
    }

}
