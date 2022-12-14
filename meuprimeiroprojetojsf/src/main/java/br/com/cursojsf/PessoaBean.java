package br.com.cursojsf;


import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import javax.xml.bind.DatatypeConverter;

import com.google.gson.Gson;

import br.com.dao.DaoGeneric;
import br.com.entidades.Cidades;
import br.com.entidades.Estados;
import br.com.entidades.Pessoa;
import br.com.jpautil.JPAUtil;
import br.com.repository.IDaoPessoa;
import net.bootsfaces.component.selectOneMenu.SelectOneMenu;

//@ViewScoped
//@SessionScoped
//@ApplicationScoped
//@ManagedBean
@javax.faces.view.ViewScoped // do cdi pacote view
@Named(value = "pessoaBean")// manegedBean do cdi
public class PessoaBean implements Serializable {

	
	private static final long serialVersionUID = 1L;

	private Pessoa pessoa = new Pessoa();
		
	private List<Pessoa> pessoas = new ArrayList<Pessoa>();
	
	private List<SelectItem> estados;
	
	private List<SelectItem> cidades;
	
	private Part arquivofoto;
	
	@Inject
	private EntityManager entityManager;
	
	@Inject
	private DaoGeneric<Pessoa> daoGeneric;
	
	@Inject
	private IDaoPessoa iDaoPessoa;
	
	@Inject
	private JPAUtil jpaUtil;
	

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
	
	public void setPessoas(List<Pessoa> pessoas) {
		this.pessoas = pessoas;
	}
	
	public List<Pessoa> getPessoas() {
		return pessoas;
	}
	

	public void setEstados(List<SelectItem> estados) {
		this.estados = estados;
	}
	
	public List<SelectItem> getEstados() {
		estados = iDaoPessoa.listaEstados();
		return estados;
	}
	
	public void setCidades(List<SelectItem> cidades) {
		this.cidades = cidades;
	}
	
	public List<SelectItem> getCidades() {
		return cidades;
	}
	
	public void setArquivofoto(Part arquivofoto) {
		this.arquivofoto = arquivofoto;
	}
	
	public Part getArquivofoto() {
		return arquivofoto;
	}
	

	public void salvar() throws IOException {
		
		if(arquivofoto !=null) {
		/*Processar imagem*/
		byte[] imagemByte = getByte(arquivofoto.getInputStream());
		pessoa.setFotoIconBase64Original(imagemByte); /*salva imagem original*/
		
		/*transformar em um bufferimage*/
		BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imagemByte));
		
