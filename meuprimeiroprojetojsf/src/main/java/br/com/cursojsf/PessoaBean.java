package br.com.cursojsf;


import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;


import br.com.dao.DaoGeneric;
import br.com.entidades.Pessoa;

@ViewScoped
//@SessionScoped
//@ApplicationScoped
@ManagedBean(name = "pessoaBean")
public class PessoaBean {

	private Pessoa pessoa = new Pessoa();
	
	private DaoGeneric<Pessoa> daoGeneric = new DaoGeneric<Pessoa>();
	
	
	public void salvar() {
		
		pessoa = daoGeneric.merge(pessoa);
		
		/*
		daoGeneric.salvar(pessoa);
		pessoa = new Pessoa();
		*/
		
	}
	
	public void novo() {
		pessoa = new Pessoa();
	}
	
	public void remove() {
		daoGeneric.DeletePorId(pessoa);
		pessoa = new Pessoa();
	}


	public Pessoa getPessoa() {
		return pessoa;
	}


	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}


	public DaoGeneric<Pessoa> getDaoGeneric() {
		return daoGeneric;
	}


	public void setDaoGeneric(DaoGeneric<Pessoa> daoGeneric) {
		this.daoGeneric = daoGeneric;
	}
	
	 
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	private String nome;
	private String senha;
	private String texto;

	private HtmlCommandButton commandButton;

	private List<String> nomes = new ArrayList<String>();

	public String addNome() {

		nomes.add(nome);

		if (nomes.size() > 3) {
			commandButton.setDisabled(true);
			return "paginanavegada?faces-redirect=true";
		}

		return ""; // null ou vazio retorna na mesma pagina -> outcome

	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public void setCommandButton(HtmlCommandButton commandButton) {
		this.commandButton = commandButton;
	}

	public HtmlCommandButton getCommandButton() {
		return commandButton;
	}

	public void setNomes(List<String> nomes) {
		this.nomes = nomes;
	}

	public List<String> getNomes() {
		return nomes;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
*/
}
