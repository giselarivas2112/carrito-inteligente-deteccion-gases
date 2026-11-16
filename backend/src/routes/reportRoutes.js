
const express = require('express');

const authMiddleware = require('../middleware/authMiddleware');

const {
    createReport,
    addReadings,
    updateReport,
    getReports,
    getReportById
} = require('../controllers/reportController');

const router = express.Router();

router.use(authMiddleware);

router.post('/', createReport);

router.get('/', getReports);

router.get('/:reportId', getReportById);

router.post('/:reportId/readings', addReadings);

router.patch('/:reportId', updateReport);

module.exports = router;
