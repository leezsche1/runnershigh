package runnershigh.project.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PhoneNumberDTO {

    @NotNull(message = "번호를 입력해주세요")
    public String phoneNumber;

}
