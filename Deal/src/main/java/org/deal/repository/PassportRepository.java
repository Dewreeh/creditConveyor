package org.deal.repository;

import org.deal.model.Passport;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface PassportRepository extends CrudRepository<Passport, UUID> {
    @Override
    <S extends Passport> S save(S entity);
}
