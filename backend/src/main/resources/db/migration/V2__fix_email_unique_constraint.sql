ALTER TABLE clients DROP CONSTRAINT IF EXISTS clients_email_key;

CREATE UNIQUE INDEX idx_clients_email_unique 
ON clients(email) 
WHERE deleted_at IS NULL;
