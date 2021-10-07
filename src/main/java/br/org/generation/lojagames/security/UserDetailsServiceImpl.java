package br.org.generation.lojagames.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.org.generation.lojagames.model.Usuario;
import br.org.generation.lojagames.repository.UsuarioRepository;

/*	Implementa a interface UserDetails, sendo responsável por recuperar os dados
 *  do usuário no Banco de Dados pelo usuário e converter em um objeto da Classe 
 *  UserDetailsImpl */

@Service	// Indica que é uma Service / Serviço
public class UserDetailsServiceImpl implements UserDetailsService {
	
	@Autowired
	private UsuarioRepository userRepository;

	/* Obtem os dados de um usuário com um determinado nome de usuário */
	
	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		Optional<Usuario> usuario = userRepository.findByUsuario(userName);
		
		// Se o Usuário informado não existir, lnaça uma Exception 
		usuario.orElseThrow(() -> new UsernameNotFoundException(userName + " not found."));
		
		/* Retorna um objeto do tipo UserDetailsImpl(com caracteristicas e direitos) 
		 * criado com os dados recuperados do BD */ 
		return usuario.map(UserDetailsImpl::new).get();
	}
}
