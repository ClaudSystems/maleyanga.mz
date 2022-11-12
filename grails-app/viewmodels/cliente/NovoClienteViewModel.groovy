package cliente

import grails.transaction.Transactional
import mz.maleyanga.ClienteService
import mz.maleyanga.GenerateRandomNumber
import mz.maleyanga.UtilizadorService
import mz.maleyanga.cliente.Cliente
import mz.maleyanga.conta.Conta
import mz.maleyanga.security.Utilizador
import org.springframework.stereotype.Service
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.NotifyChange
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zul.Label
import org.zkoss.zul.ListModelList
import org.zkoss.zul.Window

@Transactional
@Service
class NovoClienteViewModel {
    ClienteService clienteService
    UtilizadorService utilizadorService
    private String error_ = "Erro na gravação dos dados"
    private  String filter
    private ListModelList gestores
    private ListModelList tiposDeInde
    private ListModelList generos
    private Utilizador selectedGestor
    private ListModelList<Conta> integradoras
    private ListModelList estadosCivil
    private Cliente novoCliente = new Cliente()
    private  @Wire Label info
    private  @Wire Window win_novo_cliente
    private String blue = "color:blues;font-size:14pt"
    private String red = "color:red;font-size:14pt"
    private ListModelList<Cliente> clientes


    @Command
    def searchCliente(){
        def clientes = clienteService.findAllByName(novoCliente.nome)
          info.value = clientes
    }

    String getFilter() {
        return filter
    }

    void setFilter(String filter) {
        this.filter = filter
    }

    @Command
    void doSearch() {
        fecharEditor()
        clientes.clear()
        List<Cliente> allItems = Cliente.all
        if (filter == null || "".equals(filter)) {
            clientes.addAll(allItems)
        } else {
            for (Cliente item : allItems) {
                if (item.nome.toLowerCase().indexOf(filter.toLowerCase()) >= 0 ||
                        item.nuit.toString().indexOf(filter) >= 0 ||
                        item.residencia.toString().indexOf(filter) >= 0 ||
                        item.dateCreated.format('dd/MM/yyyy').toString().indexOf(filter) >= 0 ||
                        item.numeroDeIndentificao.indexOf(filter) >= 0) {
                    clientes.add(item)
                }
            }
        }
    }
    ListModelList<Cliente> getClientes() {
        if(clientes==null){
            clientes = new ListModelList<Cliente>()
        }
        return clientes
    }

    @NotifyChange(["novoCliente","clientes"])
    @Command
    def showClientes(){
        Executions.sendRedirect("/cliente/")

    }

    @Command
    @NotifyChange(["novoCliente"])
    def addNovoCliente(){
        novoCliente = new Cliente()
    }

    @Command
    @NotifyChange(["novoCliente"])
    def fecharEditor(){
        novoCliente = null
    }
    Utilizador getSelectedGestor() {
        return selectedGestor
    }

    void setSelectedGestor(Utilizador selectedGestor) {
        this.selectedGestor = selectedGestor
    }

    Cliente getNovoCliente() {
        return novoCliente
    }
    ListModelList getEstadosCivil() {
        if(estadosCivil==null){
            estadosCivil = new ListModelList<String>(["Solteiro","Solteira","Casado","Casada","Separado Judicialmente","Separada Judicialmente", "Outro(a)"])
        }

        return estadosCivil
    }
    ListModelList getTiposDeInde() {
        if(tiposDeInde == null){
            tiposDeInde = new ListModelList<String>(["BI","Passaporte","Carta de conducao", "Outro"])
        }
        return tiposDeInde
    }

    ListModelList getGeneros() {
        if(generos==null){
            generos = new ListModelList<String>(["masculino","feminino","transgênero", "não-binário","agênero","pangênero","genderqueer"," two-spirit","outro"])
        }
        return generos
    }

