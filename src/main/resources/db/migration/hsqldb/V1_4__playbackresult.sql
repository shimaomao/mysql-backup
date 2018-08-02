CREATE TABLE play_back_result
(
  id INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 100, INCREMENT BY 1) PRIMARY KEY,
  play_back_id INTEGER NOT NULL,
  success BOOLEAN NOT NULL,
  reason BLOB,
  created_at TIMESTAMP(2)
);