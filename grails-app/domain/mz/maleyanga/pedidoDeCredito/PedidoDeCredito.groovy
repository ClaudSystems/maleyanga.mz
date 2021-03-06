package mz.maleyanga.pedidoDeCredito

import mz.maleyanga.cliente.Cliente
import mz.maleyanga.credito.Credito
import mz.maleyanga.security.Utilizador
import mz.maleyanga.settings.DefinicaoDeCredito

/**
 * PedidoDeCredito
 * A domain class describes the data object and it's mapping to the database
 */
class PedidoDeCredito implements Serializable{
    private static final long serialVersionUID = 1
       Cliente cliente
    String motivo
    BigDecimal valorDeCredito
    String estado
    Date dataDeAprovacao
    Date dataDeDesembolso
    boolean creditado = false
    Utilizador utilizador
    Credito credito
    BigDecimal valorDaPenhora
    BigDecimal valorDaComissao
    DefinicaoDeCredito definicaoDeCredito
    Integer nDePrestacoes
    Date dateCreated
    Date lastUpdated
    ListaDeDesembolso listaDeDesembolso
    String frequencia
    // static hasMany = [penhoras: Penhora, notas: Nota]

    static mapping = {
        penhoras lazy: false
        cliente lazy: true
        utilizador lazy: false
        id generator: 'increment'
        batchSize(10)

    }


    static constraints = {
        dateCreated nullable: true
        lastUpdated nullable: true
        utilizador nullable: true
        credito nullable: true
        cliente nullable: false
        valorDeCredito nullable: false
        dataDeAprovacao nullable: true, display: false
        motivo nullable: false
        valorDaPenhora nullable: true
        estado inList: ["aberto", "pendente", "aprovado", "reprovado", "executado"]
        valorDaComissao nullable: true
        definicaoDeCredito nullable: true
        nDePrestacoes nullable: true
        dataDeDesembolso nullable: true
        listaDeDesembolso nullable: true
        frequencia nullable: true, inList: ['novo', 'renovação']
    }

    String getEstado() {
        if (credito != null) {
            return "executado"
        }
        if (creditado) {
            return "aprovado"
        }
        return estado
    }

    String toString() {
        return "${"id::" + id + ".valor::" + valorDeCredito}"
    }
}
