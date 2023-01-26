package curso.api.rest.service;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class ServiceEnviaEmail {

	private String userName = "wanderson9100@outlook.com";
	private String senha = "/*/*Dragonball1@ /*";

	public void enviarEmail(String assunto, String emailDestino, String mensagem) throws MessagingException {

		Properties properties = new Properties();
		properties.put("mail.smtp.auth", "true"); /* Autorização */
		properties.put("mail.smtp.starttls", "true");/* Autenticação */
		properties.put("mail.smtp.host", "smtp-mail.outlook.com");/* Servidor Microsoft */
		properties.put("mail.smtp.port", " 587"); /* Porta do servidor */
		properties.put("mail.smtp.socketFactory.port", "587");/* Expecifica porta socket */
		properties.put("mail.smtp.scoketFactory.class", "javax.net.ssl.SSLSocketFactory");/* Classe de conexão socket */

		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {

				return new PasswordAuthentication(userName, senha);
			}

		});

		Address[] toUser = InternetAddress.parse(emailDestino);

		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(userName)); /* Quem está enviando - Nós */
		message.setRecipients(Message.RecipientType.TO, toUser); /* Para quem vai o e-mail - Quem irá receber */
		message.setSubject(assunto);/* Assunto e-mail */
		message.setText(mensagem);

		Transport.send(message);

	}
}
