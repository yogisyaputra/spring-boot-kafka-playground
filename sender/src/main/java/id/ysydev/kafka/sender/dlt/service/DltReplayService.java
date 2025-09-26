package id.ysydev.kafka.sender.dlt.service;

import id.ysydev.kafka.sender.dlt.entity.DltEvent;

import java.util.UUID;

public interface DltReplayService {
    DltEvent replay(UUID id, String replayedBy);
}