    ListModelList<Conta> getIntegradoras() {
        if (integradoras==null){
            integradoras = new ListModelList<Conta>(Conta.findAllByFinalidade("conta_integradora"))
        }

        return integradoras
    }
    ListModelList getGestores() {
        if (gestores==null){
            gestores = new ListModelList<Utilizador>()
        }
        gestores.clear()
        def utilizadores = utilizadorService.all
        for(Utilizador u in utilizadores){
            if (u.authorities.any { it.authority == "CLIENTE_GESTOR" }) {
                gestores.add(u)
            }
        }
        return gestores
    }

    @NotifyChange(['novoCliente'])
    @Command
    void setNovoCliente(Cliente novoCliente) {
        this.novoCliente = novoCliente
    }
    @Command
    @NotifyChange(['novoCliente','clientes'])
    def salvarCliente(){
        info.value = ""
         try {
                novoCliente.utilizador = Utilizador.findById(selectedGestor.id)
              if(getsContaIntegradora()==null){
                  info.value = "Por favar de criar uma conta integradora com a descrição 'CLIENTES'!"
                  info.style = "color:red;font-size:14pt"
                  return
              }
            if(novoCliente.nome.equals(null)){
                info.value = "Preencha o campo 'Nome Completo'!"
                info.style = "color:red;font-size:14pt"
                return
            }

            if(Cliente.findByNome(novoCliente.nome)){
                info.value = "Já existe um cliente com este nome!"
                info.style = "color:red;font-size:14pt"
                return
            }
            if(Cliente.findById(novoCliente.id)){
                info.value = "Este Cliente já existe na base de dados!"
                info.style = "color:red;font-size:14pt"
                return
            }
            if(novoCliente.estadoCivil.equals(null)){
                info.value = "Selecione  o estado civil !"
                info.style = "color:red;font-size:14pt"
                return
            }
            if(novoCliente.dataDeExpiracao.equals(null)){
                info.value = "Preencha o campo 'Validade'"
                info.style = red
                return
            }
            if(Cliente.findByNuit(novoCliente.nuit)){
                info.value = "Já existe um cliente com este NUIT!"
                info.style = "color:red;font-size:14pt"
                return
            }
            if(novoCliente.tipoDeIndentificacao.equals(null)){
                info.value = "Selecione  o tipo de documento de indentificação!"
                info.style = "color:red;font-size:14pt"
                return
            }
            if(novoCliente.dataDeExpiracao.equals(null)){
                info.value = "Selecione  a data de expiração !"
                info.style ="color:red;font-size:14pt"
                return
            }
            if(novoCliente.numeroDeIndentificao.equals(null)){
                info.value = "Digite o número de indentificação !"
                info.style = "color:red;font-size:14pt"
                return
            }
            if(novoCliente.residencia.equals(null)){
                info.value = "Preencha o campo residência!"
                info.style = "color:red;font-size:14pt"
                return
            }
            if(novoCliente.utilizador==null){
                info.value = "Selecione um gestor!"
                info.style = "color:red;font-size:14pt"
                return
            }

            info.value = ""
            novoCliente.classificacao = "medio"
            novoCliente.utilizador = selectedGestor
            novoCliente.ativo = true
             novoCliente.codigo = GenerateRandomNumber.randomBigNumberString
            def result = clienteService.saveCliente(novoCliente)
             if(result){
                 info.value = "Dados gravados com sucesso!"
                 info.style  ="color:blue;font-size:14pt"
                 clientes.add(novoCliente)
                 novoCliente = new Cliente()
             }else {
                 info.value = "Erro na gravação dos dados"
                 info.style  ="color:red;font-size:14pt"
             }

        } catch (Exception e) {
            info.value = "Erro na gravação dos dados"
            info.style  ="color:red;font-size:14pt"
          //  System.println(e.toString())


        }




    }

    Conta getsContaIntegradora() {
        def contaDb = Conta.findByFinalidadeAndDesignacaoDaConta("conta_integradora", "CLIENTES") as Conta
         return contaDb
    }



}
