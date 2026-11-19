package com.example.iot.actiondispatcher;

import java.util.List;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;

public interface DecisionOutboxRepository extends JpaRepository<DecisionOutboxRecord, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from DecisionOutboxRecord o where o.publishedAt is null order by o.createdAt asc")
    List<DecisionOutboxRecord> lockPending(Pageable pageable);
}
