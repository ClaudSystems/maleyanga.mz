package mz.maleyanga.documento

import mz.maleyanga.pagamento.Pagamento
import mz.maleyanga.security.Utilizador
import org.grails.datastore.mapping.query.Query

/**
 * Nota
 * A domain class describes the data object and it's mapping to the database
 */
class Nota implements Serializable {

    Date dateCreated
    String messagem
    String referencia
    String autor
    Pagamento pagamento
    Nota pai
    Nota filha
    int colorR
    int colorG


/* Default (injected) attributes of GORM */
//	Long	id
//	Long	version
	
	/* Automatic timestamping of GORM */
//	Date	dateCreated
//	Date	lastUpdated
	
//	static	belongsTo	= [utilizador:Utilizador]	// tells GORM to cascade commands: e.g., delete this object if the "parent" is deleted.
//	static	hasOne		= []	// tells GORM to associate another domain object as an owner in a 1-1 mapping
//	static	hasMany		= []	// tells GORM to associate other domain objects for a 1-n or n-m mapping
//	static	mappedBy	= []	// specifies which property should be used in a mapping 
	
    static	mapping = {

    }
    
	static	constraints = {

        pai nullable: true
        filha nullable: true
        pagamento nullable: true
        colorR nullable: true
        colorG nullable: true
        referencia nullable: true


    }
	
	/*
	 * Methods of the Domain Class
	 */
//	@Override	// Override toString for a nicer / more descriptive UI 
//	public String toString() {
//		return "${name}";
//	}
}
