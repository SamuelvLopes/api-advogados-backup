ALTER TABLE advogados
    ADD COLUMN whatsapp VARCHAR(20),
    ADD COLUMN areas_atuacao VARCHAR(255);

ALTER TABLE propostas
    ADD COLUMN forma_pagamento VARCHAR(50),
    ADD COLUMN comprovante_pagamento VARCHAR(255);

ALTER TABLE causas
    ADD COLUMN sucesso BOOLEAN;

CREATE TABLE avaliacoes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    advogado_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    causa_id BIGINT NOT NULL,
    nota INT NOT NULL,
    comentario TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_avaliacao_advogado FOREIGN KEY (advogado_id) REFERENCES advogados(id) ON DELETE CASCADE,
    CONSTRAINT fk_avaliacao_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_avaliacao_causa FOREIGN KEY (causa_id) REFERENCES causas(id) ON DELETE CASCADE,
    CONSTRAINT uk_usuario_causa UNIQUE (usuario_id, causa_id)
);

