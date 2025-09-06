CREATE TABLE pagamentos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    proposta_id BIGINT NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    metodo VARCHAR(20),
    quando VARCHAR(20),
    status VARCHAR(20),
    comprovante VARCHAR(255),
    CONSTRAINT fk_pagamento_proposta FOREIGN KEY (proposta_id) REFERENCES propostas(id) ON DELETE CASCADE
);
