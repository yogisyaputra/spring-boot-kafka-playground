package id.ysydev.kafka.consumer.dlt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface DltEventRepository extends JpaRepository<DltEvent, UUID> {}
