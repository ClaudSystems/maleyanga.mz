SELECT 
  pagamento.credito_id, 
  pagamento.data_de_pagamento, 
  pagamento.data_previsto_de_pagamento, 
  pagamento.date_created, 
  pagamento.descricao, 
  pagamento.dias_de_mora, 
  pagamento.pago, 
  pagamento.total_em_divida, 
  pagamento.valor_da_prestacao, 
  pagamento.valor_de_juros_de_demora, 
  pagamento.valor_da_remissao, 
  pagamento.valor_pago
FROM 
  public.pagamento;