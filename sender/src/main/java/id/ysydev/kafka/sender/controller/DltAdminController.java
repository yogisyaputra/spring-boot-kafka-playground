package id.ysydev.kafka.sender.controller;

import id.ysydev.kafka.sender.dlt.dto.DltEventDto;
import id.ysydev.kafka.sender.dlt.service.DltReplayService;
import id.ysydev.kafka.sender.dlt.repository.DltEventRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/admin/dlt")
public class DltAdminController {

    private final DltEventRepository repo;
    private final DltReplayService replay;

    public DltAdminController(DltEventRepository repo, DltReplayService replay) {
        this.repo = repo;
        this.replay = replay;
    }

    @GetMapping
    public List<DltEventDto> list(
            @RequestParam(name = "topic", required = false) String topic,
            @RequestParam(name = "page",  defaultValue = "0") int page,
            @RequestParam(name = "size",  defaultValue = "50") int size
    ) {
        var pageable = PageRequest.of(Math.max(page, 0), Math.min(size, 200));
        var list = (topic == null || topic.isBlank())
                ? repo.findAllByOrderByReceivedAtDesc(pageable)
                : repo.findByOriginalTopicOrderByReceivedAtDesc(topic, pageable);

        return list.stream()
                .map(e -> new DltEventDto(
                        e.getId(), e.getOriginalTopic(), e.getMsgKey(),
                        e.getExceptionClass(), e.getExceptionMessage(), e.getReceivedAt()))
                .toList();
    }

    @PostMapping("/{id}/replay")
    public Map<String, Object> replay(@PathVariable("id") UUID id,
                                      @RequestHeader(name="X-User", required=false, defaultValue="admin") String user) {
        var e = replay.replay(id, user);
        return Map.of(
                "status", "REPLAYED",
                "id", e.getId(),
                "toTopic", e.getOriginalTopic(),
                "replayedAt", e.getReplayedAt(),
                "replayedBy", e.getReplayedBy()
        );
    }
}
