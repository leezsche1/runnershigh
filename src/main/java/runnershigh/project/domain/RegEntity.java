package runnershigh.project.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@EntityListeners(Member.class)
@MappedSuperclass
@Getter
public class RegEntity {

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdTime;

//    @UpdateTimestamp
//    @Column
//    private LocalDateTime updatedTime;

}
