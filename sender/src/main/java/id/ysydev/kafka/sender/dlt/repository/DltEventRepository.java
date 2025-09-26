package id.ysydev.kafka.sender.dlt.repository;

import id.ysydev.kafka.sender.dlt.entity.DltEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DltEventRepository extends JpaRepository<DltEvent, UUID> {
    List<DltEvent> findByOriginalTopicOrderByReceivedAtDesc(String topic, Pageable pageable);
    List<DltEvent> findAllByOrderByReceivedAtDesc(Pageable pageable);
}
