package br.org.generation.lojagames.service;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.org.generation.lojagames.model.Usuario;
import br.org.generation.lojagames.model.UsuarioLogin;
import br.org.generation.lojagames.repository.UsuarioRepository;

@Service
public class UsuarioService {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	public List<Usuario> listarUsuarios(){
		return usuarioRepository.findAll();
	}
	
	public Optional<Usuario> cadastrarUsuario(Usuario usuario) {
		if (usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent()) {
			return Optional.empty();
		}
		
		// Instancia um objeto da Classe BCryptPasswordEncoder para criptografar a senha
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		
		// Cria a variável senhaEncoder que receberá a senha criptografada
		String senhaEncoder = encoder.encode(usuario.getSenha());
		
		// Atualiza a senha do objeto usuário (enviado via Postman) com a senha criptografada
		usuario.setSenha(senhaEncoder);
		
		// Retorna para a Classe UsuarioController o objeto Salvo no Banco de Dados
		return Optional.of(usuarioRepository.save(usuario));
			
	}

	public Optional<UsuarioLogin> loginUsuario(Optional<UsuarioLogin> usuarioLogin){
		
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		
		Optional<Usuario> usuario = usuarioRepository
				.findByUsuario(usuarioLogin.get().getUsuario());
		
		if (usuario.isPresent()) {
			if (encoder.matches(usuarioLogin.get().getSenha(), usuario.get().getSenha())) {
				String auth = usuarioLogin.get().getUsuario() + ":" + usuarioLogin.get().getSenha();
				
				byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
				
				String authHeader = "Basic " + new String(encodedAuth);
				
				usuarioLogin.get().setId(usuario.get().getId());
				usuarioLogin.get().setNome(usuario.get().getNome());
				usuarioLogin.get().setSenha(usuario.get().getSenha());
				usuarioLogin.get().setToken(authHeader);
				
				return usuarioLogin;
			}
		}
		
		return Optional.empty();
	}
}
