package com.substring.auth.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;

import com.substring.auth.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;;

    @Override
    public void sendPdfByEmail(String to, String subject, byte[] pdfBytes) {

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("manishpraja1309@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(buildEmailBody(), true);

            helper.addAttachment("interview-report.pdf",
                    new ByteArrayResource(pdfBytes),
                    "application/pdf");

            javaMailSender.send(message);

        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private String buildEmailBody() {
        return """
                <html>
                  <body style="font-family: Arial, sans-serif; color: #333; padding: 20px;">
                    <h2>Your Interview Report is Ready! 🎉</h2>
                    <p>Your AI-generated interview report has been attached to this email.</p>
                    <p>Review your performance, strengths, and areas to improve.</p>
                    <br/>
                    <p style="color: #888; font-size: 12px;">
                      This is an automated email. Please do not reply.
                    </p>
                  </body>
                </html>
                """;
    }
}
