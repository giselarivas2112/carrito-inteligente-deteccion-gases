
const express = require('express');

const {
    register,
    login
} = require('../controllers/authController');

const authMiddleware = require('../middleware/authMiddleware');
const pool = require('../config/database');

const router = express.Router();

router.post('/register', register);

router.post('/login', login);

router.get('/me', authMiddleware, async (req, res) => {
    try {
        const result = await pool.query(
            `SELECT id, name, email, created_at
             FROM users
             WHERE id = $1`,
            [req.user.id]
        );

        if (result.rows.length === 0) {
            return res.status(404).json({
                success: false,
                message: 'Usuario no encontrado'
            });
        }

        res.json({
            success: true,
            user: result.rows[0]
        });

    } catch (error) {
        console.error('Error al consultar perfil:', error);

        res.status(500).json({
            success: false,
            message: 'Error al consultar el perfil'
        });
    }
});

module.exports = router;
