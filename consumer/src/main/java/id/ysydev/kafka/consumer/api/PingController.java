package id.ysydev.kafka.consumer.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Endpoint ping sederhana untuk cek liveness service consumer.
 */
@RestController
class PingController {
    @GetMapping("/ping")
    Map<String, Object> ping() { return Map.of("ok", true, "service", "consumer"); }
}
