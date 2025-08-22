package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListConsumerGroupOffsetsResult;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class KafkaAdminService {

    private final AdminClient adminClient;
    private static final String CONSUMER_GROUP_ID = "reserve-service-group";

    public long getConsumerGroupLag(String topicName) throws ExecutionException, InterruptedException {
        // 1. 컨슈머 그룹의 현재 오프셋 정보 가져오기
        ListConsumerGroupOffsetsResult groupOffsetsResult = adminClient.listConsumerGroupOffsets(CONSUMER_GROUP_ID);
        Map<TopicPartition, OffsetAndMetadata> groupOffsets = groupOffsetsResult.partitionsToOffsetAndMetadata().get();

        if (groupOffsets.isEmpty()) {
            return 0; // 아직 커밋된 오프셋이 없으면 랙은 0
        }

        // 2. 토픽의 마지막 오프셋(Log End Offset) 정보 가져오기
        Map<TopicPartition, Long> endOffsets = adminClient.listOffsets(
            Collections.singletonMap(new TopicPartition(topicName, 0), null) // 이 부분은 모든 파티션을 대상으로 해야 더 정확합니다.
        ).all().get().entrySet().stream()
            .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, v -> v.getValue().offset()));

        // 3. 랙 계산 (Log End Offset - Current Offset)
        long totalLag = 0;
        for (Map.Entry<TopicPartition, OffsetAndMetadata> entry : groupOffsets.entrySet()) {
            if (entry.getKey().topic().equals(topicName)) {
                long endOffset = endOffsets.getOrDefault(entry.getKey(), 0L);
                long currentOffset = entry.getValue().offset();
                long lag = Math.max(0, endOffset - currentOffset);
                totalLag += lag;
            }
        }

        return totalLag;
    }
}