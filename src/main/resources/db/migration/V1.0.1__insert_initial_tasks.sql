INSERT INTO task (description, status, is_enabled, created_at, updated_at)
VALUES
  ('Buy groceries', 'TODO', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('Finish code challenge', 'IN_PROGRESS', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('Read documentation', 'DONE', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);