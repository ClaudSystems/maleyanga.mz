package pagamento

import org.zkoss.zk.grails.*

import org.zkoss.bind.annotation.Command
import org.zkoss.bind.annotation.NotifyChange
import org.zkoss.bind.annotation.Init
import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zul.Hbox

class PagamentoListViewModel {

    String message
    @Wire  btnHello
    @Wire Hbox hb_entrada

    @Init init() {
        // initialzation code here
    }

    @NotifyChange(['message'])
    @Command clickMe() {
        message = "Clicked"
    }

}
