package id.ysydev.kafka.sender.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Endpoint health/ping informal untuk cek cepat service hidup.
 */
@RestController
class PingController {
    @GetMapping("/ping")
    Map<String, Object> ping() { return Map.of("ok", true, "service", "sender"); }
}
