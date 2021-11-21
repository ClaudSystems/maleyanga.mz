package mz.maleyanga.saidas

import mz.maleyanga.conta.Conta
import mz.maleyanga.diario.Diario
import mz.maleyanga.security.Utilizador

import java.math.RoundingMode

/**
 * Despesa
 * A domain class describes the data object and it's mapping to the database
 */
class Saida implements Serializable {
    private static final long serialVersionUID = 1
    String descricao
    BigDecimal valor
    BigDecimal valorBackup = 0.0
    Utilizador utilizador
    Conta origem
    Conta destino
    String contaOrigem
    String contaDestino
    Date dateCreated
    Date dataDePagamento
    String formaDePagamento
    Diario diario
    Boolean invalido = false
//	Date	lastUpdated

//	static	belongsTo	= []	// tells GORM to cascade commands: e.g., delete this object if the "parent" is deleted.
//	static	hasOne		= []	// tells GORM to associate another domain object as an owner in a 1-1 mapping
//	static	hasMany		= []	// tells GORM to associate other domain objects for a 1-n or n-m mapping
//	static	mappedBy	= []	// specifies which property should be used in a mapping 

    static mapping = {
        utilizador lazy: false
    }

    static constraints = {
        diario nullable: true
        valorBackup nullable: true
        invalido nullable: true
        contaDestino nullable: true
        destino nullable: true
    }

    /*
     * Methods of the Domain Class
     */
//	@Override	// Override toString for a nicer / more descriptive UI 
    String toString() {
        return "${descricao}"
    }


}