		if(bufferedImage !=null) {
			pessoa.setFotoIconBase64Original(imagemByte);/*salva imagem original*/
				/*pega o tipo da imagem*/
				@SuppressWarnings("static-access")
				int type = bufferedImage.getType() == 0? bufferedImage.TYPE_INT_ARGB:bufferedImage.getType();
				
				int largura = 200;
				int altura = 200; 
				
				/*criar a miniatura*/
				
				BufferedImage resizedImage = new BufferedImage(largura, altura, type);
				Graphics2D g = resizedImage.createGraphics();
				g.drawImage(bufferedImage, 0, 0, largura, altura, null);
				g.dispose();
				
				/*Escrever novamente a imagem em tamanho menor*/
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				String extensao = arquivofoto.getContentType().split("\\/")[1]; /*imagem/png - pega a extens??o quebrando no array a posi????o 1*/
				ImageIO.write(resizedImage, extensao, baos);
				
				String miniImagem = "data:" + arquivofoto.getContentType() + ";base64," + DatatypeConverter.printBase64Binary(baos.toByteArray());
				/*Processar imagem*/
				
				//System.out.println(arquivofoto);
				pessoa.setFotoIconBase64(miniImagem);
				pessoa.setExtensao(extensao);
			}
		}
		
		pessoa = daoGeneric.merge(pessoa);
		carregarPessoas();
		mostrarMsg("Cadastrado com sucesso!");// mostrar mensagem por ultimo SEMPRE!
		/*
		daoGeneric.salvar(pessoa);
		pessoa = new Pessoa();
		*/
		
	}
	
	public void registraLog() { /*m??todo registra log ?? chamado antes do salvar, pelo evento actionListener*/
		
		System.out.println("Registra Log");
		/*Criar a rotina de grava????o de log*/
		
		
	}
	
	public void mudancaDeValor(ValueChangeEvent evento) {
		System.out.println("Valor antigo: " + evento.getOldValue());
		System.out.println("Valor novo: " + evento.getNewValue());
	}
	
	private void mostrarMsg(String msg) {
		
		FacesContext context = FacesContext.getCurrentInstance();
		FacesMessage message = new FacesMessage(msg);
		context.addMessage(null, message);
	}

	public void novo() {
		/*executa algum processo antes de novo*/
		pessoa = new Pessoa();
	}
	
	public void limpar() {
		/*executa algum processo antes de limpar*/
		pessoa = new Pessoa();
	}
	
	public void remove() {
		daoGeneric.DeletePorId(pessoa);
		pessoa = new Pessoa();
		carregarPessoas();
		mostrarMsg("Removido com sucesso!");
	}
	
	@PostConstruct //carrega a lista quando abrir a pagina
	public void carregarPessoas() {
		pessoas = daoGeneric.getListEntityLimit10(Pessoa.class);
	}
	
	public void pesquisaCep(AjaxBehaviorEvent event) {
		//System.out.println("Metodo pesquisa cep chamado CEP: " + pessoa.getCep());
		
		try {
			
			URL url = new URL("https://viacep.com.br/ws/"+  pessoa.getCep() +"/json/");
			URLConnection connection = url.openConnection();
			InputStream is = connection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
			
			String cep = "";
			StringBuilder jsoncep = new StringBuilder();
			
			while((cep = br.readLine()) != null) { //enquanto tiver linhas e n??o vazias
				
				jsoncep.append(cep);
			}
			
			Pessoa gson = new Gson().fromJson(jsoncep.toString(),Pessoa.class);
			
			pessoa.setCep(gson.getCep());
			pessoa.setLogradouro(gson.getLogradouro());
			pessoa.setComplemento(gson.getComplemento());
			pessoa.setBairro(gson.getBairro());
			pessoa.setLocalidade(gson.getLocalidade());
			pessoa.setUf(gson.getUf());
			pessoa.setBairro(gson.getBairro());
			pessoa.setIbge(gson.getIbge());
			pessoa.setDdd(gson.getDdd());
			
			System.out.println(gson);
			
		} catch (Exception e) {
			e.printStackTrace();
			mostrarMsg("Erro ao consultar o cep");
		}
	}
	
	
	public String deslogar() {
		
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		
		HttpServletRequest req = (HttpServletRequest) externalContext.getRequest();
		HttpSession session = req.getSession();
		
		session.removeAttribute("usuarioLogado");
		
		req.getSession().invalidate();
		
		return "index.jsf?faces-redirect=true";
	}
	
	
	public String logar() {
		
		Pessoa pessoaUser = iDaoPessoa.consultarUsuario(pessoa.getLogin(), pessoa.getSenha());
		
		if(pessoaUser!=null) { //achou usuario
			
			/*
			//adicionar o usu??rio na sess??o - usuarioLogado
			FacesContext context = FacesContext.getCurrentInstance();
			ExternalContext externalContext = context.getExternalContext();
			externalContext.getSessionMap().put("usuarioLogado", pessoaUser.getLogin());
			*/
			
			//adicionar o usu??rio na sess??o - usuarioLogado
			FacesContext context = FacesContext.getCurrentInstance();
			ExternalContext externalContext = context.getExternalContext();
			
			HttpServletRequest req = (HttpServletRequest) externalContext.getRequest();
			HttpSession session = req.getSession();
			
			session.setAttribute("usuarioLogado", pessoaUser);
			
			return "minhaprimeirapagina.jsf?faces-redirect=true";
			
		}else {
			
			FacesContext.getCurrentInstance().addMessage("msg", new FacesMessage("Usu??rio n??o encontrado!"));
		}
		
		//System.out.println(pessoa.getLogin() + " - " + pessoa.getSenha());
		
		return "index.jsf";
	}
	
	public Boolean permiteAcesso(String acesso) {
		
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		
		HttpServletRequest req = (HttpServletRequest) externalContext.getRequest();
		HttpSession session = req.getSession();
		
		Pessoa pessoaUser = (Pessoa) session.getAttribute("usuarioLogado");
		
		return pessoaUser.getPerfilUser().equals(acesso);
		
	}
	
	public void carregaCidades(AjaxBehaviorEvent event) {
		
		//String codigoEstado = (String)event.getComponent().getAttributes().get("submittedValue");
		
		Estados estado = (Estados) ((SelectOneMenu) event.getSource()).getValue(); //pega o obejto inteiro selecionado no comboBox
			
			//Estados estado = JPAUtil.getEntityManager().find(Estados.class, Long.parseLong(codigoEstado));
			
			if(estado !=null) {
				
				pessoa.setEstados(estado);
				@SuppressWarnings("unchecked")
				List<Cidades> cidades = entityManager.createQuery("from Cidades where estados.id = " +  estado.getId()).getResultList();
				
				List<SelectItem> selectItemsCidades = new ArrayList<SelectItem>();
				
				for (Cidades cidade : cidades) {
					selectItemsCidades.add(new SelectItem(cidade, cidade.getNome()));
				}
				
				setCidades(selectItemsCidades);
				
			}
			//System.out.println(codigoEstado);
		
	}
	
	public void editar() {
		
		if(pessoa.getCidades() != null) {
			
			Estados estado = pessoa.getCidades().getEstados();
			pessoa.setEstados(estado);
			
			@SuppressWarnings("unchecked")
			List<Cidades> cidades = entityManager.createQuery("from Cidades where estados.id = " +  estado.getId()).getResultList();
			
			List<SelectItem> selectItemsCidades = new ArrayList<SelectItem>();
			
			for (Cidades cidade : cidades) {
				selectItemsCidades.add(new SelectItem(cidade, cidade.getNome()));
			}
			
			setCidades(selectItemsCidades);
			
		}
		
		//System.out.println(pessoa);
	}
	

	
	/*M??todo que converte inputstream para array de bytes*/
	private byte[] getByte (InputStream is) throws IOException {
		 int len;
		 int size = 1024;
		 byte[] buf = null;
		 
		 if(is instanceof ByteArrayInputStream) {
			 size = is.available();
			 buf = new byte[size];
			 len = is.read(buf,0,size);
		 }else {
			 ByteArrayOutputStream bos = new ByteArrayOutputStream();
			 buf = new byte[size];
			 
			 while((len = is.read(buf,0,size)) != -1) {
				 bos.write(buf,0,len);
			 }
			 
			 buf = bos.toByteArray();
		 }
		 
		 return buf;
	}
	
	public void download() throws IOException {
		
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String fileDownloadId = params.get("fileDownloadId");
		System.out.println(fileDownloadId);
		
		Pessoa pessoa = daoGeneric.consultar(Pessoa.class, fileDownloadId);
		
		//System.out.println(pessoa);
		
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		
		response.addHeader("Content-Disposition", "attachment; filename=download." + pessoa.getExtensao());
		response.setContentType("application/octet-stream");
		response.setContentLengthLong(pessoa.getFotoIconBase64Original().length);
		response.getOutputStream().write(pessoa.getFotoIconBase64Original());
		response.getOutputStream().flush();
		FacesContext.getCurrentInstance().responseComplete();
		
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
