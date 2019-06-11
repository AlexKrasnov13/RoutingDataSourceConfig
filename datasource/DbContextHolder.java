package ru.lib.db.datasource;

public class DbContextHolder {

    private static final ThreadLocal<DbType> contextHolder = new ThreadLocal<>();

    public static void setDbType(DbType dbType) {
        if(dbType == null){
            throw new NullPointerException();
        }
        contextHolder.set(dbType);
    }

    public static DbType getDbType() {
        return contextHolder.get();
    }

    public static void clearDbType() {
        contextHolder.remove();
    }
}
