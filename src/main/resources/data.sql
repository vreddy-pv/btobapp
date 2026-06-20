INSERT INTO b2b_accounts (account_number, company_name, contact_name, contact_email, contact_phone, credit_limit, current_balance, tier) VALUES
('ACC-001', 'Downtown Auto Repair', 'Mike Johnson', 'mike@downtownauto.com', '555-0101', 50000.00, 12500.00, 'GOLD'),
('ACC-002', 'Quick Fix Garage', 'Sarah Lee', 'sarah@quickfix.com', '555-0102', 25000.00, 8200.00, 'SILVER'),
('ACC-003', 'Premier Motors', 'Tom Brown', 'tom@premiermotors.com', '555-0103', 100000.00, 34500.00, 'GOLD');

INSERT INTO auto_parts (sku, name, description, category, b2b_price, inventory_level, image_url) VALUES
('BRK-001', 'Ceramic Brake Pads - Front', 'Premium ceramic brake pads for front wheels, fits most sedans 2015-2023', 'Brakes', 45.99, 200, null),
('BRK-002', 'Ceramic Brake Pads - Rear', 'Premium ceramic brake pads for rear wheels, fits most sedans 2015-2023', 'Brakes', 42.99, 180, null),
('BRK-003', 'Brake Rotor - Front (Drilled)', 'Cross-drilled brake rotor, 12-inch diameter, premium alloy', 'Brakes', 89.99, 75, null),
('FLT-001', 'Oil Filter - Standard', 'Standard oil filter, compatible with most vehicles', 'Filters', 8.99, 500, null),
('FLT-002', 'Oil Filter - Premium', 'Premium synthetic oil filter, extended life', 'Filters', 14.99, 350, null),
('FLT-003', 'Air Filter - Engine', 'High-flow engine air filter, washable', 'Filters', 24.99, 280, null),
('FLT-004', 'Cabin Air Filter', 'Activated carbon cabin air filter', 'Filters', 19.99, 220, null),
('ENG-001', 'Spark Plug Set (4-Pack)', 'Iridium spark plugs, set of 4', 'Engine', 36.99, 150, null),
('ENG-002', 'Engine Oil 5W-30 (5L)', 'Synthetic engine oil, 5-liter container', 'Engine', 32.99, 400, null),
('ENG-003', 'Engine Oil 10W-40 (5L)', 'Synthetic engine oil, 5-liter container', 'Engine', 32.99, 350, null),
('SUS-001', 'Shock Absorber - Front', 'Gas-filled shock absorber, front pair', 'Suspension', 65.99, 90, null),
('SUS-002', 'Shock Absorber - Rear', 'Gas-filled shock absorber, rear pair', 'Suspension', 59.99, 85, null),
('ELC-001', 'Battery 12V 60Ah', 'Maintenance-free 12V battery, 60 amp-hours', 'Electrical', 129.99, 60, null),
('ELC-002', 'Alternator - Standard', 'Standard alternator, 120 amp output', 'Electrical', 189.99, 40, null),
('BEL-001', 'Serpentine Belt', 'Multi-rib serpentine belt, 6-rib x 72-inch', 'Belts', 22.99, 200, null);

INSERT INTO sales_orders (order_number, account_id, status, total_amount, order_date) VALUES
('ORD-001', 1, 'DELIVERED', 179.94, '2026-06-10T10:30:00'),
('ORD-002', 1, 'SHIPPED', 89.99, '2026-06-15T14:00:00'),
('ORD-003', 2, 'PENDING', 102.95, '2026-06-18T09:15:00');

INSERT INTO order_line_items (order_id, part_id, quantity, unit_price, subtotal) VALUES
(1, 1, 2, 45.99, 91.98),
(1, 2, 2, 42.99, 85.98),
(2, 3, 1, 89.99, 89.99),
(3, 4, 5, 8.99, 44.95),
(3, 6, 1, 24.99, 24.99),
(3, 9, 1, 32.99, 32.99);
