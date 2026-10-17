package com.isil.edu.pe.spedidos.compensacion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidenteConciliacionRepository extends JpaRepository<IncidenteConciliacion, Long> {
}
