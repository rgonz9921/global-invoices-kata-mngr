package com.project_kata.global_invoices_kata_mngr.infrastructure.persistence;

import com.project_kata.global_invoices_kata_mngr.domain.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
