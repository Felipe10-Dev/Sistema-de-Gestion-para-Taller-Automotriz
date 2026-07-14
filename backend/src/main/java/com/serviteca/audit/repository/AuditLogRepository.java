package com.serviteca.audit.repository;

import com.serviteca.audit.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByUsernameOrderByTimestampDesc(String username);

    List<AuditLog> findByEmpresaIdOrderByTimestampDesc(Long empresaId);

    List<AuditLog> findByActionOrderByTimestampDesc(String action);

    List<AuditLog> findBySuccessFalseOrderByTimestampDesc();

    List<AuditLog> findByTimestampBetweenOrderByTimestampDesc(LocalDateTime from, LocalDateTime to);

    List<AuditLog> findByUsernameAndTimestampBetweenOrderByTimestampDesc(
            String username, LocalDateTime from, LocalDateTime to);
}
