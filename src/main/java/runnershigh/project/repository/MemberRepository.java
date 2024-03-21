package runnershigh.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import runnershigh.project.domain.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String Email);

    Optional<Member> findByEmail(String email);
}
