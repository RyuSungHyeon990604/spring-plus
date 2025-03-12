package org.example.expert.domain.manager.repository;

import org.example.expert.domain.manager.entity.ManagerLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaveMangerLogRepository extends JpaRepository<ManagerLog, Long> {
}
