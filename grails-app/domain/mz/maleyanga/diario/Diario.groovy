package mz.maleyanga.diario

import mz.maleyanga.documento.Nota
import mz.maleyanga.pagamento.Parcela
import mz.maleyanga.saidas.Saida

/**
 * Diario
 * A domain class describes the data object and it's mapping to the database
 */
class Diario {
    String estado
    String numeroDoDiario
    Date dataDeReferencia
    Date dateCreated
    Date lastUpdated
    Date dateClosed

    String getNumeroDoDiario() {
        return numeroDoDiario
    }

    Date getDataDeReferencia() {
        if (dataDeReferencia == null) {
            return dateCreated
        }
        return dataDeReferencia
    }
//	static	belongsTo	= []	// tells GORM to cascade commands: e.g., delete this object if the "parent" is deleted.
//	static	hasOne		= []	// tells GORM to associate another domain object as an owner in a 1-1 mapping
    static hasMany = [parcelas: Parcela, saidas: Saida]
    // tells GORM to associate other domain objects for a 1-n or n-m mapping
//	static	mappedBy	= []	// specifies which property should be used in a mapping

    static mapping = {

        //  parcelas lazy: true
        //  saidas lazy: false
        numeroDoDiario unique: true
        batchSize(30)
    }

    static constraints = {
        estado inList: ["aberto", "fechado", "pendente"]
        lastUpdated nullable: true
        dateClosed nullable: true
        dataDeReferencia nullable: true
        estado inList: ["aberto", "fechado", "pendente"]
        parcelas nullable: true

    }

    /*
     * Methods of the Domain Class
     */
//	@Override	// Override toString for a nicer / more descriptive UI
    public String toString() {
        return "${numeroDoDiario}"
    }
}
