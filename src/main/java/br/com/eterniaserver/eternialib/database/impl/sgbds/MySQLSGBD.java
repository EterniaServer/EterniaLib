package br.com.eterniaserver.eternialib.database.impl.sgbds;

import br.com.eterniaserver.eternialib.database.dtos.EntityDataDTO;
import br.com.eterniaserver.eternialib.database.dtos.EntityPrimaryKeyDTO;
import br.com.eterniaserver.eternialib.database.dtos.EntityReferenceDTO;
import br.com.eterniaserver.eternialib.database.enums.FieldType;
import br.com.eterniaserver.eternialib.database.enums.ReferenceMode;
import br.com.eterniaserver.eternialib.database.impl.SGBDInterface;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class MySQLSGBD implements SGBDInterface {

    protected final Map<FieldType, String> typeMap;
    protected final Map<ReferenceMode, String> referenceMap;

    public MySQLSGBD() {
        this.typeMap = new EnumMap<>(FieldType.class);
        this.referenceMap = new EnumMap<>(ReferenceMode.class);

        typeMap.put(FieldType.UUID, "CHAR(36)");
        typeMap.put(FieldType.STRING, "VARCHAR(256)");
        typeMap.put(FieldType.TEXT, "TEXT");
        typeMap.put(FieldType.INTEGER, "BIGINT");
        typeMap.put(FieldType.DOUBLE, "DOUBLE(64, 32)");
        typeMap.put(FieldType.DECIMAL, "DECIMAL(32, 8)");
        typeMap.put(FieldType.DATE, "DATE");
        typeMap.put(FieldType.TIMESTAMP, "DATETIME");

        referenceMap.put(ReferenceMode.RESTRICT, "");
        referenceMap.put(ReferenceMode.CASCADE, " ON DELETE CASCADE");
        referenceMap.put(ReferenceMode.SET_NULL, " ON DELETE SET NULL");
    }

    @Override
    public String jdbcStr() {
        return "mysql://";
    }

    @Override
    public String selectAll(String tableName) {
        return "SELECT * FROM %s;".formatted(tableName);
    }

    @Override
    public <T> String selectByPrimary(String tableName, EntityPrimaryKeyDTO<T> primaryKeyDTO) {
        return "SELECT * FROM %s WHERE %s = ?;".formatted(tableName, primaryKeyDTO.getColumnName());
    }

    @Override
    public <T> String update(String tableName, List<EntityDataDTO<T>> entityDataDTOS, EntityPrimaryKeyDTO<T> primaryKeyDTO) {
        StringBuilder builder = new StringBuilder();

        builder.append("UPDATE ").append(tableName).append(" SET ");
        for (int i = 0; i < entityDataDTOS.size(); i++) {
            EntityDataDTO<T> entityDataDTO = entityDataDTOS.get(i);
            builder.append(entityDataDTO.getColumnName()).append(" = ");
            builder.append("?");

            if (i + 1 != entityDataDTOS.size()) {
                builder.append(", ");
            }
        }
        builder.append(" WHERE ").append(primaryKeyDTO.getColumnName());
        builder.append(" = ").append("?").append(";");

        return builder.toString();
    }

    @Override
    public <T> String insert(String tableName, List<EntityDataDTO<T>> entityDataDTOS, EntityPrimaryKeyDTO<T> primaryKeyDTO) {
        StringBuilder builder = new StringBuilder();

        builder.append("INSERT INTO ").append(tableName).append(" (");
        builder.append(primaryKeyDTO.getColumnName());
        if (!entityDataDTOS.isEmpty()) {
            builder.append(", ");
        }
        buildInsert(builder, entityDataDTOS);
        if (!entityDataDTOS.isEmpty()) {
            builder.append(", ");
        }
        builder.append("?);");

        return builder.toString();
    }

    @Override
    public <T> String delete(String tableName, EntityPrimaryKeyDTO<T> primaryKeyDTO) {
        return "DELETE FROM %s WHERE %s = ?;".formatted(tableName, primaryKeyDTO.getColumnName());
    }

    @Override
    public <T> String insertWithoutKey(String tableName, List<EntityDataDTO<T>> entityDataDTOS) {
        StringBuilder builder = new StringBuilder();

        builder.append("INSERT INTO ").append(tableName).append(" (");
        buildInsert(builder, entityDataDTOS);
        builder.append(");");

        return builder.toString();
    }

    private <T> void buildInsert(StringBuilder builder, List<EntityDataDTO<T>> entityDataDTOS) {
        for (int i = 0; i < entityDataDTOS.size(); i++) {
            EntityDataDTO<T> entityDataDTO = entityDataDTOS.get(i);
            builder.append(entityDataDTO.getColumnName());

            if (i + 1 != entityDataDTOS.size()) {
                builder.append(", ");
            }
        }
        builder.append(") VALUES (");
        for (int i = 0; i < entityDataDTOS.size(); i++) {
            builder.append("?");
            if (i + 1 != entityDataDTOS.size()) {
                builder.append(", ");
            }
        }
    }

    @Override
    public String getLastInsertId(String tableName) {
        return "SELECT LAST_INSERT_ID();";
    }

    @Override
    public <T> String buildPrimaryColumn(EntityPrimaryKeyDTO<T> primaryKeyDTO) {
        StringBuilder builder = new StringBuilder();

        builder.append(primaryKeyDTO.getColumnName()).append(" ");
        builder.append(typeMap.get(primaryKeyDTO.getFieldType()));

        if (primaryKeyDTO.isAutoIncrement()) {
            builder.append(" AUTO_INCREMENT PRIMARY KEY");
        }
        else {
            builder.append(" PRIMARY KEY NOT NULL");
        }

        return builder.toString();
    }

    @Override
    public <T> String buildDataColumn(EntityDataDTO<T> dataDTO) {
        StringBuilder builder = new StringBuilder();

        builder.append(dataDTO.getColumnName()).append(" ");
        builder.append(typeMap.get(dataDTO.getFieldType()));

        if (dataDTO.isNotNull()) {
            builder.append(" NOT NULL");
        }

        return builder.toString();
    }

    @Override
    public String buildReferenceColumn(EntityReferenceDTO referenceDTO) {
        return "FOREIGN KEY (%s) REFERENCES %s (%s)%s".formatted(
                referenceDTO.columnName(),
                referenceDTO.referenceTableName(),
                referenceDTO.referenceColumnName(),
                referenceMap.get(referenceDTO.mode())
        );
    }
}
