package runnershigh.project.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "password")
    private String memberPwd;

    @Column(name = "phoneNumber")
    private String memberPhone;

    @Column(name = "birth")
    private String birth;

    @Embedded
    private MemberInfo memberInfo;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberAgree_id")
    private MemberAgree memberAgree;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MemberRole> roles = new ArrayList<>();


    public void addRole(MemberRole role) {
        //이 롤안에는 롤은 무엇인지 set메서드로 설정하고 넣어줘야함. 멤버는 여기서 넣어주는 것임.cascade.
        this.roles.add(role);
    }

}
