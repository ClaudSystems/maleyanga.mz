package diario

import grails.plugin.springsecurity.SpringSecurityService
import mz.maleyanga.ComposerService
import mz.maleyanga.ContadorService
import mz.maleyanga.DiarioService
import mz.maleyanga.SessionStorageService
import mz.maleyanga.UtilizadorService
import mz.maleyanga.conta.Conta
import mz.maleyanga.diario.Diario
import mz.maleyanga.documento.Nota
import mz.maleyanga.pagamento.Parcela
import mz.maleyanga.ParcelaService
import mz.maleyanga.saidas.Saida
import mz.maleyanga.security.Utilizador
import mz.maleyanga.transacao.Transacao
import mz.maleyanga.transferencia.Transferencia
import org.springframework.stereotype.Service
import org.zkoss.bind.annotation.BindingParam
import org.zkoss.bind.annotation.Init
import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.NotifyChange
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zul.Button
import org.zkoss.zul.Label
import org.zkoss.zul.ListModelList
import java.math.RoundingMode
import java.sql.SQLDataException
import grails.transaction.Transactional


@Transactional
@Service
class DiarioViewModel {
    SessionStorageService sessionStorageService
    UtilizadorService utilizadorService
    String filter
    private Date dataDeReferencia     // data a que o diario se refere
    ParcelaService parcelaService
    SpringSecurityService springSecurityService
    String selectedUtilizador
    Parcela pickedParcela
    def pickedTransferencia
    ComposerService composerService
    DiarioService diarioService
    Diario selectedDiario
    @Wire Label info
    @Wire Button bt_fechar
    @Wire Button bt_abrir
    String blue="color:blue;font-size:14pt"
    String red = "color:red;font-size:14pt"
    ContadorService contadorService
    Utilizador utilizador
    private ListModelList<Diario> items
    private ListModelList<Parcela> parcelas = new ListModelList<Parcela>()
    private ListModelList<Parcela> parcels
    private ListModelList<Saida> saidas = new ListModelList<Saida>()
    private ListModelList<Saida> saids
    private Set<String> utilizadores
    BigDecimal totalParcelas
    BigDecimal totalParcels
    BigDecimal totalSaidas
    BigDecimal totalSaids
    BigDecimal saldo = 0.0
    BigDecimal sald = 0.0
    Saida saida
    Saida selectedSaida
    Conta selectedConta
    boolean viewSaida = false
    boolean viewParcela = false
    private String  notaParcela
    private String notaSaida
    private Nota selectedNota

    Nota getSelectedNota() {
        return selectedNota
    }

    void setSelectedNota(Nota selectedNota) {
        this.selectedNota = selectedNota
    }

    String getNotaParcela() {
        return notaParcela
    }

    void setNotaParcela(String notaParcela) {
        this.notaParcela = notaParcela
    }

    String getNotaSaida() {
        return notaSaida
    }

    void setNotaSaida(String notaSaida) {
        this.notaSaida = notaSaida
    }

    boolean getViewSaida() {
        return viewSaida
    }

    boolean getViewParcela() {
        return viewParcela
    }

    @Command
    @NotifyChange(["items"])
    def update(){
        if(selectedDiario.id==null){
            return
        }
        selectedDiario.dataDeReferencia = dataDeReferencia
        try {
            selectedDiario.dataDeReferencia = dataDeReferencia
            selectedDiario.merge()
            info.value = "O Diário "+selectedDiario.numeroDoDiario+" foi atualizado com sucesso!"
        }catch(Exception e){
            info.value = e.toString()
            info.style = "color:red;font-weight;font-size:16px;background:back"

        }
        selectedDiario.merge()
        info
    }
    @NotifyChange("dataDeReferencia")
    Date getDataDeReferencia() {
       /* if(selectedDiario?.dataDeReferencia!=null){
            dataDeReferencia = selectedDiario.dataDeReferencia
        }*/
        return dataDeReferencia
    }

    void setDataDeReferencia(Date dataDeReferencia) {
        this.dataDeReferencia = dataDeReferencia
    }

    Saida getSelectedSaida() {
        return selectedSaida
    }

