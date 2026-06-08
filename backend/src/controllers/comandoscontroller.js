const {
  criarComando,
  listarComandosPendentes,
  marcarComandoExecutado
} = require("../db/comandos");

const ACOES_PERMITIDAS = new Set(["LED", "VIBRAR"]);

exports.criarComando = async (req, res) => {
  const acaoBruta = req.body.acao ?? req.body.action ?? req.body.comando;
  const acao = String(acaoBruta || "").trim().toUpperCase();
  const dispositivo = String(req.body.dispositivo || req.body.device || "Pulseira Principal").trim();

  if (!ACOES_PERMITIDAS.has(acao)) {
    return res.status(400).json({
      message: "Acao invalida. Use apenas LED ou VIBRAR."
    });
  }

  try {
    const comando = await criarComando(acao, dispositivo);
    return res.status(201).json(comando);
  } catch (error) {
    console.error("Error creating command:", error);
    return res.status(500).json({
      message: "Internal server error",
      error: error.message
    });
  }
};

exports.listarComandosPendentes = async (req, res) => {
  try {
    const comandos = await listarComandosPendentes();
    return res.json(comandos);
  } catch (error) {
    console.error("Error fetching pending commands:", error);
    return res.status(500).json({
      message: "Internal server error",
      error: error.message
    });
  }
};

exports.marcarExecutado = async (req, res) => {
  try {
    const comando = await marcarComandoExecutado(req.params.id);
    if (!comando) {
      return res.status(404).json({ message: "Comando nao encontrado." });
    }
    return res.json(comando);
  } catch (error) {
    console.error("Error updating command:", error);
    return res.status(500).json({
      message: "Internal server error",
      error: error.message
    });
  }
};
