# Backend Advogados Solidários

Este repositório descreve um backend com autenticação e controle de acesso para dois perfis: cliente e advogado.

## Funcionalidades

### Cliente
- Cadastrar e fazer login;
- Criar causas;
- Receber e aceitar propostas de advogados;
- Escolher forma de pagamento (agora ou depois);
- Anexar comprovante de pagamento externo ou pagar pelo sistema;
- Avaliar o advogado após o encerramento;
- Consultar seu histórico.

### Advogado
- Cadastrar e fazer login com informações profissionais (OAB, áreas de atuação, WhatsApp);
- Visualizar causas abertas;
- Enviar propostas;
- Ser notificado quando sua proposta for aceita;
- Encerrar a causa (sucesso ou não);
- Consultar histórico;
- Manter um perfil público com média de estrelas e comentários;
- Acessar um dashboard interno com métricas financeiras (total recebido, número de casos, ticket médio e top caso).

## Regras principais
- Uma causa começa como "aberta";
- Quando o cliente aceita uma proposta, a causa muda para "em andamento" e some da lista pública;
- O advogado responsável pode encerrar o caso como sucesso ou não;
- O cliente só pode avaliar após o encerramento;
- Se o pagamento não foi feito, o cliente deve escolher pagar ou anexar comprovante externo;
- O perfil público do advogado mostra apenas avaliações de casos encerrados;
- O dashboard do advogado calcula métricas somente com casos pagos;
- RBAC garante que o cliente acesse apenas seus recursos e o advogado apenas os que é responsável;
- Toda transição de status gera registro de auditoria e notificação.

## Endpoints (alto nível)
- Autenticação: register, login, me, logout;
- Causas: criar, listar abertas, histórico, aceitar proposta, encerrar;
- Propostas: enviar, listar, aceitar;
- Pagamentos: escolher forma, pagar agora, anexar comprovante, consultar status;
- Avaliações: criar, listar por advogado;
- Perfil público do advogado;
- Dashboard de métricas do advogado.

## Fluxos de uso

### Fluxo do cliente
1. Criar causa;
2. Receber proposta;
3. Aceitar proposta;
4. Escolher forma de pagamento;
5. Advogado encerra a causa;
6. Cliente avalia.

### Fluxo do advogado
1. Ver causas abertas;
2. Enviar proposta;
3. Ser aceito;
4. Encerrar a causa;
5. Métricas atualizadas.

Este README serve como instrução básica para o desenvolvimento do Backend Advogados Solidários.
