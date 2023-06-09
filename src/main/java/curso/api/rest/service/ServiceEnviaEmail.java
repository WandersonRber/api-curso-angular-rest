package curso.api.rest.service;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

@Service
public class ServiceEnviaEmail {
	
	@Value("${spring.sendgrid.api-key}")
	private String apiKey;
	
	private String userName = "wanderson9100@outlook.com";
	
	public void enviarEmail(String assunto, String emailDestino, String mensagem) throws Exception {
		   String subject = assunto;
	       Content content = new Content("text/html", mensagem);

	       Email from = new Email(userName);
	       Email to = new Email(emailDestino);
		Mail mail = new Mail(from, subject, to, content);

	       SendGrid sg = new SendGrid(apiKey);
	       Request request = new Request();

	       request.setMethod(Method.POST);
	       request.setEndpoint("mail/send");
	       request.setBody(mail.build());

	       com.sendgrid.Response response = sg.api(request);

	       System.out.println(response.getStatusCode());
	       System.out.println(response.getHeaders());
	       System.out.println(response.getBody());	

	}
	
	
}