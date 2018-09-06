package br.com.trendsoftware.markethub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.mpconnect.model.Usuario;

public interface UserRepository extends JpaRepository<Usuario, Long> {
	public Usuario findByLoginAndSenha(String login,String senha);
}
