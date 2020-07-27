package br.com.eterniaserver.eternialib.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class SQLTask implements Runnable {

    private final Connections connections;
    private final SQLCallback sqlCallBack;

    SQLTask(Connections Connection, SQLCallback sqlCallBack) {
        this.connections = Connection;
        this.sqlCallBack = sqlCallBack;
    }

    @Override
    public void run() {
        if(connections.isClosed()) return;

        try (Connection connection = connections.getConnection()) {
            sqlCallBack.call(connection);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    void executeAsync() {
        CompletableFuture.runAsync(this);
    }


}
