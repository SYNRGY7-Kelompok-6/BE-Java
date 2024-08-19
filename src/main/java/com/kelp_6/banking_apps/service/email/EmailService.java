package com.kelp_6.banking_apps.service.email;


import com.kelp_6.banking_apps.model.email.EmailModel;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ResourceLoader resourceLoader;

    @Async
    public void notificationIncomingFunds(EmailModel data) throws MessagingException, IOException {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        DateFormat dateFormat = new SimpleDateFormat("DD/MM/YYYY");

        MimeMessage message = mailSender.createMimeMessage();
        message.setFrom("nugrahanggara016@gmail.com");
        message.setSubject("Pemberitahuan Dana Masuk");
        message.setRecipients(MimeMessage.RecipientType.TO, data.getBeneficiaryEmail());
        String htmlContent = readFileHtmlTemplate().replace("${benname}", data.getBeneficiaryName());
        htmlContent = htmlContent.replace("${benaccount}", data.getBeneficiaryAccount());
        htmlContent = htmlContent.replace("${amount}", formatter.format(data.getAmount().getValue()));
        htmlContent = htmlContent.replace("${date}", dateFormat.format(data.getTransactionDate()) );
        htmlContent = htmlContent.replace("${srcname}", data.getSender());

        message.setContent(htmlContent, "text/html;charset=utf-8");

        mailSender.send(message);
    }


    private String readFileHtmlTemplate() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:templates/email.html");
        Path path = Paths.get(resource.getURI());
        return Files.readString(path, StandardCharsets.UTF_8);
    }
}
