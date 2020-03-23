package com.cdk.qa.framework.services;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;
import com.cdk.qa.framework.utils.Constants;
import com.cdk.qa.framework.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

/**
 *  For the email configuration and sending the test result reports as attachment
 *  to the decided audience
 *  This uses AWS Service: SES (Simple Email Service) to send an email to the audience,
 *  that is confirmed on SES portal
 * @author Adil.Qayyum
 */
@Slf4j
public final class SESManager {

    private DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT);
    private LocalDateTime currentDateTime = LocalDateTime.now();

    private String emailSubject = Constants.OPERATING_SYSTEM + Constants.EMAIL_SUBJECT
            + dateTimeFormat.format(currentDateTime);

    private Session session = Session.getDefaultInstance(new Properties());

    // Create a new MimeMessage object.
    private MimeMessage message = new MimeMessage(session);
    private MimeBodyPart wrap = new MimeBodyPart();

    //To send email with attachment to the specified audience via AWS SES
    public void sendEmail () throws MessagingException {

        // Get recipients from constants.
        String recipients =FileUtils.getPropertyValue(Constants.EMAIL_PROPERTIES_PATH,
                Constants.EMAIL_RECIPIENT);
        // Adding the recipients in an array for looping
        String [] emailAddresses = recipients.split(",");
        // Create a multipart/alternative child container.
        setEmailBodyPart();
        // Create a multipart/mixed parent container.
        setAttachmentBody();
        // Iterating the send email activity
        for (String Email : emailAddresses) {
            // Add subject, from and to lines.
            message.setSubject(emailSubject, "UTF-8");
            message.setFrom(new InternetAddress(FileUtils.getPropertyValue
                    (Constants.EMAIL_PROPERTIES_PATH, Constants.EMAIL_SENDER)));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(Email));
            // Try to send the email.
            try {
                // Instantiate an Amazon SES client, which will make the service
                // call with the supplied AWS credentials.
                AmazonSimpleEmailService client =
                        AmazonSimpleEmailServiceClientBuilder.standard()
                                // Replace US_WEST_2 with the AWS Region you're using for
                                // Amazon SES.
                                .withRegion(Regions.US_EAST_1).build();

                // Send the email.
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                message.writeTo(outputStream);
                RawMessage rawMessage =
                        new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));

                SendRawEmailRequest rawEmailRequest =
                        new SendRawEmailRequest(rawMessage);

                client.sendRawEmail(rawEmailRequest);
                // If the email doesn't get sent, the flow doesn't need to
                // break and the exception is logged on console.
            } catch (Exception ex) {
                log.warn(ex.getMessage());
            }
            }
    }

    private void setEmailBodyPart() throws MessagingException{
        // Define the text part.
        MimeMultipart msgBody = new MimeMultipart("alternative");
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setContent(FileUtils.getPropertyValue(Constants.EMAIL_PROPERTIES_PATH,
                Constants.EMAIL_BODY_TEXT), "text/plain; charset=UTF-8");

        // Add the text and HTML parts to the child container.
        msgBody.addBodyPart(textPart);
        // Add the child container to the wrapper object.
        wrap.setContent(msgBody);
    }

    private void setAttachmentBody() throws MessagingException{
        // Create a multipart/mixed parent container.
        MimeMultipart msg = new MimeMultipart("mixed");

        // Add the parent container to the message.
        message.setContent(msg);

        // Add the multipart/alternative part to the message.
        msg.addBodyPart(wrap);

        // Define the attachment
        MimeBodyPart att = new MimeBodyPart();
        DataSource fds = new FileDataSource(Constants.EMAIL_ATTACHMENT);
        att.setDataHandler(new DataHandler(fds));
        att.setFileName(fds.getName());

        // Add the attachment to the message.
        msg.addBodyPart(att);
    }
}
