package com.project_kata.global_invoices_kata_mngr.infrastructure.persistence;

import com.project_kata.global_invoices_kata_mngr.domain.model.Invoice;
import com.project_kata.global_invoices_kata_mngr.domain.model.InvoiceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends MongoRepository<Invoice, String> {

    Page<Invoice> findByType(InvoiceType type, Pageable pageable);
}
