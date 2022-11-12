package mz.maleyanga.cliente

import mz.maleyanga.GenerateRandomNumber
import mz.maleyanga.conta.Conta
import mz.maleyanga.credito.Credito
import mz.maleyanga.documento.Anexo
import mz.maleyanga.entidade.Entidade
import mz.maleyanga.pagamento.Pagamento
import mz.maleyanga.pedidoDeCredito.PedidoDeCredito
import mz.maleyanga.pedidoDeCredito.Penhora
import mz.maleyanga.security.Utilizador


class Cliente implements Serializable {
    private static final long serialVersionUID = 1
    String nome
    String codigo = ""
    String nuit
    String tipoDeIndentificacao
    String numeroDeIndentificao
    String residencia
    String nomeDoArquivoDeIndentificao
    String email
    String localDeTrabalho
    String telefone
    String telefone1
    String telefone2
    Entidade entidade
    String nacionalidade
    Date dataDeExpiracao
    Date dataDeNascimento
    Date dataDeEmissao
    String estadoCivil
    String profissao
    String genero
    boolean ativo = true
    Boolean emDivida = false
    Utilizador utilizador
    String classificacao = "medio"
    Anexo anexo
    Date lastUpdated
    Date dateCreated
    BigDecimal totalEmDivida = 0.0

    Boolean getEmDivida() {
        return emDivida
    }
/*Boolean getEmDivida() {
        if (creditos == null) {
            return false
        }
        if (creditos.empty) {
            return false
        }
        if (totalEmDivida < 0) {
            return true
        }
        return false
    }*/

    /*BigDecimal getTotalEmDivida() {
        totalEmDivida = 0.0
        if (this.creditos != null) {
            if (!creditos.empty) {
                for (Credito credito in creditos) {
                    totalEmDivida += credito.valorEmDivida
                }
            }
        }

        return totalEmDivida
    }*/


    //  static hasMany = [pedidosDeCredito: PedidoDeCredito, creditos: Credito, anexos: Anexo, contas: Conta, assinantes: Assinante, penhoras: Penhora]


    static mapping = {
        id generator: 'increment'
        // assinantes lazy: false
        // pedidosDeCredito lazy: false
        nome unique: true
        nuit unique: true
    }
    static constraints = {
        /* attachment nullable: true
         fileName nullable: true, display: false
         fileType nullable: true, display:false*/

        nome nullable: false, blank: false, unique: true
        nuit nullable: true
        dataDeExpiracao nullable: true
        numeroDeIndentificao nullable: false
        residencia nullable: false
        email nullable: true
        telefone nullable: true, maxSize: 9, unique: true
        telefone1 nullable: true, maxSize: 9
        telefone2 nullable: true, maxSize: 9
        tipoDeIndentificacao inList: ["BI", "Passaporte", "Carta de conducao", "Outro"]
        genero inList: ["masculino", "feminino", "transgênero", "não-binário", "agênero", "pangênero", "genderqueer", " two-spirit", "outro"]
        estadoCivil inList: ["Solteiro", "Solteira", "Casado", "Casada", "Separado Judicialmente", "Separado Judicialmente", "Outro"]
        classificacao inList: ["excelente", "bom", "medio", "mau", "pessimo"], nullable: true
        entidade nullable: false
        //  anexos nullable: true
        ativo nullable: false
        // assinantes nullable: true
        utilizador nullable: true
        localDeTrabalho nullable: true
        anexo nullable: true
        nacionalidade nullable: true
        dataDeNascimento nullable: true
        nomeDoArquivoDeIndentificao nullable: true
        emDivida nullable: true
        totalEmDivida nullable: true
        profissao nullable: true
        // penhoras nullable: true
        dataDeEmissao nullable: true
        genero nullable: true
        codigo nullable: true, unique: true

    }

    String toString() {
        return "${nome}"
    }


}
