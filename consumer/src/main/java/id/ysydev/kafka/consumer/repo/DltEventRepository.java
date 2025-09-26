package id.ysydev.kafka.consumer.repo;
import id.ysydev.kafka.consumer.entity.DltEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface DltEventRepository extends JpaRepository<DltEvent, UUID> {}
