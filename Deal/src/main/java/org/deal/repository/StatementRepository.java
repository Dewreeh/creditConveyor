package org.deal.repository;

import org.deal.model.Statement;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface StatementRepository extends CrudRepository<Statement, UUID> {
}