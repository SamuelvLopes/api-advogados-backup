CREATE TABLE propostas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    advogado_id BIGINT NOT NULL,
    causa_id BIGINT NOT NULL,
    mensagem TEXT,
    valor_sugerido DECIMAL(10,2),
    status ENUM('ENVIADA','ACEITA','RECUSADA') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_proposta_advogado FOREIGN KEY (advogado_id) REFERENCES advogados(id) ON DELETE CASCADE,
    CONSTRAINT fk_proposta_causa FOREIGN KEY (causa_id) REFERENCES causas(id) ON DELETE CASCADE,
    CONSTRAINT uk_advogado_causa UNIQUE (advogado_id, causa_id)
);
