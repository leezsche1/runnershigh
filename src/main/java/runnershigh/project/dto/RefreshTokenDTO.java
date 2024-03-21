package runnershigh.project.dto;

import lombok.Getter;

@Getter
public class RefreshTokenDTO {

    private String memberId;
    private String memberEmail;
    private String refreshToken;
}
