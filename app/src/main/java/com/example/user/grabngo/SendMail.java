package com.example.user.grabngo;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

class SendeMail extends AsyncTask<Void,Void,Void> {

    //Declaring Variables
    private Context context;
    private Session session;

    //Information to send email
    private String email;
    private String subject;
    private String message;

    //Progressdialog to show while sending email
    private ProgressDialog progressDialog;
    private Multipart _multipart = new MimeMultipart();

    //Class Constructor
    public SendeMail(Context context, String email, String subject, String message){
        //Initializing variables
        this.context = context;
        this.email = email;
        this.subject = subject;
        this.message = message;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Showing progress dialog while sending email
        //progressDialog = ProgressDialog.show(context,"Sending...", "Please Wait...",false,false);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //Dismissing the progress dialog
        //progressDialog.dismiss();
        //Showing a success message
        return;
    }

    public void addAttachment(String filename) throws Exception {

        BodyPart messageBodyPart = new MimeBodyPart();

        DataSource source = new FileDataSource(filename);

        messageBodyPart.setDataHandler(new DataHandler(source));

        messageBodyPart.setFileName(filename);

        _multipart.addBodyPart(messageBodyPart);

    }

    @Override
    protected Void doInBackground(Void... params) {
        //Creating properties
        Properties props = new Properties();

        //Configuring properties for gmail
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        //Creating a new session
        session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    //Authenticating the password
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(Config.EMAIL, Config.PASSWORD);                    }
                });

        try {
            //Creating MimeMessage object
            MimeMessage mm = new MimeMessage(session);

            DataHandler handler = new DataHandler(new ByteArrayDataSource(

                    message.getBytes(), "text/plain"));

            //Setting sender address
            mm.setFrom(new InternetAddress(Config.EMAIL));
            //Adding receiver
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            //Adding subject
            mm.setSubject(subject);

            mm.setDataHandler(handler);

            //Adding message
            mm.setText(message);

            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(message);
            _multipart.addBodyPart(messageBodyPart);
            mm.setContent(_multipart);

            //Sending email
            Transport.send(mm);

        } catch (MessagingException e) {
            Toast.makeText(context,"Error: "+e,Toast.LENGTH_LONG).show();

            e.printStackTrace();
        }
        return null;
    }

    public class ByteArrayDataSource implements DataSource {

        private byte[] data;

        private String type;

        public ByteArrayDataSource(byte[] data, String type) {

            super();

            this.data = data;

            this.type = type;

        }

        public ByteArrayDataSource(byte[] data) {

            super();

            this.data = data;

        }

        public void setType(String type) {

            this.type = type;

        }

        public String getContentType() {

            if (type == null)

                return "application/octet-stream";

            else

                return type;

        }

        public InputStream getInputStream() throws IOException {

            return new ByteArrayInputStream(data);

        }

        public String getName() {

            return "ByteArrayDataSource";

        }

        public OutputStream getOutputStream() throws IOException {

            throw new IOException("Not Supported");

        }

    }
}