    @NotifyChange(["viewSaida","viewParcela","selectedSaida"])
    void setSelectedSaida(Saida selectedSaida) {
        this.selectedSaida = selectedSaida
        viewSaida= true
        viewParcela = false

    }
    Conta contaCaixa =  Conta.findByUtilizadorAndFinalidade(utilizador,'conta_caixa')
    ListModelList<Conta> contas

    ListModelList<Saida> getSaids() {
        utilizador = Utilizador.findByUsername(selectedUtilizador)
        if(saids==null){
            saids = new ListModelList<Saida>()
        }
        saids.clear()
        saids = Saida.findAllByDiarioAndUtilizador(selectedDiario,utilizador)
        return saids

    }

    BigDecimal getTotalParcels() {
        totalParcels = 0.0
        for(Parcela parcela in parcels){
            totalParcels+=parcela.valorPago
        }
        return totalParcels
    }

    BigDecimal getTotalSaids() {
        totalSaids = 0.0
        for(Saida said in saids){
            totalSaids+=said.valor
        }
        return totalSaids
    }

    BigDecimal getSald() {
        getUtilizador()
        getParcels()
        getSaids()
        return getTotalParcels()-getTotalSaids()
    }

    @Command
    def alertDelete(){
        Utilizador user = springSecurityService.currentUser as Utilizador
        if (!user.authorities.any { it.authority == "PARCELA_DELETE" }) {
            info.value="Este utilizador não tem permissão para executar esta acção !"
            info.style = "color:red;font-weight;font-size:14pt;background:back"
        }
        if(selectedSaida.invalido){
            info.value="Este recibo já foi invalidado!"
            info.style = "color:red;font-weight;font-size:14pt;background:back"

        }

        else {
            info.value="Double Click para eliminar este credito!"
            info.style = "color:red;font-weight;font-size:14pt;background:back"
        }
    }

    @Command
    @NotifyChange(["selectedSaida","saidas"])
    def eliminarSaida(){
        try {
            Utilizador user = springSecurityService.currentUser as Utilizador
            if (!user.authorities.any { it.authority == "SAIDA_DELETE" }) {
                info.value = "Este utilizador não tem permissão para executar esta acção !"
                info.style = "color:red;font-weight;font-size:14ptpt;background:back"
                return
            }

            Transacao tDebito = new Transacao()
            Transacao tCredito = new Transacao()
            tCredito.valor = selectedSaida.valor
            tCredito.descricao="Estorno"
            tDebito.descricao = "Estorno"
            tDebito.valor = selectedSaida.valor
            tCredito.credito = true
            tDebito.credito = false

            def credora = Conta.findById(selectedSaida.destino.id)
            def devedora = Conta.findById(selectedSaida.origem.id)
            if (credora.transacoes == null) {
                credora.transacoes = new LinkedHashSet<Transacao>()
            }
            if (devedora.transacoes == null) {
                devedora.transacoes = new LinkedHashSet<Transacao>()
            }

            tDebito.save(flush: true)
            tCredito.save(flush: true)
            credora.transacoes.add(tCredito)
            credora.merge()

            devedora.transacoes.add(tDebito)
            devedora.merge()
            selectedSaida.valorBackup = selectedSaida.valor
            selectedSaida.invalido = true
            selectedSaida.valor = 0.0
            selectedSaida.merge(flush: true)
            info.value = "gravação feita com sucesso!"
            info.style = "color:red;font-weight;font-size:14ptpt;background:back"

            info.value = "Operações feitas com sucesso!"
            info.style = "color:red;font-weight;font-size:14ptpt;background:back"
            parcelaService.saidaInstance = saida
            saidas.remove(saida)
        }catch(Exception e){
            System.println(e.toString())
        }


    }

    @Command
    @NotifyChange(["selectedSaida"])
    def addNotaSaida(){
        Utilizador user = springSecurityService.currentUser as Utilizador
        if (!user.authorities.any { it.authority == "NOTA_CREATE" }) {
            info.value="Este utilizador não tem permissão para executar esta acção !"
            info.style = "color:red;font-weight;font-size:16px;background:back"
            return
        }
        Nota nota = new Nota()
        nota.autor = user
        nota.messagem = notaSaida
        nota.referencia = selectedSaida.numeroDaSaida

        try {
            nota.save(flush: true)
            def notaDB = Nota.findById(nota.id)
            if(notaDB){

                if(selectedSaida.notas==null){
                    selectedSaida.notas= new ArrayList<Nota>()
                }
                selectedSaida.notas.add(notaDB)
                selectedSaida.merge(flush: true)
                info.value= "A nota foi gravada com sucesso!"
                info.style = "color:blue;font-weight;font-size:16px;background:back"

            }
        }catch(Exception e){
            info.value = e.toString()
            info.style = "color:red;font-weight;font-size:16px;background:back"

        }

    }

