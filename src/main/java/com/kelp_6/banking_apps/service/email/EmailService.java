package com.kelp_6.banking_apps.service.email;

import com.kelp_6.banking_apps.model.email.EmailModel;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
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
@Slf4j
public class EmailService {
    private final static Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSenderImpl mailSender;

    @Autowired
    private ResourceLoader resourceLoader;

    @Async
    public void notificationIncomingFunds(EmailModel data) throws MessagingException, IOException {
        LOGGER.info("accessed");

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true);


        try {
            // Set the sender email and display name
            message.setFrom(new InternetAddress("no-reply@connect.bca.com", "admin"));

            // Set the email subject and recipient
            message.setSubject("Pemberitahuan Dana Masuk");
            message.setTo(data.getBeneficiaryEmail());

            // Prepare the HTML content
            String htmlContent = readFileHtmlTemplate()
                    .replace("${benname}", data.getBeneficiaryName())
                    .replace("${benaccount}", data.getBeneficiaryAccount())
                    .replace("${amount}", formatter.format(data.getAmount().getValue()))
                    .replace("${date}", dateFormat.format(data.getTransactionDate()))
                    .replace("${srcname}", data.getSender());

            // Set the HTML content
            message.setText(htmlContent, true);

            // Send the email
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error("failed send email");
        }
    }


    private String readFileHtmlTemplate() throws IOException {
        LOGGER.info("accessed");

        Resource resource = resourceLoader.getResource("classpath:templates/email.html");
        Path path = Paths.get(resource.getURI());
        return Files.readString(path, StandardCharsets.UTF_8);
    }
}
