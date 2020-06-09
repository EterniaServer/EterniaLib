package br.com.eterniaserver.eternialib.sql;

import java.sql.Connection;
import java.sql.SQLException;

public interface SQLCallback {

    void call(Connection t) throws SQLException;

}
