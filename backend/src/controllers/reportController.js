
const pool = require('../config/database');

// ==========================================
// CREAR REPORTE
// ==========================================
const createReport = async (req, res) => {
    try {
        const { title, startedAt, routeSegments } = req.body;

        if (!title || !title.trim()) {
            return res.status(400).json({
                success: false,
                message: 'El título del reporte es obligatorio'
            });
        }

        const result = await pool.query(
            `INSERT INTO reports (
                user_id,
                title,
                status,
                risk_level,
                started_at,
                route_segments
            )
            VALUES ($1, $2, 'in_progress', 'safe', $3, $4)
            RETURNING *`,
            [
                req.user.id,
                title.trim(),
                startedAt || new Date(),
                JSON.stringify(routeSegments || [])
            ]
        );

        return res.status(201).json({
            success: true,
            message: 'Reporte creado correctamente',
            report: result.rows[0]
        });

    } catch (error) {
        console.error('Error al crear reporte:', error);

        return res.status(500).json({
            success: false,
            message: 'Error al crear el reporte'
        });
    }
};

// ==========================================
// GUARDAR LECTURAS DE SENSORES
// ==========================================
const addReadings = async (req, res) => {
    const client = await pool.connect();

    try {
        const { readings } = req.body;
        const { reportId } = req.params;

        if (!Array.isArray(readings) || readings.length === 0) {
            return res.status(400).json({
                success: false,
                message: 'Debes enviar al menos una lectura'
            });
        }

        const allowedSensors = ['mq2', 'mq6', 'mq135', 'flame'];
        const allowedRisks = ['safe', 'caution', 'danger'];

        for (const reading of readings) {
            if (
                !allowedSensors.includes(reading.sensorType) ||
                reading.value === undefined ||
                reading.value === null ||
                !Number.isFinite(Number(reading.value)) ||
                !reading.unit ||
                !allowedRisks.includes(reading.riskLevel || 'safe')
            ) {
                return res.status(400).json({
                    success: false,
                    message: 'Una o más lecturas tienen datos inválidos'
                });
            }
        }

        await client.query('BEGIN');

        const report = await client.query(
            `SELECT id
             FROM reports
             WHERE id = $1 AND user_id = $2
             FOR UPDATE`,
            [reportId, req.user.id]
        );

        if (report.rows.length === 0) {
            await client.query('ROLLBACK');

            return res.status(404).json({
                success: false,
                message: 'Reporte no encontrado'
            });
        }

        const savedReadings = [];

        for (const reading of readings) {
            const result = await client.query(
                `INSERT INTO sensor_readings (
                    report_id,
                    sensor_type,
                    value,
                    unit,
                    risk_level,
                    segment_number
                )
                VALUES ($1, $2, $3, $4, $5, $6)
                RETURNING *`,
                [
                    reportId,
                    reading.sensorType,
                    Number(reading.value),
                    reading.unit,
                    reading.riskLevel || 'safe',
                    reading.segmentNumber ?? null
                ]
            );

            savedReadings.push(result.rows[0]);
        }

        await client.query('COMMIT');

        return res.status(201).json({
            success: true,
            message: 'Lecturas guardadas correctamente',
            readings: savedReadings
        });

    } catch (error) {
        await client.query('ROLLBACK');

        console.error('Error al guardar lecturas:', error);

        return res.status(500).json({
            success: false,
            message: 'Error al guardar las lecturas'
        });

    } finally {
        client.release();
    }
};

// ==========================================
// FINALIZAR O INTERRUMPIR REPORTE
// ==========================================
const updateReport = async (req, res) => {
    try {
        const { status, riskLevel, finishedAt, durationSeconds } = req.body;
        const { reportId } = req.params;

        const validStatuses = ['completed', 'interrupted'];
        const validRisks = ['safe', 'caution', 'danger'];

        if (!validStatuses.includes(status)) {
            return res.status(400).json({
                success: false,
                message: 'Estado del reporte inválido'
            });
        }

        if (riskLevel && !validRisks.includes(riskLevel)) {
            return res.status(400).json({
                success: false,
                message: 'Nivel de riesgo inválido'
            });
        }

        if (
            durationSeconds !== undefined &&
            (!Number.isInteger(durationSeconds) || durationSeconds < 0)
        ) {
            return res.status(400).json({
                success: false,
                message: 'La duración debe ser un entero positivo'
            });
        }

        const result = await pool.query(
            `UPDATE reports
             SET status = $1,
                 risk_level = COALESCE($2, risk_level),
                 finished_at = COALESCE($3, NOW()),
                 duration_seconds = $4
             WHERE id = $5
               AND user_id = $6
               AND status = 'in_progress'
             RETURNING *`,
            [
                status,
                riskLevel || null,
                finishedAt || null,
                durationSeconds ?? null,
                reportId,
                req.user.id
            ]
        );

        if (result.rows.length === 0) {
            return res.status(404).json({
                success: false,
                message: 'Reporte no encontrado o ya finalizado'
            });
        }

        return res.json({
            success: true,
            message: 'Reporte actualizado correctamente',
            report: result.rows[0]
        });

    } catch (error) {
        console.error('Error al actualizar reporte:', error);

        return res.status(500).json({
            success: false,
            message: 'Error al actualizar el reporte'
        });
    }
};

// ==========================================
// OBTENER HISTORIAL DE REPORTES
// ==========================================
const getReports = async (req, res) => {
    try {
        const result = await pool.query(
            `SELECT *
             FROM reports
             WHERE user_id = $1
             ORDER BY created_at DESC`,
            [req.user.id]
        );

        return res.json({
            success: true,
            reports: result.rows
        });

    } catch (error) {
        console.error('Error al consultar reportes:', error);

        return res.status(500).json({
            success: false,
            message: 'Error al consultar los reportes'
        });
    }
};

// ==========================================
// OBTENER UN REPORTE CON SUS LECTURAS
// ==========================================
const getReportById = async (req, res) => {
    try {
        const { reportId } = req.params;

        const reportResult = await pool.query(
            `SELECT *
             FROM reports
             WHERE id = $1 AND user_id = $2`,
            [reportId, req.user.id]
        );

        if (reportResult.rows.length === 0) {
            return res.status(404).json({
                success: false,
                message: 'Reporte no encontrado'
            });
        }

        const readingsResult = await pool.query(
            `SELECT *
             FROM sensor_readings
             WHERE report_id = $1
             ORDER BY recorded_at ASC`,
            [reportId]
        );

        return res.json({
            success: true,
            report: reportResult.rows[0],
            readings: readingsResult.rows
        });

    } catch (error) {
        console.error('Error al consultar reporte:', error);

        return res.status(500).json({
            success: false,
            message: 'Error al consultar el reporte'
        });
    }
};

module.exports = {
    createReport,
    addReadings,
    updateReport,
    getReports,
    getReportById
};
