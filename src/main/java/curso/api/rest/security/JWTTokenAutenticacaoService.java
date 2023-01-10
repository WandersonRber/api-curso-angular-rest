package curso.api.rest.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import curso.api.rest.ApplicationContextLoad;
import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
@Component
public class JWTTokenAutenticacaoService {

	/* Tempo de validade do Token 2 dias */
	private static final long EXPIRATION_TIME = 172800000;

	/* Uma senha uinica para compor a autenticação e ajudar na segurança */
	private static final String SECRET = "SenhaExtremamenteSecreta";

	/* Prefixo padrão de Token */
	private static final String TOKEN_PREFIX = "Bearer";

	/**/
	private static final String HEADER_STRING = "Authorization";

	/* Gerando Token de autencicação e adicionando ao cabeçalho e resposta Htpp */
	public void addAuthentication(HttpServletResponse response, String username) throws IOException {

		/* Montagem do Token */
		String JWT = Jwts.builder()/* Chama o gerador de Token */
				.setSubject(username)/* Adiciona o usuário */
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))/* Tempo expiração */
				.signWith(SignatureAlgorithm.HS512, SECRET).compact();/* Compactação e algoritimos de geração */

		/* Juntar o Token com o prefixo */
		String token = TOKEN_PREFIX + " " + JWT; /**/

		/* Adiciona no cabeçalho http */
		response.addHeader(HEADER_STRING, token); /* Authorization: */
		
		/*Liberando resposta para porta diferente do projeto Angular*/
		response.addHeader("Access-Control-Allow-Origin", "*");
		
		
		ApplicationContextLoad.getApplicationContext().getBean(UsuarioRepository.class).atualizaTokenUser(JWT,
				username);

		/*
		 * Liberando resposta para portas diferentes que usam a API ou caso clientes web
		 */
		liberacaoCors(response);
		


		/* Escreve token como resposta no corpo http */
		response.getWriter().write("{\"Authorization\": \"" + token + "\"}");

	}

	/* Retorna o usuário valiado com token ou caso não seja valida retorna null */
	public UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request,
			HttpServletResponse response) {

		/* Pega o token enviado co cabeçalho http */

		String token = request.getHeader(HEADER_STRING);

		try {
			if (token != null) {

				String tokenLimpo = token.replace(TOKEN_PREFIX, "").trim();

				/* Faz a validação do token do usuário na requisição */
				String user = Jwts.parser().setSigningKey(SECRET)/**/
						.parseClaimsJws(tokenLimpo).getBody().getSubject(); /* João Silva */

				if (user != null) {

					Usuario usuario = ApplicationContextLoad.getApplicationContext().getBean(UsuarioRepository.class)
							.findUserByLogin(user);

					/* Retorna o usuário logado */
					if (usuario != null) {

						if (tokenLimpo.equalsIgnoreCase(usuario.getToken())) {

							return new UsernamePasswordAuthenticationToken(
									usuario.getLogin(), 
									usuario.getSenha(),
									usuario.getAuthorities());

						}

					}

				}
			} /* FIM da condição TOKEN */

		} catch (io.jsonwebtoken.ExpiredJwtException e) {
			try {
				response.getOutputStream()
						.println("Seu token está expirado, faça o login ou informe um novo teken para AUTENTICAÇÃO");
			} catch (IOException e1) {
			}
		}

		response.addHeader("Access-Control-Allow-Origin", "*");
		liberacaoCors(response);
		return null; /* Não autorizado */

	}

	private void liberacaoCors(HttpServletResponse response) {

		if (response.getHeader("Access-Control-Allow-Origin") == null) {
			response.addHeader("Access-Control-Allow-Origin", "*");
		}

		if (response.getHeader("Access-Control-Allow-Headers") == null) {
			response.addHeader("Access-Control-Allow-Headers", "*");
		}

		if (response.getHeader("Access-Control-Resquest-Headers") == null) {
			response.addHeader("Access-Control-Resquest-Headers", "*");
		}

		if (response.getHeader("Access-Control-Allow-Methods") == null) {
			response.addHeader("Access-Control-Allow-Methods", "*");
		}

	}

}
