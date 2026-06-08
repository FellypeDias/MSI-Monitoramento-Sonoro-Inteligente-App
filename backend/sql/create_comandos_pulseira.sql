CREATE TABLE IF NOT EXISTS comandos_pulseira (
  id SERIAL PRIMARY KEY,
  acao VARCHAR(20) NOT NULL CHECK (acao IN ('LED', 'VIBRAR')),
  dispositivo VARCHAR(80) NOT NULL DEFAULT 'Pulseira Principal',
  status VARCHAR(20) NOT NULL DEFAULT 'pendente' CHECK (status IN ('pendente', 'executado')),
  criado_em TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  executado_em TIMESTAMPTZ NULL
);

CREATE INDEX IF NOT EXISTS idx_comandos_pulseira_status_criado_em
ON comandos_pulseira (status, criado_em ASC);
