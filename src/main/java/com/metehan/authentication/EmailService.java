package com.metehan.authentication;

import com.metehan.authentication.confirmationToken.ConfirmationToken;
import com.metehan.authentication.confirmationToken.ConfirmationTokenRepository;
import com.metehan.authentication.exception.ExpiredConfirmationTokenException;
import com.metehan.authentication.exception.NoSuchConfirmationToken;
import com.metehan.authentication.exception.NoSuchUserExists;
import com.metehan.authentication.user.User;
import com.metehan.authentication.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final UserRepository userRepository;

    //String mail = "please click to link in order to verify your email localhost:8080/api/v1/verify?" + confirmationToken;
            //mailSender.send(email);
    @Async
    public boolean sendEmail(String email){
        String confirmationToken = generateConfirmationToken(email);
        String mail = "please click to link in order to verify your email http://localhost:8080/api/v1/verify?token=" + confirmationToken;
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("Complete Registration!");
        mailMessage.setText(mail);
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getName());
        try{
            mailSender.send(mailMessage);
        }catch(MailSendException ex){
            System.out.println(ex);
        }
        return true;
    }

    public String generateConfirmationToken(String email){
        // choose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(16);

        for (int i = 0; i < 16; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        String generatedString = sb.toString();

        ConfirmationToken token = ConfirmationToken.builder()
                .confirmationToken(generatedString)
                .createdDate(new Date())
                .user(userRepository.findUserByEmail(email).get())
                .build();
        confirmationTokenRepository.save(token);
        return generatedString;
    }

    public boolean verifyEmail(String confirmationToken){
        ConfirmationToken fetchedConfirmationToken = confirmationTokenRepository.findByConfirmationToken(confirmationToken)
                .orElseThrow(() -> new NoSuchConfirmationToken("No such confirmation Token found"));

        if(fetchedConfirmationToken.calculateExpirationDate().before(new Date()))
            throw new ExpiredConfirmationTokenException("Confirmation Token has expired!!");

        User user = userRepository.findUserByEmail(fetchedConfirmationToken.getUser().getEmail())
                .orElseThrow(() -> new NoSuchUserExists("No such user exists!!"));

        user.setEnabled(true);
        userRepository.save(user);

        return true;
    }

}
