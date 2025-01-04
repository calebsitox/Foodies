package com.tfg.app.foodies.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.tfg.app.foodies.entities.User;
import com.tfg.app.foodies.repository.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailService {
	

	@Autowired
	private JavaMailSender emailSender;

	private String generatedCode;

	public void sentSetPassword(String email) throws MessagingException {
		MimeMessage mimeMessage = emailSender.createMimeMessage();
		MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
		mimeMessageHelper.setTo(email);
		mimeMessageHelper.setSubject("Change Password");
		mimeMessageHelper.setText(" Credential for confirmation \n" + createCredetntials());

		emailSender.send(mimeMessage);
	}

	private String createCredetntials() {
		Random random = new Random();
		StringBuilder confirmationCode = new StringBuilder();
		for (int i = 0; i < 6; i++) {
			int randomInt = random.nextInt(10); // Any integer
			String randomString = Integer.toString(randomInt);
			confirmationCode.append(randomString).append(" ");
		}
		generatedCode = confirmationCode.toString().trim(); // Save the generated code return generatedCode;
		return generatedCode;

	}

	public boolean confirmCode(String userInput) {
		return userInput.equals(generatedCode);
	}


}
