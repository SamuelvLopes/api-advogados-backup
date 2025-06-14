-- Tabela central de autenticação
CREATE TABLE accounts (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          email VARCHAR(100) UNIQUE NOT NULL,
                          senha VARCHAR(255) NOT NULL,
                          role ENUM('USUARIO', 'ADVOGADO') NOT NULL
);

CREATE TABLE usuarios (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          account_id BIGINT UNIQUE NOT NULL,
                          nome VARCHAR(100) NOT NULL,
                          FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
);

CREATE TABLE advogados (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           account_id BIGINT UNIQUE NOT NULL,
                           nome VARCHAR(100) NOT NULL,
                           oab VARCHAR(20) UNIQUE NOT NULL,
                           FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
);

-- Tabela de causas, vinculada ao usuário (cliente)
CREATE TABLE causas (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        usuario_id BIGINT NOT NULL,
                        titulo VARCHAR(255) NOT NULL,
                        descricao TEXT,
                        status ENUM('ABERTA','NEGOCIANDO','FECHADA') NOT NULL,
                        FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Tabela de lances, vinculada ao advogado e à causa
CREATE TABLE lances (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        advogado_id BIGINT NOT NULL,
                        causa_id BIGINT NOT NULL,
                        valor DECIMAL(10,2) NOT NULL,
                        status ENUM('PENDENTE', 'ACEITO', 'NEGOCIANDO') NOT NULL,
                        FOREIGN KEY (advogado_id) REFERENCES advogados(id) ON DELETE CASCADE,
                        FOREIGN KEY (causa_id) REFERENCES causas(id) ON DELETE CASCADE
);

CREATE TABLE mensagens (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           lance_id BIGINT NOT NULL,
                           remetente ENUM('USUARIO', 'ADVOGADO') NOT NULL,
                           mensagem TEXT NOT NULL,
                           data_envio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           FOREIGN KEY (lance_id) REFERENCES lances(id) ON DELETE CASCADE
);
