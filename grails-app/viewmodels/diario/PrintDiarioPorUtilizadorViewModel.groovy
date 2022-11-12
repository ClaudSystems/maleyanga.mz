package diario


import mz.maleyanga.SessionStorageService
import mz.maleyanga.diario.Diario
import mz.maleyanga.documento.Nota
import mz.maleyanga.pagamento.Parcela
import mz.maleyanga.saidas.Saida
import mz.maleyanga.security.Utilizador
import org.zkoss.bind.annotation.Init
import org.zkoss.zul.ListModelList

class PrintDiarioPorUtilizadorViewModel {
    SessionStorageService sessionStorageService
    Diario diario
    Utilizador utilizador
    BigDecimal totalParcelas = 0.0
    BigDecimal totalSaidas = 0.0
    BigDecimal totalSaidasAtivas = 0.0
    BigDecimal totalSaidasPassivas = 0.0
    BigDecimal saldo = 0.0
    List<Parcela> parcelas
    List<Saida> saidas
    List<Saida> saidasAtivas
    List<Saida> saidasPassivas
    ListModelList<Nota> notas

    ListModelList<Nota> getNotas() {
        if(notas==null){
            notas = new ListModelList<Nota>()
        }
        def saidas = getSaidas()
        def parcelas = getParcelas()
        for (Saida saida in saidas){
            for(Nota nota in saida.notas){
                notas.add(nota)
            }
        }
        for(Parcela parcela in parcelas){
            for(Nota nota in parcela.notas){
                notas.add(nota)
            }
        }
        return notas
    }


    BigDecimal getTotalSaidasAtivas() {
        totalSaidasAtivas =0.0
        for(Saida saida in saidasAtivas){
            if(saida.origem.finalidade=="conta_caixa"){
                totalSaidasAtivas+=saida.valor
            }

        }
        return totalSaidasAtivas
    }

    BigDecimal getTotalSaidasPassivas() {
        totalSaidasPassivas = 0.0
        for(Saida saida in saidasPassivas){
            if(saida.origem.finalidade=="conta_caixa"){
                totalSaidasPassivas+=saida.valor
            }

        }
        return totalSaidasPassivas
    }

    BigDecimal getSaldo() {
        return getTotalParcelas()-getTotalSaidas()
    }

    void setSaldo(BigDecimal saldo) {
        this.saldo = saldo
    }

    Utilizador getUtilizador() {
        return  utilizador
    }

    BigDecimal getTotalParcelas() {
        totalParcelas = 0.0
        for(Parcela parcela in parcelas){
            totalParcelas +=parcela.valorPago
        }
        return totalParcelas
    }

    BigDecimal getTotalSaidas() {
        totalSaidas = 0.0
        for(Saida saida in saidas){
            if(saida.origem.finalidade=="conta_caixa"){
                totalSaidas +=saida.valor
            }

        }
        return totalSaidas
    }

    List<Parcela> getParcelas() {
        if (parcelas==null){
            parcelas = new ListModelList<Parcela>(Parcela.findAllByValorPagoGreaterThanAndDiarioAndUtilizador(0.0,diario,utilizador))
        }
        return parcelas
    }

    List<Saida> getSaidas() {
        if(saidas==null){
            saidas = new ListModelList<Saida>(Saida.findAllByDiarioAndUtilizador(diario,utilizador))
        }
        return saidas
    }

    List<Saida> getSaidasAtivas() {
        if(saidasAtivas==null){
            saidasAtivas = new ListModelList<Saida>()
        }
        saidas = new ListModelList<Saida>(Saida.findAllByDiarioAndUtilizador(diario,utilizador))
        for(Saida saida in saidas){
            if(saida.valor>0){
                if(saida.origem.finalidade=="conta_caixa"){
                    if(saida.destino.ativo){
                        saidasAtivas.add(saida)
                    }

                }
            }

        }
        return saidasAtivas
    }


    List<Saida> getSaidasPassivas() {
        if(saidasPassivas==null){
            saidasPassivas = new ListModelList<Saida>()
        }
        saidas = new ListModelList<Saida>(Saida.findAllByDiarioAndUtilizador(diario,utilizador))
        for(Saida saida in saidas){
            if(saida.origem.finalidade=="conta_caixa"){
                if(!saida.destino.ativo){
                    saidasPassivas.add(saida)
                }

            }

        }
        return saidasPassivas
    }

    Diario getDiario() {

        return diario
    }

    void setDiario(Diario diario) {
        this.diario = diario
    }
    @Init init() {
        diario = sessionStorageService.diario as Diario
        //  utilizador = Utilizador.findByUsername(composerService.utilizador)
        def user = sessionStorageService.getUtilizador() as String
        utilizador = Utilizador.findByUsername(user)

    }


}
