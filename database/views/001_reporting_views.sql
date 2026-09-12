-- Financial & Operational Reporting Views
CREATE OR REPLACE VIEW view_daily_financials AS
SELECT
    DATE(created_at) AS report_date,
    COUNT(id) AS total_transactions,
    SUM(amount) AS total_volume
FROM wallet_transactions
GROUP BY DATE(created_at);