    @Command
    @NotifyChange(["pickedParcela"])
    def addNotaParcela(){
        Utilizador user = springSecurityService.currentUser as Utilizador
        if (!user.authorities.any { it.authority == "REMISSAO_CREATE" }) {
            info.value="Este utilizador não tem permissão para executar esta acção !"
            info.style = "color:red;font-weight;font-size:16px;background:back"
            return
        }
        Nota nota = new Nota()
        nota.autor = user
        nota.messagem = notaParcela
        nota.referencia = pickedParcela.numeroDoRecibo

        try {
            nota.save(flush: true)
            def notaDB = Nota.findById(nota.id)
            if(notaDB){

                if(pickedParcela.notas==null){
                    pickedParcela.notas= new ArrayList<Nota>()
                }
                pickedParcela.notas.add(notaDB)
                pickedParcela.merge(flush: true)
                info.value= "A nota foi gravada com sucesso!"
                info.style = "color:blue;font-weight;font-size:16px;background:back"

            }
        }catch(Exception e){
            info.value = e.toString()
            info.style = "color:red;font-weight;font-size:16px;background:back"

        }

    }

    @Command
    @NotifyChange(["saida","selectedDiario","viewParcela","viewSaida"])
    def fecharEditor(){
        viewSaida=false
        viewParcela = false
    }
    ListModelList<Conta> getContas() {
        if(contas ==null){
            contas = new ListModelList<Conta>(Conta.findAllByFinalidadeOrFinalidade("conta_movimento","conta_capital"))
        }
        return contas
    }

    Conta getSelectedConta() {
        return selectedConta
    }

    void setSelectedConta(Conta selectedConta) {
        this.selectedConta = selectedConta
    }

    Saida getSaida() {
        return saida
    }

    void setSaida(Saida saida) {
        this.saida = saida
    }

    @Command
    @NotifyChange(["filterDestinos","contadestinos"])

    BigDecimal getSaldo() {
        return getTotalParcelas()-getTotalSaidas()
    }
    BigDecimal getTotalParcelas() {
        totalParcelas = 0.0
        def parcelas = Parcela.findAllByDiario(selectedDiario)
        for(Parcela parcela in parcelas){
            totalParcelas +=parcela.valorPago
        }
        return totalParcelas
    }
    BigDecimal getTotalSaidas() {
        totalSaidas = 0.0
        def saidas = Saida.findAllByDiario(selectedDiario)
        for(Saida saida in saidas){
            totalSaidas +=saida.valor
        }
        return totalSaidas
    }
    String  getSelectedUtilizador() {
        return selectedUtilizador
    }

    @NotifyChange(["parcels","saids","sald","saida","selectedUtilizador","utilizador"])
    void setSelectedUtilizador(String selectedUtilizador) {
        this.selectedUtilizador = selectedUtilizador
        sessionStorageService.utilizador = selectedUtilizador
        saida = null
    }
    private boolean lb_items = true
    private boolean bt_remover = false

    ListModelList<Parcela> getParcelas() {
        if(selectedDiario){
            parcelas = Parcela.findAllByDiario(selectedDiario)
        }
        parcelas.sort{it.id}
        return parcelas
    }
    List<Parcela> getParcels() {
        utilizador = Utilizador.findByUsername(selectedUtilizador)
        if(parcels==null){
            parcels = new ListModelList<Parcela>()
        }
        parcels.clear()
        parcels = Parcela.findAllByValorPagoGreaterThanAndDiarioAndUtilizador(0.0,selectedDiario,utilizador)
        parcels.sort{it.id}
        return parcels
    }
    ListModelList<Saida> getSaidas() {
        if(selectedDiario){
            saidas = Saida.findAllByDiarioAndValorGreaterThan(selectedDiario,0.0)
        }
        saidas.sort{it.id}
        return saidas
    }

