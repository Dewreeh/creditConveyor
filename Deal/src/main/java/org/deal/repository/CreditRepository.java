package org.deal.repository;

import org.deal.model.Credit;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface CreditRepository extends CrudRepository<Credit, UUID> {
    @Override
    <S extends Credit> S save(S entity);
}
