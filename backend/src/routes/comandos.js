const express = require("express");
const router = express.Router();

const {
  criarComando,
  listarComandosPendentes,
  marcarExecutado
} = require("../controllers/comandoscontroller");

router.post("/", criarComando);
router.get("/pendentes", listarComandosPendentes);
router.patch("/:id/executado", marcarExecutado);

module.exports = router;