    Utilizador getUtilizador() {
        utilizador = Utilizador.findByUsername(selectedUtilizador)
        return utilizador
    }

    ListModelList<String> getUtilizadores() {

        if(utilizadores == null){
            utilizadores = new HashSet<String>()
        }
        utilizadores.clear()

        for(Utilizador user in utilizadorService.all){
            utilizadores.add(user.username)
        }
        return utilizadores
    }

    Parcela getPickedParcela() {
        return pickedParcela
    }

    @NotifyChange(["viewParcela","viewSaida","pickedParcela"])
    void setPickedParcela(Parcela pickedParcela) {
        this.pickedParcela = pickedParcela
        viewParcela = true
        viewSaida = false
    }

    Diario getSelectedDiario() {
        return selectedDiario
    }

    @NotifyChange(["utilizadores","dataDeReferencia"])
    void setSelectedDiario(Diario selectedDiario) {
        sessionStorageService.diario = selectedDiario
        this.selectedDiario = selectedDiario
        gerarNumeros()

    }

    @NotifyChange (["saidas"])
    def gerarNumeros(){
        for (Saida saida1 in getSaidas()){
            if(saida1.numeroDaSaida==null){
             saida1.numeroDaSaida=  contadorService.gerarNumeroDaSaida(saida1)
                saida1.merge(flush: true)
            }
        }
    }

    Transferencia getPickedTransferencia() {
        return pickedTransferencia
    }

    void setPickedTransferencia(pickedTransferencia) {
        this.pickedTransferencia = pickedTransferencia
    }

    String getFilter() {
        return filter
    }

    void setFilter(String filter) {
        this.filter = filter
    }

    @Command
    def imprimirDiario(){
        // info.value = "Click o botão direito sobre imprimir para impressão do diário detalhado!"
        // composerService.diario = selectedDiario
        sessionStorageService.diario = selectedDiario
        Executions.sendRedirect("/diario/printDiario")
    }
    @Command
    def imprimirDiarioPorUtilizador(){
        composerService.diario = selectedDiario
        if(selectedUtilizador==null){
            info.value = "Selecione um Utilizador!"
            info.style = "color:red;font-weight;font-size:14ptpt;background:back"
            return

        }
        composerService.utilizador = selectedUtilizador
        Executions.sendRedirect("/diario/printDiarioPorUtilizador/")
    }

    boolean getBt_abrir() {
        return bt_abrir
    }

    boolean getBt_remover() {
        return bt_remover
    }

    boolean getLb_items() {
        return lb_items
    }

    ListModelList<Diario> getItems() {
        if(items==null){
            items = new ListModelList<Diario>( Diario.findAllByEstadoOrEstado("pendente","aberto"))
        }
        return items
    }
    ListModelList<Diario> getAll() {

        if(items==null){
            items = new ListModelList<Diario>()
        }
        items.clear()
        items = Diario.all
        return items
    }

    @NotifyChange(['contaCaixa','caixas'])
    Conta getContaCaixa() {
        return  contaCaixa
    }

