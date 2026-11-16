
require('dotenv').config();

const express = require('express');
const cors = require('cors');
const helmet = require('helmet');

const pool = require('./config/database');
const authRoutes = require('./routes/authRoutes');
const reportRoutes = require('./routes/reportRoutes');

const app = express();

app.use(helmet());
app.use(cors());
app.use(express.json());

app.use('/api/auth', authRoutes);
app.use('/api/reports', reportRoutes);

app.get('/api/health', (req, res) => {
    res.json({
        success: true,
        message: 'Smart Air API funcionando correctamente'
    });
});


app.get('/api/db-check', async (req, res) => {
    try {
        const result = await pool.query('SELECT NOW()');

        res.json({
            success: true,
            message: 'Base de datos conectada correctamente',
            databaseTime: result.rows[0].now
        });

    } catch (error) {
        console.error('Error al conectar con la base de datos:', error);

        res.status(500).json({
            success: false,
            message: 'No se pudo conectar con la base de datos'
        });
    }
});

const PORT = process.env.PORT || 3000;

app.listen(PORT, () => {
    console.log(`Smart Air API ejecutándose en el puerto ${PORT}`);
});
