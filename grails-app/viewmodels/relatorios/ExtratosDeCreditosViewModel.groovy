package relatorios

import mz.maleyanga.ExtratosDeCredito
import mz.maleyanga.SessionStorageService
import mz.maleyanga.cliente.Cliente
import mz.maleyanga.credito.Credito
import mz.maleyanga.pagamento.Pagamento
import mz.maleyanga.pagamento.Parcela
import mz.maleyanga.pagamento.Remissao
import org.zkoss.bind.annotation.Init
import org.zkoss.zul.ListModelList

import java.math.RoundingMode

class ExtratosDeCreditosViewModel {

SessionStorageService sessionStorageService
    List<Credito> creditos
    private Integer numeroDePrestacoesEmDia = 0
    private BigDecimal valorDaPrestacao = 0
    private BigDecimal totalJurosDeMora = 0
    private BigDecimal totalCredito = 0
    private BigDecimal valorEmMora = 0
    private BigDecimal totalDesembolsado
    BigDecimal total_juros_de_mora = 0
    BigDecimal totalPago = 0
    BigDecimal valor_em_mora = 0
    BigDecimal valor_em_ivida = 0
    BigDecimal total_creditado = 0

    BigDecimal getTotalPago() {
        return totalPago
    }

    BigDecimal getTotal_juros_de_mora() {
        return total_juros_de_mora
    }

    BigDecimal getTotalDesembolsado() {
        return totalDesembolsado
    }

    BigDecimal getValor_em_mora() {

        return valor_em_mora
    }

    BigDecimal getValor_em_ivida() {
        return valor_em_ivida
    }
    List<ExtratosDeCredito> extratosDeCreditos


    List<ExtratosDeCredito> getExtratosDeCreditos() {
       total_juros_de_mora = 0
       totalPago = 0
        valor_em_mora = 0
        valor_em_ivida = 0
        totalDesembolsado = 0.0

        if(extratosDeCreditos==null){
            extratosDeCreditos = new ListModelList<ExtratosDeCredito>()
        }

        for(Credito creditoInstance in creditos)
        {
            ExtratosDeCredito extratosDeCredito = new ExtratosDeCredito()
            Cliente cliente = Cliente.findById(creditoInstance?.clienteId)
             extratosDeCredito?.numeroDoCredito = creditoInstance?.numeroDoCredito
             extratosDeCredito?.dateConcecao = creditoInstance?.dateConcecao
             extratosDeCredito?.nome = cliente.codigo
             extratosDeCredito?.gestor = cliente.getUtilizador()
             extratosDeCredito?.numeroDePrestacoesEmDia =  getNumeroDePrestacoesEmDia(creditoInstance)
             extratosDeCredito?.valorDaPrestacao = getValorDaPrestacao(creditoInstance)
             extratosDeCredito?.totalJurosDeMora = getTotalJurosDeMora(creditoInstance)
             extratosDeCredito?.totalCredito = getTotalCredito(creditoInstance)
             extratosDeCredito?.valorEmDivida  = getValorEmDivida(creditoInstance)
            extratosDeCredito?.valorEmMora = getValorEmMora(extratosDeCredito.valorEmDivida as BigDecimal,
                    extratosDeCredito.valorDaPrestacao as BigDecimal,extratosDeCredito.numeroDePrestacoesEmDia,
                    extratosDeCredito.totalJurosDeMora as BigDecimal, extratosDeCredito.totalCredito as BigDecimal)
           /* extratosDeCredito.valorEmDivida = creditoInstance?.valorEmDivida
            ((vm.valorDaPrestacao*vm.numeroDePrestacoesEmDia)+vm.totalJurosDeMora)+vm.totalCredito
            System.println(creditoInstance)*/
           extratosDeCredito?.valorCreditado = getValorCredito(creditoInstance)
            extratosDeCredito?.periodicidade = creditoInstance?.periodicidade
            extratosDeCredito?.contacto =cliente.telefone
            valorDaPrestacao*numeroDePrestacoesEmDia+totalJurosDeMora+totalCredito
          // extratosDeCredito.valorEmDivida = extratosDeCredito.valorDaPrestacao*extratosDeCredito.numeroDePrestacoesEmDia
            +extratosDeCredito.totalJurosDeMora+extratosDeCredito.totalCredito

           total_juros_de_mora +=extratosDeCredito?.totalJurosDeMora
           totalPago +=extratosDeCredito?.totalCredito
           valor_em_mora += extratosDeCredito?.valorEmMora
           valor_em_ivida +=extratosDeCredito?.valorEmDivida
           totalDesembolsado+=creditoInstance?.valorCreditado
           if(extratosDeCredito?.valorEmDivida<0){
               extratosDeCreditos?.add(extratosDeCredito)
           }

        }

        return extratosDeCreditos
    }

