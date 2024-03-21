package runnershigh.project.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class MemberAgree {

    //세 종류, 전체 동의 1번만 동의 2번만 동의.
    //세 개의 데이터만 db에 넣자. member는 memberAgree외래키를 이 테이블에 연결시켜주자.
    //1, 0, 0       전체 동의
    //0, 1, 0       필수만 동의
    //0, 0, 1       어라? 이건 없어도 되네? 필수만 동의했다는 건 선택은 동의하지 않았다는 뜻이네.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memberAgree_id")
    private Long id;

    private int allAgree;
    private int firstAgree;     //필수
//    private int secondAgree;    //선택


}
