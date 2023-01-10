package curso.api.rest.security;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import curso.api.rest.service.ImplementacaoUserDetailsService;

/*Mapeia URL, endereços, autoriza ou bloqueia acesso a URL*/
@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter {

	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;

	/* Configura as solicitações de acesso Http */
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		/* Ativando a proteção contra usuário que não estão validados por token */
		http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())

				/*
				 * Ativando a permição para o acesso a página inicial do sistema EX:
				 * sistema.com.br/index.html
				 */
				.disable().authorizeRequests().antMatchers("/").permitAll().antMatchers("/index").permitAll()
				
				.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()

				/* URL de Logout - Redireciona após o user deslogar do sistema */
				.anyRequest().authenticated().and().logout().logoutSuccessUrl("/index")

				/* Mapeia a URl de Logout e invalida o usuário */
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))

		/* Filtra as requisições de login para autenticação */
				.and().addFilterBefore(new JWTLoginFilter("/login", authenticationManager()), 
											UsernamePasswordAuthenticationFilter.class)
		

		/*
		 * Filtra demais requisições para ferificar a presença do TOKEN JWT no HEADER
		 * HTTP
		 */
				.addFilterBefore(new JwtApiAutencacaoFilter(), UsernamePasswordAuthenticationFilter.class);

	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		/* Service que irá consultar usuário no bancos de dados */
		auth.userDetailsService(implementacaoUserDetailsService).

		/* Padrão de codificação de senha */
				passwordEncoder(new BCryptPasswordEncoder());
	}
}
