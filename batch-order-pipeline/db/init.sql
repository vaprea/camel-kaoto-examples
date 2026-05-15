-- Customers master table (used for Content Enricher lookups)
CREATE TABLE IF NOT EXISTS customers (
    customer_id   VARCHAR(20)  PRIMARY KEY,
    customer_name  VARCHAR(100) NOT NULL,
    customer_email VARCHAR(100) NOT NULL,
    tier           VARCHAR(20)  NOT NULL DEFAULT 'standard'
);

INSERT INTO customers (customer_id, customer_name, customer_email, tier) VALUES
    ('CUST-001', 'Acme Corp',           'orders@acme.com',          'gold'),
    ('CUST-002', 'Globex Industries',   'billing@globex.com',       'silver'),
    ('CUST-003', 'Initech Solutions',   'ap@initech.com',           'standard'),
    ('CUST-004', 'Umbrella Ltd',        'finance@umbrella.com',     'gold'),
    ('CUST-005', 'Wayne Enterprises',   'orders@wayne.com',         'platinum'),
    ('CUST-006', 'Stark Industries',    'procurement@stark.com',    'platinum'),
    ('CUST-007', 'Cyberdyne Systems',   'orders@cyberdyne.com',     'silver'),
    ('CUST-008', 'Soylent Corp',        'billing@soylent.com',      'standard')
ON CONFLICT (customer_id) DO NOTHING;
