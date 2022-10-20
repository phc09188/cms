package com.zerobase.cms.user.service;


import com.zerobase.cms.user.domain.SignUpForm;
import com.zerobase.cms.user.domain.model.Customer;
import com.zerobase.cms.user.domain.repository.CustomerRepository;
import com.zerobase.cms.user.exception.CustomerException;
import com.zerobase.cms.user.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SignUpCustomerService {
    private final CustomerRepository customerRepository;
    public Customer signUp(SignUpForm form){
        return customerRepository.save(Customer.from(form));
    }
    public boolean isEmailExit(String email){
        return customerRepository.findByEmail(email.toLowerCase(Locale.ROOT)).isPresent();
    }
    @Transactional
    public LocalDateTime ChangeCustomerValidateEmail(Long customerId, String verificationCode){
        Optional<Customer> c =  customerRepository.findById(customerId);
        if(c.isPresent()){
            Customer customer = c.get();
            customer.setVerificationCode(verificationCode);
            customer.setVerifyExpiredAt(LocalDateTime.now().plusDays(1));
            return customer.getVerifyExpiredAt();
        }
        throw new CustomerException(ErrorCode.NOT_FOUND_USER);
    }

    @Transactional
    public void verifyEmail(String email, String code){
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomerException(ErrorCode.NOT_FOUND_USER));
        if(customer.isVerify()){
            throw new CustomerException(ErrorCode.ALREADY_VERIFY);
        }else if(!customer.getVerificationCode().equals(code)){
            throw new CustomerException(ErrorCode.WRONG_VERIFICATION);
        }
        if(customer.getVerifyExpiredAt().isBefore(LocalDateTime.now())){
            throw new CustomerException(ErrorCode.EXPIRE_CODE);
        }
        customer.setVerify(true);

    }
}
