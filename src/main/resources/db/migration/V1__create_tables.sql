CREATE TABLE usuarios (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          nome VARCHAR(100),
                          email VARCHAR(100) UNIQUE,
                          senha VARCHAR(255)
);

CREATE TABLE advogados (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           nome VARCHAR(100),
                           oab VARCHAR(20) UNIQUE,
                           email VARCHAR(100),
                           senha VARCHAR(255)
);

CREATE TABLE causas (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        usuario_id BIGINT,
                        titulo VARCHAR(255),
                        descricao TEXT,
                        FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE lances (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        advogado_id BIGINT,
                        causa_id BIGINT,
                        valor DECIMAL(10,2),
                        status ENUM('PENDENTE', 'ACEITO', 'NEGOCIANDO'),
                        FOREIGN KEY (advogado_id) REFERENCES advogados(id),
                        FOREIGN KEY (causa_id) REFERENCES causas(id)
);

CREATE TABLE mensagens (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           lance_id BIGINT,
                           remetente ENUM('USUARIO', 'ADVOGADO'),
                           mensagem TEXT,
                           data_envio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           FOREIGN KEY (lance_id) REFERENCES lances(id)
);
