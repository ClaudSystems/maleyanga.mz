package mz.maleyanga.pagamento

import mz.maleyanga.cliente.Cliente
import mz.maleyanga.diario.Diario
import mz.maleyanga.documento.Anexo
import mz.maleyanga.documento.Nota
import mz.maleyanga.saidas.Saida
import mz.maleyanga.security.Utilizador
import mz.maleyanga.transacao.Transacao

import java.math.RoundingMode

/**
 * A  Parcela de pagam
 * A domain class describes the data object and it's mapping to the database
 */
class Parcela implements Serializable {
    private static final long serialVersionUID = 1

    String descricao
    String numeroDoRecibo
    BigDecimal valorParcial = 0.0
    BigDecimal valorPago = 0.0
    BigDecimal valorPagoBackup = 0.0
    Pagamento pagamento
    Date dataDePagamento
    Utilizador utilizador
    Cliente cliente
    String nomeDoCliente = ""
    Anexo anexo
    String formaDePagamento
    Date dateCreated
    Date lastUpdated
    Diario diario
    Boolean invalido
    static hasMany = [notas: Nota]
    static mapping = {
        notas lazy: false
        // diario lazy: false
        batchSize(10)
    }

    String getNomeDoCliente() {
        if (pagamento != null) {
            return pagamento.credito.cliente.nome
        } else
            return nomeDoCliente
    }
    static constraints = {
        pagamento nullable: true
        cliente nullable: true
        utilizador nullable: true
        valorParcial(validator: { return it >= 0 })
        pagamento nullable: true
        dataDePagamento nullable: false
        descricao nullable: true
        anexo nullable: true
        numeroDoRecibo nullable: true, unique: false
        dateCreated nullable: true
        lastUpdated nullable: true
        valorPago nullable: true
        diario nullable: true
        invalido nullable: true
        valorPagoBackup nullable: true
        nomeDoCliente nullable: true
        notas nullable: true
        }

    /*
     * Methods of the Domain Class
     */
//	@Override	// Override toString for a nicer / more descriptive UI 
    String toString() {
        return "${descricao + "-" + valorParcial}"
    }

}
