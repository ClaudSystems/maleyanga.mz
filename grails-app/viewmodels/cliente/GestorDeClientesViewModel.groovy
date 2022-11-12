package cliente

import grails.plugin.springsecurity.SpringSecurityService
import grails.transaction.Transactional
import mz.maleyanga.AssinanteService
import mz.maleyanga.ClienteService
import mz.maleyanga.CreditoService
import mz.maleyanga.ExtratoDeConta
import mz.maleyanga.GenerateRandomNumber
import mz.maleyanga.UtilizadorService
import mz.maleyanga.cliente.Assinante
import mz.maleyanga.cliente.Cliente
import mz.maleyanga.conta.Conta
import mz.maleyanga.ContaService
import mz.maleyanga.credito.Credito
import mz.maleyanga.pagamento.Pagamento
import mz.maleyanga.pedidoDeCredito.PedidoDeCredito
import mz.maleyanga.security.Utilizador
import mz.maleyanga.transacao.Transacao
import org.springframework.security.access.method.P
import org.springframework.stereotype.Service
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.NotifyChange
import org.zkoss.bind.annotation.Init
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zul.Hbox
import org.zkoss.zul.Label
import org.zkoss.zul.ListModelList
import org.zkoss.zul.Tab
import org.zkoss.zul.Window

@Transactional
@Service
class GestorDeClientesViewModel {
    SpringSecurityService springSecurityService
    ClienteService clienteService
    ContaService contaService
    AssinanteService assinanteService
    CreditoService creditoService
    UtilizadorService utilizadorService

     @Wire Label info
    @Wire Window win_cliente
    @Wire Tab tab_credito
    @Wire Tab tab_assinante
    @Wire Tab tab_assinantes_do_cliente
    @Wire Tab tab_pagamento
    @Wire Hbox hb_edit_cliente
    @Wire Hbox hb_new_cliente
    private String  filterCliente
    private String  filter
    private String blue = "color:blues;font-size:14pt"
    private String red = "color:red;font-size:14pt"
    private String error_ = "Erro na gravação dos dados"
    private String alerta = "Se realemente deseja eliminar este Objecto faça double click!"
    private ListModelList gestores
    private ListModelList creditos
    private ListModelList tiposDeInde
    private ListModelList generos
    private ListModelList estadosCivil
    private ListModelList<Cliente> clientes
    private ListModelList<Cliente> clientesDosassinantes
    private ListModelList<Cliente> todosClientes
    private ListModelList<Assinante> allAssinantes
    private ListModelList<PedidoDeCredito> pedidoDeCreditos
    private Conta contaCliente
    private ListModelList<Assinante> assinantes
    private ListModelList<Pagamento> pagamentos
    private ListModelList<Conta> integradoras
    private  Utilizador selectedGestor
    private Conta sContaIntegradora
    private Cliente selectedCliente
    private Cliente editCliente
    private  Credito sCredito
    private Assinante sAssinante
    private  Pagamento sPagamento
    private  BigDecimal  totalPrestacao = 0.0
    private BigDecimal totalJuros = 0.0
    private BigDecimal toTalamor = 0.0
    private BigDecimal toTalPago = 0.0
    private BigDecimal totalDivida = 0.0
    private String filterAssinante

    @Command
    @NotifyChange(["pedidoDeCreditos"])
    ListModelList<PedidoDeCredito> getPedidoDeCreditos() {
        if(pedidoDeCreditos ==null){
            pedidoDeCreditos = new ListModelList<PedidoDeCredito>()
        }
        if(selectedCliente!=null){
            pedidoDeCreditos = PedidoDeCredito.findAllByCliente(selectedCliente)
        }
        return pedidoDeCreditos
    }


    ListModelList<ExtratoDeConta> extratoDeContas = new ListModelList<ExtratoDeConta>()
    ListModelList<Transacao> transacoes = new ListModelList<Transacao>()
    ListModelList<ExtratoDeConta> getExtratoDeContas() {
        getTransacoes()
        BigDecimal saldo=0.0
        for(Transacao transacao in transacoes){

            if(transacao.credito){
                ExtratoDeConta extratoDeConta = new ExtratoDeConta()
                saldo-=transacao.valor
                extratoDeConta.data = transacao.dateCreated
                extratoDeConta.credito=transacao.valor
                extratoDeConta.debito = 0.0
                extratoDeConta.descricao = transacao.descricao
                extratoDeConta.saldo=saldo
                extratoDeContas.add(extratoDeConta)
            }else {
                ExtratoDeConta extratoDeConta = new ExtratoDeConta()
                saldo+=transacao.valor
                extratoDeConta.data = transacao.dateCreated
                extratoDeConta.credito=0.0
                extratoDeConta.debito =transacao.valor
                extratoDeConta.descricao = transacao.descricao
                extratoDeConta.saldo =saldo
                extratoDeContas.add(extratoDeConta)
            }

        }
        return extratoDeContas
    }

