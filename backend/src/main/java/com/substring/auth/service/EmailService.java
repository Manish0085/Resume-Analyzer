package com.substring.auth.service;

public interface EmailService {

    void sendPdfByEmail(String to, String subject, byte[] pdfBytes);

}
