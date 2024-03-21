package runnershigh.project.controller;

import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import runnershigh.project.domain.Member;
import runnershigh.project.domain.MemberRole;
import runnershigh.project.dto.*;
import runnershigh.project.security.util.JwtTokenizer;
import runnershigh.project.service.MemberService;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Validated
public class MemberController {
    private final MemberService memberService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenizer jwtTokenizer;
    private final RedisTemplate redisTemplate;
    private final DefaultMessageService defaultMessageService;

    private final static Duration contactDuration = Duration.ofMinutes(3);
    private final static Duration refreshDuration = Duration.ofDays(7);

    @PostMapping("/hi")
    public ResponseEntity hi() {
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/join")
    public ResponseEntity join(@RequestBody @Valid MemberJoinDTO memberJoinDTO, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        Member member = new Member();
        member.setEmail(memberJoinDTO.getMemberEmail());
        member.setName(memberJoinDTO.getMemberNm());
        member.setMemberPwd(bCryptPasswordEncoder.encode(memberJoinDTO.getMemberPwd()));
        member.setBirth(memberJoinDTO.getBirth());
        member.setMemberPhone(memberJoinDTO.getMemberPhone());

        memberService.join(member, memberJoinDTO.getMemberAgree());

        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PostMapping("/join/idCheck")
    public ResponseEntity idCheck(@RequestBody IdCheckDTO idCheckDTO) {

        boolean aboolean = memberService.existsByEmail(idCheckDTO.getEmail());
        if (aboolean == true) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity(HttpStatus.OK);
        }

    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid MemberLoginDTO memberLoginDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        Optional<Member> byEmail = memberService.findByEmail(memberLoginDTO.getMemberEmail());
        if (byEmail.isEmpty()) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        Member member = byEmail.get();
        if (!bCryptPasswordEncoder.matches(memberLoginDTO.getMemberPwd(), member.getMemberPwd())) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        List<String> roles = member.getRoles().stream().map(MemberRole::getRoleName).collect(
                Collectors.toList());

//        System.out.println("================================================");
//        for (String role : roles) {
//            System.out.println(role);
//        }
//        System.out.println("================================================");

        String accessToken = jwtTokenizer.createAccessToken(member.getId(), member.getEmail(), member.getName(), roles);
        String refreshToken = jwtTokenizer.createRefreshToken(member.getId(), member.getEmail(), member.getName(), roles);

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(member.getId() + member.getEmail(), refreshToken, refreshDuration);

        MemberLoginResponseDTO memberLoginResponseDTO = new MemberLoginResponseDTO();
        memberLoginResponseDTO.setAccessToken(accessToken);
        memberLoginResponseDTO.setRefreshToken(refreshToken);
        memberLoginResponseDTO.setMemberEmail(member.getEmail());
        memberLoginResponseDTO.setId(member.getId());
        memberLoginResponseDTO.setName(member.getName());


        return new ResponseEntity(memberLoginResponseDTO, HttpStatus.OK);

    }

    @PostMapping("/logout")
    public ResponseEntity logout(@RequestBody @Valid MemberLogoutDTO memberLogoutDTO, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        ValueOperations valueOperations = redisTemplate.opsForValue();
        String savedToken = (String) valueOperations.get(memberLogoutDTO.getMemberId() + memberLogoutDTO.getMemberEmail());
        if (savedToken.equals(memberLogoutDTO.getRefreshToken())) {
            redisTemplate.delete(memberLogoutDTO.getMemberId() + memberLogoutDTO.getMemberEmail());
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("/refresh")
    public ResponseEntity refresh(@RequestBody RefreshTokenDTO refreshTokenDTO) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String savedToken = (String) valueOperations.get(refreshTokenDTO.getMemberId() + refreshTokenDTO.getMemberEmail());
        if (savedToken.equals(refreshTokenDTO.getRefreshToken())) {

            Claims claims = jwtTokenizer.parseRefreshToken(refreshTokenDTO.getRefreshToken());
            Long userId = Long.valueOf((Integer) claims.get("userId"));
            List roles = (List) claims.get("roles");
            String email = claims.getSubject();
            String name = (String)claims.get("name");

            String accessToken = jwtTokenizer.createAccessToken(userId, email, name, roles);

            MemberLoginResponseDTO memberLoginResponseDTO = new MemberLoginResponseDTO();
            memberLoginResponseDTO.setAccessToken(accessToken);
            memberLoginResponseDTO.setId(userId);
            memberLoginResponseDTO.setMemberEmail(email);
            memberLoginResponseDTO.setName(name);
            memberLoginResponseDTO.setRefreshToken(refreshTokenDTO.getRefreshToken());

            return ResponseEntity.status(HttpStatus.OK).body(memberLoginResponseDTO);
        } else {

            return new ResponseEntity(HttpStatus.BAD_REQUEST);

        }


    }

    @PostMapping("/auth/sms")
    public ResponseEntity<SingleMessageSentResponse> phoneP(@RequestBody @Valid PhoneNumberDTO number, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Random rand = new Random();
        String numStr = "";
        for (int i = 0; i < 4; i++) {
            String ran = Integer.toString(rand.nextInt(10));
            numStr += ran;
        }

        String phoneNumber = number.getPhoneNumber();
//        int numbering = Integer.parseInt(phoneNumber);


        Message message = new Message();
        // 발신번호 및 수신번호는 반드시 01012345678 형태로 입력되어야 합니다.
        message.setFrom("01065512471");
        message.setTo(phoneNumber);
        message.setText("다음 인증번호를 입력해주세요" + numStr);

        SingleMessageSentResponse response = defaultMessageService.sendOne(new SingleMessageSendingRequest(message));
        System.out.println(response);
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set(phoneNumber, numStr, contactDuration);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/auth/smsCheck")
    public ResponseEntity smsCheck(@RequestBody @Valid SmsCheckDTO smsCheckDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        ValueOperations valueOperations = redisTemplate.opsForValue();

        String authNumber1 = (String) valueOperations.get(smsCheckDTO.getPhoneNumber());
        if (authNumber1.equals(smsCheckDTO.getAuthNumber())) {

            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

    }

}