    ListModelList<Transacao> getTransacoes() {
        getContaCliente()
        for(Transacao transacao in contaCliente?.transacoes?.sort{it.id}){
            transacoes.add(transacao)
        }
        transacoes.sort{it.id}
        return transacoes
    }
    Conta getContaCliente() {
        if(selectedCliente!=null){
            contaCliente = Conta.findById(selectedCliente.id)
        }
        return contaCliente
    }

    Cliente getEditCliente() {
        return editCliente
    }

    void setEditCliente(Cliente editCliente) {
        tab_assinantes_do_cliente.label = "Assinantes do(a) "+editCliente.nome
        this.editCliente = editCliente
    }


    ListModelList<Conta> getIntegradoras() {
        if (integradoras==null){
            integradoras = new ListModelList<Conta>(Conta.findAllByFinalidade("conta_integradora"))
        }

        return integradoras
    }

    Conta getsContaIntegradora() {
        return sContaIntegradora
    }

    void setsContaIntegradora(Conta sContaIntegradora) {
        this.sContaIntegradora = sContaIntegradora
    }


    @Command
    def showPagamento(){
       Executions.sendRedirect("/pagamento"+sPagamento.id)
    }

    ListModelList<Cliente> getTodosClientes() {
        if(todosClientes==null){
            todosClientes = new ListModelList<Cliente>()
        }
     //  todosClientes.clear()
      //  todosClientes = Cliente.all
        return todosClientes
    }

    ListModelList<Cliente> getClientesDosassinantes() {
        if(clientesDosassinantes==null){
            clientesDosassinantes=new ListModelList<Cliente>(Cliente.findAll())
        }
        return clientesDosassinantes
    }

    String getFilterAssinante() {
        return filterAssinante
    }

    void setFilterAssinante(String filterAssinante) {
        this.filterAssinante = filterAssinante
    }

    ListModelList<Assinante> getAllAssinantes() {
        if(allAssinantes==null){
            allAssinantes = new ListModelList<Assinante>()
        }
        return allAssinantes
    }

    ListModelList<Assinante> getAssinantes() {
        if(assinantes==null){

           assinantes = new ListModelList<Assinante>()
        }
        assinantes.clear()
        assinantes = Assinante.findAllByCliente(selectedCliente)
        return assinantes
    }

    Assinante getsAssinante() {
        return sAssinante
    }

    void setsAssinante(Assinante sAssinante) {
        this.sAssinante = sAssinante
    }

    @Command
    @NotifyChange(['sAssinante','allAssinantes'])
    def addAssinate(){
        sAssinante = new Assinante()
    }

    @Command
    static printExtrato(){
      //  Executions.sendRedirect("/credito/printExtratoDeCredito")
    }

    BigDecimal getTotalPrestacao() {
        return totalPrestacao
    }

    BigDecimal getTotalJuros() {
        return totalJuros
    }

    BigDecimal getToTalamor() {
        return toTalamor
    }

    BigDecimal getToTalPago() {
        return toTalPago
    }

    BigDecimal getTotalDivida() {
        return totalDivida
    }

    ListModelList getPagamentos() {
        if(pagamentos == null){
            pagamentos = new ListModelList<Pagamento>()
        }
        pagamentos = Pagamento.findAllByCredito(sCredito)
        return pagamentos
    }

    @Command
    @NotifyChange(['pagamentos','sPagamento','totalPrestacao','toTalamor','totalJuros','totalDivida'])
    def showPagamentos(){
        getPagamentos()
        creditoService.credito = sCredito
        totalJuros=0.0
        totalDivida =0.0
        toTalamor =0.0
        totalPrestacao =0.0
        toTalPago=0.0
        for (Pagamento p in pagamentos){
            totalJuros+=p.valorDeJuros
            totalDivida +=p.totalEmDivida
            toTalamor +=p.valorDeAmortizacao
            totalPrestacao +=p.valorDaPrestacao
            toTalPago+=p.valorPago
        }

    }
    Pagamento getsPagamento() {
        return sPagamento
    }

