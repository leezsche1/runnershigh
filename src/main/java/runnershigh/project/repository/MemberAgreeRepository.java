package runnershigh.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import runnershigh.project.domain.MemberAgree;

public interface MemberAgreeRepository extends JpaRepository<MemberAgree, Long> {
}
