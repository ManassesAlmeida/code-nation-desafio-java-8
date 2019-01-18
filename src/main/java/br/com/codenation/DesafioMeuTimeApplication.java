package br.com.codenation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import br.com.codenation.desafio.annotation.Desafio;
import br.com.codenation.desafio.app.MeuTimeInterface;
import br.com.codenation.desafio.exceptions.CapitaoNaoInformadoException;
import br.com.codenation.desafio.exceptions.IdentificadorUtilizadoException;
import br.com.codenation.desafio.exceptions.JogadorNaoEncontradoException;
import br.com.codenation.desafio.exceptions.TimeNaoEncontradoException;
import br.com.codenation.entity.Jogador;
import br.com.codenation.entity.Time;

public class DesafioMeuTimeApplication implements MeuTimeInterface {

	private Collection<Time> times = new HashSet<Time>();
	private Collection<Jogador> jogadores = new HashSet<Jogador>();

	@Desafio("incluirTime")
	public void incluirTime(Long id, String nome, LocalDate dataCriacao, String corUniformePrincipal,
			String corUniformeSecundario) {

		if (findTime(id).isPresent()) {
			throw new IdentificadorUtilizadoException();
		}
		times.add(new Time(id, nome, dataCriacao, corUniformePrincipal, corUniformeSecundario));
	}

	@Desafio("incluirJogador")
	public void incluirJogador(Long id, Long idTime, String nome, LocalDate dataNascimento, Integer nivelHabilidade, BigDecimal salario) {
		
		if(findJogador(id).isPresent()) {
			throw new IdentificadorUtilizadoException();
		}
		findTime(idTime).orElseThrow(TimeNaoEncontradoException::new);
		
		jogadores.add(new Jogador(id, idTime, nome, dataNascimento, nivelHabilidade, salario));
	}

	@Desafio("definirCapitao")
	public void definirCapitao(Long idJogador) {
		
		Jogador jogador = findJogador(idJogador).orElseThrow(JogadorNaoEncontradoException::new);
		times.stream().filter(t -> t.getId().equals(jogador.getIdTime())).forEach(t -> t.setIdJogadorCapitao(idJogador));;
	}

	@Desafio("buscarCapitaoDoTime")
	public Long buscarCapitaoDoTime(Long idTime) {
		
		Time time = findTime(idTime).orElseThrow(TimeNaoEncontradoException::new); 
		if(time.getIdJogadorCapitao() == null) {
			throw new CapitaoNaoInformadoException();
		}
		
		return time.getIdJogadorCapitao();
	}

	@Desafio("buscarNomeJogador")
	public String buscarNomeJogador(Long idJogador) {
		
		return findJogador(idJogador).orElseThrow(JogadorNaoEncontradoException::new).getNome();
	}

	@Desafio("buscarNomeTime")
	public String buscarNomeTime(Long idTime) {
		
		return findTime(idTime).orElseThrow(TimeNaoEncontradoException::new).getNome(); 
	}

	@Desafio("buscarJogadoresDoTime")
	public List<Long> buscarJogadoresDoTime(Long idTime) {
		
		findTime(idTime).orElseThrow(TimeNaoEncontradoException::new); 
		
		return jogadores.stream()
				.filter(j -> j.getIdTime().equals(idTime))
				.sorted(Comparator.comparing(Jogador::getId))
				.map(Jogador::getId).collect(Collectors.toList());
	}

	@Desafio("buscarMelhorJogadorDoTime")
	public Long buscarMelhorJogadorDoTime(Long idTime) {
		
		findTime(idTime).orElseThrow(TimeNaoEncontradoException::new); 
		
		return jogadores.stream()
				.filter(j -> j.getIdTime().equals(idTime))
				.sorted(Comparator.comparing(Jogador::getNivelHabilidade).reversed())
				.findFirst().orElse(null).getId();
	}

	@Desafio("buscarJogadorMaisVelho")
	public Long buscarJogadorMaisVelho(Long idTime) {
		
		findTime(idTime).orElseThrow(TimeNaoEncontradoException::new); 
		
		return jogadores.stream()
				.filter(j -> j.getIdTime().equals(idTime))
				.sorted(Comparator.comparing(Jogador::getDataNascimento)
						.thenComparingLong(Jogador::getId))
				.findFirst().orElse(null).getId();
	}

	@Desafio("buscarTimes")
	public List<Long> buscarTimes() {
		
		return times.stream().sorted(Comparator.comparingLong(Time::getId))
				.map(Time::getId).collect(Collectors.toList());
	}

	@Desafio("buscarJogadorMaiorSalario")
	public Long buscarJogadorMaiorSalario(Long idTime) {
		
		findTime(idTime).orElseThrow(TimeNaoEncontradoException::new); 
		
		return jogadores.stream()
				.filter(j -> j.getIdTime().equals(idTime))
				.sorted(Comparator.comparing(Jogador::getSalario).reversed()
						.thenComparingLong(Jogador::getId))
				.findFirst().orElse(null).getId();
	}
	
	@Desafio("buscarSalarioDoJogador")
	public BigDecimal buscarSalarioDoJogador(Long idJogador) {
		
		return findJogador(idJogador).orElseThrow(JogadorNaoEncontradoException::new).getSalario();
	}

	@Desafio("buscarTopJogadores")
	public List<Long> buscarTopJogadores(Integer top) {
		
		return jogadores.stream()
				.sorted(Comparator.comparing(Jogador::getNivelHabilidade).reversed()
						.thenComparing(Jogador::getId))
				.limit(top)
				.map(Jogador::getId).collect(Collectors.toList());
	}

	@Desafio("buscarCorCamisaTimeDeFora")
	public String buscarCorCamisaTimeDeFora(Long timeDaCasa, Long timeDeFora) {
		
		Time timeCasa = findTime(timeDaCasa).orElseThrow(TimeNaoEncontradoException::new); 
		Time timeFora = findTime(timeDeFora).orElseThrow(TimeNaoEncontradoException::new);
		
		if(timeCasa.getCorUniformePrincipal().equals(timeFora.getCorUniformePrincipal())) {
			return timeFora.getCorUniformeSecundario();
		} else {
			return timeFora.getCorUniformePrincipal();
		}
	}

	private Optional<Time> findTime(Long idTime) {
		if (idTime != null) {
			return times.stream().filter(t -> t.getId().equals(idTime)).findFirst();
		}
		return null;
	}

	private Optional<Jogador> findJogador(Long idJogador) {
		if (idJogador != null) {
			return jogadores.stream().filter(j -> j.getId().equals(idJogador)).findFirst();
		}
		return null;
	}

}