    @NotifyChange(["items","selectedDiario","filter"])
    @Command
    void doSearch() {
        info.value = ""
        items.clear()
        List<Diario> allItems = Diario.all
        if (filter == null || "".equals(filter)) {
            items.addAll(allItems)
        } else {
            for (Diario c : allItems) {
                if (c.numeroDoDiario.toLowerCase().indexOf(filter.toLowerCase()) >= 0 ||
                        c.dateCreated.toString().indexOf(filter.toLowerCase()) >= 0 ||
                        c.estado.toString().indexOf(filter.toLowerCase()) >= 0 ||
                        c.dateClosed.toString()?.toLowerCase()?.indexOf(filter.toLowerCase()) >= 0
                ) {
                    items.add(c)
                }
            }
        }
    }
    @Command
    @NotifyChange(["selectedDiario","items"])
    def addItem(){

        try {
            for(Diario d in items){
                if(d.dataDeReferencia.format("dd/MM/yyyy")==dataDeReferencia.format("dd/MM/yyyy")){
                    info.value = "Já existe um diário com esta data de referencia!"
                    info.style =red
                    return
                }
            }
            Date hoje = new Date()
            if(dataDeReferencia.after(hoje)){
                info.value = "Não se pode referenciar uma data futura!"
                info.style =red
                return
            }
            def diarios = Diario.findAllByEstado("aberto")
            if(diarios.size()>=1){
                info.value = "Por favor Feiche todos os diários antes de criar um novo!"
                info.style =red
                return
            }
            if(diarios.size()==1){
                Utilizador user = springSecurityService.currentUser as Utilizador
                if (!user.authorities.any { it.authority == "DIARIO_CREATE" }) {
                    info.value="Este utilizador não tem permissão para executar esta acção !"
                    info.style = "color:red;font-weight;font-size:14ptpt;background:back"
                    return
                }

            }
            def numero =contadorService.gerarNumeroDoDiario()
            Diario diario = new Diario(estado: "aberto",numeroDoDiario:numero,dataDeReferencia: dataDeReferencia).save(flush: true)
            selectedDiario = Diario?.findById(diario?.id)
            if(selectedDiario!=null){
                info.value = "O diário com o Nº " +selectedDiario.numeroDoDiario+" foi criado com sucesso!"
                info.style = red
                items.add(selectedDiario)
            }else {
                info.value = "Erro na criação do diário!"
                info.style = red
            }
            getItems()
        }catch(Exception e){
            info.value = "Erro na criação do diário!"
            System.println(e.toString())
            info.style = red
        }

    }

    @Command
    @NotifyChange(["items","selectedDiario","saida","saids","parcels","utilizador","selectedUtilizador","dataDeReferencia"])
    def fecharDiario(){

        try {
            Utilizador user = springSecurityService.currentUser as Utilizador
            if (!user.authorities.any { it.authority == "DIARIO_FECHAR" }) {
                info.value = "Este utilizador não tem permissão para executar esta acção !"
                info.style = "color:red;font-weight;font-size:14ptpt;background:back"
                return
            }

            saldo =getSaldo().setScale(2,RoundingMode.DOWN)
            if(saldo!=0.0) {
                info.value = "Regularize o saldo do Diário antes de fechar!"+" Saldo = "+saldo
                info.style = "color:red;font-weight;font-size:14ptpt;background:back"
                if(selectedUtilizador==null){
                    info.value+=". Selecione um utilizador com saldo em caixa!"

                }else {

                    System.println("utilizador "+utilizador)
                    getParcels()
                    getSaids()
                    System.println(getParcels())
                    System.println(getSaids())
                    System.println("getTotalParcels"+getTotalParcels())
                    System.println("getTotalSaids"+getTotalSaids())
                    if(getSald()>0){
                        saida = new Saida()
                        saida.valor = getSald()
                        saida.dataDePagamento = new Date()
                        saida.utilizador = utilizador
                        saida.descricao="Alocação do saldo diario Nº"+selectedDiario.numeroDoDiario
                        saida.formaDePagamento ="numerário"
                    }


                }
                return
            }
            info.value = ""
            selectedDiario.estado = "fechado"
            selectedDiario.dateClosed = new Date()
            selectedDiario.merge(flush: true)
            info.value = "Diário Nº."+selectedDiario.numeroDoDiario+"Fechada com sucesso!"
            info.style = blue
            bt_abrir.visible=true
            bt_fechar.visible=false
            bt_remover = true
            checkEstado()

        }catch (SQLDataException e){
            info.value = e.toString()
            info.style = red
        }

    }

    @Init init() {
        utilizador = Utilizador.findById(springSecurityService.principal?.id)
    }
    @Command
    void showIt(@BindingParam("index") Long index) {

    }

