package diario

import mz.maleyanga.ComposerService
import mz.maleyanga.SessionStorageService
import mz.maleyanga.diario.Diario
import mz.maleyanga.documento.Nota
import mz.maleyanga.pagamento.Parcela
import mz.maleyanga.saidas.Saida
import org.springframework.stereotype.Service
import org.zkoss.zk.grails.*

import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.NotifyChange
import org.zkoss.bind.annotation.Init
import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zul.ListModelList
@Service
class PrintDiarioViewModel {
    SessionStorageService sessionStorageService
    BigDecimal totalSaidas = 0.0
    BigDecimal totalSaidasAtivas = 0.0
    BigDecimal totalSaidasPassivas = 0.0
    ListModelList<Saida> saidas
    ListModelList<Saida> saidasAtivas
    ListModelList<Saida> saidasPassivas
    ListModelList<Nota> notas
     Diario diario
    BigDecimal totalParcelas = 0.0
    ListModelList<Parcela> parcelas

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

    BigDecimal getTotalParcelas() {
        totalParcelas = 0.0
        def parcelas = Parcela.findAllByDiario(diario)
        for(Parcela parcela in parcelas){
            totalParcelas +=parcela.valorPago
        }
        return totalParcelas
    }
    ListModelList<Saida> getSaidasAtivas() {
        if(saidasAtivas ==null){
            saidasAtivas = new ListModelList<Saida>()
        }
        saidas = new ListModelList<Saida>(Saida.findAllByDiario(diario))
        for(Saida s in saidas){
            if(s?.destino?.ativo){
                if(s.origem.finalidade=="conta_caixa"){
                    saidasAtivas.add(s)
                }

            }
        }
        return saidasAtivas
    }

    ListModelList<Saida> getSaidasPassivas() {
        if(saidasPassivas==null){
            saidasPassivas = new ListModelList<Saida>()
        }
        saidas = new ListModelList<Saida>(Saida.findAllByDiario(diario))
        for(Saida s in saidas){
            if(!s?.destino?.ativo){
                if(s.origem.finalidade=="conta_caixa"){
                    saidasPassivas.add(s)
                }

            }
        }
        return saidasPassivas
    }

    List<Parcela> getParcelas() {
        if(parcelas==null){
            parcelas = new ListModelList<Parcela>(Parcela.findAllByValorPagoGreaterThanAndDiario(0.0,diario))
        }
        return parcelas
    }

    Diario getDiario() {

        return diario
    }

    void setDiario(Diario diario) {
        this.diario = diario
    }

    @Init init() {
        diario = sessionStorageService.diario as Diario
     }

    ListModelList<Saida> getSaidas() {
        if(saidas ==null){
            saidas = new ListModelList<Saida>(Saida.findAllByDiario(diario))
        }
        return saidas
    }
    BigDecimal getTotalSaidas() {

        totalSaidas = 0.0
        def saidas = Saida.findAllByDiario(diario)
        for(Saida saida in saidas){
            if(saida.origem.finalidade=="conta_caixa"){
                totalSaidas +=saida.valor
            }

        }
        return totalSaidas
    }

    BigDecimal getTotalSaidasAtivas() {
        totalSaidasAtivas = 0.0
        for(Saida s in saidasAtivas){
            if(s.origem.finalidade=="conta_caixa"){
                totalSaidasAtivas+=s.valor
            }

        }
        return totalSaidasAtivas
    }

    BigDecimal getTotalSaidasPassivas() {
        totalSaidasPassivas = 0.0
        for(Saida s in saidasPassivas){
            if(s.origem.finalidade=="conta_caixa"){
                totalSaidasPassivas+=s.valor
            }

        }
        return totalSaidasPassivas
    }

    BigDecimal getSaldo() {
        return getTotalParcelas()-getTotalSaidas()
    }
}
