const pool = require("./connection");
const TIPOS_PERMITIDOS = ["campainha", "alarme"];

async function saveEvent(type, intensity, deviceId) {
  const result = await pool.query(
    "INSERT INTO eventos (tipo_evento, intensidade, dispositivo_id) VALUES ($1, $2, $3) RETURNING *",
    [type, intensity, deviceId]
  );

  return result.rows[0];
}

async function getEvents() {
  const result = await pool.query(
    "SELECT * FROM eventos WHERE tipo_evento = ANY($1::text[]) ORDER BY data_hora DESC",
    [TIPOS_PERMITIDOS]
  );

  return result.rows;
}

module.exports = {
  saveEvent,
  getEvents
};
