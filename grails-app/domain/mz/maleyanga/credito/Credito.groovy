package mz.maleyanga.credito

import mz.maleyanga.cliente.Cliente
import mz.maleyanga.documento.Anexo
import mz.maleyanga.pagamento.Pagamento
import mz.maleyanga.security.Utilizador


/**
 * Credito
 * A domain class describes the data object and it's mapping to the database
 */
class Credito implements Serializable {
    private static final long serialVersionUID = 1
    BigDecimal valorCreditado = 0.0
    Date dateCreated
    Date lastUpdated
    Date dateConcecao
    Integer moras = 0
    Date validade
    String numeroDoCredito
    BigDecimal percentualDejuros = 0.0
    BigDecimal percentualJurosDeDemora = 0.000
    BigDecimal totalDaDividaSemMoras = 0.0
    BigDecimal valorEmDivida = 0.0
    BigDecimal totalPago = 0.0
    BigDecimal totalMoras = 0.0
    BigDecimal taxas = 0
    String estado
    String formaDeCalculo       // Determina a formula usada no calculo das prestaçoes
    // pmt é a formula universal para calculo de juros
    // taxafixa somento calcula o juros sobre o valor total e divide pelo numero de prestacoes
    String periodicidade
    boolean emDivida = true
    boolean invalido = false
    int numeroDePrestacoes
    Integer recorenciaDeMoras
    Cliente cliente
    boolean reterCapital = false
    Utilizador utilizador
    BigDecimal valorDeJuros = 0.0
    Boolean emMora = false
    Integer periodoVariavel
    Boolean ignorarValorPagoNoPrazo

    BigDecimal getTotalPago() {
        totalPago = 0.0
        if (pagamentos?.empty) {
            return 0.0
        } else {
            for (Pagamento p in pagamentos) {
                totalPago += p.totalPago
            }
            return totalPago
        }
    }

    BigDecimal getTotalMoras() {
        totalMoras = 0.0
        if (pagamentos?.empty) {
            return 0.0
        } else {
            for (Pagamento p in pagamentos) {
                totalMoras += p.valorDeJurosDeDemora
            }
        }
        return totalMoras
    }

    BigDecimal getValorEmDivida() {
        valorEmDivida = 0.0

        if (pagamentos?.empty) {
            return 0.0
        } else if (pagamentos == null) {
            return 0.0
        } else {
            for (Pagamento p in pagamentos) {
                valorEmDivida += p.valorDaPrestacao + p.valorDeJurosDeDemora + p.totalPago + p.valorDaRemissao
            }
        }
        return valorEmDivida
    }

    BigDecimal getTotalDaDividaSemMoras() {
        totalDaDividaSemMoras = 0.0
        def moras = getTotalMoras() * (-1)
        totalDaDividaSemMoras = getValorEmDivida() + moras + getTotalPago()
        if (totalDaDividaSemMoras > 0) {
            totalDaDividaSemMoras = 0.0
        }
        return totalDaDividaSemMoras
    }

    String getEstado() {
        if (!emDivida) {
            estado = "Fechado"
        }
        if (emDivida) {
            if (pagamentos.findAll { it.diasDeMora > 0 }) {
                estado = "Pendente"
            } else estado = "Aberto"
            if (pagamentos.findAll { it.pago }) {
                estado = "EmProgresso"
            }
        }
        return estado
    }

    boolean getEmDivida() {
        return valorEmDivida < 0
    }
    static hasMany = [pagamentos: Pagamento]


    static mapping = {
        //   cliente lazy: false
        id generator: 'increment'
        batchSize(30)
    }

    static constraints = {
        valorDeJuros nullable: true
        totalMoras nullable: true
        recorenciaDeMoras nullable: true
        totalDaDividaSemMoras nullable: true
        emMora nullable: true
        periodoVariavel nullable: true
        moras nullable: true
        validade nullable: true
        //  anexos nullable: true
        utilizador nullable: true
        lastUpdated nullable: true
        taxas nullable: true
        valorCreditado nullable: false
        percentualDejuros nullable: false, min: 0.0, max: 100.0, scale: 2
        estado nullable: false, inList: ['Aberto', 'Pendente', 'Fechado', 'EmProgresso']
        periodicidade nullable: false, inList: ['mensal', 'quinzenal', 'semanal', 'diario', 'doisdias', 'variavel']
        formaDeCalculo nullable: false, inList: ['pmt', 'taxafixa']
        cliente nullable: false
        totalPago nullable: true
        ignorarValorPagoNoPrazo nullable: true
        numeroDePrestacoes(validator: {
            return it > 0
        })
        valorEmDivida nullable: true
        numeroDoCredito unique: false
    }

    String toString() {
        return "${"id::" + id + "Nº:" + numeroDoCredito + ".valor::" + valorCreditado}"
    }

    BigDecimal getValorDeJuros() {
        valorDeJuros = 0.0
        if (pagamentos != null) {
            for (Pagamento p in pagamentos) {
                valorDeJuros += p.valorDeJuros
            }
        }

        return valorDeJuros
    }

    Boolean getEmMora() {
        if (pagamentos != null) {
            for (Pagamento p in this.pagamentos) {
                if (p?.diasDeMora > 0) {
                    return true
                }
            }
        }
        return false
    }

    Date getValidade() {
        validade = new Date()
        if (pagamentos != null) {
            if (!pagamentos.empty) {
                validade = pagamentos?.sort { it?.id }?.last()?.dataPrevistoDePagamento
                return validade
            }
        }

        return validade
    }
}