     BigDecimal getValorEmMora(BigDecimal valorEmDivida,BigDecimal valorDaPrestacao, Integer numeroDePrestacoesEmDia, BigDecimal totalJurosDeMora, BigDecimal totalCredito) {
        def vem=((valorDaPrestacao*numeroDePrestacoesEmDia)+totalJurosDeMora)+totalCredito
        if(vem<0){
            vem =valorEmDivida*(-1)
        }
       return vem
    }

    static BigDecimal getValorEmDivida(Credito creditoInstance){
        def valorEmDivida = 0
        for (Pagamento p in creditoInstance.pagamentos){
            valorEmDivida += p.valorDaPrestacao + p.valorDeJurosDeDemora + p.totalPago +p.valorDaRemissao

        }
        return valorEmDivida
    }
    BigDecimal getTotalCredito(Credito creditoInstance) {
        totalCredito = 0.0
        for(Pagamento pagamento in creditoInstance.pagamentos.sort{it.id}){
            for(Parcela parcela in pagamento.parcelas.sort{it.id}){
                if(parcela.valorPago>0.0){
                    totalCredito +=parcela.valorPago*(-1)
                }
            }
            for(Remissao remissao in pagamento.remissoes.sort{it.id}){
                totalCredito+=remissao.valorDaRemissao*(-1)
            }
        }
        return totalCredito
    }

     BigDecimal getValorCredito(Credito creditoInstance) {
        BigDecimal valorCreditado = 0.0
        for(Pagamento p in creditoInstance.pagamentos){
            if(p.descricao!="CAPITALIZACAO"){
                valorCreditado+=p.valorDaPrestacao
            }

        }
        return valorCreditado*(-1)
    }

    BigDecimal getTotalJurosDeMora(Credito creditoInstance) {
        totalJurosDeMora = 0.0
        for(Pagamento pagamento in creditoInstance.pagamentos){
            if (pagamento.valorDeJurosDeDemora * (-1) > 0.0){
              totalJurosDeMora+=  pagamento.valorDeJurosDeDemora * (-1)
            }
            if (pagamento.descricao=="CAPITALIZACAO") {
                totalJurosDeMora+=  pagamento.valorDaPrestacao * (-1)
            }
        }
        return totalJurosDeMora
    }

     BigDecimal getValorDaPrestacao(Credito creditoInstance) {
         BigDecimal  valor=0.0
        def pag = creditoInstance?.pagamentos?.find {it?.descricao=="1º- Prestação"}
         if(pag){
             valor=pag?.valorDaPrestacao
         }
       return   valor*(-1)
    }

     Integer getNumeroDePrestacoesEmDia(Credito creditoInstance) {
        Integer num = 0
        Date hoje = new Date()
        for(Pagamento p in creditoInstance.pagamentos){

            if(p.descricao!="CAPITALIZACAO"){
                if(p.dataPrevistoDePagamento.before(hoje)){
                    num++
                }
            }

        }
        return num

    }

    @Init init() {
        if(sessionStorageService.creditos){
            creditos = sessionStorageService.getCreditos() as List<Credito>
        }

    }

    BigDecimal getSaldoEmMora(Credito creditoInstance) {
      def  saldoEmMora = 0
        saldoEmMora = valorDaPrestacao*numeroDePrestacoesEmDia+totalJurosDeMora+totalCredito
        return saldoEmMora.setScale(2, RoundingMode.HALF_UP)
    }

}
