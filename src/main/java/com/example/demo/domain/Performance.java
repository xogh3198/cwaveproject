// package com.example.demo.domain;
// import java.util.Map;
// import java.util.concurrent.ConcurrentHashMap;
// import java.util.concurrent.atomic.AtomicInteger;

// import io.swagger.v3.oas.annotations.media.Schema;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
// import lombok.Setter;


// @Entity
// @Table(name = "performances")
// @Schema(description = "공연 정보")
// @Getter @Setter 
// @NoArgsConstructor
// public class Performance {
//     @Schema(description = "공연 ID", example = "1")
//     private Long id;

//     @Schema(description = "공연 이름", example = "오페라의 유령")
//     private String name;

//     @Schema(description = "공연 설명", example = "세계적으로 유명한 뮤지컬 오페라의 유령")
//     private String description;

//     @Schema(description = "일정별 잔여 좌석 수", example = "{\"2024-12-25 19:00\": 50, \"2024-12-26 19:00\": 30}")
//     private Map<String, AtomicInteger> remainingSeatsBySchedule;

//     public Performance(Long id, String name, String description, Map<String, Integer> scheduleWithSeats) {
//         this.id = id;
//         this.name = name;
//         this.description = description;
//         this.remainingSeatsBySchedule = new ConcurrentHashMap<>();
//         scheduleWithSeats.forEach((schedule, seats) -> {
//             this.remainingSeatsBySchedule.put(schedule, new AtomicInteger(seats));
//         });


// }
// }

package com.example.demo.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "performances")
@Getter @Setter
@NoArgsConstructor
public class Performance {

    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    // @JsonIgnore와 FetchType.EAGER를 모두 제거합니다.
    @OneToMany(mappedBy = "performance", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Schedule> schedules = new ArrayList<>();
}