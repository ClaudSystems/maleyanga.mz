package mz.maleyanga.relatorio


import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional
import mz.maleyanga.BasicController
import mz.maleyanga.cliente.Cliente
import mz.maleyanga.credito.Credito
import mz.maleyanga.entidade.Entidade
import mz.maleyanga.pagamento.Pagamento
import mz.maleyanga.security.Utilizador
import org.springframework.security.access.annotation.Secured


import static org.springframework.http.HttpStatus.*

/**
 * RelatoriosController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
@Transactional(readOnly = true)
@Secured(['ROLE_ADMIN'])
class RelatoriosController extends BasicController {
    def pagamentoService
    def sessionStorageService
    def springSecurityService
    def relatoriosService

    @Secured(['ROLE_ADMIN', 'RELATORIOS_INDEX'])
    def index() {}

    @Secured(['ROLE_ADMIN', 'RELATORIOS_INDEX'])
    def printClientes() {}

    @Secured(['ROLE_ADMIN', 'RELATORIOS_INDEX'])
    def extratosDeCreditos() {

    }

    @Secured(['ROLE_ADMIN', 'RELATORIOS_RECEITAS'])
    def receitas() {}

    @Secured(['ROLE_ADMIN', 'RELATORIOS_RECEITAS'])
    def printDiariosDoCaixa() {
        def caixa = sessionStorageService.getSelectedCaixa() as Utilizador
        [dataInicial: relatoriosService.dataInicial, dataFinal: relatoriosService.dataFinal, username: caixa.username]
    }

    @Secured(['ROLE_ADMIN', 'RELATORIOS_LOGS'])
    def logs() {

    }

    def printRelDeClientesDoGestor() {
        Utilizador utilizador = sessionStorageService.selectedGestor as Utilizador
        [nome: utilizador.username]
    }


    @Secured(['ROLE_ADMIN', 'RELATORIOS_ACTION_LOGGING'])
    def actionLogging() {}

    @Secured(['ROLE_ADMIN', 'RELATORIOS_CREDITOS'])
    def creditos() {}

    @Secured(['ROLE_ADMIN', 'RELATORIOS_EXTRATO_DE_CREDITO'])
    def diarios() {

    }

    @Secured(['ROLE_ADMIN', 'RELATORIOS_EXTRATO_DE_CREDITO'])
    def clientes() {

    }

    @Secured(['ROLE_ADMIN', 'RELATORIOS_EXTRATO_PRESTACOES_EM_DIVIDA'])
    def extratoPrestacoesEmDivida() {
        String[] extensoes = ["pdf", "doc"]
        [extensoes: extensoes]
    }

    @Secured(['ROLE_ADMIN', 'RELATORIOS_IMPRIMIR_RECEITAS'])
    def imprimirReceitas() {
        System.println(params.inicio_value)
        System.println(params.fim_value)
        String reportName = ''
        reportName = "/receitas"
        redirect(action: printReport, params: [reportExt:params.ext,reportName:reportName,data_inicio:params.inicio_value,data_fim:params.fim_value])
    }

    @Secured(['ROLE_ADMIN','RELATORIOS_IMPRIMIR_BALANCO'])
    def imprimirBalanco() {

        String reportName = ''
        reportName = "/balanco_de_recebimentos"
        redirect(action: printReport, params: [reportExt: params.ext, reportName: reportName, data_inicio: params.iniciob_value, data_fim: params.fimb_value])
    }

    @Secured(['ROLE_ADMIN','RELATORIOS_IMPRIMIR_BALANCO_GERAL'])
    def imprimirBalancoGeral() {

        String reportName = ''
        reportName = "/balanco_geral_de_recebimentos"
        redirect(action: printReport, params: [reportExt: params.ext, reportName: reportName, data_inicio: params.inicioc_value, data_fim: params.fimc_value])
    }

    @Secured(['ROLE_ADMIN','RELATORIOS_IMPRIMIR_BALANCO_GERAL_DE_RECEBIMENTOS_DE_CREDITOS_FECHADOS'])
    def imprimirBalancoGeralDeRecebimentosDeCreditosFechados() {

        def rela = params.creditos_fechados
        String reportName = ''

        reportName = "/balanco_geral_de_recebimentos_fechados"


        redirect(action: printReport, params: [reportExt: params.ext, reportName: reportName, data_inicio: params.iniciox_value, data_fim: params.fimx_value])
    }

    @Secured(['ROLE_ADMIN','RELATORIOS_IMPRIMIR_ACTION_LOGGING'])
    def imprimirActionLogging(){

        String reportName = ''
        reportName = "/actionLoggingReport"
        redirect(action: printReport, params: [reportExt:params.ext,reportName:reportName,data_inicio:params.inicio_value,data_fim:params.fim_value])
    }


    @Secured(['ROLE_ADMIN','RELATORIOS_IMPRIMIR_CREDITOS'])
    def imprimirCreditos(){
        def rela = params.creditos_fechados
        System.println('imprimirCreditos.params.creditos_fechados' + params.creditos_fechados)
        if (rela == "on") {
            String reportName = ''
            reportName = "/creditos_incuindo_fechados"
            redirect(action: printReport, params:
                    [reportExt: params.ext, reportName: reportName, data_inicio: params.inicio_value, data_fim: params.fim_value])
        } else {
            String reportName = ''
            reportName = "/creditos"
            redirect(action: printReport, params:
                    [reportExt: params.ext, reportName: reportName, data_inicio: params.inicio_value, data_fim: params.fim_value])
        }

    }

    @Secured(['ROLE_ADMIN','RELATORIOS_IMPRIMIR_EXTRATO_DE_CREDITO'])
    def imprimirExtratoDeCredito(){
        String reportName = ''
        reportName = "/creditoReport"
        // def utilizador = springSecurityService.principal?.id
        redirect(action: printReport, params: [reportName:reportName,id:params.id,reportExt:params.ext])
    }

    @Secured(['ROLE_ADMIN','RELATORIOS_IMPRIMIR_COMTRATO'])
    def imprimirContrato() {
        Credito credito = Credito.findById(params.id)

        String contrato = "Entre:\n" + credito.cliente.entidade.nome +
                ", representado pelo seu Director Geral " + credito.cliente.entidade.proprietario + ", com domicilio em Maputo.\n" +
                "E,\n" + credito.cliente.nome +
                ", identificado pelo " + credito.cliente.tipoDeIndentificacao + " , No. " + credito.cliente.numeroDeIndentificao + ", com validade at?? " + credito.cliente.dataDeExpiracao.format("dd/MM/yyyy") + ", residente no " + credito.cliente.residencia + ". ?? celebrado um contrato de credito cujo teor ?? reciprocamente aceite pelas partes nos termos e condi????es contantes a seguir:\n" +
                "\n" +
                "1.\tMONTANTE\n" +
                "A " + credito.cliente.entidade.nome + " concede a V.Excia um empr??stimo no valor de " + credito.valorCreditado + "MT, quantia a qual V.Excia se confessa devedor(a) da mesma.\n" +
                "\n" +
                "2.\tPrazo\n" +
                "O presente contrato ?? concedido por um per??odo de " + credito.numeroDePrestacoes * 30 + " dias .\n" +
                "3.\tPLANO DE PAGAMENTO\n" +
                "i)\tO Mutu??rio pagar?? as presta????es at?? a data prevista de pagamento conforme o plano de pagamento anexo a este contrato.\n" +
                "ii)\tO respectivo plano de amortiza????o pode ser alterado em fun????o da disposi????o financeira do mutu??rio, desde que compra com as datas previstas de pagamento. \n" +
                "iii)\tO reembolso do referido valor ser?? feito ?? boca do caixa da empresa ou deposito banc??rio numa das contas indiadas pela nossa institui????o.\n" +
                "\n" +
                "4.\tDECLARA????O DE GRARANTIAS\n" +
                "i)\tPara assegurar o reembolso do capital, juros e despesas que a mutu??ria tenha que fazer em rela????o ao empr??stimo, o mutu??rio deposita como garantia o descrito abaixo:\n" + params.descricao_da_penhora +
                "\n" +
                "ii)\tPelo n??o comprimento das suas obriga????es, a institui????o reserva-se o direito de se fazer pagar pelas garantias assumidas. Devendo o mutu??rio entregar voluntariamente ?? institui????o os bens de penhora acima referidos, assim que for notificado, na primeira inst??ncia.\n" +
                "iii)\tO n??o comprimento do pagamento da data prevista, incorre a penaliza????o de 2% referente a juros de Mora por cada dia em atraso.\n" +
                "iv)\t?? da responsabilidade do mutu??rio entregar o comprovante do deposito ou transfer??ncia dentro do prazo estipulado no contrato, sob pela de capitaliza????o.\n" +
                "v)\tEm caso de incumprimentos ser??o acrecidos 15% referentes a cobran??a coerviva. \n" +
                "\n" +
                "5.\tPROIBI????ES\n" +
                "i)\tN??o ?? reconhecido a conbra??a ou entrga de valores, ou cart??es de controlo fora dos nossos balc??es de atendimento.\n" +
                "\n" +
                "6.\tFORO\n" +
                "(1)\tConvenciona-se que no caso de litigio, o foro ser?? um dos Tribunais de Maputo.\n" +
                "(2)\tAs d??vidas e omiss??es ser??o resolvidos, em primeira instancia amigavelmente o familiarmente. Na falta de consenso, por recurso a Lei comercial vigente e demais legisla????o aplic??vel. Para acc??oes emergentes deste contrato s??o exclusivamente competentes os Tribunais da cidade de Maputo.\n" +
                ""



        String reportName = ''
        reportName = "/contrato"
        // def utilizador = springSecurityService.principal?.id
        redirect(action: printReport, params: [reportName: reportName, credito_id: params.id, reportExt: params.ext, contrato: contrato])
    }

    @Secured(['ROLE_ADMIN','RELATORIOS_IMPRIMIR_PRESTACAO'])
    def  imprimirPrestacao(){
        // def p = session.getAttribute("pagamento") as Pagamento
        def p = pagamentoService.pagamentoInstance
        String reportName = ''
        reportName = "/prestacaoReport"
        //def id_utilizador = springSecurityService.principal?.id

        redirect(action: printReport, params: [reportName: reportName, id: params.id, id_credito: p.credito.id, reportExt: params.ext])
    }


    def prestacoes() {

    }

    def printPrestacoes() {}

    def printCreditos() {}

    def printPrestacoesDoGestor() {}

    def printCreditosDoGestor() {}

    def printExtratoDePrestacoes() {
        [dataInicial: relatoriosService.dataInicial, dataFinal: relatoriosService.dataFinal]
    }

    def printExtratoDosCreditos() {
        [dataInicial: relatoriosService.dataInicial, dataFinal: relatoriosService.dataFinal]
    }

    def printExtratoDePrestacoesDoGestor() {
        [dataInicial: relatoriosService.dataInicial, dataFinal: relatoriosService.dataFinal, selectedGestor: relatoriosService.selectedGestor]
    }

    def printExtratoDePrestacoesDoCliente() {
        [dataInicial: relatoriosService.dataInicial, dataFinal: relatoriosService.dataFinal, selectedGestor: relatoriosService.selectedGestor]
    }

    def printExtratoDePrestacoesDoClienteDetalhado() {
        def cliente = sessionStorageService.getCliente() as Cliente
        [nome: cliente.nome]
    }

    def printExtratoDosCreditosDoGestor() {
        [dataInicial: relatoriosService.dataInicial, dataFinal: relatoriosService.dataFinal, selectedGestor: relatoriosService.selectedGestor]
    }

    def printExtratoDosCreditosDoCliente() {
        [dataInicial: relatoriosService.dataInicial, dataFinal: relatoriosService.dataFinal, selectedGestor: relatoriosService.selectedGestor]
    }

    @Secured(['ROLE_ADMIN', 'RELATORIOS_IMPRIMIR_PAGAMENTOS_POR_RECEBER'])
    def imprimirPagamentosPorReceber() {

        String reportName = ''
        reportName = "/pagamentos_em_divida"
        redirect(action: printReport, params:
                [reportExt: params.ext, reportName: reportName, data_inicio: params.inicio_value, data_fim: params.fim_value])
    }


    @Secured(['ROLE_ADMIN','RELATORIOS_LOGS_REPORT'])
    def logsReport(){

    }

    @Secured(['ROLE_ADMIN','RELATORIOS_ACTION_LOG_REPORT'])
    def actionLogReport(){

        String reportName = ''
        reportName = "/actionLoggingReport"
        redirect(action: printReport, params:
                [reportExt:params.ext,reportName:reportName,data_inicio:params.inicio_value,data_fim:params.fim_value])
    }

    def pedidosDeCredito(){

    }

    @Secured(['ROLE_ADMIN','RELATORIOS_IMPRIMIR_EXTRATO_DE_PAGAMENTO'])
    def imprimirExtratoDePagamento(){
        def id_utilizador = springSecurityService.principal?.id
        String reportName = ''
        reportName = "/recibo"
        redirect(action: printReport, params: [reportName:reportName,id:params.id,reportExt:params.ext,id_utilizador:id_utilizador])
    }

    @Secured(['ROLE_ADMIN','RELATORIOS_IMPRIMIR_EXTRATO_DE_CREDITO'])
    def imprimirExtratoDeRemissao(){
        def id_utilizador = springSecurityService.principal?.id
        String reportName = ''
        reportName = "/remissao"
        redirect(action: printReport, params: [reportName:reportName,id:params.id,reportExt:params.ext,id_utilizador:id_utilizador])
    }

    @Secured(['ROLE_ADMIN','RELATORIOS_IMPRIMIR_EXTRATO_DO_PEDIDO_DE_CREDITO'])
    def imprimirExtratoDoPedidoDeCredito(){
        // def id_utilizador = springSecurityService.principal?.id
        String reportName = ''
        reportName = "/pedidoDeCredito"
        redirect(action: printReport, params: [reportName:reportName,id:params.id,reportExt:params.ext])

    }


    @Secured(['ROLE_ADMIN','RELATORIOS_IMPRIMIR_EXTRATO_DE_CLIENTE'])
    def imprimirExtratoDeCliente(){
        String reportName = ''
        reportName = "/extratoDoClinte"
        redirect(action: printReport, params: [reportName:reportName,id:params.id,reportExt:params.ext])
    }


    def imprimirPedidosDeCredito(){

    }

    @Secured(['ROLE_ADMIN','RELATORIOS_IMPRIMIR_PRESTACOES_ATRASADAS'])
    def imprimirPrestacoesAtrasadas() {
        String reportName = ''
        reportName = "/prestacoes_atrasados"
        redirect(action: printReport, params: [reportName: reportName, reportExt: params.ext])
    }

    @Secured(['ROLE_ADMIN','RELATORIOS_IMPRIMIR_PRESTACOES_DO_DIA'])
    def imprimirPrestacoesDoDia() {
        String reportName = ''
        reportName = "/prestacoes_do_dia"
        def data = params.data_unica_value
        System.println(params.data_unica_value)
        redirect(action: printReport, params: [reportName: reportName, reportExt: params.ext, data: params.data_unica_value])
    }


}