    void setsPagamento(Pagamento sPagamento) {
        this.sPagamento = sPagamento
    }

    @NotifyChange(["pagamentos","info",'selectedCliente','clientes','selectedGestor','clientes','sAssinante',"editCliente"])
    @Command
    void doSearchCliente() {
        fecharEditor()
        todosClientes.clear()
        List<Cliente> allItems = clienteService.findAllByName(filterCliente)
        if (filterCliente == null || "".equals(filterCliente)) {
            hb_edit_cliente.visible = false
        } else {
            for (Cliente item : allItems) {
                if (item.nome.toLowerCase().indexOf(filterCliente.toLowerCase()) >= 0 ||
                        item.nuit.toString().indexOf(filterCliente) >= 0 ||
                        item.telefone.toString().indexOf(filterCliente) >= 0 ||
                        item.telefone1.toString().indexOf(filterCliente) >= 0 ||
                        item.telefone2.toString().indexOf(filterCliente) >= 0 ||
                        item.residencia.toString().indexOf(filterCliente) >= 0 ||
                        item.dateCreated.format('dd/MM/yyyy').toString().indexOf(filterCliente) >= 0 ||
                        item.numeroDeIndentificao.indexOf(filterCliente) >= 0) {
                    todosClientes.add(item)
                }
            }
        }
    }

    @Command
    void doSearch() {
        info.value = ""
        clientes.clear()
        List<Cliente> allItems = Cliente.findAllByUtilizador(selectedGestor)
        if (filter == null || "".equals(filter)) {
            clientes.addAll(allItems)
        } else {
            for (Cliente item : allItems) {
                if (item.dataDeExpiracao.toString().toLowerCase().indexOf(filter.toLowerCase()) >= 0 ||
                        item.getId().toString().indexOf(filter) >= 0 ||
                        item.nome.toLowerCase().indexOf(filter) >= 0 ||
                        item.nome.indexOf(filter) >= 0 ||
                        item.nuit.toString().toLowerCase().indexOf(filter) >= 0 ||
                        item.telefone.toString().indexOf(filter) >= 0 ||
                        item.telefone1.toString().indexOf(filter) >= 0 ||
                        item.telefone2.toString().indexOf(filter) >= 0 ||
                        item.residencia.toString().indexOf(filter) >= 0 ||
                        item.estadoCivil.toString().indexOf(filter) >= 0 ||
                        item.classificacao.indexOf(filter) >= 0 ||
                        item.ativo.toString().indexOf(filter) >= 0 ||
                        item.utilizador.username.toString().indexOf(filter) >= 0 ||
                        item.localDeTrabalho.toString().indexOf(filter) >= 0 ||
                        item.tipoDeIndentificacao.toLowerCase().indexOf(filter) >= 0) {
                    clientes.add(item)
                }

            }
        }
    }


    @Command
    void doSearchAssinantes() {
        info.value = ""
        allAssinantes.clear()
        List<Assinante> allItems = Assinante.all
        if (filterAssinante == null || "".equals(filterAssinante)) {
            allAssinantes.clear()
        } else {
            for (Assinante item : allItems) {
                if (item.nomeDoAssinante.toString().toLowerCase().indexOf(filterAssinante.toLowerCase()) >= 0 ||
                        item.nuitDoAssinante.toString().indexOf(filterAssinante) >= 0 ||
                        item.tipoDeIndentificacaoDoAssinante.indexOf(filterAssinante) >= 0 ||
                        item.numeroDeIndentificaoDoAssinante.toString().toLowerCase().indexOf(filterAssinante) >= 0 ||
                        item.residenciaDoAssinante.toString().indexOf(filterAssinante) >= 0 ||
                        item.emailDoAssinante.toString().indexOf(filterAssinante) >= 0 ||
                        item?.telefone2?.toString()?.indexOf(filterAssinante) >= 0 ||
                        item?.telefone?.toString()?.indexOf(filterAssinante) >= 0 ||
                        item?.telefone1?.toString()?.indexOf(filterAssinante) >= 0 ||
                        item.utilizador.toString().indexOf(filterAssinante) >= 0 ||
                        item.utilizador.username.toLowerCase().indexOf(filterAssinante) >= 0) {
                    allAssinantes.add(item)
                }

            }
        }
    }
    String getFilterCliente() {
        return filterCliente
    }

    void setFilterCliente(String filterCliente) {
        this.filterCliente = filterCliente
    }

