package org.deal.repository;

import org.deal.model.Client;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ClientRepository extends CrudRepository<Client, UUID> {
    @Override
    <S extends Client> S save(S entity);
}
