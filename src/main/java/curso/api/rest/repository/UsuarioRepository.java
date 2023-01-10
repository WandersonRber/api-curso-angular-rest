package curso.api.rest.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ser.std.StdKeySerializers.Default;

import curso.api.rest.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

		@Query("select u from Usuario u where u.login =?1")
		Usuario findUserByLogin(String login);
		
		@Query("select u from Usuario u where u.nome like %?1%")
		List<Usuario> findUserByName(String nome);
		
		
		@Transactional
		@Modifying
		@Query(nativeQuery = true, value = "update usuario set token = ?1 where login = ?2")
		void atualizaTokenUser(String token, String login);

		default Page<Usuario> findUserByNamePage(String nome, PageRequest pageRequest){
			
			Usuario usuario = new Usuario();
			usuario.setNome(nome);
			
			/* Configurando para pesquisar por nome e paginação */
			ExampleMatcher exampleMatcher = ExampleMatcher.matchingAny()
					.withMatcher("nome", ExampleMatcher.GenericPropertyMatchers
							.contains().ignoreCase());
			
			Example<Usuario> example = Example.of(usuario, exampleMatcher);
			
			Page<Usuario> retorno = findAll(example, pageRequest);
			
			return retorno;
			
		};
		
		
		
}
