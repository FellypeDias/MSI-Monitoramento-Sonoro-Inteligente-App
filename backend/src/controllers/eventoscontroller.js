const { saveEvent, getEvents } = require("../db/events");

const TIPOS_PERMITIDOS = new Set(["campainha", "alarme"]);

exports.createEvent = async (req, res) => {
  const { type, intensity, deviceId } = req.body;
  const tipoNormalizado = String(type || "").trim().toLowerCase();

  if (!TIPOS_PERMITIDOS.has(tipoNormalizado)) {
    return res.status(400).json({
      message: "Tipo de evento invalido. Use apenas campainha ou alarme."
    });
  }

  try {
    const event = await saveEvent(tipoNormalizado, intensity, deviceId);
    return res.status(201).json(event);
  } catch (error) {
    console.error("Error creating event:", error);
    return res.status(500).json({
      message: "Internal server error",
      error: error.message
    });
  }
};

exports.listEvents = async (req, res) => {
  try {
    const events = await getEvents();
    return res.json(events);
  } catch (error) {
    console.error("Error fetching events:", error);
    return res.status(500).json({
      message: "Internal server error",
      error: error.message
    });
  }
};
