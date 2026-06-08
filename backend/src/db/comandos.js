const pool = require("./connection");

const ACOES_PERMITIDAS = ["LED", "VIBRAR"];

async function criarComando(acao, dispositivo) {
  const result = await pool.query(
    "INSERT INTO comandos_pulseira (acao, dispositivo, status) VALUES ($1, $2, 'pendente') RETURNING *",
    [acao, dispositivo]
  );

  return result.rows[0];
}

async function listarComandosPendentes() {
  const result = await pool.query(
    "SELECT * FROM comandos_pulseira WHERE status = 'pendente' AND acao = ANY($1::text[]) ORDER BY criado_em ASC",
    [ACOES_PERMITIDAS]
  );

  return result.rows;
}

async function marcarComandoExecutado(id) {
  const result = await pool.query(
    "UPDATE comandos_pulseira SET status = 'executado', executado_em = NOW() WHERE id = $1 RETURNING *",
    [id]
  );

  return result.rows[0] || null;
}

module.exports = {
  criarComando,
  listarComandosPendentes,
  marcarComandoExecutado
};