    @NotifyChange(["bt_remover",'items','selectedDiario',"parcelas","saidas","dataDeReferencia"])
    @Command
    def checkEstado(){
        info.value = ""
        if(selectedDiario.estado=="pendente"||selectedDiario.estado=="aberto"){
            bt_abrir.label=""
        }
        if(selectedDiario.estado =="fechado"){
            bt_remover = false
            bt_abrir.label ="Abrir Diário "+selectedDiario.numeroDoDiario+"."
        }else {
            bt_remover = true
            bt_fechar.label = "Fechar o diário "+selectedDiario.numeroDoDiario+"."

        }
        dataDeReferencia=selectedDiario.dataDeReferencia
    }
    @Command
    @NotifyChange(["items","selectedDiario","selectedUtilizador","parcels","saids"])
    def abrirDiario(){
        Utilizador user = springSecurityService.currentUser as Utilizador
        if (!user.authorities.any { it.authority == "DIARIO_ABRIR" }) {
            info.value = "Este utilizador não tem permissão para executar esta acção !"
            info.style = "color:red;font-weight;font-size:14ptpt;background:back"
            return
        }

        getAll()
        info.value = ""
        if(selectedDiario?.estado =="fechado"){
            try {
                if(Diario.findByEstado("pendente")){
                    info.value ="Existe um diário pendente!"
                    info.style = red
                    return
                }
                selectedDiario.estado = "pendente"

                selectedDiario.merge()
                info.value = "O Diário "+selectedDiario.numeroDoDiario+" foi reaberto!"
                info.style = blue
            }catch ( SQLDataException e){
                info.value = e.toString()
                info.style = red
            }

        }else {
            bt_remover = true
            info.value = ""
            info.style = red
        }
    }

    @NotifyChange(["diarios"])
    @Command
    def salvarSaida(){
        try {
            Utilizador user = springSecurityService.currentUser as Utilizador
            if (!user.authorities.any { it.authority == "SAIDA_SAVE" }) {
                info.value = "Este utilizador não tem permissão para executar esta acção !"
                info.style = "color:red;font-weight;font-size:14ptpt;background:back"
                return
            }
            if(selectedUtilizador==null){
                info.value ="Selecione um utilizador!"
                info.style = red
                return
            }
            contaCaixa = Conta.findByUtilizadorAndFinalidade(Utilizador.findByUsername(selectedUtilizador),"conta_caixa")
            System.println(utilizador.username)
            if(Saida.findById(saida.id)){
                info.value = "Este Pagamento já foi lançado!"
                info.style = "color:red;font-weight;font-size:14ptpt;background:back"
                return
            }
            if(0>=saida.valor){
                info.value = "Valor inválido!"
                info.style = "color:red;font-weight;font-size:14ptpt;background:back"
                return
            }
            if(saida.dataDePagamento==null){
                info.value = "Data inválido!"
                info.style = "color:red;font-weight;font-size:14ptpt;background:back"
                return
            }
            if(saida.formaDePagamento==null){
                info.value = "Forma de Pagamento não foi selecionado!"
                info.style = "color:red;font-weight;font-size:14ptpt;background:back"
                return
            }

            info.value = ""

            if(contaCaixa==null){
                info.value="O Utilizador "+selectedUtilizador+" não tem nehuma conta associada de forma poder lançar pagamentos!"
                info.style ="color:red;font-weight;font-size:14ptpt;background:back"

                return
            }


            if (saida.hasErrors()) {
                return
            }
            saida.contaOrigem=contaCaixa.codigo
            saida.contaDestino=selectedConta.codigo
            saida.origem = contaCaixa
            saida.destino = selectedConta
            saida.diario = selectedDiario
            saida.save(flush: true)
            info.value = "gravação feita com sucesso!"
            info.style = "color:red;font-weight;font-size:14ptpt;background:back"

            Transacao tCredito = new Transacao()
            Transacao tDebito = new Transacao()
            tCredito.valor = saida.valor
            tCredito.descricao=saida.descricao+"-"+saida.formaDePagamento
            tDebito.descricao = saida.descricao+"-"+saida.formaDePagamento
            tDebito.valor = saida.valor
            tCredito.credito = true
            tDebito.credito = false
            def credora = Conta.findById(contaCaixa.id)
            def devedora = Conta.findById(selectedConta.id)
            if (credora.transacoes == null) {
                credora.transacoes = new LinkedHashSet<Transacao>()
            }
            if (devedora.transacoes == null) {
                devedora.transacoes = new LinkedHashSet<Transacao>()
            }
            tCredito.save(flush: true)
            tDebito.save(flush: true)
            credora.transacoes.add(tCredito)
            devedora.transacoes.add(tDebito)
            credora.merge(flush: true)
            devedora.merge(flush: true)


            info.value = "Operações feitas com sucesso!"
            info.style = "color:red;font-weight;font-size:14ptpt;background:back"
            parcelaService.saidaInstance = saida
        }catch(Exception e){
            System.println(e.toString())
        }


    }
}
