package br.com.eterniaserver.eternialib.database.impl;

import br.com.eterniaserver.eternialib.database.dtos.EntityDataDTO;
import br.com.eterniaserver.eternialib.database.dtos.EntityPrimaryKeyDTO;
import br.com.eterniaserver.eternialib.database.dtos.EntityReferenceDTO;

import java.util.List;

public interface SGBDInterface {

    String jdbcStr(String... args);

    String selectBy(String tableName, List<EntityDataDTO<?>> entityDataDTOs);

    String selectLike(String tableName, EntityDataDTO<?> entityDataDTO);

    String selectAll(String tableName);

    String selectByPrimaryInList(String tableName, EntityPrimaryKeyDTO<?> primaryKeyDTO);

    String selectByPrimary(String tableName, EntityPrimaryKeyDTO<?> primaryKeyDTO);

    String update(String tableName, List<? extends EntityDataDTO<?>> entityDataDTOS, EntityPrimaryKeyDTO<?> primaryKeyDTO);

    String insert(String tableName, List<? extends EntityDataDTO<?>> entityDataDTOS, EntityPrimaryKeyDTO<?> primaryKeyDTO);

    String delete(String tableName, EntityPrimaryKeyDTO<?> primaryKeyDTO);

    String insertWithoutKey(String tableName, List<? extends EntityDataDTO<?>> entityDataDTOS);

    String getLastInsertId(String tableName);

    String buildPrimaryColumn(EntityPrimaryKeyDTO<?> primaryKeyDTO);

    String buildDataColumn(EntityDataDTO<?> dataDTO);

    String buildReferenceColumn(EntityReferenceDTO referenceDTO);

}
