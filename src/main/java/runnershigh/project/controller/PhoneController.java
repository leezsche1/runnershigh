//package runnershigh.project.controller;
//
//import lombok.RequiredArgsConstructor;
//import net.nurigo.sdk.NurigoApp;
//import net.nurigo.sdk.message.model.Message;
//import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
//import net.nurigo.sdk.message.response.SingleMessageSentResponse;
//import net.nurigo.sdk.message.service.DefaultMessageService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.ValueOperations;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import runnershigh.project.dto.PhoneNumberDTO;
//
//import java.time.Duration;
//import java.util.Random;
//
//@RestController
//@RequestMapping("/auth")
//@RequiredArgsConstructor
//public class PhoneController {
//
//    @Value("${cool.api.key}")
//    private String apiKey;
//    @Value("${cool.api.secret}")
//    private String apiSecretKey;
//    private final static Duration contactDuration = Duration.ofMinutes(3);
//
//    private final DefaultMessageService messageService;
//    private final RedisTemplate redisTemplate;
//
//    @PostMapping("/sms")
//    public SingleMessageSentResponse phoneP(@RequestBody PhoneNumberDTO number) {
//
//        Random rand = new Random();
//        String numStr = "";
//        for (int i = 0; i < 4; i++) {
//            String ran = Integer.toString(rand.nextInt(10));
//            numStr += ran;
//        }
//
//        String phoneNumber = number.getPhoneNumber();
////        int numbering = Integer.parseInt(phoneNumber);
//
//
//        Message message = new Message();
//        // 발신번호 및 수신번호는 반드시 01012345678 형태로 입력되어야 합니다.
//        message.setFrom("01065512471");
//        message.setTo(phoneNumber);
//        message.setText("다음 인증번호를 입력해주세요" + numStr);
//
//        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
//        System.out.println(response);
//        ValueOperations valueOperations = redisTemplate.opsForValue();
//        valueOperations.set(phoneNumber, numStr, contactDuration);
//
//        return response;
//    }
//
//
//}
