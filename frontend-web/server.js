const express = require("express");
const path = require("path");

const app = express();

const DIST_DIRECTORY = "dist/frontend-web";

app.use(express.static(path.join(__dirname, DIST_DIRECTORY)));

app.get((req, res) => res.sendFile(path.join(DIST_DIRECTORY, "index.html")));

app.listen(process.env.PORT);
