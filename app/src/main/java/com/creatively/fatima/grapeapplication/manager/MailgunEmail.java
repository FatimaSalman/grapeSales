package com.creatively.fatima.grapeapplication.manager;

import android.annotation.SuppressLint;

import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.support.v4.util.Preconditions.checkNotNull;
import static android.support.v4.util.Preconditions.checkState;


public class MailgunEmail {

    public final Contact from;
    public final List<Contact> to;
    public final List<Contact> cc;
    public final List<Contact> bcc;
    public final String subject;
    public final String text;
    public final String html;
    public final List<Attachment> attachments;
    public final List<Attachment> inlines;
    public final List<String> tags;
    public final String campaignId;
    public final Boolean dkimEnabled;
    public final boolean testMode;
    public final Boolean tracking;
    public final String trackingClicks;
    public final Boolean trackingOpens;
    public final boolean requireTls;
    public final boolean skipVerification;
    public final List<Header> extraHeaders;
    public final List<Variable> variables;

    private MailgunEmail(Contact from, List<Contact> to, List<Contact> cc, List<Contact> bcc,
                         String subject, String text, String html, List<Attachment> attachments,
                         List<Attachment> inlines, List<String> tags, String campaignId, Boolean dkimEnabled,
                        boolean testMode, Boolean tracking, String trackingClicks,
                         Boolean trackingOpens, boolean requireTls, boolean skipVerification,
                         List<Header> extraHeaders, List<Variable> variables) {
        this.from = from;
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
        this.subject = subject;
        this.text = text;
        this.html = html;
        this.attachments = attachments;
        this.inlines = inlines;
        this.tags = tags;
        this.campaignId = campaignId;
        this.dkimEnabled = dkimEnabled;
        this.testMode = testMode;
        this.tracking = tracking;
        this.trackingClicks = trackingClicks;
        this.trackingOpens = trackingOpens;
        this.requireTls = requireTls;
        this.skipVerification = skipVerification;
        this.extraHeaders = extraHeaders;
        this.variables = variables;
    }

    public MultipartBody toMultipartBody() {
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("from", from.toString())
                .addFormDataPart("subject", subject);

        if (text != null) bodyBuilder.addFormDataPart("text", text);
        if (html != null) bodyBuilder.addFormDataPart("html", html);
        if (campaignId != null) bodyBuilder.addFormDataPart("o:campaign", campaignId);
        if (dkimEnabled != null) bodyBuilder.addFormDataPart("o:dkim", dkimEnabled ? "yes" : "no");

        if (testMode) bodyBuilder.addFormDataPart("o:testmode", "yes");
        if (tracking != null) bodyBuilder.addFormDataPart("o:tracking", tracking ? "yes" : "no");
        if (trackingClicks != null) bodyBuilder.addFormDataPart("o:tracking-clicks", trackingClicks);
        if (trackingOpens != null) {
            bodyBuilder.addFormDataPart("o:tracking-opens", trackingOpens ? "yes" : "no");
        }
        if (requireTls) bodyBuilder.addFormDataPart("o:require-tls", "yes");
        if (skipVerification) bodyBuilder.addFormDataPart("o:skip-verification", "yes");

        for (Contact contact : to) {
            bodyBuilder.addFormDataPart("to", contact.toString());
        }
        for (Contact contact : cc) {
            bodyBuilder.addFormDataPart("cc", contact.toString());
        }
        for (Contact contact : bcc) {
            bodyBuilder.addFormDataPart("bcc", contact.toString());
        }
        for (Attachment attachment : attachments) {
            bodyBuilder.addFormDataPart("attachment", attachment.name, attachment.file);
        }
        for (Attachment inline : inlines) {
            bodyBuilder.addFormDataPart("inline", inline.name, inline.file);
        }
        for (String tag : tags) {
            bodyBuilder.addFormDataPart("o:tag", tag);
        }
        for (Header extraHeader : extraHeaders) {
            bodyBuilder.addFormDataPart("h:" + extraHeader.name, extraHeader.value);
        }
        for (Variable variable : variables) {
            bodyBuilder.addFormDataPart("v:" + variable.name, variable.json);
        }

        return bodyBuilder.build();
    }

