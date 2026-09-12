-- Default Game Configurations Seed Data
INSERT INTO games (id, name, category, status) VALUES
('coin_flip', 'Coin Flip', 'CASINO', 'ACTIVE'),
('classic_dice', 'Classic Dice', 'CASINO', 'ACTIVE'),
('keno', 'Keno', 'CASINO', 'ACTIVE'),
('mines', 'Mines', 'CASINO', 'ACTIVE'),
('perya_color', 'Perya Color Game', 'CASINO', 'ACTIVE'),
('ring_of_fortune', 'Ring of Fortune', 'CASINO', 'ACTIVE'),
('double', 'Double', 'CASINO', 'ACTIVE'),
('limbo', 'Limbo', 'CASINO', 'ACTIVE')
ON CONFLICT (id) DO NOTHING;
