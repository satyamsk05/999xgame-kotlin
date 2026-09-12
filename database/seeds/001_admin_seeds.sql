-- Admin Roles & Permissions Seed Data
INSERT INTO admin_roles (id, name, description) VALUES
('role_super_admin', 'SUPER_ADMIN', 'Full system access'),
('role_finance_admin', 'FINANCE_ADMIN', 'Withdrawals, Deposits, Wallet access'),
('role_game_admin', 'GAME_ADMIN', 'Game configurations and round management')
ON CONFLICT (id) DO NOTHING;
