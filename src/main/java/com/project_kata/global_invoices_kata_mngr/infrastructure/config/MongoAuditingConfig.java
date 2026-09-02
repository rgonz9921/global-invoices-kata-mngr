package com.project_kata.global_invoices_kata_mngr.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@EnableMongoAuditing(auditorAwareRef = "securityAuditorAware")
public class MongoAuditingConfig {
}
