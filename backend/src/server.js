const express = require("express");
const app = express();

const eventosRoutes = require("./routes/eventos");
const comandosRoutes = require("./routes/comandos");

app.use(express.json());

app.get("/", (req, res) => {
  res.json({ ok: true, message: "API rodando" });
});

app.use("/eventos", eventosRoutes);
app.use("/comandos", comandosRoutes);

const PORT = process.env.PORT || 3000;

app.listen(PORT, "0.0.0.0", () => {
  console.log(`Servidor rodando na porta ${PORT}`);
});
