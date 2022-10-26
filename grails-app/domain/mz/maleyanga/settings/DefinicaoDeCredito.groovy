package mz.maleyanga.settings

import mz.maleyanga.Taxa.Taxa

/**
 * DefinicaoDeCredito
 * A domain class describes the data object and it's mapping to the database
 */
class DefinicaoDeCredito {
    String descricao
    int numeroDePrestacoes = 1
    String periodicidade
    String formaDeCalculo
    int recorenciaDeMoras = 0
    BigDecimal percentualDejuros = 0.0
    BigDecimal percentualJurosDeDemora = 0.00
    Taxa taxa
    boolean ativo = true
    Boolean excluirSabados = true
    Boolean excluirDomingos = true
    Boolean excluirDiaDePagNoSabado = false
    Boolean excluirDiaDePagNoDomingo = true
    Integer periodoVariavel


    /* Default (injected) attributes of GORM */
//	Long	id
//	Long	version

    /* Automatic timestamping of GORM */
//	Date	dateCreated
//	Date	lastUpdated

//	static	belongsTo	= []	// tells GORM to cascade commands: e.g., delete this object if the "parent" is deleted.
//	static	hasOne		= []	// tells GORM to associate another domain object as an owner in a 1-1 mapping
//	static	hasMany		= []	// tells GORM to associate other domain objects for a 1-n or n-m mapping
//	static	mappedBy	= []	// specifies which property should be used in a mapping 

    static mapping = {
        taxa lazy: false
        batchSize(5)
    }

    static constraints = {
        taxa nullable: true
        descricao unique: true
        excluirDomingos nullable: true
        excluirSabados nullable: true
        excluirDiaDePagNoSabado nullable: true
        excluirDiaDePagNoDomingo nullable: true
        periodoVariavel nullable: true
        percentualJurosDeDemora nullable: false, min: 0.00, max: 100.00, scale: 3
    }

    /*
     * Methods of the Domain Class
     */
//	@Override	// Override toString for a nicer / more descriptive UI 
//	public String toString() {
//		return "${name}";
//	}
}
