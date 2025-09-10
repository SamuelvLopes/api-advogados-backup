-- Índices para otimizar consultas de perfis
CREATE INDEX IF NOT EXISTS idx_avaliacoes_causa ON avaliacoes (causa_id);
CREATE INDEX IF NOT EXISTS idx_avaliacoes_created_at ON avaliacoes (created_at);

CREATE INDEX IF NOT EXISTS idx_causas_advogado ON causas (advogado_id);
CREATE INDEX IF NOT EXISTS idx_causas_status ON causas (status);

CREATE INDEX IF NOT EXISTS idx_propostas_causa_adv_status ON propostas (causa_id, advogado_id, status);

CREATE INDEX IF NOT EXISTS idx_advogados_account ON advogados (account_id);
