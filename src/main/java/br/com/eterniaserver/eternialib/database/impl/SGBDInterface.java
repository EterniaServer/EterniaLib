package br.com.eterniaserver.eternialib.database.impl;

import br.com.eterniaserver.eternialib.database.dtos.EntityDataDTO;
import br.com.eterniaserver.eternialib.database.dtos.EntityPrimaryKeyDTO;
import br.com.eterniaserver.eternialib.database.dtos.EntityReferenceDTO;

import java.util.List;

public interface SGBDInterface {

    String jdbcStr();

    String selectAll(String tableName);

    String selectByPrimary(String tableName, EntityPrimaryKeyDTO primaryKeyDTO, Object primaryKey);

    String insert(String tableName, List<EntityDataDTO> entityDataDTOS, EntityPrimaryKeyDTO primaryKeyDTO);

    String insertWithoutKey(String tableName, List<EntityDataDTO> entityDataDTOS);

    String getLastInsertId(String tableName);

    String buildPrimaryColumn(EntityPrimaryKeyDTO primaryKeyDTO);

    String buildDataColumn(EntityDataDTO dataDTO);

    String buildReferenceColumn(EntityReferenceDTO referenceDTO);

}
