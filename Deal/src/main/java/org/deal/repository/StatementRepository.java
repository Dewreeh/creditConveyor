package org.deal.repository;

import org.deal.model.Statement;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface StatementRepository extends CrudRepository<Statement, UUID> {
    @Override
    <S extends Statement> S save(S entity);
    <S extends  Statement> S getByStatementId(UUID uuid);

}
