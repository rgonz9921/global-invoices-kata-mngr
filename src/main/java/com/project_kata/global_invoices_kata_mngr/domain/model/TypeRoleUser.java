package com.project_kata.global_invoices_kata_mngr.domain.model;

/**
 * Roles del sistema (RF-05).
 * <ul>
 *     <li>{@code OPERADOR}: crea facturas y ve el listado. No accede al Dashboard.</li>
 *     <li>{@code AUDITOR}: ve el Dashboard y el listado. No puede crear facturas.</li>
 * </ul>
 * Spring Security antepone el prefijo {@code ROLE_} al mapear a autoridad.
 */
public enum TypeRoleUser {
    OPERADOR,
    AUDITOR
}