    ListModelList getCreditos() {
        if(creditos==null){
            creditos = new ListModelList<Credito>()
        }
        if(selectedCliente){
            creditos = Credito.findAllByCliente(selectedCliente)
        }

        return creditos
    }

    Credito getsCredito() {
        return sCredito
    }

    void setsCredito(Credito sCredito) {

        this.sCredito = sCredito
    }

    @NotifyChange(["clientes"])
    void setSelectedGestor(Utilizador selectedGestor) {
        this.selectedGestor = selectedGestor

    }

    ListModelList getEstadosCivil() {
        if(estadosCivil==null){
            estadosCivil = new ListModelList<String>(["Solteiro","Solteira","Casado","Casada","Separado Judicialmente","Separada Judicialmente", "Outro(a)"])
        }

        return estadosCivil
    }





    @Command
    @NotifyChange(['selectedCliente','clientes','selectedGestor','clientes','todosClientes','assinantes','allAssinantes'])
    def salvarAssinante(){
        if(sAssinante.nomeDoAssinante.equals(null)){
            info.value = "Preencha o campo 'Nome Completo'!"
            return
        }
        if(Assinante.findByNomeDoAssinante(sAssinante.nomeDoAssinante)){
            info.value = "Já existe um Assinante com este nome!"
            info.style = red
            return
        }
        if(sAssinante.nuitDoAssinante.equals(null)){
            info.value = "Preencha o campo 'NUIT'"
            info.style = red
            return
        }
        if(Assinante.findByNuitDoAssinante(sAssinante.nuitDoAssinante)){
            info.value = "Já existe um assinante com este NUIT!"
            info.style = red
            return
        }
        if(sAssinante.dataDeExpiracaoDoAssinante.equals(null)){
            info.value = "Selecione  a data de expiração !"
            info.style = red
            return
        }
        if(sAssinante.estadoCivilDoAssinante.equals(null)){
            info.value = "Selecione  o estado civil !"
            info.style = red
            return
        }
        if(sAssinante.numeroDeIndentificaoDoAssinante.equals(null)){
            info.value = "Preencha o campo número de indentificação!"
            info.style = red
            return
        }
        if(sAssinante.residenciaDoAssinante.equals(null)){
            info.value = "Preencha o campo residência!"
            info.style = red
            return
        }
        if(selectedCliente.id.equals(null)){
            info.value = "Selecione um cliente!"
            info.style = red
            return
        }




     //   selectedCliente.entidade =""
        sAssinante.cliente = selectedCliente
      //  System.println(sAssinante.cliente)

       boolean ok =  assinanteService.saveAssinante(sAssinante)
        if(ok){
            info.value="Dados gravados com sucesso!"
            info.style = "color:blue;font-size:14pt"

            sAssinante = new Assinante()
            getAllAssinantes()
        }else {
            info.value =error_
            info.style = red
        }

    }

    @Command
    @NotifyChange(['selectedCliente','clientes','selectedGestor','clientes','todosClientes',"editCliente","sContaIntegradora","selectedGestor"])
    def addCliente(){
        win_cliente.detach()
        Executions.sendRedirect("/cliente/novoCliente")
    }

    @NotifyChange(['selectedCliente','clientes','selectedGestor','clientes','sAssinante',"editCliente","editCliente"])
    @Command editarCliente(){

    }
    @Command
    @NotifyChange(['selectedCliente','clientes','selectedGestor','clientes','sAssinante',"editCliente"])
    def fecharEditor(){
        hb_edit_cliente.visible = false
    }

    @NotifyChange(['selectedCliente','clientes','selectedGestor','clientes'])
    @Command
      showAlerta(){
     //   System.println(editCliente)
        info.value ="Se realmente pretende eliminar o item seleccionado, Faça Double Click!"
        info.style="color:red;font-size:14pt"
    }


