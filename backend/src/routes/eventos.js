const express = require("express");
const router = express.Router();

const {
  createEvent,
  listEvents
} = require("../controllers/eventoscontroller");

router.post("/", createEvent);
router.get("/", listEvents);

module.exports = router;
