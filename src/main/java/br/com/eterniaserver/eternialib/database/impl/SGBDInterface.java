package br.com.eterniaserver.eternialib.database.impl;

import br.com.eterniaserver.eternialib.database.dtos.EntityDataDTO;
import br.com.eterniaserver.eternialib.database.dtos.EntityPrimaryKeyDTO;
import br.com.eterniaserver.eternialib.database.dtos.EntityReferenceDTO;

import java.util.List;

public interface SGBDInterface {

    String jdbcStr();

    String selectAll(String tableName);

    <T> String selectByPrimary(String tableName, EntityPrimaryKeyDTO<T> primaryKeyDTO);

    <T> String update(String tableName, List<EntityDataDTO<T>> entityDataDTOS, EntityPrimaryKeyDTO<T> primaryKeyDTO);

    <T> String insert(String tableName, List<EntityDataDTO<T>> entityDataDTOS, EntityPrimaryKeyDTO<T> primaryKeyDTO);

    <T> String delete(String tableName, EntityPrimaryKeyDTO<T> primaryKeyDTO);

    <T> String insertWithoutKey(String tableName, List<EntityDataDTO<T>> entityDataDTOS);

    <T> String getLastInsertId(String tableName);

    <T> String buildPrimaryColumn(EntityPrimaryKeyDTO<T> primaryKeyDTO);

    <T> String buildDataColumn(EntityDataDTO<T> dataDTO);

    String buildReferenceColumn(EntityReferenceDTO referenceDTO);

}
