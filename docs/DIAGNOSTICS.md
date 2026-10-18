# Diagnostico interno - ACTestLab

O ACTestLab e um harness standalone. Nao conversa com processos externos,
nao abre sockets, nao usa overlays, nao declara acessibilidade, nao usa root.

## O que faz
- Gera entidades sinteticas deterministicas.
- Renderiza caixas/IDs/distancia em Canvas local.
- Executa 6 diagnosticos internos (DT-01 a DT-06).
- Persiste configuracoes via DataStore.

## O que NAO faz
- Enviar mensagens a um anticheat.
- Handshake/token.
- Inspecionar memoria de outros processos.
- Interceptar rede ou UI do jogo.

## Limitacoes
- Release desliga ESP (BuildConfig.DEBUG_ESP_ENABLED = false).
- Metricas de performance devem ser coletadas no dispositivo alvo.
