package runnershigh.project.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberLoginResponseDTO {
    private String accessToken;
    private String refreshToken;

    private Long id;
    private String memberEmail;
    private String name;
}