    public static class Contact {
        public final String name;
        public final String email;

        public Contact(String name, String email) {
            this.name = name;
            this.email = email;
        }

        @Override public String toString() {
            return String.format("%s <%s>", name, email);
        }
    }

    public static class Attachment {
        public final String name;
        public final RequestBody file;

        public Attachment(String name, RequestBody file) {
            this.name = name;
            this.file = file;
        }
    }

    public static class Header {
        public final String name;
        public final String value;

        public Header(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }

    public static class Variable {
        public final String name;
        public final String json;

        public Variable(String name, String json) {
            this.name = name;
            this.json = json;
        }
    }

    public static class Builder {
        private Contact from;
        private List<Contact> to = new LinkedList<>();
        private List<Contact> cc = new LinkedList<>();
        private List<Contact> bcc = new LinkedList<>();
        private String subject;
        private String text;
        private String html;
        private List<Attachment> attachments = new LinkedList<>();
        private List<Attachment> inlines = new LinkedList<>();
        private List<String> tags = new LinkedList<>();
        private String campaignId;
        private Boolean dkimEnabled;
        private boolean testMode;
        private Boolean tracking;
        private String trackingClicks;
        private Boolean trackingOpens;
        private boolean requireTls;
        private boolean skipVerification;
        private List<Header> extraHeaders = new LinkedList<>();
        private List<Variable> variables = new LinkedList<>();

        public Builder from(Contact from) {
            this.from = from;
            return this;
        }

        public Builder addTo(Contact contact) {
            this.to.add(contact);
            return this;
        }

        public Builder addCc(Contact contact) {
            this.cc.add(contact);
            return this;
        }

        public Builder addBcc(Contact contact) {
            this.bcc.add(contact);
            return this;
        }

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder html(String html) {
            this.html = html;
            return this;
        }

        public Builder addAttachment(Attachment attachment) {
            this.attachments.add(attachment);
            return this;
        }

        public Builder addInline(Attachment inline) {
            this.inlines.add(inline);
            return this;
        }

        public Builder addTag(String tag) {
            this.tags.add(tag);
            return this;
        }

        public Builder campaignId(String campaignId) {
            this.campaignId = campaignId;
            return this;
        }

        public Builder dkimEnabled(Boolean dkimEnabled) {
            this.dkimEnabled = dkimEnabled;
            return this;
        }


        public Builder testMode(Boolean testMode) {
            this.testMode = testMode;
            return this;
        }

        public Builder tracking(Boolean tracking) {
            this.tracking = tracking;
            return this;
        }

        public Builder trackingClicks(String trackingClicks) {
            this.trackingClicks = trackingClicks;
            return this;
        }

        public Builder trackingOpens(Boolean trackingOpens) {
            this.trackingOpens = trackingOpens;
            return this;
        }

        public Builder requireTls(Boolean requireTls) {
            this.requireTls = requireTls;
            return this;
        }

        public Builder skipVerification(Boolean skipVerification) {
            this.skipVerification = skipVerification;
            return this;
        }

        public Builder addExtraHeader(Header header) {
            this.extraHeaders.add(header);
            return this;
        }

        public Builder addVariable(Variable variable) {
            this.variables.add(variable);
            return this;
        }

        @SuppressLint("RestrictedApi")
        public MailgunEmail build() {
            checkNotNull(from, "Missing from contact");
            checkState(!to.isEmpty() || !cc.isEmpty() || !bcc.isEmpty(), "Need at least one recipient");
            checkState(text != null || html != null, "Missing a text or html body");
            checkState(tags.size() < 4, "Mailgun allows at most 3 tags");
            checkState(trackingClicks == null
                            || trackingClicks.equals("yes")
                            || trackingClicks.equals("no")
                            || trackingClicks.equals("htmlonly"),
                    "Invalid tracking clicks option");

            return new MailgunEmail(from, to, cc, bcc, subject, text, html, attachments, inlines, tags,
                    campaignId, dkimEnabled, testMode, tracking, trackingClicks, trackingOpens,
                    requireTls, skipVerification, extraHeaders, variables);
        }
    }
}