    @NotifyChange(['selectedCliente','clientes','selectedGestor','clientes','todosClientes'])
    @Command
    def deleteCliente(){

        try {
            Utilizador user = springSecurityService.currentUser as Utilizador
            if (!user.authorities.any { it.authority == "CLIENTE_DELETE" }) {
                info.value="Este utilizador não tem permissão para executar esta acção !"
                info.style = "color:red;font-weight;font-size:14ptpt;background:back"
                return
            }
       //     System.println(editCliente)
            def creditos = Credito.findAllByCliente(editCliente)
            for(Credito credito in creditos){
                if(credito.invalido){
                    credito.delete(flush: true)
                }

            }
            def contaDb = Conta.findByCliente(editCliente)
            if(contaDb!=null){
                contaDb.cliente = null
                def result =  contaService.update(contaDb)
                if(result){
                    info.value = "a Conta do "+editCliente.nome+ " foi eliminado com sucesso!"
                    info.style = "color:blue;font-weight;font-size:14ptpt;background:back"
                }else {
                    info.value = "Erro na Eliminação da conta do  "+editCliente.nome
                    info.style = "color:blue;font-weight;font-size:14ptpt;background:back"
                }
            }
           def result = clienteService.deleteliente(editCliente)
           // editCliente.delete(flush: true)
            if(result){
                info.value="O Cliente foi eliminado com sucesso!"
                info.style = "color:red;font-weight;font-size:14ptpt;background:back"
                editCliente = null
            }else {
                info.value="Não foi possivel eliminar este cliente!"
                info.style = "color:red;font-weight;font-size:14ptpt;background:back"
            }

        }catch(Exception e){
      //      System.println(e.toString())
            info.value=e.toString()
            info.style = "color:red;font-weight;font-size:14ptpt;background:back"
        }
        Utilizador user = springSecurityService.currentUser as Utilizador

    }
    @NotifyChange(['selectedCliente','clientes','selectedGestor','clientes','todosClientes','allAssinantes','sAssinante'])
    def deleteAssinante(){
        clienteService.deleteAssinante(sAssinante)
    }
    @Command
    @NotifyChange(['selectedCliente','clientes','selectedGestor','clientes','todosClientes','sAssinante','allAssinantes'])
    def updateCliente(){
        if(selectedGestor!=null){
            editCliente.utilizador = selectedGestor
        }

      def ok =   clienteService.mergeCliente(editCliente)
        if(ok){
            info.style = "color:blue;font-size:14pt"
            info.value = "Dados gravados com sucesso!"
        }else {
            info.value = error_
        }
    }

    @Command
    def updateAssinante(){
        sAssinante.cliente = selectedCliente
      def ok =   clienteService.mergeAssinante(sAssinante)
        if(ok){
            info.style = "color:blue;font-size:14pt"
            info.value = "Dados gravados com sucesso!"
        }else {
            info.value = error_
        }
    }
    @Command
    @NotifyChange(['selectedCliente'])
    def changeAtivo(){
        if(selectedCliente.ativo){
            selectedCliente.ativo = false
        }else {
            selectedCliente.ativo = true
        }
    }

    @Command
    @NotifyChange(['editCliente'])
    def changeAtiv(){
        if(editCliente.ativo){
            editCliente.ativo = false
        }else {
            editCliente.ativo = true
        }
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
    Cliente getSelectedCliente() {
        return selectedCliente
    }

    Utilizador getSelectedGestor() {
        return selectedGestor
    }

    String getFilter() {
        return filter
    }


    void setFilter(String filter) {
        this.filter = filter
    }

    void setSelectedCliente(Cliente selectedCliente) {
        this.selectedCliente = selectedCliente
    }

    ListModelList getClientes() {
        if(clientes==null){
            clientes = new ListModelList<Cliente>()
        }

        if(selectedGestor){
            clientes = clienteService.getClientes(selectedGestor)
        }
       return clientes
    }

    ListModelList getGestores() {
        if (gestores==null){
            gestores = new ListModelList<Utilizador>()
        }
        gestores.clear()
        def utilizadores = utilizadorService.getAll()
        for(Utilizador u in utilizadores){
            if (u.authorities.any { it.authority == "CLIENTE_GESTOR" }) {
               gestores.add(u)
            }
        }
        return gestores
    }

    @Command
    @NotifyChange(['selectedCliente','clientes','selectedGestor','clientes','sCredito'])
    def showClientes(){
        hb_edit_cliente.visible=true
      }

    @Init init() {

        // initialzation code here
    }
    @Command
    @NotifyChange(["selectedCliente"])
    def cleanInfo(){
        info.value = ""
        info.style = blue
    }

    @Command
    @NotifyChange(['creditos','assinantes','extratoDeContas'])
    def showCreditos(){
        sCredito =null
        tab_assinante.label = "Assinantes do(a) "+selectedCliente.nome
        tab_pagamento.label = "Prestações do(a)"+selectedCliente.nome
        tab_credito.label = "Creditos do(a)"+selectedCliente.nome
         getCreditos()
        getAssinantes()
        getExtratoDeContas()

    }

    @Command
    @NotifyChange('creditos')
    def showAssinantes(){
        sAssinante =null
        getAssinantes()
    }


}
