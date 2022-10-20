package com.zerobase.cms.user.application;

import com.zerobase.cms.user.client.MailgunClient;
import com.zerobase.cms.user.client.mailgun.SendMailForm;
import com.zerobase.cms.user.domain.SignUpForm;
import com.zerobase.cms.user.domain.model.Customer;
import com.zerobase.cms.user.exception.CustomerException;
import com.zerobase.cms.user.exception.ErrorCode;
import com.zerobase.cms.user.service.SignUpCustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignUpApplication {
    private final MailgunClient mailgunClient;
    private final SignUpCustomerService service;

    public void customerVerify(String email, String code){
        service.verifyEmail(email, code);
    }

    public String customerSignUp(SignUpForm form){
        if(service.isEmailExit(form.getEmail())){
            throw new CustomerException(ErrorCode.ALREADY_REGISTER_USER);
        }else{
            Customer c = service.signUp(form);
            LocalDateTime now = LocalDateTime.now();

            String code = getRandomCode();
            SendMailForm form1 =  SendMailForm.builder()
                    .from("gocks0918@gmail.com")
                    .to(form.getEmail())
                    .subject("Verification Email!")
                    .text(getVerificationEmailBody(form.getEmail(), form.getName(),code))
                    .build();

            log.info("Send email result : " + mailgunClient.sendEmail(form1).getBody());
            service.ChangeCustomerValidateEmail(c.getId(), code);
            return "회원가입 성공";
        }
    }
    private String getRandomCode(){
        return RandomStringUtils.random(10,true,true);
    }

    private String getVerificationEmailBody(String email, String name, String code){
        StringBuilder sb = new StringBuilder();
        return sb.append("Hello").append(name).append("! Please Click Link for verification.\n\n")
                .append("http://localhost:8081/customer/signup/verify?email=")
                .append(email)
                .append("&code=")
                .append(code).toString();
    }
}
