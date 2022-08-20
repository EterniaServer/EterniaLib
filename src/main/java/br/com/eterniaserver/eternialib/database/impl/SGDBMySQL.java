package br.com.eterniaserver.eternialib.database.impl;

import br.com.eterniaserver.eternialib.database.SGBDInterface;
import br.com.eterniaserver.eternialib.database.dtos.EntityDataDTO;
import br.com.eterniaserver.eternialib.database.dtos.EntityPrimaryKeyDTO;
import br.com.eterniaserver.eternialib.database.dtos.EntityReferenceDTO;
import br.com.eterniaserver.eternialib.database.enums.FieldType;
import br.com.eterniaserver.eternialib.database.enums.ReferenceMode;

import java.util.HashMap;
import java.util.Map;

public class SGDBMySQL implements SGBDInterface {

    private final Map<FieldType, String> typeMap;
    private final Map<ReferenceMode, String> referenceMap;

    public SGDBMySQL() {
        this.typeMap = new HashMap<>();
        this.referenceMap = new HashMap<>();

        typeMap.put(FieldType.STRING, "VARCHAR(256)");
        typeMap.put(FieldType.TEXT, "TEXT");
        typeMap.put(FieldType.INTEGER, "BIGINT");
        typeMap.put(FieldType.DOUBLE, "DOUBLE(64, 32)");
        typeMap.put(FieldType.DECIMAL, "DECIMAL(32, 8)");
        typeMap.put(FieldType.DATE, "DATE");
        typeMap.put(FieldType.DATETIME, "DATETIME");

        referenceMap.put(ReferenceMode.RESTRICT, "");
        referenceMap.put(ReferenceMode.CASCADE, " ON DELETE CASCADE");
        referenceMap.put(ReferenceMode.SET_NULL, " ON DELETE SET NULL");
    }

    @Override
    public String jdbcStr() {
        return "mysql";
    }

    @Override
    public String selectByPrimary(String tableName, EntityPrimaryKeyDTO primaryKeyDTO, Object primaryKey) {
        return "SELECT * FROM " + tableName +
                " WHERE " + tableName + "." + primaryKeyDTO.columnName() +
                " = " + primaryKey;
    }

    @Override
    public String buildPrimaryColumn(EntityPrimaryKeyDTO primaryKeyDTO) {
        StringBuilder builder = new StringBuilder();

        builder.append(primaryKeyDTO.columnName()).append(" ");
        builder.append(typeMap.get(primaryKeyDTO.type()));

        if (primaryKeyDTO.autoIncrement()) {
            builder.append(" AUTO_INCREMENT PRIMARY KEY");
        }
        else {
            builder.append(" PRIMARY KEY NOT NULL");
        }

        return builder.toString();
    }

    @Override
    public String buildDataColumn(EntityDataDTO dataDTO) {
        StringBuilder builder = new StringBuilder();

        builder.append(dataDTO.columnName()).append(" ");
        builder.append(typeMap.get(dataDTO.type()));

        if (dataDTO.notNull()) {
            builder.append(" NOT NULL");
        }

        return builder.toString();
    }

    @Override
    public String buildReferenceColumn(EntityReferenceDTO referenceDTO) {

        return "FOREIGN KEY (" + referenceDTO.columnName() + ")" +
                " REFERENCES " + referenceDTO.referenceTableName() + "(" +
                referenceDTO.referenceColumnName() + ")" +
                referenceMap.get(referenceDTO.mode());
    }
}
