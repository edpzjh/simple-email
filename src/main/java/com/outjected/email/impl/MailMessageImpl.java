/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.outjected.email.impl;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.mail.Session;
import javax.mail.internet.InternetAddress;

import com.outjected.email.api.ContentDisposition;
import com.outjected.email.api.ContentType;
import com.outjected.email.api.EmailAttachment;
import com.outjected.email.api.EmailContact;
import com.outjected.email.api.EmailMessage;
import com.outjected.email.api.EmailMessageType;
import com.outjected.email.api.Header;
import com.outjected.email.api.SessionConfig;
import com.outjected.email.api.MailContext;
import com.outjected.email.api.MailMessage;
import com.outjected.email.api.MailTransporter;
import com.outjected.email.api.MessagePriority;
import com.outjected.email.api.SendFailedException;
import com.outjected.email.api.TemplateProvider;
import com.outjected.email.impl.attachments.BaseAttachment;
import com.outjected.email.impl.attachments.FileAttachment;
import com.outjected.email.impl.attachments.InputStreamAttachment;
import com.outjected.email.impl.util.EmailAttachmentUtil;
import com.outjected.email.impl.util.MailUtility;

/**
 * @author Cody Lerum
 */
public class MailMessageImpl implements MailMessage {

    private EmailMessage emailMessage;

    private MailTransporter mailTransporter;
    private Session session;
    private SessionConfig mailConfig;

    private TemplateProvider subjectTemplate;
    private TemplateProvider textTemplate;
    private TemplateProvider htmlTemplate;
    private Map<String, Object> templateContext = new HashMap<String, Object>();
    private boolean templatesMerged;

    private MailMessageImpl() {
        emailMessage = new EmailMessage();

    }

    public MailMessageImpl(Session session) {
        this();
        this.session = session;
    }

    public MailMessageImpl(MailTransporter mailTransporter) {
        this();
        this.mailTransporter = mailTransporter;
    }

    public MailMessageImpl(SessionConfig mailConfig) {
        this();
        this.mailConfig = mailConfig;
    }

    // Begin Addressing

    public MailMessage from(String... address) {
        emailMessage.addFromAddresses(MailUtility.internetAddress(address));
        return this;
    }

    public MailMessage from(InternetAddress emailAddress) {
        emailMessage.addFromAddress(emailAddress);
        return this;
    }

    public MailMessage from(EmailContact emailContact) {
        if (emailContact != null) {
            emailMessage.addFromAddress(MailUtility.internetAddress(emailContact));
        }
        return this;
    }

    public MailMessage from(Collection<? extends EmailContact> emailContacts) {
        emailMessage.addFromAddresses(MailUtility.internetAddress(emailContacts));
        return this;
    }

    public MailMessage replyTo(String... address) {
        emailMessage.addReplyToAddresses(MailUtility.internetAddress(address));
        return this;
    }

    public MailMessage replyTo(InternetAddress emailAddress) {
        emailMessage.addReplyToAddress(emailAddress);
        return this;
    }

    public MailMessage replyTo(EmailContact emailContact) {
        if (emailContact != null) {
            emailMessage.addReplyToAddress(MailUtility.internetAddress(emailContact));
        }
        return this;
    }

    public MailMessage replyTo(Collection<? extends EmailContact> emailContacts) {
        emailMessage.addReplyToAddresses(MailUtility.internetAddress(emailContacts));
        return this;
    }

    public MailMessage addHeader(String name, String value) {
        emailMessage.addHeader(new Header(name, value));
        return this;
    }

    public MailMessage to(String... address) {
        emailMessage.addToAddresses(MailUtility.internetAddress(address));
        return this;
    }

    public MailMessage to(InternetAddress emailAddress) {
        emailMessage.addToAddress(emailAddress);
        return this;
    }

    public MailMessage to(EmailContact emailContact) {
        if (emailContact != null) {
            emailMessage.addToAddress(MailUtility.internetAddress(emailContact));
        }
        return this;
    }

    public MailMessage to(Collection<? extends EmailContact> emailContacts) {
        emailMessage.addToAddresses(MailUtility.internetAddress(emailContacts));
        return this;
    }

    public MailMessage cc(String... address) {
        emailMessage.addCcAddresses(MailUtility.internetAddress(address));
        return this;
    }

    public MailMessage cc(InternetAddress emailAddress) {
        emailMessage.addCcAddress(emailAddress);
        return this;
    }

    public MailMessage cc(EmailContact emailContact) {
        if (emailContact != null) {
            emailMessage.addCcAddress(MailUtility.internetAddress(emailContact));
        }
        return this;
    }

    public MailMessage cc(Collection<? extends EmailContact> emailContacts) {
        emailMessage.addCcAddresses(MailUtility.internetAddress(emailContacts));
        return this;
    }

    public MailMessage bcc(String... address) {
        emailMessage.addBccAddresses(MailUtility.internetAddress(address));
        return this;
    }

    public MailMessage bcc(InternetAddress emailAddress) {
        emailMessage.addBccAddress(emailAddress);
        return this;
    }

    public MailMessage bcc(EmailContact emailContact) {
        if (emailContact != null) {
            emailMessage.addBccAddress(MailUtility.internetAddress(emailContact));
        }
        return this;
    }

