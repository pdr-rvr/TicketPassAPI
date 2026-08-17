ALTER TABLE users ADD COLUMN password VARCHAR(255);
ALTER TABLE users ADD COLUMN role VARCHAR(50) NOT NULL DEFAULT 'ROLE_USER';

-- Insert admin user: email=admin@ticketpass.com, password=admin123, role=ROLE_ADMIN
INSERT INTO users (name, email, password, role) 
VALUES ('Administrator', 'admin@ticketpass.com', '$2a$10$p3AmTXv0eDsFALOjcIalTu/wgg.tUCWS1xmy.OeDHfAGvXSDzWtt2', 'ROLE_ADMIN');
