package runnershigh.project.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class MemberInfo {

    private String nickName;
    private String sex;

    private String height;
    private String weight;

}