    public MailMessage bcc(Collection<? extends EmailContact> emailContacts) {
        emailMessage.addBccAddresses(MailUtility.internetAddress(emailContacts));
        return this;
    }

    // End Addressing

    public MailMessage subject(String value) {
        emailMessage.setSubject(value);
        return this;
    }

    public MailMessage deliveryReceipt(String address) {
        emailMessage.addDeliveryReceiptAddress(MailUtility.internetAddress(address));
        return this;
    }

    public MailMessage readReceipt(String address) {
        emailMessage.addReadReceiptAddress(MailUtility.internetAddress(address));
        return this;
    }

    public MailMessage importance(MessagePriority messagePriority) {
        emailMessage.setImportance(messagePriority);
        return this;
    }

    public MailMessage messageId(String messageId) {
        emailMessage.setMessageId(messageId);
        return this;
    }

    public MailMessage bodyText(String text) {
        emailMessage.setTextBody(text);
        return this;
    }

    public MailMessage bodyHtml(String html) {
        emailMessage.setHtmlBody(html);
        return this;
    }

    public MailMessage bodyHtmlTextAlt(String html, String text) {
        emailMessage.setTextBody(text);
        emailMessage.setHtmlBody(html);
        return this;
    }

    // Begin Attachments

    public MailMessage addAttachment(EmailAttachment attachment) {
        emailMessage.addAttachment(attachment);
        return this;
    }

    public MailMessage addAttachments(Collection<? extends EmailAttachment> attachments) {
        emailMessage.addAttachments(attachments);
        return this;
    }

    public MailMessage addAttachment(String fileName, String mimeType, ContentDisposition contentDispostion, byte[] bytes) {
        addAttachment(new BaseAttachment(fileName, mimeType, contentDispostion, bytes));
        return this;
    }

    public MailMessage addAttachment(String fileName, String mimeType, ContentDisposition contentDispostion, InputStream inputStream) {
        addAttachment(new InputStreamAttachment(fileName, mimeType, contentDispostion, inputStream));
        return this;
    }

    public MailMessage addAttachment(ContentDisposition contentDispostion, File file) {
        addAttachment(new FileAttachment(contentDispostion, file));
        return this;
    }

    // End Attachments

    // Begin Calendar

    public MailMessage iCal(String html, byte[] bytes) {
        emailMessage.setType(EmailMessageType.INVITE_ICAL);
        emailMessage.setHtmlBody(html);
        emailMessage.addAttachment(new BaseAttachment(null, "text/calendar;method=CANCEL", ContentDisposition.INLINE, bytes, "urn:content-classes:calendarmessage"));
        return this;
    }

    // End Calendar

    public MailMessage subject(TemplateProvider subject) {
        subjectTemplate = subject;
        return this;
    }

    public MailMessage bodyText(TemplateProvider textBody) {
        textTemplate = textBody;
        return this;
    }

    public MailMessage bodyHtml(TemplateProvider htmlBody) {
        htmlTemplate = htmlBody;
        return this;
    }

    public MailMessage bodyHtmlTextAlt(TemplateProvider htmlBody, TemplateProvider textBody) {
        bodyHtml(htmlBody);
        bodyText(textBody);
        return this;
    }

    public MailMessage charset(String charset) {
        emailMessage.setCharset(charset);
        return this;
    }

    public MailMessage contentType(ContentType contentType) {
        emailMessage.setRootContentType(contentType);
        return this;
    }

    public MailMessage put(String key, Object value) {
        templateContext.put(key, value);
        return this;
    }

    public MailMessage put(Map<String, Object> values) {
        templateContext.putAll(values);
        return this;
    }

    public EmailMessage getEmailMessage() {
        return emailMessage;
    }

    public void setEmailMessage(EmailMessage emailMessage) {
        this.emailMessage = emailMessage;
    }

    public void setMailTransporter(MailTransporter mailTransporter) {
        this.mailTransporter = mailTransporter;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public EmailMessage mergeTemplates() {

        put("mailContext", new MailContext(EmailAttachmentUtil.getEmailAttachmentMap(emailMessage.getAttachments())));

        if (subjectTemplate != null) {
            emailMessage.setSubject(subjectTemplate.merge(templateContext));
        }

        if (textTemplate != null) {
            emailMessage.setTextBody(textTemplate.merge(templateContext));
        }

        if (htmlTemplate != null) {
            emailMessage.setHtmlBody(htmlTemplate.merge(templateContext));
        }

        templatesMerged = true;

        return emailMessage;
    }

    public EmailMessage send(MailTransporter mailTransporter) throws SendFailedException {

        if (!templatesMerged) {
            mergeTemplates();
        }

        try {
            mailTransporter.send(emailMessage);
        }
        catch (Exception e) {
            throw new SendFailedException("Send Failed", e);
        }

        return emailMessage;
    }

    private EmailMessage send(Session session) throws SendFailedException {
        return send(new MailTransporterImpl(session));
    }

    public EmailMessage send(SessionConfig mailConfig) {
        return send(MailUtility.createSession(mailConfig));
    }

    public EmailMessage send() throws SendFailedException {
        if (mailTransporter != null) {
            return send(mailTransporter);
        }
        else if (session != null) {
            return send(session);
        }
        else if (mailConfig != null) {
            return send(mailConfig);
        }
        else {
            throw new SendFailedException("No Resource availiable to send. How was this constructed?");
        }
    }
}
