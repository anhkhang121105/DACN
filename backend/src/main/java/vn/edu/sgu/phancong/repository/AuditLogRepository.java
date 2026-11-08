package vn.edu.sgu.phancong.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.sgu.phancong.entity.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
