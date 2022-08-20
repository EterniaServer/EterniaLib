package br.com.eterniaserver.eternialib.database;

import br.com.eterniaserver.eternialib.database.dtos.EntityDataDTO;
import br.com.eterniaserver.eternialib.database.dtos.EntityPrimaryKeyDTO;
import br.com.eterniaserver.eternialib.database.dtos.EntityReferenceDTO;

public interface SGBDInterface {

    String jdbcStr();

    String selectByPrimary(String tableName, EntityPrimaryKeyDTO primaryKeyDTO, Object primaryKey);

    String buildPrimaryColumn(EntityPrimaryKeyDTO primaryKeyDTO);

    String buildDataColumn(EntityDataDTO dataDTO);

    String buildReferenceColumn(EntityReferenceDTO referenceDTO);

}
